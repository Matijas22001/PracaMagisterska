package pl.polsl.MathHelper

import android.app.Application
import android.content.Context
import com.microsoft.signalr.HubConnection
import com.orhanobut.hawk.Hawk
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import org.linphone.core.Core
import org.linphone.core.Factory
import pl.polsl.MathHelper.utils.AppPreferences
import pl.polsl.MathHelper.utils.TextToSpeechSingleton
import pl.polsl.MathHelper.view_binding.DaggerAppComponent
import pl.polsl.MathHelper.view_binding.HawkWrapper
import javax.inject.Inject
import android.media.AudioManager





class App : Application(), HasAndroidInjector {
    companion object{
        var textToSpeechSingleton: TextToSpeechSingleton? = null
        var hubConnection: HubConnection? = null
        lateinit var core: Core

    }

    @JvmField
    @Inject
    var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>? = null
    override fun onCreate() {
        super.onCreate()
        textToSpeechSingleton = TextToSpeechSingleton(this)
        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        core = factory.createCore(null, null, this)
        AppPreferences.init(this)
        core.isNativeRingingEnabled = true
        core.ring = null
        core.ringback = null
        HawkWrapper.init(applicationContext)
        Hawk.put("Is_Logged_In", false)
        Hawk.put("Is_In_Call",false)
        if(Hawk.contains("Teacher_phone_number")){ Hawk.delete("Teacher_phone_number") }
        if(Hawk.contains("Student_phone_number")){ Hawk.delete("Student_phone_number") }
        if(Hawk.contains("Currently_chosen_user_id")){ Hawk.delete("Currently_chosen_user_id") }
        DaggerAppComponent.builder()
            .application(this)
            ?.build()
            ?.inject(this)

    }

    override fun androidInjector(): AndroidInjector<Any>? {
        return dispatchingAndroidInjector
    }



}