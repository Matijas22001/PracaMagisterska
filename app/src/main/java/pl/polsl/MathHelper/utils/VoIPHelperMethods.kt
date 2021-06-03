package pl.polsl.MathHelper.utils

import pl.polsl.MathHelper.App.Companion.core
import org.linphone.core.Factory
import org.linphone.core.MediaEncryption

class VoIPHelperMethods {
    companion object{
        private fun unregister() {
            // Here we will disable the registration of our Account
            val account = core.defaultAccount
            account ?: return

            val params = account.params
            // Returned params object is const, so to make changes we first need to clone it
            val clonedParams = params.clone()

            // Now let's make our changes
            clonedParams.registerEnabled = false

            // And apply them
            account.params = clonedParams
        }

        private fun delete() {
            // To completely remove an Account
            val account = core.defaultAccount
            account ?: return
            core.removeAccount(account)

            // To remove all accounts use
            core.clearAccounts()

            // Same for auth info
            core.clearAllAuthInfo()
        }

        fun outgoingCall(userName: String) {
            //val username = "5001"
            val domain = "157.158.57.43"
            // As for everything we need to get the SIP URI of the remote and convert it to an Address
            val remoteSipUri = "sip:$userName@$domain"
            val remoteAddress = Factory.instance().createAddress(remoteSipUri)
            remoteAddress ?: return // If address parsing fails, we can't continue with outgoing call process

            // We also need a CallParams object
            // Create call params expects a Call object for incoming calls, but for outgoing we must use null safely
            val params = core.createCallParams(null)
            params ?: return // Same for params

            // We can now configure it
            // Here we ask for no encryption but we could ask for ZRTP/SRTP/DTLS
            params.mediaEncryption = MediaEncryption.None
            // If we wanted to start the call with video directly
            //params.enableVideo(true)

            // Finally we start the call
            core.inviteAddressWithParams(remoteAddress, params)
            // Call process can be followed in onCallStateChanged callback from core listener
        }

    }
}