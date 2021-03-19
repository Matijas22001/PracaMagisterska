package com.example.myapplication

import android.app.Application
import android.os.Build
import android.view.View
import com.android.volley.RequestQueue
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.MockedData
import com.example.myapplication.view_binding.DaggerAppComponent
import com.example.myapplication.view_binding.HawkWrapper
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {
        @JvmField
    @Inject
    var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>? = null
    override fun onCreate() {
        super.onCreate()

        MockedData.initializeMockData()
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