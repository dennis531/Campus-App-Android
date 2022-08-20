package de.uos.campusapp.service.di

import dagger.Subcomponent
import de.uos.campusapp.component.tumui.grades.GradeNotificationDeleteReceiver
import de.uos.campusapp.component.ui.onboarding.StartupActivity
import de.uos.campusapp.service.DownloadWorker

@Subcomponent(modules = [DownloadModule::class])
interface DownloadComponent {

    fun inject(downloadWorker: DownloadWorker)
    fun inject(startupActivity: StartupActivity)
    fun inject(gradeNotificationDeleteReceiver: GradeNotificationDeleteReceiver)
}
