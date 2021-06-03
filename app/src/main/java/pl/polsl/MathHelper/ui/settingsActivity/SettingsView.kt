package pl.polsl.MathHelper.ui.settingsActivity

interface SettingsView {
    fun showMessage(resId: Int)
    fun showMessage(message: String?)
}