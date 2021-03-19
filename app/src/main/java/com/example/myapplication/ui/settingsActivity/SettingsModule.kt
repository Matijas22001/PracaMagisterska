package com.example.myapplication.ui.settingsActivity

import dagger.Module
import dagger.Provides

@Module
class SettingsModule {
    @Provides
    fun provideSettingsView(activity: SettingsActivity): SettingsView {
        return activity
    }

    @Provides
    fun provideCSettingsNavigator(activity: SettingsActivity): SettingsNavigator {
        return activity
    }

    @Provides
    fun provideSettingsPresenter(view: SettingsView?, navigator: SettingsNavigator?): SettingsPresenter {
        return SettingsPresenter(view, navigator)
    }
}