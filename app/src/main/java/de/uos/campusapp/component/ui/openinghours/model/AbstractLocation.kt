package de.uos.campusapp.component.ui.openinghours.model

/**
 * Represents a location
 *
 * @param id Location ID, e.g. 100
 * @param category Location category, e.g. library, cafeteria, info
 * @param name Location name, e.g. Studentenwerksbibliothek
 *
 * @param address Address, e.g. Arcisstr. 21 (optional)
 * @param room Room, e.g. MI 00.01.123 (optional)
 * @param transport Transportation station name, e.g. U2 Königsplatz (optional)
 * @param hours Opening hours, e.g. Mo–Fr 8–24 (optional)
 * @param info Additional information, e.g. Tel: 089-11111 (optional)
 * @param url Location URL, e.g. http://stud.ub.uni-muenchen.de/ (optional)
 */
abstract class AbstractLocation {
    abstract var id: String
    abstract var category: String
    abstract var name: String

    // optional properties
    abstract var hours: String
    abstract var address: String
    abstract var room: String
    abstract var transport: String
    abstract var info: String
    abstract var url: String

    fun toLocationItem(): LocationItem {
        return LocationItem(id, category, name, hours, address, room, transport, info, url)
    }
}