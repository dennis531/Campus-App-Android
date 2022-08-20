package de.uos.campusapp.utils

import android.content.Context
import de.uos.campusapp.api.general.TUMCabeClient
import de.uos.campusapp.database.TcaDb

val Context.tcaDb: TcaDb
    get() = TcaDb.getInstance(this)

val Context.tumCabeClient: TUMCabeClient
    get() = TUMCabeClient.getInstance(this)
