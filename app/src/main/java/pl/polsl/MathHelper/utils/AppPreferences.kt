package pl.polsl.MathHelper.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "SpinKotlin"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    private val SPEECH_SPEED = Pair("SPEECH_SPEED", 1f)
    private val TAP_INTERVAL = Pair("TAP_INTERVAL", 600L)
    private val CURRENTLY_CHOSEN_USER = Pair("CURRENTLY_CHOSEN_USER", -1)
    private val CURRENT_USER_LIST = Pair("CURRENTL_USER_LIST", "")
    private val USER_ID_IMAGE_ID = Pair("USER_ID_IMAGE_ID", "")
    private val IMAGE_LIST_SERIALIZED = Pair("IMAGE_LIST_SERIALIZED", "")
    private val DESCRIPTION_LIST_SERIALIZED = Pair("DESCRIPTION_LIST_SERIALIZED", "")
    private val TEST_LIST_SERIALIZED = Pair("TEST_LIST_SERIALIZED", "")
    //private val CURRENTLY_CHOSEN_SUBJECT = Pair("CURRENTLY_CHOSEN_SUBJECT", 0)
    private val CURRENTLY_CHOSEN_SECTION = Pair("CURRENTLY_CHOSEN_SECTION", "")
    private val CURRENTLY_CHOSEN_TASK = Pair("CURRENTLY_CHOSEN_TASK", "")
    private val CURRENTLY_CHOSEN_TASK_ID = Pair("CURRENTLY_CHOSEN_TASK_ID", -1)
    private val CURRENTLY_CHOSEN_TASK_DESCRIPTION = Pair("CURRENTLY_CHOSEN_TASK_DESCRIPTION", "")
    private val CURRENTLY_CHOSEN_TASK_TESTS = Pair("CURRENTLY_CHOSEN_TASK_TESTS", "")
    private val CURRENTLY_CHOSEN_TASK_TEST = Pair("CURRENTLY_CHOSEN_TASK_TEST", "")
    private val CURRENTLY_CHOSEN_QUESTION = Pair("CURRENTLY_CHOSEN_QUESTION", "")
    private val CURRENTLY_CHOSEN_IMAGE_SIZE = Pair("CURRENTLY_CHOSEN_IMAGE_SIZE", 10)
    private val CURRENTLY_CHOSEN_IMAGE_SIZE_ID = Pair("CURRENTLY_CHOSEN_IMAGE_SIZE_ID", -1)
    private val CURRENTLY_CHOSEN_TEST_ID = Pair("CURRENTLY_CHOSEN_TEST_ID", -1)
    private val CURRENTLY_CHOSEN_QUESTION_ID = Pair("CURRENTLY_CHOSEN_QUESTION_ID", -1)
    private val CURRENTLY_CHOSEN_SECTION_ID = Pair("CURRENTLY_CHOSEN_SECTION_ID", -1)
    private val CURRENT_ANSWER_LIST = Pair("CURRENT_ANSWER_LIST", "")
    private val CURRENTLY_CHOSEN_SUBJECT = Pair("CURRENTLY_CHOSEN_QUESTION", "")
    private val CURRENTLY_CHOSEN_SUBJECT_ID = Pair("CURRENTLY_CHOSEN_QUESTION_ID", -1)
    private val IS_USER_LOGGED_IN = Pair("IS_USER_LOGGED_IN", false)
    private val APP_MODE = Pair("APP_MODE", 0)
    private val CURRENTLY_CHOSEN_STUDENT_ID = Pair("CURRENTLY_CHOSEN_STUDENT_ID", -1)
    private val CURRENT_POINTS_NUMBER = Pair("CURRENT_POINTS_NUMBER", 3)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var chosenStudent: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_STUDENT_ID.first, CURRENTLY_CHOSEN_STUDENT_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_STUDENT_ID.first, value)
        }

    var isUserLoggedIn: Boolean
        get() = preferences.getBoolean(IS_USER_LOGGED_IN.first, IS_USER_LOGGED_IN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_USER_LOGGED_IN.first, value)
        }

    var appMode: Int
        get() = preferences.getInt(APP_MODE.first, APP_MODE.second)
        set(value) = preferences.edit {
            it.putInt(APP_MODE.first, value)
        }

    var pointNumber: Int
        get() = preferences.getInt(CURRENT_POINTS_NUMBER.first, CURRENT_POINTS_NUMBER.second)
        set(value) = preferences.edit {
            it.putInt(CURRENT_POINTS_NUMBER.first, value)
        }

    var chosenSubjectId: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_SUBJECT_ID.first, CURRENTLY_CHOSEN_SUBJECT_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_SUBJECT_ID.first, value)
        }

    var chosenSubject: String
        get() = preferences.getString(CURRENTLY_CHOSEN_SUBJECT.first, CURRENTLY_CHOSEN_SUBJECT.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_SUBJECT.first, value)
        }

    var speechSpeed: Float
        get() = preferences.getFloat(SPEECH_SPEED.first, SPEECH_SPEED.second)
        set(value) = preferences.edit {
            it.putFloat(SPEECH_SPEED.first, value)
        }

    var tapInterval: Long
        get() = preferences.getLong(TAP_INTERVAL.first, TAP_INTERVAL.second)
        set(value) = preferences.edit {
            it.putLong(TAP_INTERVAL.first, value)
        }


    var chosenUser: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_USER.first, CURRENTLY_CHOSEN_USER.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_USER.first, value)
        }

    var userList: String
        get() = preferences.getString(CURRENT_USER_LIST.first, CURRENT_USER_LIST.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENT_USER_LIST.first, value)
        }

    var userIdImageIdList: String
        get() = preferences.getString(USER_ID_IMAGE_ID.first, USER_ID_IMAGE_ID.second)!!
        set(value) = preferences.edit {
            it.putString(USER_ID_IMAGE_ID.first, value)
        }

    var imageList: String
        get() = preferences.getString(IMAGE_LIST_SERIALIZED.first, IMAGE_LIST_SERIALIZED.second)!!
        set(value) = preferences.edit {
            it.putString(IMAGE_LIST_SERIALIZED.first, value)
        }

    var descriptionList: String
        get() = preferences.getString(DESCRIPTION_LIST_SERIALIZED.first, DESCRIPTION_LIST_SERIALIZED.second)!!
        set(value) = preferences.edit {
            it.putString(DESCRIPTION_LIST_SERIALIZED.first, value)
        }

    var testList: String
        get() = preferences.getString(TEST_LIST_SERIALIZED.first, TEST_LIST_SERIALIZED.second)!!
        set(value) = preferences.edit {
            it.putString(TEST_LIST_SERIALIZED.first, value)
        }

    var answerList: String
        get() = preferences.getString(CURRENT_ANSWER_LIST.first, CURRENT_ANSWER_LIST.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENT_ANSWER_LIST.first, value)
        }
    
    var chosenSection: String
        get() = preferences.getString(CURRENTLY_CHOSEN_SECTION.first, CURRENTLY_CHOSEN_SECTION.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_SECTION.first, value)
        }

    var chosenTask: String
        get() = preferences.getString(CURRENTLY_CHOSEN_TASK.first, CURRENTLY_CHOSEN_TASK.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_TASK.first, value)
        }

    var chosenTaskDescription: String
        get() = preferences.getString(CURRENTLY_CHOSEN_TASK_DESCRIPTION.first, CURRENTLY_CHOSEN_TASK_DESCRIPTION.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_TASK_DESCRIPTION.first, value)
        }

    var chosenTaskTests: String
        get() = preferences.getString(CURRENTLY_CHOSEN_TASK_TESTS.first, CURRENTLY_CHOSEN_TASK_TESTS.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_TASK_TESTS.first, value)
        }

    var chosenTest: String
        get() = preferences.getString(CURRENTLY_CHOSEN_TASK_TEST.first, CURRENTLY_CHOSEN_TASK_TEST.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_TASK_TEST.first, value)
        }

    var chosenQuestion: String
        get() = preferences.getString(CURRENTLY_CHOSEN_QUESTION.first, CURRENTLY_CHOSEN_QUESTION.second)!!
        set(value) = preferences.edit {
            it.putString(CURRENTLY_CHOSEN_QUESTION.first, value)
        }

    var chosenImageSize: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_IMAGE_SIZE.first, CURRENTLY_CHOSEN_IMAGE_SIZE.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_IMAGE_SIZE.first, value)
        }

    var chosenSectionId: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_SECTION_ID.first, CURRENTLY_CHOSEN_SECTION_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_SECTION_ID.first, value)
        }

    var chosenTaskId: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_TASK_ID.first, CURRENTLY_CHOSEN_TASK_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_TASK_ID.first, value)
        }

    var chosenImageThickness: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_IMAGE_SIZE_ID.first, CURRENTLY_CHOSEN_IMAGE_SIZE_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_IMAGE_SIZE_ID.first, value)
        }

    var chosenTestId: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_TEST_ID.first, CURRENTLY_CHOSEN_TEST_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_TEST_ID.first, value)
        }

    var chosenQuestionId: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_QUESTION_ID.first, CURRENTLY_CHOSEN_QUESTION_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_QUESTION_ID.first, value)
        }

    var chosenAnswerId: Int
        get() = preferences.getInt(CURRENTLY_CHOSEN_TASK_ID.first, CURRENTLY_CHOSEN_TASK_ID.second)
        set(value) = preferences.edit {
            it.putInt(CURRENTLY_CHOSEN_TASK_ID.first, value)
        }

}