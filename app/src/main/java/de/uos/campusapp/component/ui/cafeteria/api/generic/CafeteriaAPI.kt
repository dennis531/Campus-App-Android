package de.uos.campusapp.component.ui.cafeteria.api.generic

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.cafeteria.model.Cafeteria
import de.uos.campusapp.component.ui.cafeteria.model.CafeteriaMenu

interface CafeteriaAPI: BaseAPI {
    fun getCafeterias(): List<Cafeteria>

    /**
     * Gets menus of all cafeterias
     */
    fun getMenus(): List<CafeteriaMenu>
}