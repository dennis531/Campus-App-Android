package de.uos.campusapp.component.ui.cafeteria.api.generic

import de.uos.campusapp.api.generic.BaseAPI
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteria
import de.uos.campusapp.component.ui.cafeteria.model.AbstractCafeteriaMenu

/**
 * Api interface for the cafeteria component
 */
interface CafeteriaAPI: BaseAPI {

    /**
     * Gets all available cafeterias
     *
     * @return List of cafeterias
     */
    fun getCafeterias(): List<AbstractCafeteria>

    /**
     * Gets menus of all cafeterias
     *
     * @return List of cafeteria menus
     */
    fun getCafeteriaMenus(): List<AbstractCafeteriaMenu>
}