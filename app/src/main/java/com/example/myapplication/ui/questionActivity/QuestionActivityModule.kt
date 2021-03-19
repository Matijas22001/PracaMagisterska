package com.example.myapplication.ui.questionActivity

import com.example.myapplication.ui.testActivity.TestActivity
import com.example.myapplication.ui.testActivity.TestActivityNavigator
import com.example.myapplication.ui.testActivity.TestActivityPresenter
import com.example.myapplication.ui.testActivity.TestActivityView
import dagger.Module
import dagger.Provides

@Module
class QuestionActivityModule {
    @Provides
    fun provideQuestionActivityView(activity: QuestionActivity): QuestionActivityView {
        return activity
    }

    @Provides
    fun provideQuestionActivityNavigator(activity: QuestionActivity): QuestionActivityNavigator {
        return activity
    }

    @Provides
    fun provideQuestionActivityPresenter(view: QuestionActivityView?, navigator: QuestionActivityNavigator?): QuestionActivityPresenter {
        return QuestionActivityPresenter(view, navigator)
    }
}