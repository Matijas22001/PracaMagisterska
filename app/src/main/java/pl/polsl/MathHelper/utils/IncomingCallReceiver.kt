package pl.polsl.MathHelper.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.sip.SipAudioCall
import android.net.sip.SipProfile

/**
* Listens for incoming SIP calls, intercepts and hands them off to WalkieTalkieActivity.
*/
class IncomingCallReceiver : BroadcastReceiver() {

    /**
     * Processes the incoming call, answers it, and hands it over to the
     * WalkieTalkieActivity.
     * @param context The context under which the receiver is running.
     * @param intent The intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        //val wtActivity = context as WalkieTalkieActivity
//
        //var incomingCall: SipAudioCall? = null
        //try {
        //    incomingCall = wtActivity.sipManager?.takeAudioCall(intent, listener)
        //    incomingCall?.apply {
        //        answerCall(30)
        //        startAudio()
        //        setSpeakerMode(true)
        //        if (isMuted) {
        //            toggleMute()
        //        }
        //        wtActivity.call = this
        //        wtActivity.updateStatus(this)
        //    }
        //} catch (e: Exception) {
        //    incomingCall?.close()
        //}
    }

    private val listener = object : SipAudioCall.Listener() {

        override fun onRinging(call: SipAudioCall, caller: SipProfile) {
            try {
                call.answerCall(30)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}