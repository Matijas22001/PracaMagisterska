package pl.polsl.MathHelper.utils

import android.os.AsyncTask
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single
import org.linphone.core.tools.Log


class signalRHelper(signalRCallbacks: SignalRCallbacks) {
    private val mainSignalRCallbacks: SignalRCallbacks = signalRCallbacks


    fun signalr(serverToken: String?) {
        pl.polsl.MathHelper.App.hubConnection = HubConnectionBuilder.create("http://157.158.57.124:50820/api/rmlHub")
            .withAccessTokenProvider(
                Single.defer { Single.just(serverToken) }).build()

        pl.polsl.MathHelper.App.hubConnection?.on("SessionStart") {
            Log.i("SessionStarted")
            mainSignalRCallbacks.onSessionStart()
            // Przejście aplikacji w tryb zdalny czy coś
        }

        pl.polsl.MathHelper.App.hubConnection?.on("SessionEnd") {
            Log.i("SessionEnded")
            mainSignalRCallbacks.onSessionEnd()
            // Przeczytanie komunikatu o zakończeniu sesji,
            // powrót do listy użytkowników itp.
        }

        pl.polsl.MathHelper.App.hubConnection?.on(
            "StatusChange", { userName, status ->
                Log.i("StatusChange $userName + $status")
                mainSignalRCallbacks.onStatusChange(userName, status)
                // Opcjonalna obsluga informacji o zmianie statusu jakiegoś uzytkownika
                // np. uderzenie do api po zaktualizowaną listę uzytkowników online
            },
            String::class.java,
            String::class.java
        )

        pl.polsl.MathHelper.App.hubConnection?.on("Click", { click ->
            Log.i("Click $click")
            mainSignalRCallbacks.onClick(click)
            // Obsługa wyświetlenia kliknięcia
        }, String::class.java)

        HubConnectionTask().execute(pl.polsl.MathHelper.App.hubConnection)
    }

    fun StartSession(studentId: Int){
        pl.polsl.MathHelper.App.hubConnection?.send("StartSession", studentId)
    }

    fun SendClick(click: String){
        pl.polsl.MathHelper.App.hubConnection?.send("SendClick", click)
    }

    fun EndSession(){
        pl.polsl.MathHelper.App.hubConnection?.send("EndSession")
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
    }

}