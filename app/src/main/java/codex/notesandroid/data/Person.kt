package codex.notesandroid.data

import java.io.Serializable

/**
 * Created by AksCorp on 31.01.2018.
 */

data class Person(
  var id: String?,
  var name: String?,
  var email: String?,
  var photo: String?
) : Serializable {
    override fun equals(
      other: Any?
    ): Boolean {
        return (other as Person?)?.id.equals(id)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}