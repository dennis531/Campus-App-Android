package de.uos.campusapp.component.ui.messages.di

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import de.uos.campusapp.component.ui.messages.MessagesDownloadAction
import de.uos.campusapp.component.ui.messages.fragment.CreateMessageFragment
import de.uos.campusapp.component.ui.messages.fragment.MessagesDetailsFragment
import de.uos.campusapp.component.ui.messages.fragment.MessagesFragment
import de.uos.campusapp.service.DownloadWorker

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