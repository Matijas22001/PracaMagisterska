package pl.polsl.shapeofworld.utils

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.example.myapplication.vals.CustomSharedPreferencesValues
import com.example.myapplication.vals.CustomSharedPreferencesValues.CURRENT_DIRECTORY_ID
import com.example.myapplication.vals.CustomSharedPreferencesValues.CURRENT_DIRECTORY_NAME
import com.example.myapplication.vals.CustomSharedPreferencesValues.EXERCISES_MENU_CURRENT_POSITION
import com.example.myapplication.vals.CustomSharedPreferencesValues.EXERCISE_CURRENT_DIRECTORY_ID
import com.example.myapplication.vals.CustomSharedPreferencesValues.EXERCISE_LAST_ITEM_POSITION
import com.example.myapplication.vals.CustomSharedPreferencesValues.EXERCISE_PREVIOUS_DIRECTORY_ID
import com.example.myapplication.vals.CustomSharedPreferencesValues.LAST_ITEM_POSITION
import com.example.myapplication.vals.CustomSharedPreferencesValues.LEARNING_MODE
import com.example.myapplication.vals.CustomSharedPreferencesValues.MAIN_MENU_CURRENT_POSITION
import com.example.myapplication.vals.CustomSharedPreferencesValues.PREVIOUS_DIRECTORY_ID
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_FILE_ID
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_FILE_NAME
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_OBJECT
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_QUESTION_INDEX
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_TEST_ID
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_TEST_ID_CONCATENATED
import com.example.myapplication.vals.CustomSharedPreferencesValues.SELECTED_USER
import com.example.myapplication.vals.CustomSharedPreferencesValues.SHOW_TESTING_SVG
import com.example.myapplication.vals.CustomSharedPreferencesValues.SPEECH_SPEED
import com.example.myapplication.vals.CustomSharedPreferencesValues.START_TEST_TIMESTAMP
import com.example.myapplication.vals.CustomSharedPreferencesValues.SVG_RADIUS
import com.example.myapplication.vals.CustomSharedPreferencesValues.SVG_STROKE_WIDTH
import com.example.myapplication.vals.CustomSharedPreferencesValues.TESTS_MENU_CURRENT_POSITION
import timber.log.Timber

class CustomSharedPreferences(context: Context?) {
    private val sharedPref: SharedPreferences
    private val editor: SharedPreferences.Editor
    fun resetVariables() {
        Timber.i("[CustomSharedPreferences] Called resetVariables ")
        lastItemPosition = -1
        setCurrentDirectoryId("")
        setPreviousDirectoryId("")
        selectedTestId = 0
        setSelectedTestIdConcatenated("")
        selectedQuestionIndex = 0
        setSelectedFileId("")
        setSelectedUser("")
        setSelectedFileName("")
        testsMenuCurrentPosition = 0
        exercisesMenuCurrentPosition = 0
        setMainMenuCurrentPosition("")
    }

    fun resetVariablesQuestion() {
        Timber.i("[CustomSharedPreferences] Called resetVariablesQuestion ")
        selectedQuestionIndex = 0
    }

    var lastItemPosition: Int
        get() {
            val lastItemPosition = sharedPref.getInt(CustomSharedPreferencesValues.LAST_ITEM_POSITION, -1)
            Timber.i("[CustomSharedPreferences] Called getLastItemPosition $lastItemPosition")
            return lastItemPosition
        }
        set(value) {
            Timber.i("[CustomSharedPreferences] Called setLastItemPosition $value")
            editor.putInt(LAST_ITEM_POSITION, value)
            editor.commit()
        }
    val currentDirectoryId: String?
        get() {
            val currentDirectoryId = sharedPref.getString(CURRENT_DIRECTORY_ID, "")
            Timber.i("[CustomSharedPreferences] Called getCurrentDirectoryId $currentDirectoryId")
            return currentDirectoryId
        }

    fun setCurrentDirectoryId(currentDirectoryId: String) {
        Timber.i( "[CustomSharedPreferences] Called setCurrentDirectoryId $currentDirectoryId")
        editor.putString(CURRENT_DIRECTORY_ID, currentDirectoryId)
        editor.commit()
    }

    val currentDirectoryName: String?
        get() {
            val currentDirectoryName = sharedPref.getString(CURRENT_DIRECTORY_NAME, "")
            Timber.i("[CustomSharedPreferences] Called getCurrentDirectoryName $currentDirectoryName")
            return currentDirectoryName
        }

    fun setCurrentDirectoryName(currentDirectoryName: String) {
        Timber.i( "[CustomSharedPreferences] Called setCurrentDirectoryName $currentDirectoryName")
        editor.putString(CURRENT_DIRECTORY_NAME, currentDirectoryName)
        editor.commit()
    }

