package com.example.myapplication.ui.chooseSubjectActivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.myapplication.R
import com.example.myapplication.adapters.CustomAdapter
import com.example.myapplication.adapters.UserListAdapter
import com.example.myapplication.ui.chooseImageSizeActivity.ChooseImageSizeActivity
import com.example.myapplication.ui.chooseTaskActivity.ChooseTaskActivity
import com.example.myapplication.ui.mainActivity.MainActivity
import com.example.myapplication.ui.mainActivity.MainActivityPresenter
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.ui.userListActivity.UserListActivity
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.MockedData
import com.example.myapplication.utils.TextToSpeechSingleton
import com.example.myapplication.utils.ViewUtils
import dagger.android.AndroidInjection
import javax.inject.Inject

class ChooseSubjectActivity: AppCompatActivity(), ChooseSubjectActivityView, ChooseSubjectActivityNavigator {
    var textToSpeechSingleton: TextToSpeechSingleton? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var subjectList: ArrayList<String> = ArrayList()
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    private var stringAdapter: CustomAdapter? = null
    private var chosenSubject: String? = null
    private var currentlyChosenSubjectID: Int = 0

    @BindView(R.id.rv_subjects)
    lateinit var subjectsRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: ChooseSubjectActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subjects)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        textToSpeechSingleton = TextToSpeechSingleton(this)
        ViewUtils.fullScreenCall(window)
        mockInicializeLists()
        initializeRecyclerView()
    }

    private fun initializeRecyclerView(){
        linearLayoutManager = LinearLayoutManager(this)
        subjectsRecyclerView.layoutManager = linearLayoutManager
        stringAdapter = CustomAdapter(subjectList)
        subjectsRecyclerView.adapter = stringAdapter
        subjectsRecyclerView.addItemDecoration(DividerItemDecoration(subjectsRecyclerView.context, linearLayoutManager.orientation))
        chosenSubject = stringAdapter?.getItem(currentlyChosenSubjectID)
    }

    @OnClick(R.id.btn_back)
    fun goBack(){
        //ToDO
        clickCountBack++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountBack) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                    2 -> {
                        val myIntent = Intent(this@ChooseSubjectActivity, UserListActivity::class.java)
                        this@ChooseSubjectActivity.startActivity(myIntent)
                        finish()
                    }
                }
                clickCountBack = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_previous)
    fun goPrevious(){
        clickCountPrevious++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountPrevious) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_previous))
                    2 -> choosePreviousSubject()
                }
                clickCountPrevious = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_next)
    fun goNext(){
        clickCountNext++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountNext) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_next))
                    2 -> chooseNextSubject()
                }
                clickCountNext = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_select)
    fun goSelect(){
        clickCountSelect++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSelect) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_select))
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wybrany przedmiot to $chosenSubject")
                        val myIntent = Intent(this@ChooseSubjectActivity, MainActivity::class.java)
                        this@ChooseSubjectActivity.startActivity(myIntent)
                        finish()
                    }
                }
                clickCountSelect = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_settings)
    fun goSettings(){
        clickCountSettings++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSettings) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_settings))
                    2 -> {
                        val myIntent = Intent(this@ChooseSubjectActivity, SettingsActivity::class.java)
                        this@ChooseSubjectActivity.startActivity(myIntent)
                    }
                }
                clickCountSettings = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_test)
    fun goTest(){
        clickCountTest++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountTest) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_T))
                    2 -> textToSpeechSingleton?.speakSentence("Wybór przedmiotu")
                }
                clickCountTest = 0
            }
        }.start()
    }


    fun chooseNextSubject(){
        val subjectListSize = subjectList.size - 1
        currentlyChosenSubjectID += 1
        if(currentlyChosenSubjectID>subjectListSize){
            currentlyChosenSubjectID = 0
        }
        chosenSubject = stringAdapter?.getItem(currentlyChosenSubjectID)
        textToSpeechSingleton?.speakSentence("Wybrany przedmiot to $currentlyChosenSubjectID")
    }

    fun choosePreviousSubject(){
        val subjectListSize = subjectList.size - 1
        currentlyChosenSubjectID -= 1
        if(currentlyChosenSubjectID<0){
            currentlyChosenSubjectID = subjectListSize
        }
        chosenSubject = stringAdapter?.getItem(currentlyChosenSubjectID)
        textToSpeechSingleton?.speakSentence("Wybrany przedmiot to $currentlyChosenSubjectID")
    }

    private fun mockInicializeLists() {
        subjectList.add("Matematyka")
        subjectList.add("Biologia")
        subjectList.add("Fizyka")
    }

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}
}