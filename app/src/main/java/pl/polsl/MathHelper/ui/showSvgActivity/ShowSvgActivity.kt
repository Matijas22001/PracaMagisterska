package pl.polsl.MathHelper.ui.showSvgActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.android.volley.RequestQueue
import com.google.gson.Gson
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
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.model.*
import pl.polsl.MathHelper.ui.chooseTaskActivity.ChooseTaskActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.testActivity.TestActivity
import pl.polsl.MathHelper.utils.*
import javax.inject.Inject


class ShowSvgActivity: AppCompatActivity(), ShowSvgActivityView,ShowSvgActivityNavigator, signalRHelper.SignalRCallbacks {

    var queue: RequestQueue? = null
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
    var tests: Tests? = null
    var signalRHelperClass: signalRHelper? = null

    @Inject
    lateinit var presenter: ShowSvgActivityPresenter

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun initializeWebView(){
        textToSpeechSingleton?.speakSentence("Obecny moduł to widok zadania")
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
        svgImage?.svgXML = svgImage?.svgXML?.replace("stroke-width:([\" \"]?)+[1-9]+".toRegex(), "stroke-width: $svgStrokeWidth")
        svgImage?.svgXML = svgImage?.svgXML?.replace("id=".toRegex(), " onclick=\"onClickEvent(evt)\" id=")
        //svgImage?.svgXML = svgImage?.svgXML?.replace("<path ".toRegex(), "<path onclick=\"onClickEvent(evt)\" ")
        //svgImage?.svgXML = svgImage?.svgXML?.replace("<circle ".toRegex(), "<circle onclick=\"onClickEvent(evt)\" ")
        //svgImage?.svgXML = svgImage?.svgXML?.replace("<rect ".toRegex(), "<rect onclick=\"onClickEvent(evt)\" ")
        //svgImage?.svgXML = svgImage?.svgXML?.replace("<image ".toRegex(), "<image onclick=\"onClickEvent(evt)\" ")
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

    fun sendClickDetails(elementId: String,X: Long?, Y: Long?, fileId: Int){
        val serverToken = Hawk.get<String>("Server_Token")
        presenter.sendImageClickDataToServer(queue!!, X, Y, elementId, fileId, serverToken)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_svg)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        ViewUtils.fullScreenCall(window)
        initializeOnClicks()
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        svgImage = Gson().fromJson(AppPreferences.chosenTask, SvgImage::class.java)
        svgImageDescription = Gson().fromJson(AppPreferences.chosenTaskDescription, SvgImageDescription::class.java)
        tests = Gson().fromJson(AppPreferences.chosenTaskTests, Tests::class.java)
        initializeWebView()
        signalRHelperClass = signalRHelper(this)
        val serverToken = Hawk.get<String>("Server_Token")
        signalRHelperClass?.signalr(serverToken)
        if(!Hawk.get<Boolean>("Is_In_Call")){
            var phoneNumber: String? = null
            if (AppPreferences.appMode == 2){
                if(Hawk.contains("Teacher_phone_number")){
                    phoneNumber = Hawk.get<String>("Teacher_phone_number")
                }
            } else{
                if(Hawk.contains("Student_phone_number")) {
                    phoneNumber = Hawk.get<String>("Student_phone_number")
                }
            }
            if(phoneNumber!= null)login(phoneNumber)
        }
    }

