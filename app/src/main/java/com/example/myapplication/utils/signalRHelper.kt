package com.example.myapplication.utils

import android.app.Activity
import android.os.AsyncTask
import com.example.myapplication.App
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single
import org.linphone.core.tools.Log

class signalRHelper {

    companion object{
        fun signalr(serverToken: String?) {
            App.hubConnection = HubConnectionBuilder.create("http://157.158.57.124:50820/api/testHub").withAccessTokenProvider(
                Single.defer { Single.just(serverToken) }).build()

            App.hubConnection?.on("SessionStart") {
                Log.i("SessionStarted")
                // Przejście aplikacji w tryb zdalny czy coś
            }

            App.hubConnection?.on("SessionEnd") {
                Log.i("SessionEnded")
                // Przeczytanie komunikatu o zakończeniu sesji,
                // powrót do listy użytkowników itp.
            }

            App.hubConnection?.on("StatusChange",{ userName, status ->
                Log.i("StatusChange $userName + $status")
                // Opcjonalna obsluga informacji o zmianie statusu jakiegoś uzytkownika
                // np. uderzenie do api po zaktualizowaną listę uzytkowników online
            },
                String::class.java,
                String::class.java)

            App.hubConnection?.on("Click", { click ->
                Log.i("Click $click")
                // Obsługa wyświetlenia kliknięcia
            }, String::class.java)

            App.hubConnection?.on(
                "ReceiveMessage", { user, message ->  Log.i("message $user + $message") },
                String::class.java,
                String::class.java
            )
            HubConnectionTask().execute(App.hubConnection)
        }

        internal class HubConnectionTask : AsyncTask<HubConnection?, Void?, Void?>() {
            override fun doInBackground(vararg hubConnections: HubConnection?): Void? {
                val hubConnection = hubConnections[0]
                hubConnection?.start()?.blockingAwait()
                return null
            }
        }
        interface SignalRCallbacks{
            fun onSessionStart()
            fun onSessionEnd()
            fun onStatusChange(userName: String, status:String)
            fun onClick(click: String)
            fun onReceiveMessage(userName: String, message:String)
        }
    }
}