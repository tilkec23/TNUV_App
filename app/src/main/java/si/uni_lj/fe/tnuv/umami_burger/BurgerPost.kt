package si.uni_lj.fe.tnuv.umami_burger

data class BurgerPost(
    val nameOfPerson: String,
    val wordReview: String,
    val numberOfLikes: Int,
    val numberOfComments: Int,
    val comments: List<Comment>,
    val location: Location,
    val nameOfBurger: String,
    val priceOfBurger: Float,
    val postTimestamp: Long,
    val ratingScore: RatingScore,
    val reviewID: Int
) {
    data class Comment(
        val commenterName: String,
        val comment: String,
        val commentTimestamp: Long
    )

    data class Location(
        val coordinates: Pair<Double, Double>,
        val placeId: String
    )

    data class RatingScore(
        val pattyRating: Float,
        val veggieRating: Float,
        val sauceRating: Float,
        val overallRating: Float,
        val calculatedRating: Float
    )
}
