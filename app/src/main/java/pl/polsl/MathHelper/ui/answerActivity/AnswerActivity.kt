package pl.polsl.MathHelper.ui.answerActivity

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
import pl.polsl.MathHelper.App.Companion.textToSpeechSingleton
import pl.polsl.MathHelper.R
import pl.polsl.MathHelper.helper_data_containers.AnswerChosen
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForQuestion
import pl.polsl.MathHelper.helper_data_containers.ChosenAnswersForTest
import pl.polsl.MathHelper.model.*
import pl.polsl.MathHelper.ui.questionActivity.QuestionActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.ViewUtils
import pl.polsl.MathHelper.utils.VolleySingleton
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import javax.inject.Inject

class AnswerActivity: AppCompatActivity(), AnswerActivityNavigator, AnswerActivityView {
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
    var question: Question? = null
    var answerNameList: ArrayList<String>? = null
    var currentlyChosenAnswerId: Int = 0
    var chosenAnswers: ArrayList<AnswerChosen>? = null
    var chosenAnswersForTest: ChosenAnswersForTest? = null
    var queue: RequestQueue? = null


    @BindView(R.id.wv_image)
    lateinit var imageWebView: WebView

    @Inject
    lateinit var presenter: AnswerActivityPresenter

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun initializeWebView(){
        textToSpeechSingleton?.speakSentence("Obecny moduł to zaznaczanie odpowiedzi")
        textToSpeechSingleton?.speakSentence("Zaznaczona odpowiedź to " + answerNameList?.get(currentlyChosenAnswerId))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_svg)
        ButterKnife.bind(this)
        AndroidInjection.inject(this)
        //textToSpeechSingleton = TextToSpeechSingleton(this)
        ViewUtils.fullScreenCall(window)
        queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        svgImage = Gson().fromJson(AppPreferences.chosenTask, SvgImage::class.java)
        svgImageDescription = Gson().fromJson(AppPreferences.chosenTaskDescription, SvgImageDescription::class.java)
        test = Gson().fromJson(AppPreferences.chosenTest, Test::class.java)
        chosenAnswersForTest = Gson().fromJson(AppPreferences.answerList, ChosenAnswersForTest::class.java)
        question = Gson().fromJson(AppPreferences.chosenQuestion, Question::class.java)
        if(chosenAnswers==null) chosenAnswers = ArrayList()
        if(answerNameList==null) answerNameList = ArrayList()
        initializeAnswerList()
        initializeWebView()
    }

    override fun onResume() {
        super.onResume()
        initializeWebView()
    }

    private fun initializeAnswerList(){
        answerNameList?.clear()
        for(item in question?.answerList!!){
            answerNameList?.add(item.answerDescription!!)
            chosenAnswers?.add(AnswerChosen(item.answerId,false))
        }
    }

    private fun checkIfItemIsOnList(): Boolean{
        for(item in chosenAnswersForTest?.listOfQuestions!!){
            if(item.questionId == question?.questionId){
                return true
            }
        }
        return false
    }

    private fun checkItemIdOnList(): ChosenAnswersForQuestion?{
        for(item in chosenAnswersForTest?.listOfQuestions!!){
            if(item.questionId == question?.questionId){
                return item
            }
        }
        return null
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
                        textToSpeechSingleton?.speakSentence("Odpowiedzi zostały zapisane")
                        if(chosenAnswersForTest?.listOfQuestions == null) chosenAnswersForTest?.listOfQuestions = ArrayList()
                        if(checkIfItemIsOnList()){
                            chosenAnswersForTest?.listOfQuestions?.remove(checkItemIdOnList())
                            chosenAnswersForTest?.listOfQuestions?.add(ChosenAnswersForQuestion(question?.questionId,chosenAnswers))
                        }else{
                            chosenAnswersForTest?.listOfQuestions?.add(ChosenAnswersForQuestion(question?.questionId,chosenAnswers))
                        }
                        AppPreferences.answerList = Gson().toJson(chosenAnswersForTest)
                        val myIntent = Intent(this@AnswerActivity, QuestionActivity::class.java)
                        this@AnswerActivity.startActivity(myIntent)
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
                    1 -> textToSpeechSingleton?.speakSentence("Zaznacz odpowiedź")
                    2 -> {
                        chosenAnswers?.get(currentlyChosenAnswerId)?.isChosen = !chosenAnswers?.get(currentlyChosenAnswerId)?.isChosen!!
                        if(chosenAnswers?.get(currentlyChosenAnswerId)?.isChosen!!){
                            textToSpeechSingleton?.speakSentence("Zaznaczono odpowiedź")
                        }else{
                            textToSpeechSingleton?.speakSentence("Odznaczono odpowiedź")
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
                        val myIntent = Intent(this@AnswerActivity, SettingsActivity::class.java)
                        this@AnswerActivity.startActivity(myIntent)
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
            if (item.pathId == selectedId) {
                for(item1 in item.onClickDescriptions!!){
                    if(item1.questionId == question?.questionId &&item1.testId == test?.testId){
                        textToSpeechSingleton?.speakSentence(item1.oneClick)
                        break
                    }
                }
            }
        }
    }

    private fun onDoubleClick() {
        for (item in svgImageDescription?.svgModel!!) {
            if (item.pathId == selectedId) {
                for(item1 in item.onClickDescriptions!!){
                    if(item1.questionId == question?.questionId &&item1.testId == test?.testId){
                        textToSpeechSingleton?.speakSentence(item1.doubleClick)
                        break
                    }
                }
            }
        }
    }

    private fun onTripleClick() {
        for (item in svgImageDescription?.svgModel!!) {
            if (item.pathId == selectedId) {
                for(item1 in item.onClickDescriptions!!){
                    if(item1.questionId == question?.questionId &&item1.testId == test?.testId){
                        textToSpeechSingleton?.speakSentence(item1.tripleClick)
                        break
                    }
                }
            }
        }
    }

    fun chooseNextQuestion(){
        val answerListSize = answerNameList?.size!! - 1
        currentlyChosenAnswerId += 1
        if(currentlyChosenAnswerId>answerListSize){
            currentlyChosenAnswerId = 0
        }
        textToSpeechSingleton?.speakSentence("Wybrana odpowiedź to ${answerNameList?.get(currentlyChosenAnswerId)}")
    }

    fun choosePreviousQuestion(){
        val answerListSize = answerNameList?.size!! - 1
        currentlyChosenAnswerId -= 1
        if(currentlyChosenAnswerId<0){
            currentlyChosenAnswerId = answerListSize
        }
        textToSpeechSingleton?.speakSentence("Wybrany odpowiedź to ${answerNameList?.get(currentlyChosenAnswerId)}")
    }

    private fun getCurrentAnswer(): Answer? {
        for(item in question?.answerList!!){
            if(item.answerDescription == answerNameList?.get(currentlyChosenAnswerId)){
                return item
            }
        }
        return null
    }

    fun sendClickDetails(x: Long, y: Long, elementId: String, fileId: Int, testId: Int, questionId: Int){
        val serverToken = Hawk.get<String>("Server_Token")
        presenter.sendImageClickDataToServer(queue!!, x, y, elementId, fileId, testId, questionId, serverToken)
    }

    class WebViewInterface {
        var activity: AnswerActivity? = null

        constructor(activity: AnswerActivity){
            this.activity = activity
        }
        @JavascriptInterface
        fun showDetail(content: String, x: Long, y: Long) {
            Log.e("Kliknięto", "ID: $content x $x y $y")
            activity?.sendClickDetails(x, y, content, activity?.svgImage?.svgId!!, activity?.test?.testId!!, activity?.question?.questionId!!)
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