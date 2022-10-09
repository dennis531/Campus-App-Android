package de.uos.campusapp.utils

import android.content.Context
import de.uos.campusapp.database.CaDb

val Context.tcaDb: CaDb
    get() = CaDb.getInstance(this)
