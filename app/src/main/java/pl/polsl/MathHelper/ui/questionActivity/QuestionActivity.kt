package pl.polsl.MathHelper.ui.questionActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.android.volley.RequestQueue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.show_svg.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.json.JSONObject
import org.linphone.core.*
import pl.polsl.MathHelper.App
import pl.polsl.MathHelper.App.Companion.core
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.helper_data_containers.*
import pl.polsl.MathHelper.model.*
import pl.polsl.MathHelper.ui.answerActivity.AnswerActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.showSvgActivity.ShowSvgActivity
import pl.polsl.MathHelper.ui.testActivity.TestActivity
import pl.polsl.MathHelper.utils.*
import javax.inject.Inject

class QuestionActivity: AppCompatActivity(), QuestionActivityNavigator, QuestionActivityView, signalRHelper.SignalRCallbacks {
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    var clickCount = 0
    var selectedId = ""
    var X: Long? = null
    var Y: Long? = null
    var svgImage: SvgImage? = null
    var svgImageDescription: SvgImageDescription? = null
    var test: Test? = null
    var questionNameList: ArrayList<String>? = null
    var currentlyChosenQuestionId: Int = 0
    var chosenAnswersForTest: ChosenAnswersForTest? = null
    var queue: RequestQueue? = null
    var signalRHelperClass: signalRHelper? = null
    var userImagesIdsPairList: ArrayList<UserImageIdsPair>? = null
    var svgImageList: ArrayList<SvgImage>? = null
    var imageTestList: ArrayList<ImageIdTestsForImage>? = null
    var svgImageDescriptionList: ArrayList<SvgImageDescription>? = null
    var currentUserSvgImageList: ArrayList<SvgImage>? = null
    var currentUserImageIdsPair: UserImageIdsPair? = null

    var currentRemoteStudentId: Int? = null
    var viewsList: ArrayList<View>? = null

