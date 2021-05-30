package com.example.myapplication.ui.mainActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.volley.RequestQueue
import com.example.myapplication.App
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
import com.example.myapplication.ui.userListActivity.UserListActivity
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.utils.VolleySingleton
import com.example.myapplication.utils.signalRHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import org.linphone.core.*
import org.linphone.core.tools.Log
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainActivityView, MainActivityNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    var queue: RequestQueue? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    private var clickCountOne = 0
    private var clickCountTwo = 0
    private var clickCountThree = 0
    private var clickCountFour = 0
    private var clickCountFive = 0
    private var clickCountSix = 0
    private var clickCountSeven = 0
    private var clickCountEight = 0
    private var clickCountNine = 0
    private var clickCountZero = 0
    private var sectionList: ArrayList<String> = ArrayList()
    private var chosenSection: String? = null
    private var currentlyChosenSectionID: Int = 0
    private var stringAdapter: CustomAdapter? = null
    var imageListToDownload: ArrayList<Int>? = null
    var svgDescriptionList: ArrayList<SvgImageDescription>? = null
    var imageIdTestsForImageList: ArrayList<ImageIdTestsForImage>? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var svgImageList: ArrayList<SvgImage>? = null
    private var serverToken: String? = null
    var hasBeenLoggedIn: Boolean = false

    var jeden: Button? = null
    var dwa: Button? = null
    var trzy: Button? = null
    var cztery: Button? = null
    var piec: Button? = null
    var szesc: Button? = null
    var siedem: Button? = null
    var osiem: Button? = null
    var dziewiec: Button? = null
    var zero: Button? = null

    var etUsername: TextView? = null
    var etPassword: TextView? = null

    var insertedUsername: String? = null

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
        if(Hawk.contains("Is_Logged_In")){
            hasBeenLoggedIn = Hawk.get("Is_Logged_In")
        }
        if(hasBeenLoggedIn){
            if(AppPreferences.chosenSectionId != -1) currentlyChosenSectionID = AppPreferences.chosenSectionId
            val userImageIdsPairType = object : TypeToken<List<UserImageIdsPair>>() {}.type
            userImagesIdsPairList = Gson().fromJson<ArrayList<UserImageIdsPair>>(AppPreferences.userIdImageIdList, userImageIdsPairType)
            val svgImageType = object : TypeToken<List<SvgImage>>() {}.type
            svgImageList = Gson().fromJson<ArrayList<SvgImage>>(AppPreferences.imageList, svgImageType)
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
            if(textToSpeechSingleton?.isTTSready() == true){
                textToSpeechSingleton?.speakSentenceWithoutDisturbing("Prosze podać nazwę użytkownika")
            }else{
                val handler = Handler()
                handler.postDelayed({
                    textToSpeechSingleton?.speakSentenceWithoutDisturbing("Prosze podać nazwę użytkownika")
                }, 1000)
            }
            displayLoginUsernamePrompt()
            hideKeyboard(this)
            initializeRecyclerView()
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

    fun hideKeyboard(activity: Activity) {
        val view = activity.findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun displayLoginUsernamePrompt(){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.login_prompt, null)
        etUsername = dialogView.findViewById(R.id.et_username)
        initializeNumbers(dialogView, etUsername!!)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Nazwa Użytkownika")
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
        dialog.setOnShowListener {
            val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                insertedUsername = etUsername?.text.toString()
                displayLoginPasswordPrompt()
                //presenter.loginUser(queue!!, etUsername?.text.toString(), etPassword?.text.toString(), dialog)
                dialog.dismiss()
            }
        }
        hideKeyboard(this)
        dialog.show()
    }

    private fun displayLoginPasswordPrompt(){
        runOnUiThread { textToSpeechSingleton?.speakSentence("Prosze podać hasło") }
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.password_prompt, null)
        etPassword = dialogView.findViewById(R.id.et_password)
        initializeNumbers(dialogView, etPassword!!)
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Hasło")
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
        dialog.setOnShowListener {
            val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                presenter.loginUser(queue!!, insertedUsername!!, etPassword?.text.toString(), dialog)
                dialog.dismiss()
            }
        }
        hideKeyboard(this)
        dialog.show()
    }

    private fun initializeNumbers(view: View?, editText: TextView){
        jeden = view?.findViewById(R.id.jeden)
        jeden?.setOnClickListener { insertNumber(1, editText) }
        dwa = view?.findViewById(R.id.dwa)
        dwa?.setOnClickListener { insertNumber(2, editText) }
        trzy = view?.findViewById(R.id.trzy)
        trzy?.setOnClickListener { insertNumber(3, editText) }
        cztery = view?.findViewById(R.id.cztery)
        cztery?.setOnClickListener { insertNumber(4, editText) }
        piec = view?.findViewById(R.id.piec)
        piec?.setOnClickListener { insertNumber(5, editText) }
        szesc = view?.findViewById(R.id.szesc)
        szesc?.setOnClickListener { insertNumber(6, editText) }
        siedem = view?.findViewById(R.id.siedem)
        siedem?.setOnClickListener { insertNumber(7, editText) }
        osiem = view?.findViewById(R.id.osiem)
        osiem?.setOnClickListener { insertNumber(8, editText) }
        dziewiec = view?.findViewById(R.id.dziewiec)
        dziewiec?.setOnClickListener { insertNumber(9, editText) }
        zero = view?.findViewById(R.id.zero)
        zero?.setOnClickListener { insertNumber(0, editText) }
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
                        if(AppPreferences.appMode == 2){
                            val myIntent = Intent(this@MainActivity, UserListActivity::class.java)
                            this@MainActivity.startActivity(myIntent)
                            finish()
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
        //outgoingCall()
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
        Hawk.put("Is_Logged_In", true)
        textToSpeechSingleton?.speakSentence("Zalogowano pomyślnie, pobieranie danych")
        if(loginResponse.user?.studentId!=null){
            AppPreferences.appMode = 1
            AppPreferences.chosenUser = loginResponse.user?.studentId!!
            presenter.getUserImageIdsFromServer(queue!!, AppPreferences.chosenUser, loginResponse.token!!)
        }else{
            AppPreferences.appMode = 2
            AppPreferences.chosenUser = loginResponse.user?.teacherId!!
            if(AppPreferences.appMode == 2) {
                val myIntent = Intent(this@MainActivity, UserListActivity::class.java)
                this@MainActivity.startActivity(myIntent)
                finish()
            }
        }
        //presenter.getUserImageIdsFromServer(queue!!, 1, loginResponse.token!!)
    }

    override fun userLoggedInFailedLogic() {
        textToSpeechSingleton?.speakSentence("Podano błędne dane logowania")
        displayLoginUsernamePrompt()
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
            Hawk.put("Is_Logged_In", true)
            if(AppPreferences.appMode == 2){
                val myIntent = Intent(this@MainActivity, UserListActivity::class.java)
                this@MainActivity.startActivity(myIntent)
                finish()
            }else{
                login("5001")
            }

            //hubConnection?.send("SendMessage", "uzytTest", "wiadomoscTest")

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
        val accountParams = App.core.createAccountParams()

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
        val account = App.core.createAccount(accountParams)

        // Now let's add our objects to the Core
        App.core.addAuthInfo(authInfo)
        App.core.addAccount(account)

        // Also set the newly added account as default
        App.core.defaultAccount = account

        // Allow account to be removed

        // To be notified of the connection status of our account, we need to add the listener to the Core
        App.core.addListener(coreListener)
        // We can also register a callback on the Account object
        account.addListener { _, state, message ->
            // There is a Log helper in org.linphone.core.tools package
            Log.i("[Account] Registration state changed: $state, $message")
        }

        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        App.core.start()

        if (packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, packageName) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }

    }

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych, serwer do rozmów nie jest dostępny")
            } else if (state == RegistrationState.Ok) {
                textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych, serwer do rozmów jest dostępny")
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
                //Call.State.OutgoingInit -> {
                //    // First state an outgoing call will go through
                //}
                //Call.State.OutgoingProgress -> {
                //    // Right after outgoing init
                //}
                //Call.State.OutgoingRinging -> {
                //    // This state will be reached upon reception of the 180 RINGING
                //}
                //Call.State.Connected -> {
                //    // When the 200 OK has been received
                //}
                //Call.State.StreamsRunning -> {
                //    // This state indicates the call is active.
                //    // You may reach this state multiple times, for example after a pause/resume
                //    // or after the ICE negotiation completes
                //    // Wait for the call to be connected before allowing a call update
//
                //    // Only enable toggle camera button if there is more than 1 camera and the video is enabled
                //    // We check if core.videoDevicesList.size > 2 because of the fake camera with static image created by our SDK (see below)
                //}
                //Call.State.Paused -> {
                //    // When you put a call in pause, it will became Paused
                //}
                //Call.State.PausedByRemote -> {
                //    // When the remote end of the call pauses it, it will be PausedByRemote
                //}
                //Call.State.Updating -> {
                //    // When we request a call update, for example when toggling video
                //}
                //Call.State.UpdatedByRemote -> {
                //    // When the remote requests a call update
                //}
                //Call.State.Released -> {
                //    // Call state will be released shortly after the End state
                //}
                //Call.State.Error -> {
//
                //}
                Call.State.IncomingReceived -> {
                    core.currentCall?.accept()
                    signalRHelper.signalr(serverToken)
                }
            }
        }
    }
    fun insertNumber(number: Int, editText: TextView){
        when(number){
            0 -> clickCountZero++
            1 -> clickCountOne++
            2 -> clickCountTwo++
            3 -> clickCountThree++
            4 -> clickCountFour++
            5 -> clickCountFive++
            6 -> clickCountSix++
            7 -> clickCountSeven++
            8 -> clickCountEight++
            9 -> clickCountNine++
        }
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountZero) {
                    1 -> textToSpeechSingleton?.speakSentence("Zero")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "0"
                    }
                }
                clickCountZero = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountOne) {
                    1 -> textToSpeechSingleton?.speakSentence("Jeden")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "1"
                    }
                }
                clickCountOne = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountTwo) {
                    1 -> textToSpeechSingleton?.speakSentence("Dwa")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "2"
                    }
                }
                clickCountTwo = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountThree) {
                    1 -> textToSpeechSingleton?.speakSentence("Trzy")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "3"
                    }
                }
                clickCountThree = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountFour) {
                    1 -> textToSpeechSingleton?.speakSentence("Cztery")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "4"
                    }
                }
                clickCountFour = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountFive) {
                    1 -> textToSpeechSingleton?.speakSentence("Pięć")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "5"
                    }
                }
                clickCountFive = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSix) {
                    1 -> textToSpeechSingleton?.speakSentence("Sześć")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "6"
                    }
                }
                clickCountSix = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSeven) {
                    1 -> textToSpeechSingleton?.speakSentence("Siedem")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "7"
                    }
                }
                clickCountSeven = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountEight) {
                    1 -> textToSpeechSingleton?.speakSentence("Osiem")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "8"
                    }
                }
                clickCountEight = 0
            }
        }.start()
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountNine) {
                    1 -> textToSpeechSingleton?.speakSentence("Dziewięć")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "9"
                    }
                }
                clickCountNine = 0
            }
        }.start()
    }

}