package com.example.myapplication.ui.answerActivity

import com.example.myapplication.ui.questionActivity.QuestionActivity
import com.example.myapplication.ui.questionActivity.QuestionActivityNavigator
import com.example.myapplication.ui.questionActivity.QuestionActivityPresenter
import com.example.myapplication.ui.questionActivity.QuestionActivityView
import dagger.Module
import dagger.Provides

@Module
class AnswerActivityModule {
    @Provides
    fun provideAnswerActivityView(activity: AnswerActivity): AnswerActivityView {
        return activity
    }

    @Provides
    fun provideAnswerActivityNavigator(activity: AnswerActivity): AnswerActivityNavigator {
        return activity
    }

    @Provides
    fun provideAnswerActivityPresenter(view: AnswerActivityView?, navigator: AnswerActivityNavigator?): AnswerActivityPresenter {
        return AnswerActivityPresenter(view, navigator)
    }
}