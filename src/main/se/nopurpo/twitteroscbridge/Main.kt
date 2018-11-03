package main.se.nopurpo.twitteroscbridge

import com.google.common.collect.Lists
import com.google.gson.Gson
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.HttpHosts
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.OAuth1
import main.se.nopurpo.twitteroscbridge.entity.TweetInfo
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

fun main(args: Array<String>) {

    val properties = Properties()
    properties.load(Thread.currentThread().contextClassLoader.getResourceAsStream("application.properties"))

    val apiKey = properties.getProperty("twitter.api_key")
    val apiSecret = properties.getProperty("twitter.api_secret")
    val token = properties.getProperty("twitter.token")
    val tokenSecret = properties.getProperty("twitter.token_secret")

    /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
    val msgQueue = LinkedBlockingQueue<String>(100000)
    val eventQueue = LinkedBlockingQueue<Event>(1000)

    /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
    val hosebirdHosts = HttpHosts(Constants.STREAM_HOST)
    val hosebirdEndpoint = StatusesFilterEndpoint()
    // Optional: set up some followings and track terms
    //val followings = Lists.newArrayList(1234L, 566788L)
    val terms = Lists.newArrayList("svpol")
    //hosebirdEndpoint.followings(followings)
    hosebirdEndpoint.trackTerms(terms)

    // These secrets should be read from a config file
    val hosebirdAuth = OAuth1(apiKey, apiSecret, token, tokenSecret)

    val builder = ClientBuilder()
            .name("Hosebird-Client-01") // optional: mainly for the logs
            .hosts(hosebirdHosts)
            .authentication(hosebirdAuth)
            .endpoint(hosebirdEndpoint)
            .processor(StringDelimitedProcessor(msgQueue))
            .eventMessageQueue(eventQueue) // optional: use this if you want to process client events

    val hosebirdClient = builder.build()
    // Attempts to establish a connection.
    hosebirdClient.connect()

    var gson = Gson()

    while(!hosebirdClient.isDone) {
        val msg = msgQueue.take()

        var tweet = gson.fromJson(msg, TweetInfo.Tweet::class.java)

        System.out.println("\n\nNEW TWEET")
        System.out.println("Timestamp: "+ tweet.created_at)
        System.out.println(String.format("Author handle: %s\nAuthor name: %s\nText: %s", tweet.user.name, tweet.user.screen_name, tweet.text))
    }

    System.out.println("alright")
}
