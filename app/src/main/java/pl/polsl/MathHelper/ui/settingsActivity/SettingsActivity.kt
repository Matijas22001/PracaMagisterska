package pl.polsl.MathHelper.ui.settingsActivity

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.ViewUtils
import dagger.android.AndroidInjection
import pl.polsl.MathHelper.adapters.CustomAdapter
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

class SettingsActivity: AppCompatActivity(), SettingsView, SettingsNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    private var settingsList: ArrayList<String> = ArrayList()
    private var chosenSetting: String? = null
    private var currentlyChosenSetting: Int = 0
    private var stringAdapter: CustomAdapter? = null
    private var workingMode = 0 //0 - choose setting, 1 - voice speed, 2 - click interval, 3 - line thickness,  4 - quitApp
    val df = DecimalFormat("#.##")


    @Inject
    lateinit var presenter: SettingsPresenter

    @BindView(R.id.rv_setting)
    lateinit var settingRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        ViewUtils.fullScreenCall(window)
        mockInicializeLists()
        initializeRecyclerView()
        df.roundingMode = RoundingMode.CEILING
    }

    private fun initializeRecyclerView(){
        textToSpeechSingleton?.speakSentence("Obecny moduł opcje. Zaznaczona opcja to zmiana prędkości mowy.")
        linearLayoutManager = LinearLayoutManager(this)
        settingRecyclerView.layoutManager = linearLayoutManager
        stringAdapter =
            CustomAdapter(settingsList)
        settingRecyclerView.adapter = stringAdapter
        settingRecyclerView.addItemDecoration(DividerItemDecoration(settingRecyclerView.context, linearLayoutManager.orientation))
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSetting)
        stringAdapter?.notifyDataSetChanged()
        chosenSetting = stringAdapter?.getItem(currentlyChosenSetting)
    }

    @OnClick(R.id.btn_back)
    fun goBack(){
        clickCountBack++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountBack) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                    2 -> {
                        if(workingMode==0){
                            textToSpeechSingleton?.speakSentence("Powrót do wcześniejszego ekranu")
                            finish()
                        }else{
                            reactToWorkingModeBack()
                        }
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
                    1 -> {
                        when(workingMode){
                            0->textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_previous))
                            1->textToSpeechSingleton?.speakSentence("Zmniejsz prędkość mowy")
                            2->textToSpeechSingleton?.speakSentence("Zmniejsz interwał między kliknięciami")
                            3->textToSpeechSingleton?.speakSentence("Zmniejsz grubość linii")
                            4->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to wyjście z aplikacji. Brak operacji do wykonania.")
                        }
                    }
                    2 -> {
                        if(workingMode==0){
                            choosePreviousSetting()
                        }else{
                            reactToWorkingModePrevoius()
                        }
                    }
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
                    1 ->  {
                        when(workingMode){
                            0->textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_next))
                            1->textToSpeechSingleton?.speakSentence("Zwiększ prędkość mowy")
                            2->textToSpeechSingleton?.speakSentence("Zwiększ interwał między kliknięciami")
                            3->textToSpeechSingleton?.speakSentence("Zwiększ grubość linii")
                            4->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to wyjście z aplikacji. Brak operacji do wykonania.")
                        }
                    }
                    2-> {
                        if(workingMode==0){
                            chooseNextSetting()
                        }else{
                            reactToWorkingModeNext()
                        }
                    }
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
                    1 -> {
                        if(workingMode==0){
                            textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_select))
                        }else{
                            textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_confirm))
                        }
                    }
                    2 -> reactToWorkingModeSelect()
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
                    2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia. Obecny moduł to ustawienia")
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
                    2 -> reactToWorkingModeTest()
                }
                clickCountTest = 0
            }
        }.start()
    }

    fun chooseNextSetting(){
        val settingsListSize = settingsList.size - 1
        currentlyChosenSetting += 1
        if(currentlyChosenSetting>settingsListSize){
            currentlyChosenSetting = 0
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSetting)
        stringAdapter?.notifyDataSetChanged()
        chosenSetting = stringAdapter?.getItem(currentlyChosenSetting)
        textToSpeechSingleton?.speakSentence("Wybrane ustawienie to $chosenSetting")
    }

    fun choosePreviousSetting(){
        val settingsListSize = settingsList.size - 1
        currentlyChosenSetting -= 1
        if(currentlyChosenSetting<0){
            currentlyChosenSetting = settingsListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSetting)
        stringAdapter?.notifyDataSetChanged()
        chosenSetting = stringAdapter?.getItem(currentlyChosenSetting)
        textToSpeechSingleton?.speakSentence("Wybrane ustawienie to $chosenSetting")
    }

    fun reactToWorkingModePrevoius(){
        when(workingMode){
            1-> {
                decrementSpeechSpeed()
                textToSpeechSingleton?.speakSentence("Zmniejszono prędkość mowy")
            }
            2->{
                decrementTapInterval()
                textToSpeechSingleton?.speakSentence("Obecny interwał pomiędzy kliknięciami to " + AppPreferences.tapInterval/1000 + " sekund")
            }
            3->{
                decrementLineThickness()
                textToSpeechSingleton?.speakSentence("Obecny grubość linii " + AppPreferences.chosenImageSize)
            }
            4->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to wyjście z aplikacji. Brak operacji do wykonania.")
        }
    }

    fun reactToWorkingModeNext(){
        when(workingMode){
            1-> {
                incrementSpeechSpeed()
                textToSpeechSingleton?.speakSentence("Zwiększono prędkość mowy")
            }
            2->{
                incrementTapInterval()
                textToSpeechSingleton?.speakSentence("Obecny interwał pomiędzy kliknięciami to " + AppPreferences.tapInterval/1000 + " sekund")
            }
            3->{
                incrementLineThickness()
                textToSpeechSingleton?.speakSentence("Obecny grubość linii " + AppPreferences.chosenImageSize)
            }
            4->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to wyjście z aplikacji. Brak operacji do wykonania.")
        }
    }


    fun reactToWorkingModeTest(){
        when(workingMode) {
            0 -> textToSpeechSingleton?.speakSentence("Opcje")
            1 -> textToSpeechSingleton?.speakSentence("Ustawienia - Prędkość mowy")
            2 -> textToSpeechSingleton?.speakSentence("Ustawienia - Czas pomiędzy przyciśnięciami")
            3 -> textToSpeechSingleton?.speakSentence("Ustawienia - Wyjście z aplikacji")
        }
    }

    fun reactToWorkingModeBack(){
        workingMode=0
        textToSpeechSingleton?.speakSentence("Nowe ustawienie zostało zapisane")
    }

    fun reactToWorkingModeSelect(){
       when(workingMode){
           0 -> setNewWorkingMode()
           1 -> {
               textToSpeechSingleton?.speakSentence("Nowe ustawienie zostało zapisane")
               textToSpeechSingleton?.setSpeechSpeed(AppPreferences.speechSpeed)
               workingMode = 0
           }
           2 -> {
               textToSpeechSingleton?.speakSentence("Nowe ustawienie zostało zapisane")
               workingMode = 0
           }
           3 -> {
               textToSpeechSingleton?.speakSentence("Nowe ustawienie zostało zapisane")
               workingMode = 0
           }
           4 -> {
               textToSpeechSingleton?.speakSentence("Zamykanie aplikacji")
               //quitApp()
           }
       }
    }

    private fun setNewWorkingMode(){
        textToSpeechSingleton?.speakSentence("Przechodzenie do zmiany ustawienia")
        workingMode = currentlyChosenSetting+1
    }

    private fun incrementSpeechSpeed(){
        if(AppPreferences.speechSpeed>=1 && AppPreferences.speechSpeed<2){
            AppPreferences.speechSpeed += 0.1f
        }
    }

    private fun decrementSpeechSpeed(){
        if(AppPreferences.speechSpeed>1 && AppPreferences.speechSpeed<=2){
            AppPreferences.speechSpeed -= 0.1f
        }
    }

    private fun incrementTapInterval(){
        if(AppPreferences.tapInterval in 800..1599){
            AppPreferences.tapInterval += 100
        }
    }

    private fun decrementTapInterval(){
        if(AppPreferences.tapInterval in 801..1600){
            AppPreferences.tapInterval -= 100
        }
    }

    private fun incrementLineThickness(){
        if(AppPreferences.chosenImageSize in 10..39){
            AppPreferences.chosenImageSize += 5
        }
    }

    private fun decrementLineThickness(){
        if(AppPreferences.chosenImageSize in 11..40){
            AppPreferences.chosenImageSize -= 5
        }
    }

    private fun mockInicializeLists() {
        settingsList.add("Prędkość mowy")
        settingsList.add("Czas pomiędzy przyciśnięciami")
        settingsList.add("Grubość linii")
        settingsList.add("Wyjście z aplikacji")
    }

    override fun showMessage(resId: Int) {}

    override fun showMessage(message: String?) {}


}