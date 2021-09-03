package pl.polsl.MathHelper.ui.mainActivity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.volley.RequestQueue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTest
import pl.polsl.MathHelper.helper_data_containers.ImageIdTestsForImage
import pl.polsl.MathHelper.helper_data_containers.UserImageIdsPair
import pl.polsl.MathHelper.model.*
import pl.polsl.MathHelper.ui.chooseTaskActivity.ChooseTaskActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.showSvgActivity.ShowSvgActivity
import pl.polsl.MathHelper.ui.userListActivity.UserListActivity
import pl.polsl.MathHelper.utils.*
import java.lang.Exception
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainActivityView, MainActivityNavigator,
    signalRHelper.SignalRCallbacks {

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
    private var clickKasuj = 0
    private var clickCzytaj = 0
    private var sectionList: ArrayList<String> = ArrayList()
    private var chosenSection: String? = null
    private var currentlyChosenSectionID: Int = 0
    private var stringAdapter: CustomAdapter? = null
    var imageListToDownload: ArrayList<Int>? = null
    var svgDescriptionList: ArrayList<SvgImageDescription>? = null
    var imageIdTestsForImageList: ArrayList<ImageIdTestsForImage>? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var imageTestList: ArrayList<ImageIdTestsForImage>? = null
    var svgImageDescriptionList: ArrayList<SvgImageDescription>? = null
    var currentUserSvgImageList: ArrayList<SvgImage>? = null
    var currentUserImageIdsPair: UserImageIdsPair? = null
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
    var kasuj: Button? = null
    var czytaj: Button? = null

    var etUsername: TextView? = null
    var etPassword: TextView? = null

    var insertedUsername: String? = null
    var insertedPassword: String? = null

    var signalRHelperClass: signalRHelper? = null
    var loginResponseSaved: LoginResponse? = null

    var currentRemoteStudentId: Int? = null

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
        signalRHelperClass = signalRHelper(this)
        initializeOnClicks()
        if (userImagesIdsPairList == null) userImagesIdsPairList = ArrayList()
        if (svgImageList == null) svgImageList = ArrayList()
        if (svgDescriptionList == null) svgDescriptionList = ArrayList()
        if (imageIdTestsForImageList == null) imageIdTestsForImageList = ArrayList()
        if (currentUserSvgImageList == null) currentUserSvgImageList = ArrayList()
        if (Hawk.contains("Is_Logged_In")) {
            hasBeenLoggedIn = Hawk.get("Is_Logged_In")
        }
        if (Hawk.contains("Currently_chosen_user_id")) {
            currentRemoteStudentId = Hawk.get<Int>("Currently_chosen_user_id")
        }
        if (hasBeenLoggedIn) {
            if (AppPreferences.chosenSectionId != -1) currentlyChosenSectionID =
                AppPreferences.chosenSectionId
            queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
            if (currentRemoteStudentId != null) {
                textToSpeechSingleton?.speakSentence("Pobieranie danych, proszę czekać")
                val token = Hawk.get<String>("Server_Token")
                serverToken = token
                presenter.getUserImageIdsFromServer(queue!!, currentRemoteStudentId!!, token)
                initializeRecyclerView()
            } else {
                //if(AppStatus.getInstance(this).isOnline){
                //    textToSpeechSingleton?.speakSentence("Pobieranie danych, proszę czekać")
                //    val token = Hawk.get<String>("Server_Token")
                //    serverToken = token
                //    presenter.getUserImageIdsFromServer(queue!!, AppPreferences.chosenUser, token)
                //    initializeRecyclerView()
                //}else{
                    val userImageIdsPairType = object : TypeToken<List<UserImageIdsPair>>() {}.type
                    userImagesIdsPairList = Gson().fromJson<ArrayList<UserImageIdsPair>>(AppPreferences.userIdImageIdList, userImageIdsPairType)
                    val svgImageType = object : TypeToken<List<SvgImage>>() {}.type
                    svgImageList = Gson().fromJson<ArrayList<SvgImage>>(AppPreferences.imageList, svgImageType)
                    initializeRecyclerView()
                    updateRecyclerView()
                //}
            }
        } else {
            if (AppPreferences.chosenSectionId != -1) currentlyChosenSectionID = AppPreferences.chosenSectionId
            queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
            if (textToSpeechSingleton?.isTTSready() == true) {
                textToSpeechSingleton?.speakSentenceWithoutDisturbing("Proszę podać nazwę użytkownika")
            } else {
                val handler = Handler()
                handler.postDelayed({
                    textToSpeechSingleton?.speakSentenceWithoutDisturbing("Proszę podać nazwę użytkownika") }, 1000)
            }
            displayLoginUsernamePrompt()
            hideKeyboard(this)
            initializeRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.fullScreenCall(window)
    }


    private fun inicializeListRemote() {
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
            if(currentRemoteStudentId!=null && AppPreferences.appMode == 2){
                if(currentRemoteStudentId == item.userId)
                    currentUserImageIdsPair = item
            }else{
                if(AppPreferences.chosenUser == item.userId)
                    currentUserImageIdsPair = item
            }
        }
        currentUserSvgImageList?.clear()
        for(item in currentUserImageIdsPair?.svgIdListFromServer!!){
            for(item1 in svgImageList!!){
                if(item == item1.svgId){
                    currentUserSvgImageList?.add(item1)
                }
            }
        }
    }

    private fun readAndSendOfflineObjects(){
        if(AppPreferences.offlineTests != ""){
            val testArrayListType = object : TypeToken<ArrayList<ChosenAnswersForTest>>() {}.type
            val testArrayList: ArrayList<ChosenAnswersForTest>? = Gson().fromJson<ArrayList<ChosenAnswersForTest>>(AppPreferences.offlineTests, testArrayListType)
            if(testArrayList?.size!!>0){
                presenter.sendTestToServer(queue!!, testArrayList, serverToken!!)
            }
        }
        if(AppPreferences.offlineClicks != ""){
            val clickArrayListType = object : TypeToken<ArrayList<Click>>() {}.type
            val clickArrayList: ArrayList<Click>? = Gson().fromJson<ArrayList<Click>>(AppPreferences.offlineClicks, clickArrayListType)
            if(clickArrayList?.size!!>0){
                presenter.sendImageClickDataToServer(queue!!, clickArrayList, serverToken!!)
            }
        }
    }

    private fun initializeRecyclerView(){
        linearLayoutManager = LinearLayoutManager(this)
        sectionRecyclerView.layoutManager = linearLayoutManager
        stringAdapter =
            CustomAdapter(sectionList)
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
            etUsername?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if(s.toString().length == 6){
                        button.callOnClick()
                    }
                }
            })
            button.setOnClickListener {
                insertedUsername = etUsername?.text.toString()
                displayLoginPasswordPrompt()
                //presenter.loginUser(queue!!, etUsername?.text.toString(), etPassword?.text.toString(), dialog)
                dialog.dismiss()
            }
        }
        hideKeyboard(this)
        ViewUtils.fullScreenCall(dialog.window!!)
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
            etPassword?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if(s.toString().length == 4){
                        button.callOnClick()
                    }
                }
            })
            button.setOnClickListener {
                insertedPassword = etPassword?.text.toString()
                if(AppStatus.getInstance(this).isOnline) {
                    presenter.loginUser(queue!!, insertedUsername!!, insertedPassword!!, dialog)
                    ViewUtils.fullScreenCall(dialog.window!!)
                    ViewUtils.fullScreenCall(window)
                }else{
                    if(AppPreferences.lastLogin == insertedUsername && AppPreferences.lastPassword == insertedPassword){
                        textToSpeechSingleton?.speakSentence("Brak połączenia internetowego, uruchomiono tryb offline")
                        Hawk.put("Is_Logged_In", true)
                        val userImageIdsPairType = object : TypeToken<List<UserImageIdsPair>>() {}.type
                        userImagesIdsPairList = Gson().fromJson<ArrayList<UserImageIdsPair>>(AppPreferences.userIdImageIdList, userImageIdsPairType)
                        val svgImageType = object : TypeToken<List<SvgImage>>() {}.type
                        svgImageList = Gson().fromJson<ArrayList<SvgImage>>(AppPreferences.imageList, svgImageType)
                        inicializeList()
                        initializeRecyclerView()
                        updateRecyclerView()
                    } else {
                        textToSpeechSingleton?.speakSentence("Podane dane nie są zgodne z ostatnim użytkownikiem, tryb offline nie jest dostepny")
                    }
                    ViewUtils.fullScreenCall(dialog.window!!)
                    ViewUtils.fullScreenCall(window)
                }
                dialog.dismiss()
            }
        }
        hideKeyboard(this)
        ViewUtils.fullScreenCall(dialog.window!!)
        ViewUtils.fullScreenCall(window)
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
        kasuj = view?.findViewById(R.id.delete_button)
        kasuj?.setOnClickListener { insertNumber(10, editText) }
        czytaj = view?.findViewById(R.id.read_text)
        czytaj?.setOnClickListener { insertNumber(11, editText) }
    }

    fun initializeOnClicks(){
        btn_back.setOnClickListener {
            clickCountBack++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountBack) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                        2 -> {
                            if (AppPreferences.appMode == 2) {
                                signalRHelperClass?.EndSession()
                                val myIntent = Intent(this@MainActivity, UserListActivity::class.java)
                                this@MainActivity.startActivity(myIntent)
                                finish()
                            }else{
                                textToSpeechSingleton?.speakSentence("Brak modułu")
                            }
                        }
                    }
                    clickCountBack = 0
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
                        2 -> choosePreviousSection()
                        3 -> choose5thPreviousSection()
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
                        2 -> chooseNextSection()
                        3 -> choose5thNextSection()
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
        btn_settings.setOnClickListener {
            clickCountSettings++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
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
        btn_test.setOnClickListener {
            clickCountTest++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
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
        AppPreferences.lastLogin = insertedUsername!!
        AppPreferences.lastPassword = insertedPassword!!
        clearAppData()
        ViewUtils.fullScreenCall(window)
        loginResponseSaved = loginResponse
        serverToken =  loginResponse.token
        Hawk.put("Server_Token",serverToken)
        Hawk.put("Is_Logged_In", true)
        if(AppStatus.getInstance(this).isOnline){
            try {
                signalRHelperClass?.signalr(serverToken)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        if(loginResponse.user?.studentId!=null){
            textToSpeechSingleton?.speakSentence("Zalogowano pomyślnie, pobieranie danych")
            AppPreferences.appMode = 1
            AppPreferences.chosenUser = loginResponse.user?.studentId!!
            presenter.getUserImageIdsFromServer(queue!!, AppPreferences.chosenUser, loginResponse.token!!)
        }else{
            textToSpeechSingleton?.speakSentence("Zalogowano pomyślnie, przechodzenie do modułu wyboru ucznia")
            AppPreferences.appMode = 2
            AppPreferences.chosenUser = loginResponse.user?.teacherId!!
            if(AppPreferences.appMode == 2) {
                if(loginResponseSaved?.user?.voipNumber!= null) Hawk.put("Teacher_phone_number", loginResponseSaved?.user?.voipNumber)
                val myIntent = Intent(this@MainActivity, UserListActivity::class.java)
                this@MainActivity.startActivity(myIntent)
                finish()
            }
        }
    }

    override fun userLoggedInFailedLogic() {
        ViewUtils.fullScreenCall(window)
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
        if (imageId == imageListToDownload?.last()) {
            val stringTestListSerialized = Gson().toJson(imageIdTestsForImageList)
            AppPreferences.testList = stringTestListSerialized
            //textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych")
            updateRecyclerView()
            if(AppPreferences.appMode == 2){
                if(Hawk.contains("Teacher_phone_number")){
                    val phoneNumber = Hawk.get<String>("Teacher_phone_number")
                    login(phoneNumber)
                }
            }else{
                textToSpeechSingleton?.speakSentence("Zakończono pobieranie danych")
                if(loginResponseSaved?.user?.voipNumber!= null) {
                    Hawk.put("Student_phone_number", loginResponseSaved?.user?.voipNumber)
                    login(loginResponseSaved?.user?.voipNumber!!)
                }
                readAndSendOfflineObjects()
                inicializeListRemote()
            }

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
                    textToSpeechSingleton?.speakSentence("Połączenie przychodzące wciśnij przycisk wybierz aby odebrać lub cofnij aby odrzucić")
                    reactToCall()
                }
                Call.State.Released -> {
                    //if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager.mode = AudioManager.MODE_NORMAL
                        audioManager.isSpeakerphoneOn = false
                    //}
                    Hawk.put("Is_In_Call",false)
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
            10-> clickKasuj++
            11-> clickCzytaj++
        }
        val testTapInterval: Long = 400
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountZero) {
                    1 -> textToSpeechSingleton?.speakSentence("Zero")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano zero")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "0"
                    }
                }
                clickCountZero = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountOne) {
                    1 -> textToSpeechSingleton?.speakSentence("Jeden")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano jeden")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "1"
                    }
                }
                clickCountOne = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountTwo) {
                    1 -> textToSpeechSingleton?.speakSentence("Dwa")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano dwa")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "2"
                    }
                }
                clickCountTwo = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountThree) {
                    1 -> textToSpeechSingleton?.speakSentence("Trzy")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano trzy")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "3"
                    }
                }
                clickCountThree = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountFour) {
                    1 -> textToSpeechSingleton?.speakSentence("Cztery")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano cztery")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "4"
                    }
                }
                clickCountFour = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountFive) {
                    1 -> textToSpeechSingleton?.speakSentence("Pięć")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano pięć")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "5"
                    }
                }
                clickCountFive = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSix) {
                    1 -> textToSpeechSingleton?.speakSentence("Sześć")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano sześć")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "6"
                    }
                }
                clickCountSix = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountSeven) {
                    1 -> textToSpeechSingleton?.speakSentence("Siedem")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano siedem")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "7"
                    }
                }
                clickCountSeven = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountEight) {
                    1 -> textToSpeechSingleton?.speakSentence("Osiem")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano osiem")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "8"
                    }
                }
                clickCountEight = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountNine) {
                    1 -> textToSpeechSingleton?.speakSentence("Dziewięć")
                    2 -> {
                        textToSpeechSingleton?.speakSentence("Wpisano dziewięć")
                        val currentText: String = editText?.text.toString()
                        editText?.text = currentText + "9"
                    }
                }
                clickCountNine = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickKasuj) {
                    1 -> textToSpeechSingleton?.speakSentence("Kasuj")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        if(currentText.isNotEmpty()){
                            textToSpeechSingleton?.speakSentence("Skasowano " + currentText.substring(currentText.length-1, currentText.length))
                            if(currentText.isNotEmpty())
                                editText?.text = currentText.substring(0, currentText.length-1)
                        }else{
                            textToSpeechSingleton?.speakSentence("Brak tekstu do skasowania")
                        }
                    }
                }
                clickKasuj = 0
            }
        }.start()
        object: CountDownTimer(testTapInterval,testTapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCzytaj) {
                    1 -> textToSpeechSingleton?.speakSentence("Czytaj obecnie wpisany tekst")
                    2 -> {
                        val currentText: String = editText?.text.toString()
                        textToSpeechSingleton?.speakSentence(currentText)
                    }
                }
                clickCzytaj = 0
            }
        }.start()
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
                            core.currentCall?.accept()
                            core.currentCall?.startRecording()
                            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                            audioManager.isSpeakerphoneOn = true
                            toggleSpeaker()
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
                            core.currentCall?.decline(Reason.Declined)
                            Hawk.put("Is_In_Call",false)
                            resetViewState()
                        }
                    }
                    clickCountBack = 0
                }
            }.start()
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

    private fun resetViewState(){
        btn_test.isEnabled = true
        btn_next.isEnabled = true
        btn_previous.isEnabled = true
        btn_settings.isEnabled = true
        btn_select.setOnClickListener {
            clickCountSelect++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
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
            }.start() }
        btn_back.setOnClickListener {  clickCountBack++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountBack) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                        2 -> {
                            textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
                            if (AppPreferences.appMode == 2) {
                                val myIntent =
                                    Intent(this@MainActivity, UserListActivity::class.java)
                                this@MainActivity.startActivity(myIntent)
                                finish()
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

    override fun onImageChange(imageId: Int) {
        runOnUiThread {
            if(AppPreferences.appMode == 1){
                AppPreferences.chosenTask = Gson().toJson(getCurrentTask(imageId))
                AppPreferences.chosenTaskDescription = Gson().toJson(getCurrentTaskDescription(imageId))
                AppPreferences.chosenTaskTests = Gson().toJson(getCurrentTaskTests(imageId))
                //AppPreferences.chosenTaskId = currentlyChosenTaskID
                Hawk.put("Is_task_from_teacher", true)
                val myIntent = Intent(this@MainActivity, ShowSvgActivity::class.java)
                this@MainActivity.startActivity(myIntent)
                finish()
            }
        }
    }

    private fun getCurrentTask(svgId: Int): SvgImage?{
        for(item in currentUserSvgImageList!!){
            if(item.svgId == svgId){
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
}