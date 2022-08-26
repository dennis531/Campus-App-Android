package de.uos.campusapp.component.ui.openinghours.model

/**
 * Simple implementation of [AbstractLocation]
 */
class Location(
    override var id: String,
    override var category: String,
    override var name: String,
    override var hours: String = "",
    override var address: String = "",
    override var room: String = "",
    override var transport: String = "",
    override var info: String = "",
    override var url: String = ""
) : AbstractLocation()
