package pl.polsl.MathHelper.ui.chooseImageSizeActivity

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
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.ui.chooseTaskActivity.ChooseTaskActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.showSvgActivity.ShowSvgActivity
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.ViewUtils
import dagger.android.AndroidInjection
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.adapters.CustomAdapter
import javax.inject.Inject

class ChooseImageSizeActivity: AppCompatActivity(), ChooseImageSizeView,ChooseImageSizeNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    private var sizeList: ArrayList<String> = ArrayList()
    private var chosenSize: String? = null
    private var currentlyChosenSizeID: Int = 0
    private var stringAdapter: CustomAdapter? = null

    @BindView(R.id.rv_image_size)
    lateinit var sizeRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: ChooseImageSizePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_image_size)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        //textToSpeechSingleton = TextToSpeechSingleton(this)
        ViewUtils.fullScreenCall(window)
        if(AppPreferences.chosenImageThickness != -1) currentlyChosenSizeID = AppPreferences.chosenImageThickness
        mockInicializeLists()
        initializeRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.fullScreenCall(window)
    }


    private fun initializeRecyclerView(){
        linearLayoutManager = LinearLayoutManager(this)
        sizeRecyclerView.layoutManager = linearLayoutManager
        stringAdapter = CustomAdapter(sizeList)
        sizeRecyclerView.adapter = stringAdapter
        sizeRecyclerView.addItemDecoration(DividerItemDecoration(sizeRecyclerView.context, linearLayoutManager.orientation))
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSizeID)
        stringAdapter?.notifyDataSetChanged()
        chosenSize = stringAdapter?.getItem(currentlyChosenSizeID)
        textToSpeechSingleton?.speakSentence("Grubo??ci linii. Zaznaczony rozmiar $chosenSize")
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
                        val myIntent = Intent(this@ChooseImageSizeActivity, ChooseTaskActivity::class.java)
                        this@ChooseImageSizeActivity.startActivity(myIntent)
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
                    2 -> choosePreviousTask()
                    3 -> choose5thPreviousTask()
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
                    2 -> chooseNextTask()
                    3 -> choose5thNextTask()
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
                        //textToSpeechSingleton?.speakSentence("Wybrany rozmiar to $chosenSize")
                        AppPreferences.chosenImageSize = Integer.parseInt(chosenSize?.split(" ")?.get(1).toString())
                        AppPreferences.chosenImageThickness = currentlyChosenSizeID
                        val myIntent = Intent(this@ChooseImageSizeActivity, ShowSvgActivity::class.java)
                        this@ChooseImageSizeActivity.startActivity(myIntent)
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
                        val myIntent = Intent(this@ChooseImageSizeActivity, SettingsActivity::class.java)
                        this@ChooseImageSizeActivity.startActivity(myIntent)
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
                    2 -> textToSpeechSingleton?.speakSentence("Lista rozmiar??w")
                }
                clickCountTest = 0
            }
        }.start()
    }

    fun chooseNextTask(){
        val sizeListSize = sizeList.size - 1
        currentlyChosenSizeID += 1
        if(currentlyChosenSizeID>sizeListSize){
            currentlyChosenSizeID = 0
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSizeID)
        stringAdapter?.notifyDataSetChanged()
        chosenSize = stringAdapter?.getItem(currentlyChosenSizeID)
        textToSpeechSingleton?.speakSentence("Wybrany rozmiar $chosenSize")
    }

    fun choose5thNextTask(){
        val sizeListSize = sizeList.size - 1
        currentlyChosenSizeID += 4
        if(currentlyChosenSizeID>sizeListSize){
            currentlyChosenSizeID = 0 + kotlin.math.abs(4 - sizeListSize)
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSizeID)
        stringAdapter?.notifyDataSetChanged()
        chosenSize = stringAdapter?.getItem(currentlyChosenSizeID)
        textToSpeechSingleton?.speakSentence("Wybrany rozmiar $chosenSize")
    }

    fun choose5thPreviousTask(){
        val sizeListSize = sizeList.size - 1
        currentlyChosenSizeID -= 4
        if(currentlyChosenSizeID<0){
            currentlyChosenSizeID += sizeListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSizeID)
        stringAdapter?.notifyDataSetChanged()
        chosenSize = stringAdapter?.getItem(currentlyChosenSizeID)
        textToSpeechSingleton?.speakSentence("Wybrany rozmiar $chosenSize")
    }

    fun choosePreviousTask(){
        val sizeListSize = sizeList.size - 1
        currentlyChosenSizeID -= 1
        if(currentlyChosenSizeID<0){
            currentlyChosenSizeID = sizeListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSizeID)
        stringAdapter?.notifyDataSetChanged()
        chosenSize = stringAdapter?.getItem(currentlyChosenSizeID)
        textToSpeechSingleton?.speakSentence("Wybrany rozmiar $chosenSize")
    }

    private fun mockInicializeLists() {
        sizeList.add("Grubo???? 1")
        sizeList.add("Grubo???? 2")
        sizeList.add("Grubo???? 3")
        sizeList.add("Grubo???? 4")
        sizeList.add("Grubo???? 5")
        sizeList.add("Grubo???? 6")
        sizeList.add("Grubo???? 7")
    }


    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}
}