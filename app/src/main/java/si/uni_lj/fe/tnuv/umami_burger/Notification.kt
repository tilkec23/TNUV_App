package si.uni_lj.fe.tnuv.umami_burger

data class Notification(
    val likerUserId: String,  // The ID of the user who liked the post
    val post: BurgerPost,     // The post that was liked
    val timestamp: Long       // The time at which the post was liked
)
