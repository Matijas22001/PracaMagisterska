package com.example.myapplication.ui.testActivity

import com.example.myapplication.ui.showSvgActivity.ShowSvgActivity
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityNavigator
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityPresenter
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityView
import dagger.Module
import dagger.Provides

@Module
class TestActivityModule {
    @Provides
    fun provideTestActivityView(activity: TestActivity): TestActivityView {
        return activity
    }

    @Provides
    fun provideTestActivityNavigator(activity: TestActivity): TestActivityNavigator {
        return activity
    }

    @Provides
    fun provideTestActivityPresenter(view: TestActivityView?, navigator: TestActivityNavigator?): TestActivityPresenter {
        return TestActivityPresenter(view, navigator)
    }
}