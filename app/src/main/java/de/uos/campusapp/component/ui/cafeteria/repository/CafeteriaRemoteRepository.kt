package de.uos.campusapp.component.ui.cafeteria.repository

import android.annotation.SuppressLint
import android.content.Context
import de.uos.campusapp.utils.ConfigUtils
import de.uos.campusapp.utils.Utils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CafeteriaRemoteRepository @Inject constructor(
    private val context: Context,
    private val localRepository: CafeteriaLocalRepository
) {

    val cafeteriaClient = ConfigUtils.getCafeteriaClient(context)

    /**
     * Downloads cafeterias and stores them in the local repository.
     *
     * First checks whether a sync is necessary
     * Then clears current cache
     * Insert new cafeterias
     * Lastly updates last sync
     *
     */
    @SuppressLint("CheckResult")
    fun fetchCafeterias(force: Boolean) {
        Observable.fromCallable { cafeteriaClient.getCafeterias() }
                .filter { localRepository.getLastSync() == null || force }
                .subscribeOn(Schedulers.io())
                .doOnNext { localRepository.clear() }
                .doAfterNext { localRepository.updateLastSync() }
                .subscribe(localRepository::addCafeterias, Utils::log)
    }
}
