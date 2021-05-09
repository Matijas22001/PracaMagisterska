package com.example.myapplication

import android.app.Application
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.TextToSpeechSingleton
import com.example.myapplication.view_binding.DaggerAppComponent
import com.example.myapplication.view_binding.HawkWrapper
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class App : Application(), HasAndroidInjector {
    companion object{
        var textToSpeechSingleton: TextToSpeechSingleton? = null
        var hubConnection: HubConnection? = null
    }

    @JvmField
    @Inject
    var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>? = null
    override fun onCreate() {
        super.onCreate()
        textToSpeechSingleton = TextToSpeechSingleton(this)
        AppPreferences.init(this)
        HawkWrapper.init(applicationContext)
        DaggerAppComponent.builder()
                .application(this)
                ?.build()
                ?.inject(this)

    }

    override fun androidInjector(): AndroidInjector<Any>? {
        return dispatchingAndroidInjector
    }
}