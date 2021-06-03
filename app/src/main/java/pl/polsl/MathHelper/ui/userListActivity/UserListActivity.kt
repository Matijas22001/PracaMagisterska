package pl.polsl.MathHelper.ui.userListActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.volley.RequestQueue
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.adapters.UserListAdapter
import pl.polsl.MathHelper.model.*
import pl.polsl.MathHelper.ui.mainActivity.MainActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.utils.*
import pl.polsl.MathHelper.utils.VoIPHelperMethods.Companion.outgoingCall
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import org.linphone.core.*
import org.linphone.core.tools.Log
import javax.inject.Inject
import pl.polsl.MathHelper.R


class UserListActivity : AppCompatActivity(), UserListActivityView, UserListActivityNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    var queue: RequestQueue? = null
    var studentList: ArrayList<Student>? = null
    var imageListToDownload: ArrayList<Int>? = null
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
    private lateinit var core: Core

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
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        if (studentList == null) studentList = ArrayList()
        //if (AppPreferences.chosenUser != -1) currentlyChosenUserID = AppPreferences.chosenUser - 1

        val serverToken = Hawk.get<String>("Server_Token")
        presenter.getUserListFromServer(queue!!, serverToken)
        initializeRecyclerView()
    }


    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                textToSpeechSingleton?.speakSentence("Nie udało się zalogować do serwera Voip")
            } else if (state == RegistrationState.Ok) {
                textToSpeechSingleton?.speakSentence("Zalogowano do serwera Voip")
                //AppPreferences.chosenUser = currentlyChosenUserID + 1
            }
        }
        override fun onAudioDeviceChanged(core: Core, audioDevice: AudioDevice) {
            // This callback will be triggered when a successful audio device has been changed
        }

        override fun onAudioDevicesListUpdated(core: Core) {
            // This callback will be triggered when the available devices list has changed,
            // for example after a bluetooth headset has been connected/disconnected.
        }
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            // This function will be called each time a call state changes,
            // which includes new incoming/outgoing calls

            when (state) {
                Call.State.OutgoingInit -> {
                    // First state an outgoing call will go through
                }
                Call.State.OutgoingProgress -> {
                    // Right after outgoing init
                }
                Call.State.OutgoingRinging -> {
                    // This state will be reached upon reception of the 180 RINGING
                }
                Call.State.Connected -> {
                    // When the 200 OK has been received
                }
                Call.State.StreamsRunning -> {
                    // This state indicates the call is active.
                    // You may reach this state multiple times, for example after a pause/resume
                    // or after the ICE negotiation completes
                    // Wait for the call to be connected before allowing a call update

                    // Only enable toggle camera button if there is more than 1 camera and the video is enabled
                    // We check if core.videoDevicesList.size > 2 because of the fake camera with static image created by our SDK (see below)
                    core.enableMic(true)
                    toggleSpeaker()
                }
                Call.State.Paused -> {
                    // When you put a call in pause, it will became Paused
                }
                Call.State.PausedByRemote -> {
                    // When the remote end of the call pauses it, it will be PausedByRemote
                }
                Call.State.Updating -> {
                    // When we request a call update, for example when toggling video
                }
                Call.State.UpdatedByRemote -> {
                    // When the remote requests a call update
                }
                Call.State.Released -> {
                    // Call state will be released shortly after the End state
                }
                Call.State.Error -> {

                }
                Call.State.IncomingReceived -> {

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
        studentList = studentListResponse?.list
        textToSpeechSingleton?.speakSentence("Pobieranie danych. Proszę czekać")
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        if(studentList?.size!! >0){
            chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        }
        val stringUserListSerialized = gson.toJson(studentList)
        AppPreferences.userList = stringUserListSerialized
        login("5000")
    }


    @OnClick(R.id.btn_back)
    fun goBack() {
        clickCountBack++
        object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountBack) {
                    1 -> textToSpeechSingleton?.speakSentence("Zadzwoń")
                    2 ->{
                        textToSpeechSingleton?.speakSentence("Rozpoczynam próbę połączenia")
                        outgoingCall("5001")
                    }
                }
                clickCountBack = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_previous)
    fun goPrevious() {
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

    @OnClick(R.id.btn_next)
    fun goNext() {
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

    @OnClick(R.id.btn_select)
    fun goSelect() {
        clickCountSelect++
        object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSelect) {
                    1 -> textToSpeechSingleton?.speakSentence("Opuść moduł dzwonienia")
                    2 -> {
                        if (chosenStudent != null) {
                            //textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
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

    @OnClick(R.id.btn_settings)
    fun goSettings() {
        clickCountSettings++
        object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSettings) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_settings))
                    2 -> {
                        val myIntent = Intent(this@UserListActivity, SettingsActivity::class.java)
                        this@UserListActivity.startActivity(myIntent)
                    }
                }
                clickCountSettings = 0
            }
        }.start()
    }

    @OnClick(R.id.btn_test)
    fun goTest() {
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
        //val username = "5001"
        //val password = "5001"
        val domain = "157.158.57.43"
        // To configure a SIP account, we need an Account object and an AuthInfo object
        // The first one is how to connect to the proxy server, the second one stores the credentials
        // The auth info can be created from the Factory as it's only a data class
        // userID is set to null as it's the same as the username in our case
        // ha1 is set to null as we are using the clear text password. Upon first register, the hash will be computed automatically.
        // The realm will be determined automatically from the first register, as well as the algorithm
        val authInfo = Factory.instance().createAuthInfo(userName, null, userName, null, null, domain, null)

        // Account object replaces deprecated ProxyConfig object
        // Account object is configured through an AccountParams object that we can obtain from the Core
        val accountParams = pl.polsl.MathHelper.App.core.createAccountParams()

        // A SIP account is identified by an identity address that we can construct from the username and domain
        val identity = Factory.instance().createAddress("sip:$userName@$domain")
        accountParams.identityAddress = identity

        // We also need to configure where the proxy server is located
        val address = Factory.instance().createAddress("sip:$domain")
        // We use the Address object to easily set the transport protocol
        address?.transport = TransportType.Udp
        accountParams.serverAddress = address
        // And we ensure the account will start the registration process
        accountParams.registerEnabled = true

        // Now that our AccountParams is configured, we can create the Account object
        val account = pl.polsl.MathHelper.App.core.createAccount(accountParams)

        // Now let's add our objects to the Core
        pl.polsl.MathHelper.App.core.addAuthInfo(authInfo)
        pl.polsl.MathHelper.App.core.addAccount(account)

        // Also set the newly added account as default
        pl.polsl.MathHelper.App.core.defaultAccount = account

        // Allow account to be removed

        // To be notified of the connection status of our account, we need to add the listener to the Core
        pl.polsl.MathHelper.App.core.addListener(coreListener)
        // We can also register a callback on the Account object
        account.addListener { _, state, message ->
            // There is a Log helper in org.linphone.core.tools package
            Log.i("[Account] Registration state changed: $state, $message")
        }

        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        pl.polsl.MathHelper.App.core.start()

        if (packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, packageName) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }

    }

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}

}