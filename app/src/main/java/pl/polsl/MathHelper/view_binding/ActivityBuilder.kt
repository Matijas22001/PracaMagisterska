package pl.polsl.MathHelper.view_binding

import pl.polsl.MathHelper.ui.answerActivity.AnswerActivity
import pl.polsl.MathHelper.ui.answerActivity.AnswerActivityModule
import pl.polsl.MathHelper.ui.chooseImageSizeActivity.ChooseImageSizeActivity
import pl.polsl.MathHelper.ui.chooseImageSizeActivity.ChooseImageSizeModule
import pl.polsl.MathHelper.ui.chooseSubjectActivity.ChooseSubjectActivity
import pl.polsl.MathHelper.ui.chooseSubjectActivity.ChooseSubjectActivityModule
import pl.polsl.MathHelper.ui.chooseTaskActivity.ChooseTaskActivity
import pl.polsl.MathHelper.ui.chooseTaskActivity.ChooseTaskModule
import pl.polsl.MathHelper.ui.mainActivity.MainActivity
import pl.polsl.MathHelper.ui.mainActivity.MainActivityModule
import pl.polsl.MathHelper.ui.questionActivity.QuestionActivity
import pl.polsl.MathHelper.ui.questionActivity.QuestionActivityModule
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsModule
import pl.polsl.MathHelper.ui.showSvgActivity.ShowSvgActivity
import pl.polsl.MathHelper.ui.showSvgActivity.ShowSvgActivityModule
import pl.polsl.MathHelper.ui.testActivity.TestActivity
import pl.polsl.MathHelper.ui.testActivity.TestActivityModule
import pl.polsl.MathHelper.ui.userListActivity.UserListActivity
import pl.polsl.MathHelper.ui.userListActivity.UserListActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [ChooseImageSizeModule::class])
    abstract fun bindChooseImageSizeActivity(): ChooseImageSizeActivity?

    @ContributesAndroidInjector(modules = [ChooseTaskModule::class])
    abstract fun bindChooseTaskActivity(): ChooseTaskActivity?

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity?

    @ContributesAndroidInjector(modules = [UserListActivityModule::class])
    abstract fun bindUserListActivity(): UserListActivity?

    @ContributesAndroidInjector(modules = [ShowSvgActivityModule::class])
    abstract fun bindShowSvgActivity(): ShowSvgActivity?

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun bindSettingsActivity(): SettingsActivity?

    @ContributesAndroidInjector(modules = [ChooseSubjectActivityModule::class])
    abstract fun bindChooseSubjectActivit(): ChooseSubjectActivity?

    @ContributesAndroidInjector(modules = [AnswerActivityModule::class])
    abstract fun bindAnswerActivity(): AnswerActivity?

    @ContributesAndroidInjector(modules = [QuestionActivityModule::class])
    abstract fun bindQuestionActivity(): QuestionActivity?

    @ContributesAndroidInjector(modules = [TestActivityModule::class])
    abstract fun bindTestActivity(): TestActivity?
}