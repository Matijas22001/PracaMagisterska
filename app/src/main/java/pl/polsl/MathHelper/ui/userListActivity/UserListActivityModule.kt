package pl.polsl.MathHelper.ui.userListActivity

import dagger.Module
import dagger.Provides

@Module
class UserListActivityModule {
    @Provides
    fun provideUserListView(activity: UserListActivity): UserListActivityView {
        return activity
    }

    @Provides
    fun provideUserListNavigator(activity: UserListActivity): UserListActivityNavigator {
        return activity
    }

    @Provides
    fun provideUserListActivityPresenter(view: UserListActivityView?, navigator: UserListActivityNavigator?): UserListActivityPresenter {
        return UserListActivityPresenter(view, navigator)
    }
}