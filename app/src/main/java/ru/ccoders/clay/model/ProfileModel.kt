import org.json.JSONObject

data class ProfileModel(
    val id: Int,
    val username: String,
    val altName: String,
    val bio: String,
    val followers: Int,
    val followed: Int,

    ) {
    companion object {
        fun parseJSON(jsonObject: JSONObject): ProfileModel {
            return ProfileModel(
                jsonObject.getInt("id"),
                jsonObject.getString("username"),
                jsonObject.getString("alternative_name"),
                jsonObject.getString("bio"),
                jsonObject.getInt("followers"),
                jsonObject.getInt("followed"),
            )
        }
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().put("id", id)
            .put("username", username)
            .put("alternative_name", altName)
            .put("bio", bio)
            .put("followers",followers)
            .put("followed",followed)
    }
}