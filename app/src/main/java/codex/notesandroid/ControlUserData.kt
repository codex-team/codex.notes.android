package codex.notesandroid

import android.content.Context
import com.auth0.android.jwt.JWT
import codex.notesandroid.data.Content
import codex.notesandroid.data.Person
import codex.notesandroid.data.User
import codex.notesandroid.database.LocalDatabaseAPI
import codex.notesandroid.database.share.UserData
import codex.notesandroid.retrofit.ServerAuthorizationResponse
import codex.notesandroid.utilities.Utilities.Companion.saveImageByURL
import java.io.File

/**
 * Created by AksCorp on 08.03.2018.
 *
 * Control user data (get/put in local database)
 */
class ControlUserData(
  private val db: LocalDatabaseAPI,
  val context: Context
) {

    /**
     * Put information from custom jwt token to database and shared preference
     *
     * @param responseJson server JSON response
     */
    fun initUserInformation(
      responseJson: ServerAuthorizationResponse
    ): User
    {

        val jwt = JWT(responseJson.jwt)
        val token = responseJson.jwt

        val userId = jwt.getClaim("user_id").asString()
        val photoURL = responseJson.photo
        val name = responseJson.name
        val email = jwt.getClaim("email").asString()
        val person = Person(userId, name, email, photoURL)

        val db = LocalDatabaseAPI(context)

        if (db.isPersonExistInDatabase(person)) {
            db.updatePersonInDatabase(person)
        } else
            db.insertPersonInDatabase(person)

        val imageExtension = photoURL.substringAfterLast('.')
        val prefs = context.getSharedPreferences(UserData.NAME, 0)
        prefs.edit().putString(UserData.FIELDS.LAST_USER_TOKEN, token).apply()
        prefs.edit().putString(
            UserData.FIELDS.PROFILE_ICON,
            UserData.FIELDS.PROFILE_ICON + "." + imageExtension
        ).apply()
        prefs.edit().putString(UserData.FIELDS.LAST_USER_ID, userId).apply()

        val path = saveUserProfileIcon(photoURL, imageExtension)
        return User(person, responseJson.jwt, UserData.FIELDS.PROFILE_ICON + "." + imageExtension)
    }

    /**
     * Save profile image in internal directory.
     *
     * Image name is 'profile_icon' + image extension
     *
     * @param imageURL - image URL
     */
    private fun saveUserProfileIcon(
      imageURL: String,
      imageExtension: String
    ): String? {
        val storagePath = context.applicationInfo.dataDir
        val filePath =
            "$storagePath/$IMAGES_DIRECTORY/${UserData.FIELDS.PROFILE_ICON}.$imageExtension"

        // create Image directory if not exist
        val mediaStorageDir = File(storagePath, IMAGES_DIRECTORY)

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        saveImageByURL(imageURL, filePath)
        return filePath
    }

    /**
     * @return content with folders, notes from local database
     */
    fun getContentFromDatabase(): Content {
        val folders = db.getFoldersFromDatabase()
        for (folder in folders) {
            val notes = db.getNotesFromDatabase(folderId = folder.id!!)
            folder.notes = notes
        }

        val content = Content(folders)
        content.rootFolder = content.folders.filter {
            it.isRoot!!
        }.getOrNull(0)

        return content
    }
}