package pl.polsl.MathHelper.ui.chooseTaskActivity

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
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.helper_data_containers.ImageIdTestsForImage
import pl.polsl.MathHelper.helper_data_containers.UserImageIdsPair
import pl.polsl.MathHelper.model.SvgImage
import pl.polsl.MathHelper.model.SvgImageDescription
import pl.polsl.MathHelper.model.Tests
import pl.polsl.MathHelper.ui.mainActivity.MainActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.showSvgActivity.ShowSvgActivity
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.ViewUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.menu_bar.*
import org.linphone.core.*
import org.linphone.core.tools.Log
import pl.polsl.MathHelper.App
import pl.polsl.MathHelper.adapters.CustomAdapter
import pl.polsl.MathHelper.ui.userListActivity.UserListActivity
import pl.polsl.MathHelper.utils.signalRHelper
import javax.inject.Inject

class ChooseTaskActivity : AppCompatActivity(), ChooseTaskView,ChooseTaskNavigator, signalRHelper.SignalRCallbacks {

    private lateinit var linearLayoutManager: LinearLayoutManager
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    private var taskList: ArrayList<String> = ArrayList()
    private var chosenTask: String? = null
    private var currentlyChosenTaskID: Int = 0
    private var stringAdapter: CustomAdapter? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var svgImageList: ArrayList<SvgImage>? = null
    var imageTestList: ArrayList<ImageIdTestsForImage>? = null
    var svgImageDescriptionList: ArrayList<SvgImageDescription>? = null
    var currentUserSvgImageList: ArrayList<SvgImage>? = null
    var currentUserImageIdsPair: UserImageIdsPair? = null
    var signalRHelperClass: signalRHelper? = null

