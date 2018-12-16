package main.se.nopurpo.twitteroscbridge

import com.google.common.collect.Lists
import com.google.gson.Gson
import com.illposed.osc.*
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.HttpHosts
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.OAuth1
import main.se.nopurpo.twitteroscbridge.entity.TweetInfo
import java.net.InetAddress
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

private const val SONIC_PI_OSC_PORT = 4559

fun main(args: Array<String>) {

    var gson = Gson()

    val msgQueue = LinkedBlockingQueue<String>(100000)

    val hosebirdClient = TwitterInterface().connect(msgQueue, "svpol")

    val osc = OSCPortOut(InetAddress.getByName("localhost"), SONIC_PI_OSC_PORT)

    while(!hosebirdClient.isDone) {
        val msg = msgQueue.take()

        var tweet = gson.fromJson(msg, TweetInfo.Tweet::class.java)

        System.out.println("\n\nNEW TWEET")
        System.out.println("Timestamp: "+ tweet.created_at)
        System.out.println(String.format("Author handle: %s\nAuthor name: %s\nText: %s", tweet.user.name, tweet.user.screen_name, tweet.text))

        if (tweet.text.contains("@")) {
            osc.send(OSCMessage("/trigger/positive_event"))
        } else {
            osc.send(OSCMessage("/trigger/negative_event"))
        }
    }

    System.out.println("alright")
}
