package main.se.nopurpo.twitteroscbridge.entity

class TweetInfo {

    data class Tweet(
       val created_at: String,
       val text: String,
       val user: User,
       val retweeted_status: RetweetStatus?
    )

    data class User(

        val name: String?,
        val screen_name: String,
        val location: String?
    )

    data class RetweetStatus (
        val created_at: String
    )

}