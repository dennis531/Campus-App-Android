package de.uos.campusapp.api.uosbackend.model.cafeteria

import com.google.gson.annotations.SerializedName
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenuPriceInterface
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaRole

class UOSBackendMenuPrice(
    @SerializedName("role")
    var roleId: String = ROLE_STUDENT,
    @SerializedName("amount")
    override var amount: Double = -1.0
): CafeteriaMenuPriceInterface {

    override var role: CafeteriaRole = CafeteriaRole.STUDENT
        get() {
            return when (roleId) {
                ROLE_STUDENT -> CafeteriaRole.STUDENT
                ROLE_EMPLOYEE -> CafeteriaRole.EMPLOYEE
                ROLE_GUEST -> CafeteriaRole.GUEST
                else -> CafeteriaRole.STUDENT
            }
        }

    companion object {
        const val ROLE_STUDENT = "S"
        const val ROLE_EMPLOYEE = "E"
        const val ROLE_GUEST = "G"
    }
}
