package pl.polsl.MathHelper.ui.answerActivity

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