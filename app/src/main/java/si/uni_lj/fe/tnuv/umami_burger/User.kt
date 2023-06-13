package si.uni_lj.fe.tnuv.umami_burger

data class User(
    val userId: String = "",
    val userName: String = "",
    val userDescription: String = "",
    val profileImage: String = "",
    val userAccountCreationTime: Long = 0L
)