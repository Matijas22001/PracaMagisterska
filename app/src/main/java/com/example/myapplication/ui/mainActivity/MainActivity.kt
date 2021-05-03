package com.example.myapplication.ui.mainActivity

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
import com.example.myapplication.App
import com.example.myapplication.App.Companion.textToSpeechSingleton
import com.example.myapplication.R
import com.example.myapplication.adapters.CustomAdapter
import com.example.myapplication.helper_data_containers.UserImageIdsPair
import com.example.myapplication.model.SvgImage
import com.example.myapplication.ui.chooseSubjectActivity.ChooseSubjectActivity
import com.example.myapplication.ui.chooseTaskActivity.ChooseTaskActivity
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.ui.userListActivity.UserListActivity
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.TextToSpeechSingleton
import com.example.myapplication.utils.ViewUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityView, MainActivityNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    private var sectionList: ArrayList<String> = ArrayList()
    private var chosenSection: String? = null
    private var currentlyChosenSectionID: Int = 0
    private var stringAdapter: CustomAdapter? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var svgImageList: ArrayList<SvgImage>? = null
    var currentUserSvgImageList: ArrayList<SvgImage>? = null
    var currentUserImageIdsPair: UserImageIdsPair? = null

    @BindView(R.id.rv_section_list)
    lateinit var sectionRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        //textToSpeechSingleton = TextToSpeechSingleton(this)
        ViewUtils.fullScreenCall(window)
        if(currentUserSvgImageList==null) currentUserSvgImageList = ArrayList()
        if(AppPreferences.chosenSectionId != -1) currentlyChosenSectionID = AppPreferences.chosenSectionId
        inicializeList()
        initializeRecyclerView()
    }

    private fun initializeRecyclerView(){
        linearLayoutManager = LinearLayoutManager(this)
        sectionRecyclerView.layoutManager = linearLayoutManager
        stringAdapter = CustomAdapter(sectionList)
        sectionRecyclerView.adapter = stringAdapter
        sectionRecyclerView.addItemDecoration(DividerItemDecoration(sectionRecyclerView.context, linearLayoutManager.orientation))
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSectionID)
        stringAdapter?.notifyDataSetChanged()
        chosenSection = stringAdapter?.getItem(currentlyChosenSectionID)
        textToSpeechSingleton?.speakSentence("Obecny moduł to wybór działu. Zaznaczony dział to $chosenSection")
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
                        val myIntent = Intent(this@MainActivity, ChooseSubjectActivity::class.java)
                        this@MainActivity.startActivity(myIntent)
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
                    2 -> choosePreviousSection()
                    3 -> choose5thPreviousSection()
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
                    2 -> chooseNextSection()
                    3 -> choose5thNextSection()
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
                        //textToSpeechSingleton?.speakSentence("Wybrany dział to $chosenSection")
                        AppPreferences.chosenSection = chosenSection!!
                        AppPreferences.chosenSectionId = currentlyChosenSectionID
                        val myIntent = Intent(this@MainActivity, ChooseTaskActivity::class.java)
                        this@MainActivity.startActivity(myIntent)
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
                        val myIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                        this@MainActivity.startActivity(myIntent)
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
                    2 -> textToSpeechSingleton?.speakSentence("Lista działów")
                }
                clickCountTest = 0
            }
        }.start()
    }

    fun chooseNextSection(){
        val sectionListSize = sectionList.size - 1
        currentlyChosenSectionID += 1
        if(currentlyChosenSectionID>sectionListSize){
            currentlyChosenSectionID = 0
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSectionID)
        stringAdapter?.notifyDataSetChanged()
        chosenSection = stringAdapter?.getItem(currentlyChosenSectionID)
        textToSpeechSingleton?.speakSentence("Wybrany dział to $chosenSection")
    }

    fun choose5thNextSection(){
        val sectionListSize = sectionList.size - 1
        currentlyChosenSectionID += 4
        if(currentlyChosenSectionID>sectionListSize){
            currentlyChosenSectionID = 0 + kotlin.math.abs(4 - sectionListSize)
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSectionID)
        stringAdapter?.notifyDataSetChanged()
        chosenSection = stringAdapter?.getItem(currentlyChosenSectionID)
        textToSpeechSingleton?.speakSentence("Wybrany dział to $chosenSection")
    }

    fun choose5thPreviousSection(){
        val sectionListSize = sectionList.size - 1
        currentlyChosenSectionID -= 4
        if(currentlyChosenSectionID<0){
            currentlyChosenSectionID += sectionListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSectionID)
        stringAdapter?.notifyDataSetChanged()
        chosenSection = stringAdapter?.getItem(currentlyChosenSectionID)
        textToSpeechSingleton?.speakSentence("Wybrany dział to $chosenSection")
    }

    fun choosePreviousSection(){
        val sectionListSize = sectionList.size - 1
        currentlyChosenSectionID -= 1
        if(currentlyChosenSectionID<0){
            currentlyChosenSectionID = sectionListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSectionID)
        stringAdapter?.notifyDataSetChanged()
        chosenSection = stringAdapter?.getItem(currentlyChosenSectionID)
        textToSpeechSingleton?.speakSentence("Wybrany dział to $chosenSection")
    }

    private fun inicializeList() {
        val gson = Gson()
        val userImageIdsPairType = object : TypeToken<List<UserImageIdsPair>>() {}.type
        userImagesIdsPairList = gson.fromJson<ArrayList<UserImageIdsPair>>(AppPreferences.userIdImageIdList, userImageIdsPairType)
        val svgImageType = object : TypeToken<List<SvgImage>>() {}.type
        svgImageList = gson.fromJson<ArrayList<SvgImage>>(AppPreferences.imageList, svgImageType)
        for(item in userImagesIdsPairList!!){
            if(AppPreferences.chosenUser == item.userId)
                currentUserImageIdsPair = item
        }
        currentUserSvgImageList?.clear()
        for(item in currentUserImageIdsPair?.svgIdListFromServer!!){
            for(item1 in svgImageList!!){
                if(item == item1.svgId){
                    currentUserSvgImageList?.add(item1)
                }
            }
        }
        prepareSectionList()
    }

    private fun prepareSectionList(){
        for(item in currentUserSvgImageList!!){
            if(sectionList.size == 0){
                sectionList.add(item.svgDirectory!!)
            }else{
                if(!checkIfSectionIsOnList(item.svgDirectory!!,sectionList)){
                    sectionList.add(item.svgDirectory!!)
                }
            }
        }
    }

    private fun checkIfSectionIsOnList(section: String, sectionList: ArrayList<String>): Boolean{
        for(item in sectionList){
            if(section == item)
                return true
        }
        return false
    }

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}
}