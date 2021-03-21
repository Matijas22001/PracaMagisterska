package com.example.myapplication.ui.userListActivity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.myapplication.adapters.UserListAdapter
import com.example.myapplication.helper_data_containers.ImageIdTestsForImage
import com.example.myapplication.helper_data_containers.UserImageIdsPair
import com.example.myapplication.model.Student
import com.example.myapplication.model.SvgImage
import com.example.myapplication.model.SvgImageDescription
import com.example.myapplication.model.Tests
import com.example.myapplication.ui.mainActivity.MainActivity
import com.example.myapplication.ui.settingsActivity.SettingsActivity
import com.example.myapplication.utils.*
import com.google.gson.Gson
import dagger.android.AndroidInjection
import javax.inject.Inject


class UserListActivity : AppCompatActivity(), UserListActivityView, UserListActivityNavigator {

    private lateinit var linearLayoutManager: LinearLayoutManager
    //var textToSpeechSingleton: TextToSpeechSingleton? = null
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
        presenter.getUserListFromServer(queue!!, studentList!!)
        initializeRecyclerView()
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
                            AppPreferences.chosenUser = currentlyChosenUserID + 1
                            val myIntent = Intent(this@UserListActivity, MainActivity::class.java)
                            this@UserListActivity.startActivity(myIntent)
                            finish()
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
        AppPreferences.chosenImageSize = -1
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