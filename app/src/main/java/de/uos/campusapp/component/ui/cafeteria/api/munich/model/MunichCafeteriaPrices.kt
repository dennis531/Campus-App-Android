package de.uos.campusapp.component.ui.cafeteria.api.munich.model

import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPrice
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceInterface
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaRole

/**
 * Hardcoded cafeteria prices
 */
object MunichCafeteriaPrices {
    private const val PRICE_100 = 1.00
    private const val PRICE_155 = 1.55
    private const val PRICE_190 = 1.90
    private const val PRICE_220 = 2.20
    private const val PRICE_240 = 2.40
    private const val PRICE_260 = 2.60
    private const val PRICE_270 = 2.70
    private const val PRICE_280 = 2.80
    private const val PRICE_290 = 2.90
    private const val PRICE_300 = 3.00
    private const val PRICE_320 = 3.20
    private const val PRICE_330 = 3.30
    private const val PRICE_340 = 3.40
    private const val PRICE_350 = 3.50
    private const val PRICE_360 = 3.60
    private const val PRICE_370 = 3.70
    private const val PRICE_390 = 3.90
    private const val PRICE_400 = 4.00
    private const val PRICE_410 = 4.10
    private const val PRICE_440 = 4.40
    private const val PRICE_450 = 4.50
    private const val PRICE_490 = 4.90
    private const val PRICE_540 = 5.40

    private val STUDENT_PRICES = mapOf(
        "Tagesgericht 1" to PRICE_100,
        "Tagesgericht 2" to PRICE_155,
        "Tagesgericht 3" to PRICE_190,
        "Tagesgericht 4" to PRICE_240,

        "Aktionsessen 1" to PRICE_155,
        "Aktionsessen 2" to PRICE_190,
        "Aktionsessen 3" to PRICE_240,
        "Aktionsessen 4" to PRICE_260,
        "Aktionsessen 5" to PRICE_280,
        "Aktionsessen 6" to PRICE_300,
        "Aktionsessen 7" to PRICE_320,
        "Aktionsessen 8" to PRICE_350,
        "Aktionsessen 9" to PRICE_400,
        "Aktionsessen 10" to PRICE_450,

        "Biogericht 1" to PRICE_155,
        "Biogericht 2" to PRICE_190,
        "Biogericht 3" to PRICE_240,
        "Biogericht 4" to PRICE_260,
        "Biogericht 5" to PRICE_280,
        "Biogericht 6" to PRICE_300,
        "Biogericht 7" to PRICE_320,
        "Biogericht 8" to PRICE_350,
        "Biogericht 9" to PRICE_400,
        "Biogericht 10" to PRICE_450
    )

    private val EMPLOYEE_PRICES = mapOf(
        "Tagesgericht 1" to PRICE_190,
        "Tagesgericht 2" to PRICE_220,
        "Tagesgericht 3" to PRICE_240,
        "Tagesgericht 4" to PRICE_280,

        "Aktionsessen 1" to PRICE_220,
        "Aktionsessen 2" to PRICE_240,
        "Aktionsessen 3" to PRICE_280,
        "Aktionsessen 4" to PRICE_300,
        "Aktionsessen 5" to PRICE_320,
        "Aktionsessen 6" to PRICE_340,
        "Aktionsessen 7" to PRICE_360,
        "Aktionsessen 8" to PRICE_390,
        "Aktionsessen 9" to PRICE_440,
        "Aktionsessen 10" to PRICE_490,

        "Biogericht 1" to PRICE_220,
        "Biogericht 2" to PRICE_240,
        "Biogericht 3" to PRICE_280,
        "Biogericht 4" to PRICE_300,
        "Biogericht 5" to PRICE_320,
        "Biogericht 6" to PRICE_340,
        "Biogericht 7" to PRICE_360,
        "Biogericht 8" to PRICE_390,
        "Biogericht 9" to PRICE_440,
        "Biogericht 10" to PRICE_490
    )

    private val GUEST_PRICES = mapOf(
        "Tagesgericht 1" to PRICE_240,
        "Tagesgericht 2" to PRICE_270,
        "Tagesgericht 3" to PRICE_290,
        "Tagesgericht 4" to PRICE_330,

        "Aktionsessen 1" to PRICE_270,
        "Aktionsessen 2" to PRICE_290,
        "Aktionsessen 3" to PRICE_330,
        "Aktionsessen 4" to PRICE_350,
        "Aktionsessen 5" to PRICE_370,
        "Aktionsessen 6" to PRICE_390,
        "Aktionsessen 7" to PRICE_410,
        "Aktionsessen 8" to PRICE_440,
        "Aktionsessen 9" to PRICE_490,
        "Aktionsessen 10" to PRICE_540,

        "Biogericht 1" to PRICE_270,
        "Biogericht 2" to PRICE_290,
        "Biogericht 3" to PRICE_330,
        "Biogericht 4" to PRICE_350,
        "Biogericht 5" to PRICE_370,
        "Biogericht 6" to PRICE_390,
        "Biogericht 7" to PRICE_410,
        "Biogericht 8" to PRICE_440,
        "Biogericht 9" to PRICE_490,
        "Biogericht 10" to PRICE_540
    )

    /**
     * Gets a list of cafeteria menu prices of the given menu type
     */
    @JvmStatic
    fun getMenuPrices(menuType: String): List<CafeteriaMenuPriceInterface> {
        val menuPrices = mutableListOf<CafeteriaMenuPriceInterface>()

        val studentPrice = STUDENT_PRICES[menuType]
        val employeePrice = STUDENT_PRICES[menuType]
        val guestPrice = STUDENT_PRICES[menuType]

        if (studentPrice != null) {
            menuPrices += CafeteriaMenuPrice(CafeteriaRole.STUDENT, studentPrice)
        }
        if (employeePrice != null) {
            menuPrices += CafeteriaMenuPrice(CafeteriaRole.EMPLOYEE, employeePrice)
        }
        if (guestPrice != null) {
            menuPrices += CafeteriaMenuPrice(CafeteriaRole.GUEST, guestPrice)
        }

        return menuPrices
    }
}
