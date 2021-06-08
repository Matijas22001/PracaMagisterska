package pl.polsl.MathHelper.ui.settingsActivity

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.menu_bar.*
import org.linphone.core.*
import org.linphone.core.tools.Log
import pl.polsl.MathHelper.App
import pl.polsl.MathHelper.App.Companion.core
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.adapters.CustomAdapter
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.ViewUtils
import pl.polsl.MathHelper.utils.signalRHelper
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.system.exitProcess

class SettingsActivity: AppCompatActivity(), SettingsView, SettingsNavigator, signalRHelper.SignalRCallbacks {

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
    var signalRHelperClass: signalRHelper? = null


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
        initializeOnClicks()
        mockInicializeLists()
        initializeRecyclerView()
        df.roundingMode = RoundingMode.CEILING
        signalRHelperClass = signalRHelper(this)
        val serverToken = Hawk.get<String>("Server_Token")
        signalRHelperClass?.signalr(serverToken)
        if(!Hawk.get<Boolean>("Is_In_Call"))login("5001")
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

    fun initializeOnClicks(){
        btn_back.setOnClickListener {
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
        btn_previous.setOnClickListener {
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
                                5->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to zakończenie rozmowy. Brak operacji do wykonania.")
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
        btn_next.setOnClickListener {
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
                                5->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to zakończenie rozmowy. Brak operacji do wykonania.")
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
        btn_select.setOnClickListener {
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
        btn_settings.setOnClickListener {
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
        btn_test.setOnClickListener {
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
            5->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to wyłączenie rozmowy. Brak operacji do wykonania.")
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
            5->textToSpeechSingleton?.speakSentence("Wybrane ustawienie to wyłączenie rozmowy. Brak operacji do wykonania.")
        }
    }


    fun reactToWorkingModeTest(){
        when(workingMode) {
            0 -> textToSpeechSingleton?.speakSentence("Opcje")
            1 -> textToSpeechSingleton?.speakSentence("Ustawienia - Prędkość mowy")
            2 -> textToSpeechSingleton?.speakSentence("Ustawienia - Czas pomiędzy przyciśnięciami")
            3 -> textToSpeechSingleton?.speakSentence("Ustawienia - Zmiana grubości linii")
            4 -> textToSpeechSingleton?.speakSentence("Ustawienia - Wyjście z aplikacji")
            5 -> textToSpeechSingleton?.speakSentence("Ustawienia - Zakończenie połączenia")
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
               finish()
               exitProcess(0)
           }
           5 -> {
               textToSpeechSingleton?.speakSentence("Kończenie rozmowy")
               core.currentCall?.terminate()
               workingMode = 0
               Hawk.put("Is_In_Call",false)
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
        settingsList.add("Zakończ obecną rozmowę")
    }

    override fun showMessage(resId: Int) {}

    override fun showMessage(message: String?) {}

    fun login(userName:String) {
        val domain = "157.158.57.43"
        val authInfo = Factory.instance().createAuthInfo(userName, null, userName, null, null, domain, null)
        val accountParams = App.core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$userName@$domain")
        accountParams.identityAddress = identity
        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = TransportType.Udp
        accountParams.serverAddress = address
        accountParams.registerEnabled = true
        val account = App.core.createAccount(accountParams)
        App.core.addAuthInfo(authInfo)
        App.core.addAccount(account)
        App.core.defaultAccount = account
        App.core.addListener(coreListener)
        account.addListener { _, state, message ->
            Log.i("[Account] Registration state changed: $state, $message")
        }
        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        App.core.start()
    }

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                android.util.Log.i("Tag","Serwer do rozmów nie jest dostępny")
            } else if (state == RegistrationState.Ok) {
                android.util.Log.i("Tag","Serwer do rozmów jest dostępny")

            }
        }
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            when (state) {
                Call.State.IncomingReceived -> {
                    reactToCall()
                }
            }
        }
    }

    private fun reactToCall(){
        btn_test.isEnabled = false
        btn_next.isEnabled = false
        btn_previous.isEnabled = false
        btn_settings.isEnabled = false
        btn_select.setOnClickListener {
            clickCountSelect++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountSelect) {
                        1 -> textToSpeechSingleton?.speakSentence("Odbierz")
                        2 -> {
                            App.core.currentCall?.accept()
                            Hawk.put("Is_In_Call",true)
                            resetViewState()
                        }
                    }
                    clickCountSelect = 0
                }
            }.start()
        }
        btn_back.setOnClickListener {
            clickCountBack++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountBack) {
                        1 -> textToSpeechSingleton?.speakSentence("Odrzuć")
                        2 -> {
                            App.core.currentCall?.decline(Reason.Declined)
                            Hawk.put("Is_In_Call",false)
                            resetViewState()
                        }
                    }
                    clickCountBack = 0
                }
            }.start()
        }
    }

    private fun resetViewState(){
        btn_test.isEnabled = true
        btn_next.isEnabled = true
        btn_previous.isEnabled = true
        btn_settings.isEnabled = true
        btn_select.setOnClickListener {
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
        btn_back.setOnClickListener {
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
    }

    override fun onSessionStart() {
        Log.i("","SessionStarted")
    }

    override fun onSessionEnd() {
        Log.i("","SessionEnded")
    }

    override fun onStatusChange(userName: String, status: String) {
        Log.i("","StatusChange $userName + $status")
    }

    override fun onClick(click: String) {
        Log.i("","Click $click")
    }

}