package de.uos.campusapp.component.ui.feedback.di

import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import de.uos.campusapp.component.ui.feedback.FeedbackActivity
import de.uos.campusapp.component.ui.feedback.FeedbackContract
import de.uos.campusapp.component.ui.feedback.FeedbackPresenter

@Subcomponent(modules = [FeedbackModule::class])
interface FeedbackComponent {

    fun inject(feedbackActivity: FeedbackActivity)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun lrzId(@LrzId lrzId: String): Builder

        fun build(): FeedbackComponent
    }
}

@Module
interface FeedbackModule {

    @Binds
    fun bindsFeedbackPresenter(impl: FeedbackPresenter): FeedbackContract.Presenter
}
