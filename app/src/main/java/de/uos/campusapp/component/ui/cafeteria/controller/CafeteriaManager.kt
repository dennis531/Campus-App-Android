package de.uos.campusapp.component.ui.cafeteria.controller

import android.content.Context
import android.preference.PreferenceManager
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.notifications.ProvidesNotifications
import de.uos.campusapp.component.other.locations.LocationManager
import de.uos.campusapp.component.ui.cafeteria.CafeteriaMenuCard
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaWithMenus
import de.uos.campusapp.component.ui.cafeteria.repository.CafeteriaLocalRepository
import de.uos.campusapp.component.ui.overview.card.Card
import de.uos.campusapp.component.ui.overview.card.ProvidesCard
import de.uos.campusapp.database.CaDb
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import java.util.*
import javax.inject.Inject

/**
 * Cafeteria Manager, handles database stuff, external imports
 */
class CafeteriaManager @Inject constructor(private val context: Context) : ProvidesCard, ProvidesNotifications {
    val localRepository: CafeteriaLocalRepository

    init {
        val db = CaDb.getInstance(context)
        localRepository = CafeteriaLocalRepository(db)
    }

    /**
     * Returns a list of [CafeteriaMenuItem]s of the best-matching cafeteria. If there's no
     * best-matching cafeteria, it returns an empty list.
     */
    val bestMatchCafeteriaMenus: List<CafeteriaMenuItem>
        get() {
            val cafeteriaId = bestMatchMensaId
            return if (cafeteriaId == Const.NO_CAFETERIA_FOUND) {
                emptyList()
            } else getCafeteriaMenusByCafeteriaId(cafeteriaId)
        }

    // Choose which mensa should be shown
    val bestMatchMensaId: String
        get() {
            val cafeteriaId = LocationManager(context).getCafeteria()
            if (cafeteriaId == Const.NO_CAFETERIA_FOUND) {
                Utils.log("could not get a Cafeteria from locationManager!")
            }
            return cafeteriaId
        }

    override fun getCards(cacheControl: CacheControl): List<Card> {
        val results = ArrayList<Card>()

        // ids have to be added to a new set because the data would be changed otherwise
        val cafeteriaIds = HashSet<String>(20)
        cafeteriaIds.addAll(PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(Const.CAFETERIA_CARDS_SETTING, HashSet(0))!!)

        // adding the location based id to the set now makes sure that the cafeteria is not shown twice
        if (cafeteriaIds.contains(Const.CAFETERIA_BY_LOCATION_SETTINGS_ID)) {
            cafeteriaIds.remove(Const.CAFETERIA_BY_LOCATION_SETTINGS_ID)
            cafeteriaIds.add(LocationManager(context).getCafeteria())
        }

        for (id in cafeteriaIds) {
            if (id == Const.NO_CAFETERIA_FOUND) {
                // no cafeteria based on the location could be found
                continue
            }
            val card = CafeteriaMenuCard(context, localRepository.getCafeteriaWithMenus(id))
            card.getIfShowOnStart()?.let {
                results.add(it)
            }
        }

        return results
    }

    override fun hasNotificationsEnabled(): Boolean {
        return Utils.getSettingBool(context, "card_cafeteria_phone", true)
    }

    private fun getCafeteriaMenusByCafeteriaId(cafeteriaId: String): List<CafeteriaMenuItem> {
        val cafeteria = CafeteriaWithMenus(cafeteriaId)

        cafeteria.menuDates = localRepository.getAllMenuDates()
        return localRepository.getCafeteriaMenus(cafeteriaId, cafeteria.nextMenuDate)
    }
}