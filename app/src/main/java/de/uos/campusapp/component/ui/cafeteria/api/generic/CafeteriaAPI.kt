package de.uos.campusapp.component.ui.cafeteria.api.generic

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteria
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteriaMenu

interface CafeteriaAPI: BaseAPI {
    fun getCafeterias(): List<AbstractCafeteria>

    /**
     * Gets menus of all cafeterias
     */
    fun getMenus(): List<AbstractCafeteriaMenu>
}