    val previousDirectoryId: String?
        get() {
            val previousDirectoryId = sharedPref.getString(PREVIOUS_DIRECTORY_ID, "")
            Timber.i( "[CustomSharedPreferences] Called getPreviousDirectoryId $previousDirectoryId")
            return previousDirectoryId
        }

    fun setPreviousDirectoryId(previousDirectoryId: String) {
        Timber.i( "[CustomSharedPreferences] Called setPreviousDirectoryId $previousDirectoryId")
        editor.putString(PREVIOUS_DIRECTORY_ID, previousDirectoryId)
        editor.commit()
    }

    var exerciseLastItemPosition: Int
        get() {
            val exerciseLastItemPosition = sharedPref.getInt(EXERCISE_LAST_ITEM_POSITION, -1)
            Timber.i( "[CustomSharedPreferences] Called getLastItemPosition $exerciseLastItemPosition")
            return exerciseLastItemPosition
        }
        set(exerciseLastItemPosition) {
            Timber.i("[CustomSharedPreferences] Called setLastItemPosition $exerciseLastItemPosition")
            editor.putInt(EXERCISE_LAST_ITEM_POSITION, exerciseLastItemPosition)
            editor.commit()
        }
    val exerciseCurrentDirectoryId: String?
        get() {
            val exerciseCurrentDirectoryId = sharedPref.getString(EXERCISE_CURRENT_DIRECTORY_ID, "")
            Timber.i( "[CustomSharedPreferences] Called getExerciseCurrentDirectoryId $exerciseCurrentDirectoryId")
            return exerciseCurrentDirectoryId
        }

    fun setExerciseCurrentDirectoryId(exerciseCurrentDirectoryId: String) {
        Timber.i("[CustomSharedPreferences] Called setCurrentDirectoryId $exerciseCurrentDirectoryId")
        editor.putString(EXERCISE_CURRENT_DIRECTORY_ID, exerciseCurrentDirectoryId)
        editor.commit()
    }

    val exercisePreviousDirectoryId: String?
        get() {
            val exercisePreviousDirectoryId = sharedPref.getString(EXERCISE_PREVIOUS_DIRECTORY_ID, "")
            Timber.i("[CustomSharedPreferences] Called getExercisePreviousDirectoryId $exercisePreviousDirectoryId")
            return exercisePreviousDirectoryId
        }

    fun setExercisePreviousDirectoryId(exercisePreviousDirectoryId: String) {
        Timber.i("[CustomSharedPreferences] Called setPreviousDirectoryId $exercisePreviousDirectoryId")
        editor.putString(EXERCISE_PREVIOUS_DIRECTORY_ID, exercisePreviousDirectoryId)
        editor.commit()
    }

    val learningMode: String?
        get() {
            val learningMode = sharedPref.getString(LEARNING_MODE, "LEARNING")
            Timber.i( "[CustomSharedPreferences] Called getLearningMode $learningMode")
            return learningMode
        }

    fun setLearningMode(learningMode: String) {
        Timber.i("[CustomSharedPreferences] Called setLearningMode $learningMode")
        editor.putString(LEARNING_MODE, learningMode)
        editor.commit()
    }

    val selectedFileId: String?
        get() {
            val selectedFileId = sharedPref.getString(SELECTED_FILE_ID, "")
            Timber.i( "[CustomSharedPreferences] Called getSelectedFileId $selectedFileId")
            return selectedFileId
        }

    fun setSelectedFileId(selectedFileId: String) {
        Timber.i( "[CustomSharedPreferences] Called setSelectedFileId $selectedFileId")
        editor.putString(SELECTED_FILE_ID, selectedFileId)
        editor.commit()
    }

    var selectedQuestionIndex: Int
        get() {
            val selectedQuestionIndex = sharedPref.getInt(SELECTED_QUESTION_INDEX, 0)
            Timber.i( "[CustomSharedPreferences] Called getSelectedQuestionIndex $selectedQuestionIndex")
            return selectedQuestionIndex
        }
        set(selectedQuestionIndex) {
            Timber.i( "[CustomSharedPreferences] Called setSelectedQuestionIndex $selectedQuestionIndex")
            editor.putInt(SELECTED_QUESTION_INDEX, selectedQuestionIndex)
            editor.commit()
        }
    val selectedQuestionIndexForXPath: Int
        get() {
            val selectedQuestionIndex = sharedPref.getInt(SELECTED_QUESTION_INDEX, 1)
            Timber.i( "[CustomSharedPreferences] Called getSelectedQuestionIndex $selectedQuestionIndex")
            return selectedQuestionIndex + 1
        }
    val selectedFileName: String?
        get() {
            val selectedfileName = sharedPref.getString(SELECTED_FILE_NAME, "")
            Timber.i( "[CustomSharedPreferences] Called selectedfileName $selectedfileName")
            return selectedfileName
        }