    override fun onResume() {
        super.onResume()
        initializeWebView()
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
                            val myIntent = Intent(this@ShowSvgActivity, ChooseTaskActivity::class.java)
                            this@ShowSvgActivity.startActivity(myIntent)
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
                        2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
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
                        2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
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
                        1 -> textToSpeechSingleton?.speakSentence("Wybór testu do rozwiązania")
                        2 ->{
                            if(AppPreferences.appMode == 2){
                                textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
                            }else{
                                if(tests?.testList?.size!! > 0){
                                    textToSpeechSingleton?.speakSentence("Uruchamianie modułu testów")
                                    val myIntent = Intent(this@ShowSvgActivity, TestActivity::class.java)
                                    this@ShowSvgActivity.startActivity(myIntent)
                                    finish()
                                }else{
                                    textToSpeechSingleton?.speakSentence("Brak testów dla tego zadania")
                                }
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
                            val myIntent = Intent(this@ShowSvgActivity, SettingsActivity::class.java)
                            this@ShowSvgActivity.startActivity(myIntent)
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
                        2 -> textToSpeechSingleton?.speakSentence("Widok zadania - menu główne")
                    }
                    clickCountTest = 0
                }
            }.start()
        }
    }

    override fun showMessage(resId: Int) {}
    override fun showMessage(message: String?) {}

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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    X = event.x.toLong()
                    Y = event.y.toLong()
                    Log.e("Kliknięto", "ID: $selectedId x $X y $Y")
                    sendClickDetails(selectedId, X, Y , svgImage?.svgId!!)
                }
            }
        return super.dispatchTouchEvent(event)
    }


    fun createPOSTObject(x: Long?, y: Long?, elementId: String, fileId: Int): JSONObject? {
        return try{
            val click = Click()
            click.studentId = AppPreferences.chosenUser
            click.fileId = fileId
            click.x = x
            click.y = y
            click.elementId = elementId
            click.timeStamp = getTime()
            val tempList: ArrayList<Click> = ArrayList()
            tempList.add(click)
            JSONObject(Gson().toJson(ClickSendObject(tempList)))
        }catch (e: Exception){
            null
        }
    }

    fun getTime(): String {
        val dt = DateTime.now()
        val fmt: DateTimeFormatter = ISODateTimeFormat.dateTime()
        return fmt.print(dt)
    }

    class WebViewInterface(activity: ShowSvgActivity) {
        var activity: ShowSvgActivity? = activity

        @JavascriptInterface
        fun showDetail(content: String) {
            activity?.clickCount =  activity?.clickCount!! + 1
            val x = activity?.X
            val y = activity?.Y
            Log.e("Kliknięto", "ID: $content x $x y $y")
            activity?.sendClickDetails(content, activity?.X, activity?.Y , activity?.svgImage?.svgId!!)
            activity?.signalRHelperClass?.SendClick(activity?.createPOSTObject(activity?.X, activity?.Y ,content, activity?.svgImage?.svgId!!).toString())
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
            org.linphone.core.tools.Log.i("[Account] Registration state changed: $state, $message")
        }
        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        App.core.start()
    }

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
                Log.i("Tag","Serwer do rozmów nie jest dostępny")
            } else if (state == RegistrationState.Ok) {
                Log.i("Tag","Serwer do rozmów jest dostępny")
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
                        1 -> textToSpeechSingleton?.speakSentence("Wybór testu do rozwiązania")
                        2 ->{
                            if(tests?.testList?.size!! > 0){
                                textToSpeechSingleton?.speakSentence("Uruchamianie modułu testów")
                                val myIntent = Intent(this@ShowSvgActivity, TestActivity::class.java)
                                this@ShowSvgActivity.startActivity(myIntent)
                                finish()
                            }else{
                                textToSpeechSingleton?.speakSentence("Brak testów dla tego zadania")
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
                        1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                        2 -> {
                            val myIntent = Intent(this@ShowSvgActivity, ChooseTaskActivity::class.java)
                            this@ShowSvgActivity.startActivity(myIntent)
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
        runOnUiThread {
            if(AppPreferences.appMode == 2){
                wv_image_show_svg.removeAllViews()
                val clickResponse = Gson().fromJson(click, ClickSendObject::class.java)
                val x = clickResponse.click?.get(0)?.x
                val y = clickResponse.click?.get(0)?.y
                val elementId = clickResponse.click?.get(0)?.elementId
                Toast.makeText(this, "Click x $x y $y", Toast.LENGTH_SHORT).show()
                textToSpeechSingleton?.speakSentence("Uczeń kliknął element o id $elementId")
                val circleView: View = Circle(this, null, x?.toFloat()!!, y?.toFloat()!!, 10F)
                wv_image_show_svg.addView(circleView)
            }
        }
        Log.i("","Click $click")
    }

}