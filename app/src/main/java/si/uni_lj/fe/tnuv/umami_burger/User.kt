package si.uni_lj.fe.tnuv.umami_burger

data class User(
    val profilePicture: String,
    val name: String,
    val numberOfPosts: Int,
    val uid: String
)