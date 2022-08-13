package de.tum.`in`.tumcampusapp.component.ui.messages.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.ui.messages.MessagesDownloadAction
import de.tum.`in`.tumcampusapp.component.ui.messages.fragment.CreateMessageFragment
import de.tum.`in`.tumcampusapp.component.ui.messages.fragment.MessagesDetailsFragment
import de.tum.`in`.tumcampusapp.component.ui.messages.fragment.MessagesFragment
import de.tum.`in`.tumcampusapp.service.DownloadWorker

@Subcomponent(modules = [MessagesModule::class])
interface MessagesComponent {
    fun inject(messagesFragment: MessagesFragment)
    fun inject(messagesDetailsFragment: MessagesDetailsFragment)
    fun inject(createMessagesFragment: CreateMessageFragment)
}

@Module
interface MessagesModule {

    @Binds
    fun bindMessagesDownloadAction(impl: MessagesDownloadAction): DownloadWorker.Action
}