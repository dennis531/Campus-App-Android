package de.uos.campusapp.utils

import android.content.Context
import de.uos.campusapp.api.tumcabe.TUMCabeClient
import de.uos.campusapp.database.TcaDb

val Context.tcaDb: TcaDb
    get() = TcaDb.getInstance(this)
