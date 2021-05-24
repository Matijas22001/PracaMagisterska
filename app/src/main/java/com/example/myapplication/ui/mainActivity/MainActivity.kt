package com.example.myapplication.ui.mainActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.myapplication.adapters.CustomAdapter
import com.example.myapplication.helper_data_containers.ImageIdTestsForImage
import com.example.myapplication.helper_data_containers.UserImageIdsPair
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.SvgImage
import com.example.myapplication.model.SvgImageDescription
import com.example.myapplication.model.Tests
import com.example.myapplication.ui.chooseTaskActivity.ChooseTaskActivity
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.utils.VolleySingleton
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import io.reactivex.Single
import org.linphone.core.*
import org.linphone.core.tools.Log
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainActivityView, MainActivityNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    var queue: RequestQueue? = null
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
    var imageListToDownload: ArrayList<Int>? = null
    var svgDescriptionList: ArrayList<SvgImageDescription>? = null
    var imageIdTestsForImageList: ArrayList<ImageIdTestsForImage>? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var svgImageList: ArrayList<SvgImage>? = null
    private lateinit var core: Core
    private var serverToken: String? = null
    var hasBeenLoggedIn: Boolean = false

    @BindView(R.id.rv_section_list)
    lateinit var sectionRecyclerView: RecyclerView

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        ViewUtils.fullScreenCall(window)
        Hawk.put("Is_Logged_In", true)
        if(Hawk.contains("Is_Logged_In")){
            hasBeenLoggedIn = Hawk.get("Is_Logged_In")
        }
        if(hasBeenLoggedIn){
            if(AppPreferences.chosenSectionId != -1) currentlyChosenSectionID = AppPreferences.chosenSectionId
            initializeRecyclerView()
            updateRecyclerView()
        }else{
            clearAppData()
            if(AppPreferences.chosenSectionId != -1) currentlyChosenSectionID = AppPreferences.chosenSectionId
            queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
            if (userImagesIdsPairList == null) userImagesIdsPairList = ArrayList()
            if (svgImageList == null) svgImageList = ArrayList()
            if (svgDescriptionList == null) svgDescriptionList = ArrayList()
            if (imageIdTestsForImageList == null) imageIdTestsForImageList = ArrayList()
            val factory = Factory.instance()
            factory.setDebugMode(true, "Hello Linphone")
            core = factory.createCore(null, null, this)
            displayLoginPrompt()
            initializeRecyclerView()
            signalr()
        }
    }


    private fun initializeRecyclerView(){
        linearLayoutManager = LinearLayoutManager(this)
        sectionRecyclerView.layoutManager = linearLayoutManager
        stringAdapter = CustomAdapter(sectionList)
        sectionRecyclerView.adapter = stringAdapter
        sectionRecyclerView.addItemDecoration(DividerItemDecoration(sectionRecyclerView.context, linearLayoutManager.orientation))

    }

    override fun updateRecyclerView() {
        inicializeList()
        stringAdapter?.setcurrentlyChosenValue(currentlyChosenSectionID)
        stringAdapter?.notifyDataSetChanged()
        chosenSection = stringAdapter?.getItem(currentlyChosenSectionID)
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
                presenter.loginUser(queue!!, "405052", "1234", dialog)
                dialog.dismiss()
            }
        }
        dialog.show()
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
                        textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
                        //val myIntent = Intent(this@MainActivity, ChooseSubjectActivity::class.java)
                        //this@MainActivity.startActivity(myIntent)
                        //finish()
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
        outgoingCall()
        //clickCountTest++
        //object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
        //    override fun onTick(millisUntilFinished: Long) {}
        //    override fun onFinish() {
        //        when (clickCountTest) {
        //            1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_T))
        //            2 -> textToSpeechSingleton?.speakSentence("Lista działów")
        //        }
        //        clickCountTest = 0
        //    }
        //}.start()
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
        for(item in svgImageList!!){
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


    override fun userLoggedInSuccessfulLogic(loginResponse: LoginResponse, dialog: AlertDialog) {
        serverToken =  loginResponse.token
        Hawk.put("Server_Token",serverToken)
        AppPreferences.chosenUser = loginResponse.user?.studentId!!
        presenter.getUserImageIdsFromServer(queue!!, loginResponse.user?.studentId!!, loginResponse.token!!)
        //presenter.getUserImageIdsFromServer(queue!!, 1, loginResponse.token!!)
    }

    override fun userLoggedInFailedLogic() {
        displayLoginPrompt()
    }

    override fun addElementToList(userId: Int?, imageIdsList: ArrayList<Int>?) {
        userImagesIdsPairList?.add(UserImageIdsPair(userId, imageIdsList))
        val stringUserImageIdsSerialized = Gson().toJson(userImagesIdsPairList)
        AppPreferences.userIdImageIdList = stringUserImageIdsSerialized
        createImageIdsToDownload()
    }

    private fun createImageIdsToDownload() {
        var intSet: MutableSet<Int>? = null
        intSet = LinkedHashSet(userImagesIdsPairList?.get(0)?.svgIdListFromServer)
        imageListToDownload = ArrayList(intSet)
        svgImageList?.clear()
        for (item in imageListToDownload!!) {
            presenter.getUserImageFromServer(queue!!, item, serverToken!!)
        }
    }

    override fun addImageToList(image: SvgImage?) {
        svgImageList?.add(image!!)
        if (image?.svgId == imageListToDownload?.last()) {
            val stringImageListSerialized = Gson().toJson(svgImageList)
            AppPreferences.imageList = stringImageListSerialized
            getImageDescriptionFromServer()
        }
    }

    private fun getImageDescriptionFromServer() {
        svgDescriptionList?.clear()
        for (item in imageListToDownload!!) {
            presenter.getImageDescriptionFromServer(queue!!, item, serverToken!!)
        }
    }

    override fun addImageDescriptionToList(image: SvgImageDescription?) {
        svgDescriptionList?.add(image!!)
        if (image?.svgId == imageListToDownload?.last()) {
            val stringDescriptionListSerialized = Gson().toJson(svgDescriptionList)
            AppPreferences.descriptionList = stringDescriptionListSerialized
            getTestsForImage()
        }
    }

    private fun getTestsForImage() {
        imageIdTestsForImageList?.clear()
        for (item in imageListToDownload!!) {
            presenter.getImageTestsFromServer(queue!!, item, serverToken!!)
        }
    }

    override fun addTestToList(imageId: Int?, tests: Tests?) {
        imageIdTestsForImageList?.add(ImageIdTestsForImage(imageId, tests))
        if (imageId == imageIdTestsForImageList?.last()?.imageId) {
            val stringTestListSerialized = Gson().toJson(imageIdTestsForImageList)
            AppPreferences.testList = stringTestListSerialized
            textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych")
            updateRecyclerView()
            login()
            hubConnection?.send("SendMessage", "uzytTest", "wiadomoscTest")
            Hawk.put("Is_Logged_In", true)
        }
    }



    private fun clearAppData(){
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

    private fun outgoingCall() {
        val username = "5000"
        val domain = "157.158.57.43"
        // As for everything we need to get the SIP URI of the remote and convert it to an Address
        val remoteSipUri = "sip:$username@$domain"
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
        val username = "5001"
        val password = "5001"
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

        if (packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, packageName) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }

    }

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                textToSpeechSingleton?.speakSentence("Nie udało się zalogować do serwera Voip")
            } else if (state == RegistrationState.Ok) {
                textToSpeechSingleton?.speakSentence("Zalogowano do serwera Voip")
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

    fun signalr() {
        hubConnection = HubConnectionBuilder.create("http://157.158.57.124:50820/api/testHub").withAccessTokenProvider(
            Single.defer { Single.just(serverToken) }).build()

        hubConnection?.on("SessionStart") {
            Log.i("SessionStarted")
            // Przejście aplikacji w tryb zdalny czy coś
        }

        hubConnection?.on("SessionEnd") {
            Log.i("SessionEnded")
            // Przeczytanie komunikatu o zakończeniu sesji,
            // powrót do listy użytkowników itp.
        }

        hubConnection?.on("StatusChange",{ userName, status ->
            Log.i("StatusChange $userName + $status")
            // Opcjonalna obsluga informacji o zmianie statusu jakiegoś uzytkownika
            // np. uderzenie do api po zaktualizowaną listę uzytkowników online
            },
            String::class.java,
            String::class.java)

        hubConnection?.on("Click", { click ->
            Log.i("Click $click")
            // Obsługa wyświetlenia kliknięcia
        }, String::class.java)

        hubConnection?.on(
            "ReceiveMessage", { user, message ->  Log.i("message $user + $message") },
            String::class.java,
            String::class.java
        )
        runOnUiThread {
            HubConnectionTask().execute(hubConnection)
        }
    }


    internal inner class HubConnectionTask : AsyncTask<HubConnection?, Void?, Void?>() {
        override fun doInBackground(vararg hubConnections: HubConnection?): Void? {
            val hubConnection = hubConnections[0]
            hubConnection?.start()?.blockingAwait()
            return null
        }
    }
}