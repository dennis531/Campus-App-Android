package de.tum.`in`.tumcampusapp.component.ui.openinghours.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

/**
 * New Location
 *
 * @param id Location ID, e.g. 100
 * @param category Location category, e.g. library, cafeteria, info
 * @param name Location name, e.g. Studentenwerksbibliothek
 * @param address Address, e.g. Arcisstr. 21
 * @param room Room, e.g. MI 00.01.123
 * @param transport Transportation station name, e.g. U2 Königsplatz
 * @param hours Opening hours, e.g. Mo–Fr 8–24
 * @param info Additional information, e.g. Tel: 089-11111
 * @param url Location URL, e.g. http://stud.ub.uni-muenchen.de/
 */
@Entity
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
open class Location(
    @PrimaryKey
    open var id: String = "",
    open var category: String = "",
    open var name: String = "",

    // optional properties
    open var hours: String = "",
    open var address: String = "",
    open var room: String = "",
    open var transport: String = "",
    open var info: String = "",
    open var url: String = "",
)