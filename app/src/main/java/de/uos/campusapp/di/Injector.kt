package de.uos.campusapp.di

import android.app.Activity
import android.app.Service
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.work.Worker
import de.uos.campusapp.App

val Context.app: App
    get() = applicationContext as App

val Activity.injector: AppComponent
    get() = app.appComponent

val Fragment.injector: AppComponent
    get() = requireContext().app.appComponent

val Service.injector: AppComponent
    get() = app.appComponent

val Worker.injector: AppComponent
    get() = (applicationContext as App).appComponent
