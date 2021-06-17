package pl.polsl.MathHelper.utils

import android.os.AsyncTask
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single
import org.linphone.core.tools.Log
import pl.polsl.MathHelper.App


class signalRHelper(signalRCallbacks: SignalRCallbacks) {
    private val mainSignalRCallbacks: SignalRCallbacks = signalRCallbacks


    fun signalr(serverToken: String?) {
        App.hubConnection = HubConnectionBuilder.create("http://157.158.57.124:50820/api/rmlHub")
            .withAccessTokenProvider(
                Single.defer { Single.just(serverToken) }).build()

        App.hubConnection?.on("SessionStart") {
            Log.i("SessionStarted")
            mainSignalRCallbacks.onSessionStart()
            // Przejście aplikacji w tryb zdalny czy coś
        }

        App.hubConnection?.on("SessionEnd") {
            Log.i("SessionEnded")
            mainSignalRCallbacks.onSessionEnd()
            // Przeczytanie komunikatu o zakończeniu sesji,
            // powrót do listy użytkowników itp.
        }

        App.hubConnection?.on(
            "StatusChange", { userName, status ->
                Log.i("StatusChange $userName + $status")
                mainSignalRCallbacks.onStatusChange(userName, status)
                // Opcjonalna obsluga informacji o zmianie statusu jakiegoś uzytkownika
                // np. uderzenie do api po zaktualizowaną listę uzytkowników online
            },
            String::class.java,
            String::class.java
        )

        App.hubConnection?.on("Click", { click ->
            Log.i("Click $click")
            mainSignalRCallbacks.onClick(click)
            // Obsługa wyświetlenia kliknięcia
        }, String::class.java)

        App.hubConnection?.on("ImageChange", { imageId ->
            Log.i("ImageChange $imageId")
            mainSignalRCallbacks.onImageChange(imageId)
            // Obsługa wyświetlenia kliknięcia
        }, Int::class.java)

        HubConnectionTask().execute(App.hubConnection)
    }

    fun StartSession(studentId: Int){
        App.hubConnection?.send("StartSession", studentId)
    }

    fun SendClick(click: String){
        App.hubConnection?.send("SendClick", click)
    }

    fun EndSession(){
        App.hubConnection?.send("EndSession")
    }

    fun ImageSelect(imageId: Int){
        App.hubConnection?.send("SelectImage", imageId)
    }

    internal class HubConnectionTask : AsyncTask<HubConnection?, Void?, Void?>() {
        override fun doInBackground(vararg hubConnections: HubConnection?): Void? {
            val hubConnection = hubConnections[0]
            hubConnection?.start()?.blockingAwait()
            return null
        }
    }

    interface SignalRCallbacks {
        fun onSessionStart()
        fun onSessionEnd()
        fun onStatusChange(userName: String, status: String)
        fun onClick(click: String)
        fun onImageChange( imageId: Int)
    }

}