    fun setSelectedFileName(selectedFileName: String) {
        Timber.i("[CustomSharedPreferences] Called selectedfileName $selectedFileName")
        editor.putString(SELECTED_FILE_NAME, selectedFileName)
        editor.commit()
    }

    var speechSpeed: Float
        get() {
            val speechSpeed = sharedPref.getFloat(SPEECH_SPEED, 1f)
            Timber.i("[CustomSharedPreferences] Called getSpeechSpeed $speechSpeed")
            return speechSpeed
        }
        set(speechSpeed) {
            Timber.i("[CustomSharedPreferences] Called setSpeechSpeed $speechSpeed")
            editor.putFloat(SPEECH_SPEED, speechSpeed)
            editor.commit()
        }
    val selectedUser: String?
        get() {
            val selectedUser = sharedPref.getString(SELECTED_USER, "")
            Timber.i( "[CustomSharedPreferences] Called getSelectedUser $selectedUser")
            return selectedUser
        }

    fun setSelectedUser(selectedUser: String) {
        Timber.i("[CustomSharedPreferences] Called setSelectedUser $selectedUser")
        editor.putString(SELECTED_USER, selectedUser)
        editor.commit()
    }

    fun setSelectedObjectId(selectedObjectId: String) {
        Timber.i( "[CustomSharedPreferences] Called setSelectedObjectId $selectedObjectId")
        editor.putString(SELECTED_OBJECT, selectedObjectId)
        editor.commit()
    }

    val selectedObjectId: String?
        get() {
            val selectedObject = sharedPref.getString(SELECTED_OBJECT, "")
            Timber.i( "[CustomSharedPreferences] Called getSelectedObjectId $selectedObject")
            return selectedObject
        }
    var selectedTestId: Int
        get() {
            val selectedTestId = sharedPref.getInt(SELECTED_TEST_ID, 0)
            Timber.i( "[CustomSharedPreferences] Called getSelectedTestId $selectedTestId")
            return selectedTestId
        }
        set(selectedTestId) {
            Timber.i( "[CustomSharedPreferences] Called setSelectedTestId $selectedTestId")
            editor.putInt(SELECTED_TEST_ID, selectedTestId)
            editor.commit()
        }

    fun setSelectedTestIdConcatenated(selectedTestId: String) {
        Timber.i( "[CustomSharedPreferences] Called setSelectedTestIdConcatenated $selectedTestId")
        editor.putString(SELECTED_TEST_ID_CONCATENATED, selectedTestId)
        editor.commit()
    }

    val selectedTestIdConcatenated: String?
        get() {
            val selectedTestId = sharedPref.getString(SELECTED_TEST_ID_CONCATENATED, "")
            Timber.i( "[CustomSharedPreferences] Called getSelectedTestIdConcatenated $selectedTestId")
            return selectedTestId
        }

    fun setStartTestTimestamp(startTestTimestamp: String) {
        Timber.i( "[CustomSharedPreferences] Called setStartTestTimestamp $startTestTimestamp")
        editor.putString(START_TEST_TIMESTAMP, startTestTimestamp)
        editor.commit()
    }

    val startTestTimestamp: String?
        get() {
            val startTestTimestamp = sharedPref.getString(START_TEST_TIMESTAMP, "")
            Timber.i("[CustomSharedPreferences] Called getStartTestTimestamp $startTestTimestamp")
            return startTestTimestamp
        }
    var exercisesMenuCurrentPosition: Int
        get() {
            val exercisesMenuCurrentPosition = sharedPref.getInt(EXERCISES_MENU_CURRENT_POSITION, 0)
            Timber.i( "[CustomSharedPreferences] Called getExercisesMenuCurrentPosition $exercisesMenuCurrentPosition")
            return exercisesMenuCurrentPosition
        }
        set(exercisesMenuCurrentPosition) {
            Timber.i( "[CustomSharedPreferences] Called setExercisesMenuCurrentPosition $exercisesMenuCurrentPosition")
            editor.putInt(EXERCISES_MENU_CURRENT_POSITION, exercisesMenuCurrentPosition)
            editor.commit()
        }
    var testsMenuCurrentPosition: Int
        get() {
            val testsMenuCurrentPosition = sharedPref.getInt(TESTS_MENU_CURRENT_POSITION, 0)
            Timber.i( "[CustomSharedPreferences] Called getTestsMenuCurrentPosition $testsMenuCurrentPosition")
            return testsMenuCurrentPosition
        }
        set(testsMenuCurrentPosition) {
            Timber.i( "[CustomSharedPreferences] Called setTestsMenuCurrentPosition $testsMenuCurrentPosition")
            editor.putInt(TESTS_MENU_CURRENT_POSITION, testsMenuCurrentPosition)
            editor.commit()
        }