    @Inject
    lateinit var presenter: QuestionActivityPresenter

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun initializeWebView(){
        textToSpeechSingleton?.speakSentence("Wyb??r pytania. Obecnie zaznaczone  " + questionNameList?.get(currentlyChosenQuestionId))
        wv_image_show_svg?.settings?.javaScriptEnabled = true
        wv_image_show_svg?.settings?.domStorageEnabled = true
        wv_image_show_svg?.settings?.useWideViewPort = true // it was true
        wv_image_show_svg?.settings?.allowFileAccess = true;
        wv_image_show_svg?.settings?.loadsImagesAutomatically = true
        wv_image_show_svg?.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        wv_image_show_svg?.webChromeClient = WebChromeClient()
        wv_image_show_svg?.setOnTouchListener { _: View?, event: MotionEvent -> event.action == MotionEvent.ACTION_MOVE }
        wv_image_show_svg?.setOnLongClickListener { true }
        wv_image_show_svg?.isLongClickable = false
        wv_image_show_svg?.addJavascriptInterface(WebViewInterface(this), "Android")
        changeSVGFile()
        svgImage?.svgXML = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=1920, user-scalable=no\" />" +
                "</head>" +
                "<body>" +
                svgImage?.svgXML +
                "</body></html>"
        try{
            val encodedHtml = Base64.encodeToString(svgImage?.svgXML?.toByteArray(), Base64.NO_PADDING)
            wv_image_show_svg.loadData(encodedHtml, "text/html", "base64")
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun changeSVGFile(){
        val svgStrokeWidth = AppPreferences.chosenImageSize
        //val svgStrokeWidth = 25
        svgImage?.svgXML = svgImage?.svgXML?.replace("stroke-width=\"[0-9]+\"".toRegex(), "stroke-width=\"$svgStrokeWidth\"")
        svgImage?.svgXML = svgImage?.svgXML?.replace("stroke-width:([\" \"]?)+[1-50]+px".toRegex(), "stroke-width: $svgStrokeWidth")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<line ".toRegex(), "<line onclick=\"onClickEvent(evt)\" ")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<path ".toRegex(), "<path onclick=\"onClickEvent(evt)\" ")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<circle ".toRegex(), "<circle onclick=\"onClickEvent(evt)\" ")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<rect ".toRegex(), "<rect onclick=\"onClickEvent(evt)\" ")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<image ".toRegex(), "<image onclick=\"onClickEvent(evt)\" ")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<use .*<\\/use>".toRegex(), "")
        val indexEndOfFirstSvgTag: Int = svgImage?.svgXML?.indexOf(">")!!
        val javascriptScript = """<script type="application/ecmascript"> <![CDATA[
        function onClickEvent(evt) {
        Android.showDetail(evt.target.getAttribute("id"));
        }
        ]]> </script>"""
        try {
            svgImage?.svgXML = svgImage?.svgXML?.substring(0, indexEndOfFirstSvgTag + 1) + javascriptScript + svgImage?.svgXML?.substring(indexEndOfFirstSvgTag + 1)
        } catch (e: Exception) {
            Log.e("SVG Image Converting", e.toString())
        }
    }


    override fun onResume() {
        super.onResume()
        initializeWebView()
        ViewUtils.fullScreenCall(window)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_svg)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        //textToSpeechSingleton = TextToSpeechSingleton(this)
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        ViewUtils.fullScreenCall(window)
        initializeOnClicks()
        if(currentUserSvgImageList==null) currentUserSvgImageList = ArrayList()
        if(viewsList == null) viewsList = ArrayList()
        inicializeListRemote()
        svgImage = Gson().fromJson(AppPreferences.chosenTask, SvgImage::class.java)
        svgImageDescription = Gson().fromJson(AppPreferences.chosenTaskDescription, SvgImageDescription::class.java)
        test = Gson().fromJson(AppPreferences.chosenTest, Test::class.java)
        chosenAnswersForTest = if(AppPreferences.answerList != ""){
            Gson().fromJson(AppPreferences.answerList, ChosenAnswersForTest::class.java)
        }else{
            ChosenAnswersForTest()
        }

        if(questionNameList==null) questionNameList = ArrayList()
        if(AppPreferences.chosenQuestionId != -1) currentlyChosenQuestionId = AppPreferences.chosenQuestionId
        initializeQuestionList()
        initializeWebView()
        signalRHelperClass = signalRHelper(this)
        val serverToken = Hawk.get<String>("Server_Token")
        if(AppStatus.getInstance(this).isOnline){
            try {
                signalRHelperClass?.signalr(serverToken)
            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }
        core.addListener(coreListener)
    }

    private fun inicializeListRemote() {
        if(Hawk.contains("Currently_chosen_user_id"))
        {
            currentRemoteStudentId = Hawk.get<Int>("Currently_chosen_user_id")
        }
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

    private fun initializeQuestionList(){
        questionNameList?.clear()
        for(item in test?.questionList!!){
            questionNameList?.add(item.questionDescription!!)
        }
    }

    fun initializeOnClicks(){
        btn_back.setOnClickListener {
            clickCountBack++
            object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountBack) {
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back)+" i wy??lij test")
                        2 -> {
                            reactToSendEvent()
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
                        2 -> choosePreviousQuestion()
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
                        2 -> chooseNextQuestion()
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
                        1 -> textToSpeechSingleton?.speakSentence("Udziel odpowiedzi")
                        2 -> {
                            if(getCurrentQuestion()?.answerList?.size!! >0){
                                textToSpeechSingleton?.speakSentence("Uruchamianie modu??u wyboru odpowiedzi")
                                AppPreferences.chosenQuestion = Gson().toJson(getCurrentQuestion())
                                AppPreferences.chosenQuestionId = currentlyChosenQuestionId
                                if(AppPreferences.answerList == "") {
                                    chosenAnswersForTest?.testId = test?.testId
                                    AppPreferences.answerList = Gson().toJson(chosenAnswersForTest)
                                }
                                val myIntent = Intent(this@QuestionActivity, AnswerActivity::class.java)
                                this@QuestionActivity.startActivity(myIntent)
                                finish()
                            }else{
                                textToSpeechSingleton?.speakSentence("Brak odpowiedzi dla tego testu")
                            }
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
                            val myIntent = Intent(this@QuestionActivity, SettingsActivity::class.java)
                            this@QuestionActivity.startActivity(myIntent)
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
                        2 -> textToSpeechSingleton?.speakSentence("Wyb??r pytania")
                    }
                    clickCountTest = 0
                }
            }.start()
        }
    }

    fun getTime(): String {
        val dt = DateTime.now()
        val fmt: DateTimeFormatter = ISODateTimeFormat.dateTime()
        return fmt.print(dt)
    }

    override fun sendTestAndCloseActivity(){
        val myIntent = Intent(this@QuestionActivity, TestActivity::class.java)
        this@QuestionActivity.startActivity(myIntent)
        finish()
    }

    var mCountDownTimer: CountDownTimer = object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            when (clickCount) {
                1 -> onSingleClick()
                2 -> onDoubleClick()
                3 -> onTripleClick()
                else -> onDoubleClick()
            }
            clickCount = 0
            selectedId = ""
        }
    }

    private fun onSingleClick() {
        for (item in svgImageDescription?.svgModel!!) {
            if (item.pathId == selectedId && item.defaultOneClick!=null) {
                textToSpeechSingleton?.speakSentence(item.defaultOneClick)
                break
            }
        }
    }

    private fun onDoubleClick() {
        for (item in svgImageDescription?.svgModel!!) {
            if (item.pathId == selectedId && item.defaultDoubleClick!=null) {
                textToSpeechSingleton?.speakSentence(item.defaultDoubleClick)
                break
            }
        }
    }

    private fun onTripleClick() {
        for (item in svgImageDescription?.svgModel!!) {
            if (item.pathId == selectedId && item.defaultTripleClick!=null) {
                textToSpeechSingleton?.speakSentence(item.defaultTripleClick)
                break
            }
        }
    }

    fun chooseNextQuestion(){
        val questionListSize = questionNameList?.size!! - 1
        currentlyChosenQuestionId += 1
        if(currentlyChosenQuestionId>questionListSize){
            currentlyChosenQuestionId = 0
        }
        textToSpeechSingleton?.speakSentence("Wybrany pytanie to ${questionNameList?.get(currentlyChosenQuestionId)}")
    }

    fun choosePreviousQuestion(){
        val questionListSize = questionNameList?.size!! - 1
        currentlyChosenQuestionId -= 1
        if(currentlyChosenQuestionId<0){
            currentlyChosenQuestionId = questionListSize
        }
        textToSpeechSingleton?.speakSentence("Wybrany pytanie to ${questionNameList?.get(currentlyChosenQuestionId)}")
    }

    private fun getCurrentQuestion(): Question? {
        for(item in test?.questionList!!){
            if(item.questionDescription == questionNameList?.get(currentlyChosenQuestionId)){
                return item
            }
        }
        return null
    }


    fun sendClickDetails(x: Long?, y: Long?, elementId: String, fileId: Int, testId: Int, type: Int){
        val serverToken = Hawk.get<String>("Server_Token")
        presenter.sendImageClickDataToServer(queue!!, x, y, elementId, fileId, testId, serverToken, type)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                X = event.x.toLong()
                Y = event.y.toLong()
                Log.e("Klikni??to", "ID: $selectedId x $X y $Y")
                if(AppStatus.getInstance(applicationContext).isOnline){
                    sendClickDetails(X, Y, selectedId, svgImage?.svgId!!, test?.testId!!,1)
                    signalRHelperClass?.SendClick(createPOSTObject(X, Y ,selectedId, svgImage?.svgId!!, test?.testId!!,1).toString())
                }else{
                    addClickToSend(X, Y, selectedId, svgImage?.svgId!!, test?.testId!!,1)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun createPOSTObject(x: Long?, y: Long?, elementId: String, fileId: Int, testId: Int, type: Int): JSONObject? {
        return try{
            val click = Click()
            click.studentId = AppPreferences.chosenUser
            click.fileId = fileId
            click.x = x
            click.y = y
            click.elementId = elementId
            click.testId = testId
            click.timeStamp = getTime()
            click.type = type
            val tempList: ArrayList<Click> = ArrayList()
            tempList.add(click)
            JSONObject(Gson().toJson(ClickSendObject(tempList)))
        }catch (e: Exception){
            null
        }
    }

    class WebViewInterface(activity: QuestionActivity) {
        var activity: QuestionActivity? = activity

        @JavascriptInterface
        fun showDetail(content: String) {
            activity?.clickCount =  activity?.clickCount!! + 1
            val x = activity?.X
            val y = activity?.Y
            Log.e("Klikni??to", "ID: $content x $x y $y")
            if(AppStatus.getInstance(activity?.applicationContext!!).isOnline){
                activity?.sendClickDetails(activity?.X, activity?.Y, content, activity?.svgImage?.svgId!!, activity?.test?.testId!!, activity?.clickCount!!)
                activity?.signalRHelperClass?.SendClick(activity?.createPOSTObject(activity?.X, activity?.Y, content, activity?.svgImage?.svgId!!, activity?.test?.testId!!, activity?.clickCount!!).toString())
            }else{
                activity?.addClickToSend(activity?.X, activity?.Y, content, activity?.svgImage?.svgId!!, activity?.test?.testId!!, activity?.clickCount!!)
            }
            activity?.selectedId = content
            activity?.mCountDownTimer?.start()
        }

        @JavascriptInterface
        fun showLog(content: String?) {
            if (null != content) {
                Log.e("CurrentElement ", content)
            } else {
                Log.e("CurrentElement ", "null")
            }
        }

        @JavascriptInterface
        fun storeClickPosition(x: Int, y: Int) {
            Log.e("CLICK", "X: $x Y: $y")
        }
    }


    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                Log.i("Tag","Serwer do rozm??w nie jest dost??pny")
            } else if (state == RegistrationState.Ok) {
                Log.i("Tag","Serwer do rozm??w jest dost??pny")

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
                    textToSpeechSingleton?.speakSentence("Po????czenie przychodz??ce wci??nij przycisk wybierz aby odebra?? lub cofnij aby odrzuci??")
                    reactToCall()
                }
                Call.State.Released -> {
                    if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
                        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager.mode = AudioManager.MODE_NORMAL
                        audioManager.isSpeakerphoneOn = false
                    }
                    Hawk.put("Is_In_Call",false)
                }
            }
        }
    }


    private fun reactToSendEvent(){
        btn_test.isEnabled = false
        btn_next.isEnabled = false
        btn_previous.isEnabled = false
        btn_settings.isEnabled = false
        textToSpeechSingleton?.speakSentence("Czy na pewno chcesz wys??a?? test? Wci??nij cofnij aby wr??ci?? do testu, a wybierz aby wys??a?? test")
        btn_select.setOnClickListener {
            clickCountSelect++
            object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountSelect) {
                        1 -> textToSpeechSingleton?.speakSentence("Wy??lij")
                        2 -> {
                            Hawk.put("Test_end", getTime())
                            textToSpeechSingleton?.speakSentence("Test zosta?? wys??any")
                            val serverToken = Hawk.get<String>("Server_Token")
                            if(AppStatus.getInstance(applicationContext).isOnline){
                                presenter.sendTestToServer(queue!!, serverToken)
                            }else{
                                addTestToSend()
                            }
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
                        1 -> textToSpeechSingleton?.speakSentence("Powr???? do rozwi??zywania testu")
                        2 -> {
                            resetViewState()
                        }
                    }
                    clickCountBack = 0
                }
            }.start()
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
                        1 -> textToSpeechSingleton?.speakSentence("Odrzu??")
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
            object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    when (clickCountSelect) {
                        1 -> textToSpeechSingleton?.speakSentence("Udziel odpowiedzi")
                        2 -> {
                            if(getCurrentQuestion()?.answerList?.size!! >0){
                                textToSpeechSingleton?.speakSentence("Uruchamianie modu??u wyboru odpowiedzi")
                                AppPreferences.chosenQuestion = Gson().toJson(getCurrentQuestion())
                                AppPreferences.chosenQuestionId = currentlyChosenQuestionId
                                if(AppPreferences.answerList == "") {
                                    chosenAnswersForTest?.testId = test?.testId
                                    AppPreferences.answerList = Gson().toJson(chosenAnswersForTest)
                                }
                                val myIntent = Intent(this@QuestionActivity, AnswerActivity::class.java)
                                this@QuestionActivity.startActivity(myIntent)
                                finish()
                            }else{
                                textToSpeechSingleton?.speakSentence("Brak odpowiedzi dla tego testu")
                            }
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
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back)+" i wy??lij test")
                        2 -> {
                            Hawk.put("Test_end", getTime())
                            textToSpeechSingleton?.speakSentence("Test zosta?? wys??any")
                            val serverToken = Hawk.get<String>("Server_Token")
                            presenter.sendTestToServer(queue!!, serverToken)
                        }
                    }
                    clickCountBack = 0
                }
            }.start()
        }
    }

    private fun addTestToSend(){
        val testArrayList: ArrayList<ChosenAnswersForTest>? = if(AppPreferences.offlineTests == ""){
            ArrayList()
        }else{
            val testArrayListType = object : TypeToken<ArrayList<ChosenAnswersForTest>>() {}.type
            Gson().fromJson<ArrayList<ChosenAnswersForTest>>(AppPreferences.offlineTests, testArrayListType)
        }
        if(createTestObject() != null){
            testArrayList?.add(createTestObject()!!)
            AppPreferences.offlineTests = Gson().toJson(testArrayList)
        }
    }

    private fun addClickToSend(x: Long?, y: Long?, elementId: String, fileId: Int, testId: Int, type: Int){
        val clickArrayList: ArrayList<Click>? = if(AppPreferences.offlineClicks == ""){
            ArrayList()
        }else{
            val clickArrayListType = object : TypeToken<ArrayList<Click>>() {}.type
            Gson().fromJson<ArrayList<Click>>(AppPreferences.offlineClicks, clickArrayListType)
        }
        if(createClickObject(x, y, elementId, fileId, testId, type) != null){
            clickArrayList?.add(createClickObject(x, y, elementId, fileId, testId, type)!!)
            AppPreferences.offlineClicks = Gson().toJson(clickArrayList)
        }
    }

    private fun createTestObject(): ChosenAnswersForTest? {
        return try{
            val chosenAnswersForTest: ChosenAnswersForTest = Gson().fromJson(AppPreferences.answerList, ChosenAnswersForTest::class.java)
            chosenAnswersForTest.studentId = AppPreferences.chosenUser
            chosenAnswersForTest.startDate = Hawk.get("Test_start")
            chosenAnswersForTest.endDate = Hawk.get("Test_end")
            return chosenAnswersForTest
        }catch (e: Exception){
            null
        }
    }

    private fun createClickObject(x: Long?, y: Long?, elementId: String, fileId: Int, testId: Int, type: Int): Click? {
        return try{
            val click = Click()
            click.studentId = AppPreferences.chosenUser
            click.fileId = fileId
            click.x = x
            click.y = y
            click.elementId = elementId
            click.testId = testId
            click.timeStamp = getTime()
            click.type = type
            return click
        }catch (e: Exception){
            null
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
        runOnUiThread {
            if(AppPreferences.appMode == 2){
                if(viewsList?.size == AppPreferences.pointNumber){
                    wv_image_show_svg.removeView(viewsList?.first())
                    viewsList?.remove(viewsList?.first())
                }
                val clickResponse = Gson().fromJson(click, ClickSendObject::class.java)
                val x = clickResponse.click?.get(0)?.x
                val y = clickResponse.click?.get(0)?.y
                val elementId = clickResponse.click?.get(0)?.elementId
                if(elementId != null && elementId != ""){
                    textToSpeechSingleton?.speakSentence("Ucze?? klikn???? element o id $elementId")
                    val circleView: View = CircleGreen(this, null, x?.toFloat()!!, y?.toFloat()!!, 10F)
                    wv_image_show_svg.addView(circleView)
                    viewsList?.add(circleView)
                }else{
                    val circleView: View = CircleRed(this, null, x?.toFloat()!!, y?.toFloat()!!, 10F)
                    wv_image_show_svg.addView(circleView)
                    viewsList?.add(circleView)
                }
            }
        }
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
                val myIntent = Intent(this@QuestionActivity, ShowSvgActivity::class.java)
                this@QuestionActivity.startActivity(myIntent)
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