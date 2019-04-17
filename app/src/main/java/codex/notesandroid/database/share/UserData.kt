package codex.notesandroid.database.share

/**
 * Created by AksCorp on 04.02.2018.
 *
 * Shared Pref structure description
 */
object UserData {
    val NAME = "UserData"

    object FIELDS {
        val LAST_USER_ID = "lastUser"
        val LAST_USER_TOKEN = "lastUserToken"
        val PROFILE_ICON = "profileIcon"
        val LAST_SYNC = "lastSync"
    }
}