    fun addDirectoryToMainMenuCurrentPosition(mainMenuCurrentPosition: Int) {
        Timber.i( "[CustomSharedPreferences] Called addDirectoryToMainMenuCurrentPosition $mainMenuCurrentPosition")
        var mainMenuCurrentPositionPath = sharedPref.getString(MAIN_MENU_CURRENT_POSITION, "")
        mainMenuCurrentPositionPath = "$mainMenuCurrentPositionPath$mainMenuCurrentPosition;"
        editor.putString(MAIN_MENU_CURRENT_POSITION, mainMenuCurrentPositionPath)
        editor.commit()
    }

    fun setMainMenuCurrentPosition(mainMenuCurrentPosition: String) {
        Timber.i("[CustomSharedPreferences] Called setMainMenuCurrentPosition $mainMenuCurrentPosition")
        editor.putString(MAIN_MENU_CURRENT_POSITION, mainMenuCurrentPosition)
        editor.commit()
    }

    val mainMenuCurrentPosition: Int
        get() {
            var result: Int? = 0
            var mainMenuCurrentPositionPathOriginal = sharedPref.getString(MAIN_MENU_CURRENT_POSITION, "")
            if (mainMenuCurrentPositionPathOriginal!!.isEmpty()) {
                result = 0
            } else {
                val endOfCurrentPositionIndex = mainMenuCurrentPositionPathOriginal.lastIndexOf(";")
                var startOfCurrentPositionIndex = 0
                val mainMenuCurrentPositionPathModified: String
                val resultString: String?
                if (endOfCurrentPositionIndex == -1) {
                    resultString = mainMenuCurrentPositionPathOriginal
                    result = Integer.valueOf(resultString!!)
                    mainMenuCurrentPositionPathOriginal = ""
                } else {
                    mainMenuCurrentPositionPathModified = mainMenuCurrentPositionPathOriginal.substring(0, endOfCurrentPositionIndex)
                    startOfCurrentPositionIndex = mainMenuCurrentPositionPathModified.lastIndexOf(";")
                    if (startOfCurrentPositionIndex != -1) {
                        resultString = mainMenuCurrentPositionPathOriginal.substring(startOfCurrentPositionIndex + 1, endOfCurrentPositionIndex)
                        result = Integer.valueOf(resultString)
                        mainMenuCurrentPositionPathOriginal = mainMenuCurrentPositionPathOriginal.substring(0, startOfCurrentPositionIndex + 1)
                    } else {
                        result = Integer.valueOf(mainMenuCurrentPositionPathOriginal.substring(0, endOfCurrentPositionIndex))
                        mainMenuCurrentPositionPathOriginal = ""
                    }
                }
            }
            Timber.i( "[CustomSharedPreferences] Called getMainMenuCurrentPosition $mainMenuCurrentPositionPathOriginal")
            editor.putString(MAIN_MENU_CURRENT_POSITION, mainMenuCurrentPositionPathOriginal)
            editor.commit()
            return result
        }
    var svgStrokeWidth: Int
        get() {
            val svgStrokeWidth = sharedPref.getInt(SVG_STROKE_WIDTH, 15)
            Timber.i("[CustomSharedPreferences] Called getSvgStrokeWidth $svgStrokeWidth")
            return svgStrokeWidth
        }
        set(svgStrokeWidth) {
            Timber.i( "[CustomSharedPreferences] Called setSvgStrokeWidth $svgStrokeWidth")
            editor.putInt(SVG_STROKE_WIDTH, svgStrokeWidth)
            editor.commit()
        }
    var svgRadius: Int
        get() {
            val svgRadius = sharedPref.getInt(SVG_RADIUS, 8)
            Timber.i( "[CustomSharedPreferences] Called getSvgRadius $svgRadius")
            return svgRadius
        }
        set(svgRadius) {
            Timber.i( "[CustomSharedPreferences] Called setSvgStrokeWidth $svgRadius")
            editor.putInt(SVG_RADIUS, svgRadius)
            editor.commit()
        }
    var showTestingSVG: Boolean
        get() {
            val showTestingSVG = sharedPref.getBoolean(SHOW_TESTING_SVG, false)
            Timber.i("[CustomSharedPreferences] Called getShowTestingSVG $showTestingSVG")
            return showTestingSVG
        }
        set(showTestingSVG) {
            Timber.i("[CustomSharedPreferences] Called setShowTestingSVG $showTestingSVG")
            editor.putBoolean(SHOW_TESTING_SVG, showTestingSVG)
            editor.commit()
        }

    init {
        Timber.i(javaClass.name + " [CustomSharedPreferences] Called Constructor")
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        editor = sharedPref.edit()
    }
}