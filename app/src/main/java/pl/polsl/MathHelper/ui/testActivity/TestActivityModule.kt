package pl.polsl.MathHelper.ui.testActivity

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