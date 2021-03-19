package com.example.myapplication.ui.testActivity

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
import com.example.myapplication.R
import com.example.myapplication.model.SvgImage
import com.example.myapplication.model.SvgImageDescription
import com.example.myapplication.model.Test
import com.example.myapplication.model.Tests
import com.example.myapplication.ui.chooseImageSizeActivity.ChooseImageSizeActivity
import com.example.myapplication.ui.questionActivity.QuestionActivity
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivity
import com.example.myapplication.ui.showSvgActivity.ShowSvgActivityPresenter
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.TextToSpeechSingleton
import com.example.myapplication.utils.ViewUtils
import com.google.gson.Gson
import dagger.android.AndroidInjection
import javax.inject.Inject

class TestActivity: AppCompatActivity(), TestActivityNavigator, TestActivityView {
    var textToSpeechSingleton: TextToSpeechSingleton? = null
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
    var tests: Tests? = null
    var testNameList: ArrayList<String>? = null
    var currentlyChosenTestId: Int = 0

    @BindView(R.id.wv_image)
    lateinit var imageWebView: WebView

    @Inject
    lateinit var presenter: TestActivityPresenter

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun initializeWebView(){
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
        imageWebView.loadData(svgImage?.svgXML, "text/html", "utf-8")
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
        Android.showDetail(evt.target.getAttribute("id"));
        }
        ]]> </script>"""
        var content = ""
        try {
            svgImage?.svgXML = svgImage?.svgXML?.substring(0, indexEndOfFirstSvgTag + 1) + javascriptScript + svgImage?.svgXML?.substring(indexEndOfFirstSvgTag + 1)
        } catch (e: Exception) {
            Log.e("SVG Image Converting", e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_svg)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        textToSpeechSingleton = TextToSpeechSingleton(this)
        ViewUtils.fullScreenCall(window)
        svgImage = Gson().fromJson(AppPreferences.chosenTask, SvgImage::class.java)
        svgImageDescription = Gson().fromJson(AppPreferences.chosenTaskDescription, SvgImageDescription::class.java)
        tests = Gson().fromJson(AppPreferences.chosenTaskTests, Tests::class.java)
        if(testNameList==null) testNameList = ArrayList()
        initializeTestList()
        initializeWebView()
    }

    private fun initializeTestList(){
        testNameList?.clear()
        for(item in tests?.testList!!){
            testNameList?.add(item.name!!)
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
                    1 -> textToSpeechSingleton?.speakSentence(resources.getString(R.string.button_home_back))
                    2 -> {
                        val myIntent = Intent(this@TestActivity, ShowSvgActivity::class.java)
                        this@TestActivity.startActivity(myIntent)
                        finish()
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
                    2 -> choosePreviousTest()
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
                    2 -> chooseNextTest()
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
                    1 -> textToSpeechSingleton?.speakSentence("Przejście do modułu pytań")
                    2 ->
                    {
                        if(getCurrentTest()?.questionList?.size!! >0){
                            textToSpeechSingleton?.speakSentence("Uruchamianie modułu odpowiedzi")
                            AppPreferences.chosenTest = Gson().toJson(getCurrentTest())
                            val myIntent = Intent(this@TestActivity, QuestionActivity::class.java)
                            this@TestActivity.startActivity(myIntent)
                            finish()
                        }else{
                            textToSpeechSingleton?.speakSentence("Brak pytań dla tego testu")
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
                        val myIntent = Intent(this@TestActivity, SettingsActivity::class.java)
                        this@TestActivity.startActivity(myIntent)
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
                    2 -> textToSpeechSingleton?.speakSentence("Wybór testu")
                }
                clickCountTest = 0
            }
        }.start()
    }

    var mCountDownTimer: CountDownTimer = object : CountDownTimer(AppPreferences.tapInterval, AppPreferences.tapInterval) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            when (clickCount) {
                1 -> textToSpeechSingleton?.speakSentence("Opisy dostępne po uruchomieniu testu - proszę wybrać test oraz pytanie") //onSingleClick()
                2 -> textToSpeechSingleton?.speakSentence("Opisy dostępne po uruchomieniu testu - proszę wybrać test oraz pytanie")//onDoubleClick()
                3 -> textToSpeechSingleton?.speakSentence("Opisy dostępne po uruchomieniu testu - proszę wybrać test oraz pytanie")//onTripleClick()
                else -> textToSpeechSingleton?.speakSentence("Opisy dostępne po uruchomieniu testu - proszę wybrać test oraz pytanie")//onDoubleClick()
            }
            clickCount = 0
            selectedId = ""
        }
    }

    fun chooseNextTest(){
        val testListSize = testNameList?.size!! - 1
        currentlyChosenTestId += 1
        if(currentlyChosenTestId>testListSize){
            currentlyChosenTestId = 0
        }
        textToSpeechSingleton?.speakSentence("Wybrany test to ${testNameList?.get(currentlyChosenTestId)}")
    }

    fun choosePreviousTest(){
        val testListSize = testNameList?.size!! - 1
        currentlyChosenTestId -= 1
        if(currentlyChosenTestId<0){
            currentlyChosenTestId = testListSize
        }
        textToSpeechSingleton?.speakSentence("Wybrany test to ${testNameList?.get(currentlyChosenTestId)}")
    }

    private fun getCurrentTest(): Test? {
        for(item in tests?.testList!!){
            if(item.name == testNameList?.get(currentlyChosenTestId)){
                return item
            }
        }
        return null
    }

    class WebViewInterface {
        var activity: TestActivity? = null

        constructor(activity: TestActivity){
            this.activity = activity
        }
        @JavascriptInterface
        fun showDetail(content: String) {
            Log.e("Kliknięto", "ID: $content")
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