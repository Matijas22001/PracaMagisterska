package pl.polsl.MathHelper.ui.showSvgActivity

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
import pl.polsl.MathHelper.model.SvgImage
import pl.polsl.MathHelper.model.SvgImageDescription
import pl.polsl.MathHelper.model.Tests
import pl.polsl.MathHelper.ui.chooseTaskActivity.ChooseTaskActivity
import pl.polsl.MathHelper.ui.settingsActivity.SettingsActivity
import pl.polsl.MathHelper.ui.testActivity.TestActivity
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.ViewUtils
import pl.polsl.MathHelper.utils.VolleySingleton
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjection
import javax.inject.Inject

class ShowSvgActivity: AppCompatActivity(), ShowSvgActivityView,ShowSvgActivityNavigator {

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
    var svgImage: SvgImage? = null
    var svgImageDescription: SvgImageDescription? = null
    var tests: Tests? = null

    @BindView(R.id.wv_image)
    lateinit var imageWebView: WebView

    @Inject
    lateinit var presenter: ShowSvgActivityPresenter

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun initializeWebView(){
        textToSpeechSingleton?.speakSentence("Obecny moduł to widok zadania")
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

    fun sendClickDetails(x: Long, y: Long, elementId: String, fileId: Int){
        val serverToken = Hawk.get<String>("Server_Token")
        presenter.sendImageClickDataToServer(queue!!, x, y, elementId, fileId, serverToken)
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
        tests = Gson().fromJson(AppPreferences.chosenTaskTests, Tests::class.java)
        initializeWebView()
    }

    override fun onResume() {
        super.onResume()
        initializeWebView()
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
                        val myIntent = Intent(this@ShowSvgActivity, ChooseTaskActivity::class.java)
                        this@ShowSvgActivity.startActivity(myIntent)
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
                    2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
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
                    2 -> textToSpeechSingleton?.speakSentence("Brak modułu do wykonania przejścia")
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

    @OnClick(R.id.btn_settings)
    fun goSettings(){
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

    @OnClick(R.id.btn_test)
    fun goTest(){
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

    class WebViewInterface {
        var activity: ShowSvgActivity? = null

        constructor(activity: ShowSvgActivity){
            this.activity = activity
        }
        @JavascriptInterface
        fun showDetail(content: String, x: Long, y: Long) {
            Log.e("Kliknięto", "ID: $content x $x y $y")
            activity?.sendClickDetails(x, y, content, activity?.svgImage?.svgId!!)
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