    @BindView(R.id.rv_task)
    lateinit var taskRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: ChooseTaskPresenter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_task)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        ViewUtils.fullScreenCall(window)
        initializeOnClicks()
        if(currentUserSvgImageList==null) currentUserSvgImageList = ArrayList()
        if(AppPreferences.chosenTaskId != -1) currentlyChosenTaskID = AppPreferences.chosenTaskId
        inicializeList()
        initializeRecyclerView()
        signalRHelperClass = signalRHelper(this)
        val serverToken = Hawk.get<String>("Server_Token")
        signalRHelperClass?.signalr(serverToken)
        if(!Hawk.get<Boolean>("Is_In_Call"))login("5001")
    }

    private fun initializeRecyclerView(){
        linearLayoutManager = LinearLayoutManager(this)
        taskRecyclerView.layoutManager = linearLayoutManager
        stringAdapter = CustomAdapter(taskList)
        taskRecyclerView.adapter = stringAdapter
        taskRecyclerView.addItemDecoration(DividerItemDecoration(taskRecyclerView.context, linearLayoutManager.orientation))
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenTaskID)
        stringAdapter?.notifyDataSetChanged()
        chosenTask = stringAdapter?.getItem(currentlyChosenTaskID)
        textToSpeechSingleton?.speakSentence("Obecny moduł to wybór zadania. Zaznaczone zadanie to $chosenTask")
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
                            val myIntent = Intent(this@ChooseTaskActivity, MainActivity::class.java)
                            this@ChooseTaskActivity.startActivity(myIntent)
                            finish()
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
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_previous))
                        2 -> choosePreviousTask()
                        3 -> choose5thPreviousTask()
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
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_next))
                        2 -> chooseNextTask()
                        3 -> choose5thNextTask()
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
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_select))
                        2 -> {
                            //textToSpeechSingleton?.speakSentence("Wybrane zadanie to $chosenTask")
                            AppPreferences.chosenTask = Gson().toJson(getCurrentTask(chosenTask!!))
                            AppPreferences.chosenTaskDescription = Gson().toJson(getCurrentTaskDescription(getCurrentTask(chosenTask!!)?.svgId!!))
                            AppPreferences.chosenTaskTests = Gson().toJson(getCurrentTaskTests(getCurrentTask(chosenTask!!)?.svgId!!))
                            AppPreferences.chosenTaskId = currentlyChosenTaskID
                            val myIntent = Intent(this@ChooseTaskActivity, ShowSvgActivity::class.java)
                            this@ChooseTaskActivity.startActivity(myIntent)
                            finish()
                        }
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
                        2 -> {
                            val myIntent = Intent(this@ChooseTaskActivity, SettingsActivity::class.java)
                            this@ChooseTaskActivity.startActivity(myIntent)
                        }
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
                        2 -> textToSpeechSingleton?.speakSentence("Lista zadań")
                    }
                    clickCountTest = 0
                }
            }.start()
        }
    }


    fun chooseNextTask(){
        val taskListSize = taskList.size - 1
        currentlyChosenTaskID += 1
        if(currentlyChosenTaskID>taskListSize){
            currentlyChosenTaskID = 0
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenTaskID)
        stringAdapter?.notifyDataSetChanged()
        chosenTask = stringAdapter?.getItem(currentlyChosenTaskID)
        textToSpeechSingleton?.speakSentence("Wybrane zadanie to $chosenTask")
    }

    fun choose5thNextTask(){
        val taskListSize = taskList.size - 1
        currentlyChosenTaskID += 4
        if(currentlyChosenTaskID>taskListSize){
            currentlyChosenTaskID = 0 + kotlin.math.abs(4 - taskListSize)
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenTaskID)
        stringAdapter?.notifyDataSetChanged()
        chosenTask = stringAdapter?.getItem(currentlyChosenTaskID)
        textToSpeechSingleton?.speakSentence("Wybrane zadanie to $chosenTask")
    }

    fun choose5thPreviousTask(){
        val taskListSize = taskList.size - 1
        currentlyChosenTaskID -= 4
        if(currentlyChosenTaskID<0){
            currentlyChosenTaskID += taskListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenTaskID)
        stringAdapter?.notifyDataSetChanged()
        chosenTask = stringAdapter?.getItem(currentlyChosenTaskID)
        textToSpeechSingleton?.speakSentence("Wybrane zadanie to $chosenTask")
    }

    fun choosePreviousTask(){
        val taskListSize = taskList.size - 1
        currentlyChosenTaskID -= 1
        if(currentlyChosenTaskID<0){
            currentlyChosenTaskID = taskListSize
        }
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenTaskID)
        stringAdapter?.notifyDataSetChanged()
        chosenTask = stringAdapter?.getItem(currentlyChosenTaskID)
        textToSpeechSingleton?.speakSentence("Wybrane zadanie to $chosenTask")
    }

    private fun inicializeList() {
        val gson = Gson()
        val userImageIdsPairType = object : TypeToken<List<UserImageIdsPair>>() {}.type
        userImagesIdsPairList = gson.fromJson<ArrayList<UserImageIdsPair>>(AppPreferences.userIdImageIdList, userImageIdsPairType)
        val svgImageType = object : TypeToken<List<SvgImage>>() {}.type
        svgImageList = gson.fromJson<ArrayList<SvgImage>>(AppPreferences.imageList, svgImageType)
        val svgImageDescriptionType = object : TypeToken<List<SvgImageDescription>>() {}.type
        svgImageDescriptionList = gson.fromJson<ArrayList<SvgImageDescription>>(AppPreferences.descriptionList, svgImageDescriptionType)
        val imageIdTestsForImageType = object : TypeToken<List<ImageIdTestsForImage>>() {}.type
        imageTestList = gson.fromJson<ArrayList<ImageIdTestsForImage>>(AppPreferences.testList, imageIdTestsForImageType)
        for(item in userImagesIdsPairList!!){
            if(AppPreferences.chosenUser == item.userId)
                currentUserImageIdsPair = item
        }
        currentUserSvgImageList?.clear()
        for(item in currentUserImageIdsPair?.svgIdListFromServer!!){
            for(item1 in svgImageList!!){
                if(item == item1.svgId && AppPreferences.chosenSection == item1.svgDirectory){
                    currentUserSvgImageList?.add(item1)
                }
            }
        }
        prepareTaskList()
    }

    private fun prepareTaskList(){
        for(item in currentUserSvgImageList!!){
            taskList.add(item.svgTitle!!)
        }
    }

    private fun getCurrentTask(taskTitle: String): SvgImage?{
        for(item in currentUserSvgImageList!!){
            if(item.svgTitle == taskTitle){
                return item
            }
        }
        return null
    }

    private fun getCurrentTaskDescription(id: Int): SvgImageDescription?{
        for(item in svgImageDescriptionList!!){
            if(id == item.svgId){
                return item
            }
        }
        return null
    }

    private fun getCurrentTaskTests(id:Int): Tests? {
        for(item in imageTestList!!){
            if(id == item.imageId){
                return item.tests
            }
        }
        return null
    }

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
        //core.currentCall?.accept()
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
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_select))
                        2 -> {
                            //textToSpeechSingleton?.speakSentence("Wybrane zadanie to $chosenTask")
                            AppPreferences.chosenTask = Gson().toJson(getCurrentTask(chosenTask!!))
                            AppPreferences.chosenTaskDescription = Gson().toJson(getCurrentTaskDescription(getCurrentTask(chosenTask!!)?.svgId!!))
                            AppPreferences.chosenTaskTests = Gson().toJson(getCurrentTaskTests(getCurrentTask(chosenTask!!)?.svgId!!))
                            AppPreferences.chosenTaskId = currentlyChosenTaskID
                            val myIntent = Intent(this@ChooseTaskActivity, ShowSvgActivity::class.java)
                            this@ChooseTaskActivity.startActivity(myIntent)
                            finish()
                        }
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
                            val myIntent = Intent(this@ChooseTaskActivity, MainActivity::class.java)
                            this@ChooseTaskActivity.startActivity(myIntent)
                            finish()
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

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}
}