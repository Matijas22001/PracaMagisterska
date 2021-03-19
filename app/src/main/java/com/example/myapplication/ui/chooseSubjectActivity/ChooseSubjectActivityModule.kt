package com.example.myapplication.ui.chooseSubjectActivity

import com.example.myapplication.ui.showSvgActivity.ShowSvgActivity
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityNavigator
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityPresenter
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityView
import dagger.Module
import dagger.Provides

@Module
class ChooseSubjectActivityModule {
    @Provides
    fun provideChooseSubjectActivityView(activity: ChooseSubjectActivity): ChooseSubjectActivityView {
        return activity
    }

    @Provides
    fun provideChooseSubjectActivityNavigator(activity: ChooseSubjectActivity): ChooseSubjectActivityNavigator {
        return activity
    }

    @Provides
    fun provideChooseSubjectActivityPresenter(view: ChooseSubjectActivityView?, navigator: ChooseSubjectActivityNavigator?): ChooseSubjectActivityPresenter {
        return ChooseSubjectActivityPresenter(view, navigator)
    }
}