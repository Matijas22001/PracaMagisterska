package com.example.myapplication.ui.chooseImageSizeActivity

import dagger.Module
import dagger.Provides

@Module
class ChooseImageSizeModule {
    @Provides
    fun provideChooseImageSizeView(activity: ChooseImageSizeActivity): ChooseImageSizeView {
        return activity
    }

    @Provides
    fun provideChooseImageSizeNavigator(activity: ChooseImageSizeActivity): ChooseImageSizeNavigator {
        return activity
    }

    @Provides
    fun provideChooseImageSizePresenter(view: ChooseImageSizeView?, navigator: ChooseImageSizeNavigator?): ChooseImageSizePresenter {
        return ChooseImageSizePresenter(view, navigator)
    }
}