package si.uni_lj.fe.tnuv.umami_burger


data class BurgerPost(
    val userId: String = "",
    var user: User? = null,
    val burgerName: String = "",
    val burgerPrice: Float = 0.0f,
    val burgerPlace: String = "",
    val burgerPattyRating: Float = 0.0f,
    val burgerVeggiesRating: Float = 0.0f,
    val burgerSauceRating: Float = 0.0f,
    val burgerOverallRating: Float = 0.0f,
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    var numberOfLikes: Int = 0,
    val comments: List<UserComment> = emptyList(),
    val nameOfPerson: String = "",
    val wordReview: String = "",
    val isEndIndicator: Boolean = false,
    var isLiked: Boolean = false,
    var likes: MutableMap<String, Boolean> = HashMap(),
    val title: String = ""
)

data class UserComment(
    val userId: String,
    val text: String,
    val timestamp: Long
)