package de.tum.`in`.tumcampusapp.component.ui.cafeteria.api.generic

import de.tum.`in`.tumcampusapp.api.generic.BaseAPI
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.Cafeteria
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.CafeteriaMenu

interface CafeteriaAPI: BaseAPI {
    fun getCafeterias(): List<Cafeteria>

    /**
     * Gets menus of all cafeterias
     */
    fun getMenus(): List<CafeteriaMenu>
}