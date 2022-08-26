package de.uos.campusapp.component.ui.transportation.model

/**
 * Simple implementation of [AbstractStation]
 */
class Station(
    override val id: String,
    override val name: String,
    override var quality: Int = 0
) : AbstractStation()
