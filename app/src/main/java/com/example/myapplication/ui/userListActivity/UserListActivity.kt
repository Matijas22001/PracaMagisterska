package com.example.myapplication.ui.userListActivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.volley.RequestQueue
import com.example.myapplication.App.Companion.hubConnection
import com.example.myapplication.App.Companion.textToSpeechSingleton
import com.example.myapplication.R
import com.example.myapplication.adapters.UserListAdapter
import com.example.myapplication.helper_data_containers.ImageIdTestsForImage
import com.example.myapplication.helper_data_containers.UserImageIdsPair
import com.example.myapplication.model.*
import com.example.myapplication.ui.chooseSubjectActivity.ChooseSubjectActivity
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.utils.*
import com.google.gson.Gson
import com.microsoft.signalr.HubConnectionBuilder
import dagger.android.AndroidInjection
import org.linphone.core.*
import org.linphone.core.tools.Log
import javax.inject.Inject


class UserListActivity : AppCompatActivity(), UserListActivityView, UserListActivityNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    var queue: RequestQueue? = null
    var studentList: ArrayList<Student>? = null
    var imageListToDownload: ArrayList<Int>? = null
    var userListAdapter: UserListAdapter? = null
    var chosenStudent: Student? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var svgImageList: ArrayList<SvgImage>? = null
    var svgDescriptionList: ArrayList<SvgImageDescription>? = null
    var imageIdTestsForImageList: ArrayList<ImageIdTestsForImage>? = null
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
        clearAppData()
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        if (studentList == null) studentList = ArrayList()
        if (userImagesIdsPairList == null) userImagesIdsPairList = ArrayList()
        if (svgImageList == null) svgImageList = ArrayList()
        if (svgDescriptionList == null) svgDescriptionList = ArrayList()
        if (imageIdTestsForImageList == null) imageIdTestsForImageList = ArrayList()
        if (AppPreferences.chosenUser != -1) currentlyChosenUserID = AppPreferences.chosenUser - 1
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        core = factory.createCore(null, null, this)
        displayLoginPrompt()
        //presenter.getUserListFromServer(queue!!, studentList!!)
        //initializeRecyclerView()
    }

    private fun displayLoginPrompt(){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.login_prompt, null)
        val etUsername = dialogView.findViewById<EditText>(R.id.et_username)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_password)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("LOGOWANIE")
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
        dialog.setOnShowListener {
            val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                //presenter.loginUser(queue!!, etUsername.text.toString(), etPassword.text.toString(), dialog)
                userLoggedInSuccessfulLogic(LoginResponse(), dialog)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun insertOrSpeakNumberInit(dialogView: View, editText: EditText){
        val btnJeden = dialogView.findViewById<Button>(R.id.jeden)
        val btnDwa = dialogView.findViewById<Button>(R.id.dwa)
        val btnTrzy = dialogView.findViewById<Button>(R.id.trzy)
        val btnCztery = dialogView.findViewById<Button>(R.id.cztery)
        val btnPiec = dialogView.findViewById<Button>(R.id.piec)
        val btnSzesc = dialogView.findViewById<Button>(R.id.szesc)
        val btnSiedem = dialogView.findViewById<Button>(R.id.siedem)
        val btnOsiem = dialogView.findViewById<Button>(R.id.osiem)
        val btnDziewiec = dialogView.findViewById<Button>(R.id.dziewiec)
        val btnZero = dialogView.findViewById<Button>(R.id.zero)
    }

    private fun insertOrSpeakNumberLogic(number: Int){
        clickCountBack++
        object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountBack) {
                    1 -> textToSpeechSingleton?.speakSentence(returnNumberInString(number))
                    2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
                }
                clickCountBack = 0
            }
        }.start()
    }

    private fun returnNumberInString(number: Int): String?{
        when(number){
            0->return "Zero"
            1->return "Jeden"
            2->return "Dwa"
            3->return "Trzy"
            4->return "Cztery"
            5->return "Pięć"
            6->return "Sześć"
            7->return "Siedem"
            8->return "Osiem"
            9->return "Dziewięć"
        }
        return null
    }


    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                textToSpeechSingleton?.speakSentence("Nie udało się zalogować do serwera Voip")
            } else if (state == RegistrationState.Ok) {
                textToSpeechSingleton?.speakSentence("Zalogowano do serwera Voip")
                AppPreferences.chosenUser = currentlyChosenUserID + 1
                val myIntent = Intent(this@UserListActivity, ChooseSubjectActivity::class.java)
                this@UserListActivity.startActivity(myIntent)
                finish()
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

    private fun outgoingCall() {
        // As for everything we need to get the SIP URI of the remote and convert it to an Address
        val remoteSipUri = "157.158.57.43"
        val remoteAddress = Factory.instance().createAddress(remoteSipUri)
        remoteAddress ?: return // If address parsing fails, we can't continue with outgoing call process

        // We also need a CallParams object
        // Create call params expects a Call object for incoming calls, but for outgoing we must use null safely
        val params = core.createCallParams(null)
        params ?: return // Same for params

        // We can now configure it
        // Here we ask for no encryption but we could ask for ZRTP/SRTP/DTLS
        params.mediaEncryption = MediaEncryption.None
        // If we wanted to start the call with video directly
        //params.enableVideo(true)

        // Finally we start the call
        core.inviteAddressWithParams(remoteAddress, params)
        // Call process can be followed in onCallStateChanged callback from core listener
    }



    private fun login() {
        val username = "5000"
        val password = "5000"
        val domain = "157.158.57.43"
        // To configure a SIP account, we need an Account object and an AuthInfo object
        // The first one is how to connect to the proxy server, the second one stores the credentials
        // The auth info can be created from the Factory as it's only a data class
        // userID is set to null as it's the same as the username in our case
        // ha1 is set to null as we are using the clear text password. Upon first register, the hash will be computed automatically.
        // The realm will be determined automatically from the first register, as well as the algorithm
        val authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null)

        // Account object replaces deprecated ProxyConfig object
        // Account object is configured through an AccountParams object that we can obtain from the Core
        val accountParams = core.createAccountParams()

        // A SIP account is identified by an identity address that we can construct from the username and domain
        val identity = Factory.instance().createAddress("sip:$username@$domain")
        accountParams.identityAddress = identity

        // We also need to configure where the proxy server is located
        val address = Factory.instance().createAddress("sip:$domain")
        // We use the Address object to easily set the transport protocol
        address?.transport = TransportType.Udp
        accountParams.serverAddress = address
        // And we ensure the account will start the registration process
        accountParams.registerEnabled = true

        // Now that our AccountParams is configured, we can create the Account object
        val account = core.createAccount(accountParams)

        // Now let's add our objects to the Core
        core.addAuthInfo(authInfo)
        core.addAccount(account)

        // Also set the newly added account as default
        core.defaultAccount = account

        // Allow account to be removed

        // To be notified of the connection status of our account, we need to add the listener to the Core
        core.addListener(coreListener)
        // We can also register a callback on the Account object
        account.addListener { _, state, message ->
            // There is a Log helper in org.linphone.core.tools package
            Log.i("[Account] Registration state changed: $state, $message")
        }

        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        core.start()
    }


    private fun unregister() {
        // Here we will disable the registration of our Account
        val account = core.defaultAccount
        account ?: return

        val params = account.params
        // Returned params object is const, so to make changes we first need to clone it
        val clonedParams = params.clone()

        // Now let's make our changes
        clonedParams.registerEnabled = false

        // And apply them
        account.params = clonedParams
    }

    private fun delete() {
        // To completely remove an Account
        val account = core.defaultAccount
        account ?: return
        core.removeAccount(account)

        // To remove all accounts use
        core.clearAccounts()

        // Same for auth info
        core.clearAllAuthInfo()
    }

    private fun initializeRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)
        userRecyclerView.layoutManager = linearLayoutManager
        userListAdapter = UserListAdapter(studentList!!, this)
        userRecyclerView.adapter = userListAdapter
        userRecyclerView.addItemDecoration(DividerItemDecoration(userRecyclerView.context, linearLayoutManager.orientation))
    }

    override fun updateRecyclerView() {
        textToSpeechSingleton?.speakSentence("Pobieranie danych. Proszę czekać")
        userListAdapter?.setCurrentlyChosenUser(currentlyChosenUserID)
        userListAdapter?.notifyDataSetChanged()
        chosenStudent = userListAdapter?.getItem(currentlyChosenUserID)
        AppPreferences.chosenUser = currentlyChosenUserID + 1
        val stringUserListSerialized = gson.toJson(studentList)
        AppPreferences.userList = stringUserListSerialized
        //textToSpeechSingleton?.speakSentence("Pobieranie danych. Proszę czekać")
        getDataFromServer()
    }

    override fun userLoggedInSuccessfulLogic(loginResponse: LoginResponse, alertDialog: AlertDialog) {

        //alertDialog.dismiss()
        presenter.getUserListFromServer(queue!!, studentList!!)
        initializeRecyclerView()
    }

    override fun userLoggedInFailedLogic(){
        textToSpeechSingleton?.speakSentence("Błąd logowania, podaj dane ponownie")
    }

    private fun getDataFromServer() {
        for (item in studentList!!) {
            presenter.getUserImageIdsFromServer(queue!!, item.id!!)
        }
    }

    override fun addElementToList(userId: Int?, imageIdsList: ArrayList<Int>?) {
        userImagesIdsPairList?.add(UserImageIdsPair(userId, imageIdsList))
        if (userId == studentList?.last()?.id) {
            val stringUserImageIdsSerialized = gson.toJson(userImagesIdsPairList)
            AppPreferences.userIdImageIdList = stringUserImageIdsSerialized
            createImageIdsToDownload()
        }
    }

    private fun createImageIdsToDownload() {
        var intSet: MutableSet<Int>? = null
        for (item in userImagesIdsPairList!!) {
            if (item.userId == 1) {
                intSet = LinkedHashSet(item.svgIdListFromServer)
            } else {
                intSet?.addAll(item.svgIdListFromServer!!)
            }
        }
        imageListToDownload = ArrayList(intSet!!)
        svgImageList?.clear()
        for (item in imageListToDownload!!) {
            presenter.getUserImageFromServer(queue!!, item)
        }
    }

    override fun addImageToList(image: SvgImage?) {
        svgImageList?.add(image!!)
        if (image?.svgId == imageListToDownload?.last()) {
            val stringImageListSerialized = gson.toJson(svgImageList)
            AppPreferences.imageList = stringImageListSerialized
            getImageDescriptionFromServer()
        }
    }

    private fun getImageDescriptionFromServer() {
        svgDescriptionList?.clear()
        for (item in imageListToDownload!!) {
            presenter.getImageDescriptionFromServer(queue!!, item)
        }
    }

    override fun addImageDescriptionToList(image: SvgImageDescription?) {
        svgDescriptionList?.add(image!!)
        if (image?.svgId == imageListToDownload?.last()) {
            val stringDescriptionListSerialized = gson.toJson(svgDescriptionList)
            AppPreferences.descriptionList = stringDescriptionListSerialized
            //textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych")
            getTestsForImage()
        }
    }

    private fun getTestsForImage() {
        imageIdTestsForImageList?.clear()
        for (item in imageListToDownload!!) {
            presenter.getImageTestsFromServer(queue!!, item)
        }
    }

    override fun addTestToList(imageId: Int?, tests: Tests?) {
        imageIdTestsForImageList?.add(ImageIdTestsForImage(imageId, tests))
        if (imageId == imageIdTestsForImageList?.last()?.imageId) {
            val stringTestListSerialized = gson.toJson(imageIdTestsForImageList)
            AppPreferences.testList = stringTestListSerialized
            textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych. Zaznaczony użytkownik to " + chosenStudent?.name + chosenStudent?.surname)
        }
    }

    @OnClick(R.id.btn_back)
    fun goBack() {
        clickCountBack++
        object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountBack) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                    2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
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
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_select))
                    2 -> {
                        if (chosenStudent != null) {
                            //textToSpeechSingleton?.speakSentence("Wybrany użytkownik to " + chosenStudent?.name + " " + chosenStudent?.surname)
                            login()

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
                    2 -> textToSpeechSingleton?.speakSentence("Lista użytkowników")
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

    private fun clearAppData(){
        //AppPreferences.chosenUser = -1
        AppPreferences.userList = ""
        AppPreferences.userIdImageIdList = ""
        AppPreferences.imageList = ""
        AppPreferences.descriptionList = ""
        AppPreferences.testList = ""
        AppPreferences.answerList = ""
        AppPreferences.chosenSection = ""
        AppPreferences.chosenTask = ""
        AppPreferences.chosenTaskDescription = ""
        AppPreferences.chosenTaskTests = ""
        AppPreferences.chosenTest = ""
        AppPreferences.chosenQuestion = ""
        AppPreferences.chosenSectionId = -1
        AppPreferences.chosenTaskId = -1
        AppPreferences.chosenImageThickness = -1
        AppPreferences.chosenTestId = -1
        AppPreferences.chosenQuestionId = -1
        AppPreferences.chosenAnswerId = -1
    }

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}

}