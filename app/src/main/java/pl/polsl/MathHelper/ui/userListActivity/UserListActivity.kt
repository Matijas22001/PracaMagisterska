package pl.polsl.MathHelper.ui.userListActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.volley.RequestQueue
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.menu_bar.*
import org.linphone.core.*
import org.linphone.core.tools.Log
import pl.polsl.MathHelper.App
import pl.polsl.MathHelper.App.Companion.core
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.adapters.UserListAdapter
import pl.polsl.MathHelper.model.*
import pl.polsl.MathHelper.ui.mainActivity.MainActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.utils.*
import pl.polsl.MathHelper.utils.VoIPHelperMethods.Companion.outgoingCall
import java.lang.Exception
import javax.inject.Inject


class UserListActivity : AppCompatActivity(), UserListActivityView, UserListActivityNavigator, signalRHelper.SignalRCallbacks {

    private lateinit var linearLayoutManager: LinearLayoutManager
    var queue: RequestQueue? = null
    var studentList: ArrayList<Student>? = null
    var userListAdapter: UserListAdapter? = null
    var chosenStudent: Student? = null
    private var currentlyChosenUserID: Int = 0
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    val gson = Gson()
    var signalRHelperClass: signalRHelper? = null

    @BindView(R.id.rv_user_list)
    lateinit var userRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: UserListActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        ViewUtils.fullScreenCall(window)
        initializeOnClicks()
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        if (studentList == null) studentList = ArrayList()
        if (AppPreferences.chosenStudent != -1) currentlyChosenUserID = AppPreferences.chosenStudent - 1
        signalRHelperClass = signalRHelper(this)
        val serverToken = Hawk.get<String>("Server_Token")
        if(AppStatus.getInstance(this).isOnline){
            try {
                signalRHelperClass?.signalr(serverToken)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        presenter.getUserListFromServer(queue!!, serverToken)
        initializeRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.fullScreenCall(window)
    }

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                //textToSpeechSingleton?.speakSentence("Nie udało się zalogować do serwera Voip")
            } else if (state == RegistrationState.Ok) {
                //textToSpeechSingleton?.speakSentence("Zalogowano do serwera Voip")
            }
        }

        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            findViewById<TextView>(R.id.tvStatus).text = state?.name.toString()
            when (state) {
                Call.State.StreamsRunning -> {
                    toggleSpeaker()
                    Hawk.put("Is_In_Call",true)
                }
            }
        }
    }

    private fun toggleSpeaker() {
        for (audioDevice in core.audioDevices) {
            if (audioDevice.type == AudioDevice.Type.Speaker) {
                core.currentCall?.outputAudioDevice = audioDevice
                return
            }
        }
    }

    private fun initializeRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)
        userRecyclerView.layoutManager = linearLayoutManager
        userListAdapter = UserListAdapter(studentList!!, this)
        userRecyclerView.adapter = userListAdapter
        userRecyclerView.addItemDecoration(DividerItemDecoration(userRecyclerView.context, linearLayoutManager.orientation))
    }

    override fun updateRecyclerView(studentListResponse: StudentListResponse?) {
        if(studentListResponse?.list!=null) studentList?.addAll(studentListResponse?.list!!)
        textToSpeechSingleton?.speakSentence("Lista aktywnych użytkowników pobrana")
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        if(studentList?.size!! > 0){
            chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        }
        val stringUserListSerialized = gson.toJson(studentList)
        AppPreferences.userList = stringUserListSerialized
        if(Hawk.contains("Teacher_phone_number")){
            val phoneNumber = Hawk.get<String>("Teacher_phone_number")
            login(phoneNumber)
        }
    }

    fun initializeOnClicks(){
        btn_back.setOnClickListener {
            clickCountBack++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    if(!Hawk.get<Boolean>("Is_In_Call")){
                        when (clickCountBack) {
                            1 -> textToSpeechSingleton?.speakSentence("Połącz z użytkownikiem")
                            2 ->{
                                textToSpeechSingleton?.speakSentence("Rozpoczynam próbę połączenia")
                                val number = userListAdapter?.getItem(currentlyChosenUserID)?.voipNumber
                                if(number!=null) outgoingCall(number)
                            }
                        }
                        clickCountBack = 0
                    }else{
                        when (clickCountBack) {
                            1 -> textToSpeechSingleton?.speakSentence("Rozłącz z użytkownikiem")
                            2 ->{
                                textToSpeechSingleton?.speakSentence("Kończę obecną rozmowę")
                                signalRHelperClass?.EndSession()
                                hangUp()
                                Hawk.put("Is_In_Call",false)
                            }
                        }
                        clickCountBack = 0
                    }
                }
            }.start()
        }
        btn_previous.setOnClickListener {
            clickCountPrevious++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountPrevious) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_previous))
                        2 -> choosePreviousUser()
                        3 -> choose5thPreviousUser()
                    }
                    clickCountPrevious = 0
                }
            }.start()
        }
        btn_next.setOnClickListener {
            clickCountNext++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountNext) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_next))
                        2 -> chooseNextUser()
                        3 -> choose5thNextUser()
                    }
                    clickCountNext = 0
                }
            }.start()
        }
        btn_select.setOnClickListener {
            clickCountSelect++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountSelect) {
                        1 -> textToSpeechSingleton?.speakSentence("Połącz z użytkownikiem")
                        2 -> {
                            val id = userListAdapter?.getItem(currentlyChosenUserID)?.id
                            signalRHelperClass?.StartSession(id!!)
                            if (chosenStudent != null) {
                                Hawk.put("Currently_chosen_user_id", id)
                                textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
                                val myIntent = Intent(this@UserListActivity, MainActivity::class.java)
                                this@UserListActivity.startActivity(myIntent)
                                finish()
                            } else {
                                textToSpeechSingleton?.speakSentence("Nie wybrano żadnego użytkownika")
                            }

                        }
                    }
                    clickCountSelect = 0
                }
            }.start()
        }
        btn_settings.setOnClickListener {
            clickCountSettings++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountSettings) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_settings))
                        2 -> {
                            Hawk.put("Is_from_user_list", true)
                            val myIntent = Intent(this@UserListActivity, SettingsActivity::class.java)
                            this@UserListActivity.startActivity(myIntent)
                        }
                    }
                    clickCountSettings = 0
                }
            }.start()
        }
        btn_test.setOnClickListener {
            clickCountTest++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountTest) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_T))
                        2 -> textToSpeechSingleton?.speakSentence("Lista zalogowanych użytkowników")
                    }
                    clickCountTest = 0
                }
            }.start()
        }
    }

    private fun hangUp() {
        if (core.callsNb == 0) return
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return
        call.terminate()
    }


    fun chooseNextUser() {
        val studentListSize = studentList?.size!! - 1
        currentlyChosenUserID += 1
        if (currentlyChosenUserID > studentListSize) {
            currentlyChosenUserID = 0
        }
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
    }

    fun choose5thNextUser() {
        val studentListSize = studentList?.size!! - 1
        currentlyChosenUserID += 4
        if (currentlyChosenUserID > studentListSize) {
            currentlyChosenUserID = 0 + kotlin.math.abs(4 - studentListSize)
        }
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
    }

    fun choose5thPreviousUser() {
        val studentListSize = studentList?.size!! - 1
        currentlyChosenUserID -= 4
        if (currentlyChosenUserID < 0) {
            currentlyChosenUserID += studentListSize
        }
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
    }

    fun choosePreviousUser() {
        val studentListSize = studentList?.size!! - 1
        currentlyChosenUserID -= 1
        if (currentlyChosenUserID < 0) {
            currentlyChosenUserID = studentListSize
        }
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
    }

    fun login(userName:String) {
        val domain = "157.158.57.43"
        val authInfo = Factory.instance().createAuthInfo(userName, null, userName, null, null, domain, null)
        val accountParams = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$userName@$domain")
        accountParams.identityAddress = identity
        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = TransportType.Udp
        accountParams.serverAddress = address
        accountParams.registerEnabled = true
        val account = core.createAccount(accountParams)
        core.addAuthInfo(authInfo)
        core.addAccount(account)
        core.defaultAccount = account
        core.addListener(coreListener)
        account.addListener { _, state, message ->
            Log.i("[Account] Registration state changed: $state, $message")
        }
        core.start()
        if (packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, packageName) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }
    }

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}

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

    override fun onImageChange(imageId: Int) {
        Log.i("","ImageChange $imageId")
    }

}