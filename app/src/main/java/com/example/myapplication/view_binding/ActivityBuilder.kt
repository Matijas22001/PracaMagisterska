package com.example.myapplication.view_binding

import com.example.myapplication.ui.answerActivity.AnswerActivity
import com.example.myapplication.ui.answerActivity.AnswerActivityModule
import com.example.myapplication.ui.chooseImageSizeActivity.ChooseImageSizeActivity
import com.example.myapplication.ui.chooseImageSizeActivity.ChooseImageSizeModule
import com.example.myapplication.ui.chooseSubjectActivity.ChooseSubjectActivity
import com.example.myapplication.ui.chooseSubjectActivity.ChooseSubjectActivityModule
import com.example.myapplication.ui.chooseTaskActivity.ChooseTaskActivity
import com.example.myapplication.ui.chooseTaskActivity.ChooseTaskModule
import com.example.myapplication.ui.mainActivity.MainActivity
import com.example.myapplication.ui.mainActivity.MainActivityModule
import com.example.myapplication.ui.questionActivity.QuestionActivity
import com.example.myapplication.ui.questionActivity.QuestionActivityModule
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.ui.settingsActivity.SettingsModule
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivity
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityModule
import com.example.myapplication.ui.testActivity.TestActivity
import com.example.myapplication.ui.testActivity.TestActivityModule
import com.example.myapplication.ui.userListActivity.UserListActivity
import com.example.myapplication.ui.userListActivity.UserListActivityModule
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