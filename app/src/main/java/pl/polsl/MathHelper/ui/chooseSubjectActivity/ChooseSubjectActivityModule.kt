package pl.polsl.MathHelper.ui.chooseSubjectActivity

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