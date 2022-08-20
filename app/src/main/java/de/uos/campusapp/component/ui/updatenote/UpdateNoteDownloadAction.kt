package de.uos.campusapp.component.ui.updatenote

import android.content.Context
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.service.DownloadWorker
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.Utils
import java.io.IOException
import javax.inject.Inject

class UpdateNoteDownloadAction @Inject constructor(
    val mContext: Context
) : DownloadWorker.Action {
    override fun execute(cacheBehaviour: CacheControl) {
        val savedNote = Utils.getSetting(mContext, Const.UPDATE_MESSAGE, "")
        if (savedNote.isNotEmpty()) {
            // note has already been downloaded
            return
        }

        try {
            // val note = TUMCabeClient.getInstance(mContext).getUpdateNote(BuildConfig.VERSION_CODE)
            // Utils.setSetting(mContext, Const.UPDATE_MESSAGE, note?.updateNote ?: "")
        } catch (e: IOException) {
            Utils.log(e)
        }
    }
}