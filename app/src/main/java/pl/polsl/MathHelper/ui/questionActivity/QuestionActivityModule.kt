package pl.polsl.MathHelper.ui.questionActivity

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