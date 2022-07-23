package de.tum.`in`.tumcampusapp.component.other.locations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import de.tum.`in`.tumcampusapp.api.generic.LMSClient
import de.tum.`in`.tumcampusapp.component.other.locations.model.BuildingToGps
import de.tum.`in`.tumcampusapp.component.other.locations.model.Campus
import de.tum.`in`.tumcampusapp.component.other.locations.model.Geo
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarController
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.api.RoomFinderAPI
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderCoordinateInterface
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.model.RoomFinderRoomInterface
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.Cafeteria
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.Station
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.ConfigConst
import de.tum.`in`.tumcampusapp.utils.ConfigUtils
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Utils
import org.jetbrains.anko.doAsync
import java.io.IOException
import java.lang.Double.parseDouble
import java.util.*
import javax.inject.Inject

/**
 * Location manager, manages intelligent location services, provides methods to easily access
 * the users current location, campus, next public transfer station and best cafeteria
 */
class LocationManager @Inject constructor(c: Context) {
    private val mContext: Context = c.applicationContext
    private val buildingToGpsDao: BuildingToGpsDao
    private var manager: android.location.LocationManager? = null
    private val apiClient: LMSClient = ConfigUtils.getLMSClient(mContext)

    init {
        val db = TcaDb.getInstance(c)
        buildingToGpsDao = db.buildingToGpsDao()
    }

    /**
     * Tests if Google Play services is available and then gets last known position
     * If location services are not available use default location if set
     * @return Returns the more or less current position or null on failure
     */
    private fun getCurrentLocation(): Location? {
        if (!servicesConnected()) {
            return null
        }

        val loc = getLastLocation()
        if (loc != null) {
            return loc
        }

        val selectedCampus = Utils.getSetting(mContext, Const.DEFAULT_CAMPUS, Const.NO_DEFAULT_CAMPUS_ID)
        val allCampi = ConfigUtils.getConfig(ConfigConst.CAMPUS, emptyList<Campus>())

        if (Const.NO_DEFAULT_CAMPUS_ID == selectedCampus) {
            return null
        }

        return allCampi.find { it.id == selectedCampus }?.getLocation()
    }

    /**
     * Returns the "id" of the current campus
     *
     * @return Campus id
     */
    private fun getCurrentCampus(): Campus? {
        val loc = getCurrentLocation() ?: return null
        return getCampusFromLocation(loc)
    }

    /**
     * Returns the cafeteria's identifier which is near the given location
     * The used radius around the cafeteria is 1km.
     *
     * @return Campus id
     */
    private fun getCafeterias(): List<Cafeteria> {
        val location = getCurrentOrNextLocation()

        val lat = location.latitude
        val lng = location.longitude
        val results = FloatArray(1)
        val list = LinkedList<Cafeteria>()
        for (cafeteria in list) {
            Location.distanceBetween(cafeteria.latitude, cafeteria.longitude, lat, lng, results)
            cafeteria.distance = results[0]
        }
        list.sort()
        return list
    }

    /**
     * Gets the current location and if it is not available guess
     * by querying for the next lecture.
     *
     * @return Any of the above described locations.
     */
    fun getCurrentOrNextLocation(): Location {
        return getCurrentLocation() ?: getNextLocation()
    }

