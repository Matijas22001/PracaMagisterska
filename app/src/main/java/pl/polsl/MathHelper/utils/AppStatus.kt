package pl.polsl.MathHelper.utils

import android.content.Context
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.util.Log
import java.lang.Exception


class AppStatus {
    var connectivityManager: ConnectivityManager? = null
    var wifiInfo: NetworkInfo? = null
    var mobileInfo: NetworkInfo? = null
    var connected = false
    val isOnline: Boolean
        get() {
            try {
                connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                val networkInfo = connectivityManager!!.activeNetworkInfo
                connected = networkInfo != null && networkInfo.isAvailable &&
                        networkInfo.isConnected
                return connected
            } catch (e: Exception) {
                println("CheckConnectivity Exception: " + e.message)
                Log.v("connectivity", e.toString())
            }
            return connected
        }

    companion object {
        private val instance = AppStatus()
        var context: Context? = null
        fun getInstance(ctx: Context): AppStatus {
            context = ctx.applicationContext
            return instance
        }
    }
}