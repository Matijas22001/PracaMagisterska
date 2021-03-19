package com.example.myapplication.ui.mainActivity

import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {
    @Provides
    fun provideMainView(activity: MainActivity): MainActivityView {
        return activity
    }

    @Provides
    fun provideMainNavigator(activity: MainActivity): MainActivityNavigator {
        return activity
    }

    @Provides
    fun provideMainActivityPresenter(view: MainActivityView?, navigator: MainActivityNavigator?): MainActivityPresenter {
        return MainActivityPresenter(view, navigator)
    }
}