    /**
     * Returns the last known location of the device
     *
     * @return The last location
     */
    fun getLastLocation(): Location? {
        // Check Location permission for Android 6.0
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        var bestResult: Location? = null
        var bestAccuracy = java.lang.Float.MAX_VALUE
        var bestTime = java.lang.Long.MIN_VALUE
        val minTime: Long = 0

        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        val matchingProviders = locationManager.allProviders
        for (provider in matchingProviders) {

            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                val accuracy = location.accuracy
                val time = location.time

                if (time > minTime && accuracy < bestAccuracy) {
                    bestResult = location
                    bestAccuracy = accuracy
                    bestTime = time
                } else if (time < minTime && bestAccuracy == java.lang.Float.MAX_VALUE && time > bestTime) {
                    bestResult = location
                    bestTime = time
                }
            }
        }
        return bestResult
    }

    /**
     * Returns the name of the station that is nearby and/or set by the user
     *
     * @return Name of the station or null if the user is not near any campus or campus has no station
     */
    fun getStation(): Station? {
        val campus = getCurrentCampus() ?: return null

        // Campus has no associated station
        if (campus.stations.isNullOrEmpty()) {
            return null
        }

        // Try to find favorite station for current campus
        val stationName = Utils.getSetting(mContext, "card_stations_default_" + campus.id, "")
        if (stationName.isNotEmpty()) {
            campus.stations.find {
                it.name == stationName
            }?.let { return it.apply { quality = Int.MAX_VALUE } }
        }
        // Otherwise fallback to first station as the default
        return campus.stations.first().apply { quality = Int.MAX_VALUE }
    }

    /**
     * Gets the campus you are currently on or if you are at home or wherever
     * query for your next lecture and find out at which campus it takes place
     */
    private fun getCurrentOrNextCampus(): Campus? {
        return getCurrentCampus() ?: getNextCampus()
    }

    /**
     * If the user is in university or a lecture has been recognized => Get nearest cafeteria
     */
    fun getCafeteria(): Int {
        val campus = getCurrentOrNextCampus()
        if (campus != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
//            val cafeteria = prefs.getString("card_cafeteria_default_" + campus.id, campus.defaultMensa) // TODO: Rework Cafeteria
            val cafeteria = null
            if (cafeteria != null) {
                return Integer.parseInt(cafeteria)
            }
        }

        val allCafeterias = getCafeterias()
        return if (allCafeterias.isEmpty()) Const.NO_CAFETERIA_FOUND else allCafeterias[0].id
    }

    /**
     * Queries your calender and gets the campus at which your next lecture takes place
     */
    private fun getNextCampus(): Campus? = getCampusFromLocation(getNextLocation())

    /**
     * Gets the location of the next room where the user has a lecture.
     * If no lectures are available Garching will be returned
     *
     * @return Location of the next lecture room
     */
    private fun getNextLocation(): Location {
        val manager = CalendarController(mContext)
        val geo = manager.nextCalendarItemGeo
        if (geo == null) {
            val firstCampus = ConfigUtils.getConfig(ConfigConst.CAMPUS, emptyList<Campus>()).firstOrNull()
            return firstCampus?.getLocation() ?: Location("defaultLocation")
        }

        val location = Location("roomfinder")
        location.latitude = parseDouble(geo.latitude)
        location.longitude = parseDouble(geo.longitude)
        return location
    }

    /**
     * This method tries to get the list of BuildingToGps by querying database or requesting the server.
     * If both two ways fail, it returns an empty list.
     * we have to fetch buildings to gps mapping first.
     * @return The list of BuildingToGps
     */
    private fun fetchBuildingsToGps(): List<BuildingToGps> {
        val results = buildingToGpsDao.all.orEmpty()
        if (results.isNotEmpty()) {
            return results
        }

        return results

//        val newResults = tryOrNull { TUMCabeClient.getInstance(mContext).building2Gps }
//        return newResults.orEmpty().also {
//            buildingToGpsDao.insert(*it.toTypedArray())
//        }
    }

    /**
     * Get Building ID accroding to the current location
     * Do not call on UI thread.
     *
     * @return the id of current building
     */
    fun fetchBuildingIDFromCurrentLocation(callback: (String?) -> Unit) {
        doAsync {
            fetchBuildingIDFromLocation(getCurrentOrNextLocation(), callback)
        }
    }

    /**
     * This might be battery draining
     *
     * @return false if permission check fails
     */
    fun getLocationUpdates(locationListener: LocationListener): Boolean {
        // Check Location permission for Android 6.0
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        // Acquire a reference to the system Location Manager
        if (manager == null) {
            manager = mContext.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        }
        // Register the listener with the Location Manager to receive location updates
        manager!!.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
        manager!!.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
        return true
    }

    fun stopReceivingUpdates(locationListener: LocationListener) {
        if (manager != null) {
            manager!!.removeUpdates(locationListener)
        }
    }

    /**
     * Checks that Google Play services are available
     */
    private fun servicesConnected(): Boolean {
        val resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS

        Utils.log("Google Play services is $resultCode")
        return resultCode
    }

    /**
     * Get the geo information for a room
     *
     * @param buildingId arch_id of the room
     * @return Location or null on failure
     */
    private fun fetchRoomGeo(room: RoomFinderRoomInterface): Geo? {
        return try {
            val coordinate = (apiClient as RoomFinderAPI).fetchRoomCoordinates(room)
            if (coordinate == null) {
                Utils.log("Coordinate api error")
                return null
            }
            convertRoomFinderCoordinateToGeo(coordinate)
        } catch (e: IOException) {
            Utils.log(e)
            null
        }
    }

    /**
     * Translates room title to Geo
     * HINT: Don't call from UI thread
     *
     * @param roomTitle Room title
     * @return Location or null on failure
     */
    fun roomLocationStringToGeo(roomTitle: String): Geo? {
        try {
            val rooms = (apiClient as RoomFinderAPI).searchRooms(roomTitle)

            if (!rooms.isEmpty()) {
                val room = rooms[0]
                return fetchRoomGeo(room)
            }
        } catch (e: Exception) {
            Utils.log(e)
        }
        return null
    }

    /**
     * Get Building ID accroding to the given location.
     * Do not call on UI thread.
     *
     * @param location the give location
     * @return the id of current building
     */
    private fun fetchBuildingIDFromLocation(location: Location, block: (String?) -> Unit) {
        val buildingToGpsList = fetchBuildingsToGps()
        if (buildingToGpsList.isEmpty()) {
            block(null)
        }

        val lat = location.latitude
        val lng = location.longitude
        val results = FloatArray(1)
        var bestDistance = java.lang.Float.MAX_VALUE
        var bestBuilding = ""

        for ((id, latitude, longitude) in buildingToGpsList) {
            val buildingLat = parseDouble(latitude)
            val buildingLng = parseDouble(longitude)

            Location.distanceBetween(buildingLat, buildingLng, lat, lng, results)
            val distance = results[0]
            if (distance < bestDistance) {
                bestDistance = distance
                bestBuilding = id
            }
        }

        val result = bestBuilding.takeIf { bestDistance < 1_000 }
        block(result)
    }

    companion object {
//        private enum class Campus(val short: String, val lat: Double, val lon: Double, val defaultMensa: String?, val defaultStation: Stations) {
//            GarchingForschungszentrum("G", 48.2648424, 11.6709511, "422", Stations.GarchingForschungszentrum),
//            GarchingHochbrueck("H", 48.249432, 11.633905, null, Stations.GarchingHochbrueck),
//            Weihenstephan("W", 48.397990, 11.722727, "423", Stations.Weihenstephan),
//            Stammgelaende("C", 48.149436, 11.567635, "421", Stations.Stammgelaende),
//            KlinikumGrosshadern("K", 48.110847, 11.4703001, "414", Stations.KlinikumGrosshadern),
//            KlinikumRechtsDerIsar("I", 48.137, 11.601119, null, Stations.KlinikumRechtsDerIsar),
//            Leopoldstrasse("L", 48.155916, 11.583095, "411", Stations.Leopoldstrasse),
//            GeschwisterSchollplatzAdalbertstrasse("S", 48.150244, 11.580665, null, Stations.GeschwisterSchollplatzAdalbertstrasse);
//        }
//
//        private enum class Stations(val station: Station) {
//            GarchingForschungszentrum(Station("1000460", "Garching-Forschungszentrum", Integer.MAX_VALUE)),
//            GarchingHochbrueck(Station("1000480", "Garching-Hochbrück", Integer.MAX_VALUE)),
//            Weihenstephan(Station("1002911", "Weihenstephan", Integer.MAX_VALUE)),
//            Stammgelaende(Station("1000120", "Theresienstraße", Integer.MAX_VALUE)),
//            KlinikumGrosshadern(Station("1001540", "Klinikum Großhadern", Integer.MAX_VALUE)),
//            KlinikumRechtsDerIsar(Station("1000580", "Max-Weber-Platz", Integer.MAX_VALUE)),
//            Leopoldstrasse(Station("1000080", "Giselastraße", Integer.MAX_VALUE)),
//            GeschwisterSchollplatzAdalbertstrasse(Station("1000070", "Universität", Integer.MAX_VALUE)),
//            Pinakotheken(Station("1000051", "Pinakotheken", Integer.MAX_VALUE)),
//            TUM(Station("1000095", "Technische Universität", Integer.MAX_VALUE)),
//            Waldhueterstrasse(Station("1001574", "Waldhüterstraße", Integer.MAX_VALUE)),
//            Martinsried(Station("1002557", "LMU Martinsried", Integer.MAX_VALUE)),
//            GarchingTUM(Station("1002070", "Garching-Technische Universität", Integer.MAX_VALUE))
//        }

        /**
         * Returns the "id" of the campus near the given location
         * The used radius around the middle of the campus is 1km.
         *
         * @param location The location to search for a campus
         * @return Campus id
         */
        private fun getCampusFromLocation(location: Location): Campus? {
            val lat = location.latitude
            val lng = location.longitude
            val results = FloatArray(1)
            var bestDistance = java.lang.Float.MAX_VALUE
            var bestCampus: Campus? = null
            for (l in ConfigUtils.getConfig(ConfigConst.CAMPUS, emptyList<Campus>())) {
                Location.distanceBetween(l.latitude, l.longitude, lat, lng, results)
                val distance = results[0]
                if (distance < bestDistance) {
                    bestDistance = distance
                    bestCampus = l
                }
            }

            return if (bestDistance < 10000000) {
                bestCampus
            } else {
                null
            }
        }

        /**
         * Converts UTM based coordinates to latitude and longitude based format
         */
        @JvmStatic
        fun convertUTMtoLL(north: Double, east: Double, zone: Double): Geo {
            val d = 0.99960000000000004
            val d1 = 6378137
            val d2 = 0.0066943799999999998
            val d4 = (1 - Math.sqrt(1 - d2)) / (1 + Math.sqrt(1 - d2))
            val d15 = east - 500000
            val d11 = (zone - 1) * 6 - 180 + 3
            val d3 = d2 / (1 - d2)
            val d10 = north / d
            val d12 = d10 / (d1 * (1 - d2 / 4 - (3 * d2 * d2) / 64 - (5 * Math.pow(d2, 3.0)) / 256))
            val d14 = d12 + ((3 * d4) / 2 - (27 * Math.pow(d4, 3.0)) / 32) * Math.sin(2 * d12) + ((21 * d4 * d4) / 16 - (55 * Math.pow(d4, 4.0)) / 32) * Math.sin(4 * d12) + ((151 * Math.pow(d4, 3.0)) / 96) * Math.sin(6 * d12)
            val d5 = d1 / Math.sqrt(1 - d2 * Math.sin(d14) * Math.sin(d14))
            val d6 = Math.tan(d14) * Math.tan(d14)
            val d7 = d3 * Math.cos(d14) * Math.cos(d14)
            val d8 = (d1 * (1 - d2)) / Math.pow(1 - d2 * Math.sin(d14) * Math.sin(d14), 1.5)
            val d9 = d15 / (d5 * d)
            var d17 = d14 - ((d5 * Math.tan(d14)) / d8) * ((d9 * d9) / 2 - ((5 + 3 * d6 + 10 * d7 - 4 * d7 * d7 - 9 * d3) * Math.pow(d9, 4.0)) / 24 + ((61 + 90 * d6 + 298 * d7 + 45 * d6 * d6 - 252 * d3 - 3 * d7 * d7) * Math.pow(d9, 6.0)) / 720)
            d17 *= 180 / Math.PI
            var d18 = (d9 - ((1 + 2 * d6 + d7) * Math.pow(d9, 3.0)) / 6 + ((5 - 2 * d7 + 28 * d6 - 3 * d7 * d7 + 8 * d3 + 24 * d6 * d6) * Math.pow(d9, 5.0)) / 120) / Math.cos(d14)
            d18 = d11 + d18 * 180 / Math.PI
            return Geo(d17, d18)
        }

        @JvmStatic
        fun convertRoomFinderCoordinateToGeo(coordinate: RoomFinderCoordinateInterface): Geo? {
            return Geo(coordinate.latitude, coordinate.longitude)
        }
    }
}
