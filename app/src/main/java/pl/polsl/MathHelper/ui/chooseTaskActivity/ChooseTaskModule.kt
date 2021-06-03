package pl.polsl.MathHelper.ui.chooseTaskActivity


import dagger.Module
import dagger.Provides

@Module
class ChooseTaskModule {
    @Provides
    fun provideChooseTaskView(activity: ChooseTaskActivity):ChooseTaskView {
        return activity
    }

    @Provides
    fun provideChooseTaskNavigator(activity: ChooseTaskActivity):ChooseTaskNavigator {
        return activity
    }

    @Provides
    fun provideChooseTaskPresenter(view: ChooseTaskView?, navigator: ChooseTaskNavigator?): ChooseTaskPresenter {
        return ChooseTaskPresenter(view, navigator)
    }
}