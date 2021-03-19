package com.example.myapplication.ui.showSvgActivity

import com.example.myapplication.ui.mainActivity.MainActivity
import com.example.myapplication.ui.mainActivity.MainActivityNavigator
import com.example.myapplication.ui.mainActivity.MainActivityPresenter
import com.example.myapplication.ui.mainActivity.MainActivityView
import dagger.Module
import dagger.Provides

@Module
class ShowSvgActivityModule {
    @Provides
    fun provideShowSvgView(activity: ShowSvgActivity): ShowSvgActivityView {
        return activity
    }

    @Provides
    fun provideShowSvgNavigator(activity: ShowSvgActivity): ShowSvgActivityNavigator {
        return activity
    }

    @Provides
    fun provideShowSvgActivityPresenter(view: ShowSvgActivityView?, navigator: ShowSvgActivityNavigator?): ShowSvgActivityPresenter {
        return ShowSvgActivityPresenter(view, navigator)
    }
}