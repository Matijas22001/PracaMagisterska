package com.example.myapplication.ui.questionActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.volley.RequestQueue
import com.example.myapplication.App
import com.example.myapplication.App.Companion.textToSpeechSingleton
import com.example.myapplication.R
import com.example.myapplication.helper_data_containers.ChosenAnswersForTest
import com.example.myapplication.model.Question
import com.example.myapplication.model.SvgImage
import com.example.myapplication.model.SvgImageDescription
import com.example.myapplication.model.Test
import com.example.myapplication.ui.answerActivity.AnswerActivity
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivity
import com.example.myapplication.ui.testActivity.TestActivity
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.TextToSpeechSingleton
import com.example.myapplication.utils.ViewUtils
import com.example.myapplication.utils.VolleySingleton
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import javax.inject.Inject

class QuestionActivity: AppCompatActivity(), QuestionActivityNavigator, QuestionActivityView {
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
    private var clickCountBack = 0
    private var clickCountPrevious = 0
    private var clickCountNext = 0
    private var clickCountSelect = 0
    private var clickCountSettings = 0
    private var clickCountTest = 0
    var clickCount = 0
    var selectedId = ""
    var svgImage: SvgImage? = null
    var svgImageDescription: SvgImageDescription? = null
    var test: Test? = null
    var questionNameList: ArrayList<String>? = null
    var currentlyChosenQuestionId: Int = 0
    var chosenAnswersForTest: ChosenAnswersForTest? = null
    var queue: RequestQueue? = null

    @BindView(R.id.wv_image)
    lateinit var imageWebView: WebView

    @Inject
    lateinit var presenter: QuestionActivityPresenter

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun initializeWebView(){
        textToSpeechSingleton?.speakSentence("Obecny moduł to wybór pytania")
        textToSpeechSingleton?.speakSentence("Zaznaczone pytanie to " + questionNameList?.get(currentlyChosenQuestionId))
        imageWebView.settings.javaScriptEnabled = true
        imageWebView.settings.domStorageEnabled = true
        imageWebView.settings.useWideViewPort = true // it was true
        imageWebView.settings.loadsImagesAutomatically = true
        imageWebView.webChromeClient = WebChromeClient()
        imageWebView.settings.javaScriptEnabled = true
        imageWebView.setOnTouchListener { _: View?, event: MotionEvent -> event.action == MotionEvent.ACTION_MOVE }
        imageWebView.setOnLongClickListener { true }
        imageWebView.isLongClickable = false
        imageWebView.addJavascriptInterface(WebViewInterface(this), "Android")
        changeSVGFile()
        svgImage?.svgXML = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=1920, user-scalable=no\" />" +
                "</head>" +
                "<body>" +
                svgImage?.svgXML +
                "</body></html>"
        imageWebView.loadData(svgImage?.svgXML!!, "text/html", "utf-8")
    }

    private fun changeSVGFile(){
        val svgStrokeWidth = AppPreferences.chosenImageSize
        svgImage?.svgXML = svgImage?.svgXML?.replace("stroke-width=\"[0-9]+\"".toRegex(), "stroke-width=\"$svgStrokeWidth\"")
        svgImage?.svgXML = svgImage?.svgXML?.replace("stroke-width:([\" \"]?)+[1-9]+".toRegex(), "stroke-width: $svgStrokeWidth")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<line id=".toRegex(), "<line onclick=\"onClickEvent(evt)\" id=")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<path id=".toRegex(), "<path onclick=\"onClickEvent(evt)\" id=")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<circle id=".toRegex(), "<circle onclick=\"onClickEvent(evt)\" id=")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<rect id=".toRegex(), "<rect onclick=\"onClickEvent(evt)\" id=")
        svgImage?.svgXML = svgImage?.svgXML?.replace("<image id=".toRegex(), "<image onclick=\"onClickEvent(evt)\" id=")
        //                    originalSvg = originalSvg.replaceAll("stroke-width=\"", "stroke-width=\"1");
        svgImage?.svgXML = svgImage?.svgXML?.replace("<use .*<\\/use>".toRegex(), "")
        val indexEndOfFirstSvgTag: Int = svgImage?.svgXML?.indexOf(">")!!
        val javascriptScript = """<script type="application/ecmascript"> <![CDATA[
        function onClickEvent(evt) {
        Android.showDetail(evt.target.getAttribute("id"), event.clientX, event.clientY);
        }
        ]]> </script>"""
        //var content = ""
        try {
            svgImage?.svgXML = svgImage?.svgXML?.substring(0, indexEndOfFirstSvgTag + 1) + javascriptScript + svgImage?.svgXML?.substring(indexEndOfFirstSvgTag + 1)
        } catch (e: Exception) {
            Log.e("SVG Image Converting", e.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        initializeWebView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_svg)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        //textToSpeechSingleton = TextToSpeechSingleton(this)
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        ViewUtils.fullScreenCall(window)
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
    }

    private fun initializeQuestionList(){
        questionNameList?.clear()
        for(item in test?.questionList!!){
            questionNameList?.add(item.questionDescription!!)
        }
    }

    @OnClick(R.id.btn_back)
    fun goBack(){
        //ToDO
        clickCountBack++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountBack) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back)+" i wyślij test")
                    2 -> {
                        Hawk.put("Test_end", getTime())
                        textToSpeechSingleton?.speakSentence("Test został wysłany")
                        val serverToken = Hawk.get<String>("Server_Token")
                        presenter.sendTestToServer(queue!!, serverToken)
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
                    2 -> choosePreviousQuestion()
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
                    2 -> chooseNextQuestion()
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
                    1 -> textToSpeechSingleton?.speakSentence("Udziel odpowiedzi")
                    2 -> {
                        if(getCurrentQuestion()?.answerList?.size!! >0){
                            textToSpeechSingleton?.speakSentence("Uruchamianie modułu wyboru odpowiedzi")
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

    @OnClick(R.id.btn_settings)
    fun goSettings(){
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

    @OnClick(R.id.btn_test)
    fun goTest(){
        clickCountTest++
        object: CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                when (clickCountTest) {
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_T))
                    2 -> textToSpeechSingleton?.speakSentence("Wybór pytania")
                }
                clickCountTest = 0
            }
        }.start()
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


    fun sendClickDetails(x: Long, y: Long, elementId: String, fileId: Int, testId: Int){
        val serverToken = Hawk.get<String>("Server_Token")
        presenter.sendImageClickDataToServer(queue!!, x, y, elementId, fileId, testId, serverToken)
    }

    class WebViewInterface {
        var activity: QuestionActivity? = null

        constructor(activity: QuestionActivity){
            this.activity = activity
        }
        @JavascriptInterface
        fun showDetail(content: String, x: Long, y: Long) {
            Log.e("Kliknięto", "ID: $content x $x y $y")
            activity?.sendClickDetails(x, y, content, activity?.svgImage?.svgId!!, activity?.test?.testId!!)
            activity?.clickCount =  activity?.clickCount!! + 1
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
}