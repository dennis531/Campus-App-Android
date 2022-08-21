package de.uos.campusapp.component.ui.cafeteria.details

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaItem
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.repository.CafeteriaLocalRepository
import de.uos.campusapp.utils.LocationHelper.calculateDistanceToCafeteria
import de.uos.campusapp.utils.Utils
import de.uos.campusapp.utils.plusAssign
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import javax.inject.Inject

class CafeteriaViewModel @Inject constructor(
    private val localRepository: CafeteriaLocalRepository
) : ViewModel() {

    private val _cafeterias = MutableLiveData<List<CafeteriaItem>>()
    val cafeterias: LiveData<List<CafeteriaItem>> = _cafeterias

    private val _selectedCafeteria = MutableLiveData<CafeteriaItem>()
    val selectedCafeteria: LiveData<CafeteriaItem> = _selectedCafeteria

    private val _cafeteriaMenus = MutableLiveData<List<CafeteriaMenuItem>>()
    val cafeteriaMenus: LiveData<List<CafeteriaMenuItem>> = _cafeteriaMenus

    private val _menuDates = MutableLiveData<List<DateTime>>()
    val menuDates: LiveData<List<DateTime>> = _menuDates

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private val compositeDisposable = CompositeDisposable()

    /**
     * Updates the currently selected [CafeteriaItem] and posts a new value to [selectedCafeteria].
     *
     * @param cafeteria The newly selected [CafeteriaItem]
     */
    fun updateSelectedCafeteria(cafeteria: CafeteriaItem) {
        _selectedCafeteria.postValue(cafeteria)
    }

    /**
     * Fetches all [CafeteriaItem]s around the provided [Location] from the database and posts the
     * results to [cafeterias].
     *
     * @param location The current [Location]
     */
    fun fetchCafeterias(location: Location) {
        compositeDisposable += localRepository.getAllCafeterias()
                .map { transformCafeteria(it, location) }
                .subscribeOn(Schedulers.io())
                .defaultIfEmpty(emptyList())
                .doOnError { _error.postValue(true) }
                .doOnNext { _error.postValue(it.isEmpty()) }
                .subscribe(_cafeterias::postValue, Utils::log)
    }

    /**
     * Fetches all menu dates from the database and posts them to [menuDates].
     */
    fun fetchMenuDates() {
        compositeDisposable += Flowable.fromCallable { localRepository.getAllMenuDates() }
                .subscribeOn(Schedulers.io())
                .defaultIfEmpty(emptyList())
                .subscribe(_menuDates::postValue, Utils::log)
    }

    fun fetchCafeteriaMenus(id: String, date: DateTime) {
        compositeDisposable += Flowable.fromCallable { localRepository.getCafeteriaMenus(id, date) }
                .subscribeOn(Schedulers.io())
                .defaultIfEmpty(emptyList())
                .subscribe { _cafeteriaMenus.postValue(it) }
    }

    /**
     * Adds the distance between user and cafeteria to model.
     */
    private fun transformCafeteria(
        cafeterias: List<CafeteriaItem>,
        location: Location
    ): List<CafeteriaItem> {
        return cafeterias.map {
            it.distance = calculateDistanceToCafeteria(it, location)
            it
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
