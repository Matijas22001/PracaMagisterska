package pl.polsl.MathHelper.ui.showSvgActivity

import dagger.Module
import dagger.Provides

@Module
class ShowSvgActivityModule {
    @Provides
    fun provideShowSvgView(activity: ShowSvgActivity): ShowSvgActivityView {
        return activity
    }

    @Provides
    fun provideShowSvgNavigator(activity: ShowSvgActivity): ShowSvgActivityNavigator {
        return activity
    }

    @Provides
    fun provideShowSvgActivityPresenter(view: ShowSvgActivityView?, navigator: ShowSvgActivityNavigator?): ShowSvgActivityPresenter {
        return ShowSvgActivityPresenter(view, navigator)
    }
}