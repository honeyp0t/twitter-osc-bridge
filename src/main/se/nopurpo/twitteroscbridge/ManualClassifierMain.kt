package main.se.nopurpo.twitteroscbridge;

import com.google.gson.Gson
import com.sun.org.apache.bcel.internal.generic.VariableLengthInstruction
import main.se.nopurpo.twitteroscbridge.entity.TweetInfo
import java.io.File
import java.io.PrintWriter
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * Utility program for creating training set data.
 */
fun main(args: Array<String>) {

    val msgQueue = LinkedBlockingQueue<String>(100000)

    val twitterClient = TwitterInterface().connect(msgQueue, "svpol")

    val gson = Gson()
    val scanner = Scanner(System.`in`)
    val fileWriter = PrintWriter(File(Thread.currentThread().contextClassLoader.getResource("svpol_training_data.csv").toURI()))

    while(!twitterClient.isDone) {
        val msg = msgQueue.take()

        var tweet = gson.fromJson(msg, TweetInfo.Tweet::class.java)

        if (tweet.retweeted_status == null) {

            System.out.println()
            System.out.println()
            val formattedTweet = tweet.text.replace("\n", " ")
                    .replace("@[\\w]+ ", "")
                    .replace("(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})\n", "")

            System.out.println("Text: <" + formattedTweet + ">")
            System.out.print("Classify as positive (p) or negative (n), or skip (enter): ")
            System.out.flush()
            val input = scanner.nextLine()
            System.out.println();

            if (input.isEmpty()) {
                continue
            }

            fileWriter.append(formattedTweet)
            fileWriter.append("|")
            if (input.equals("p")) {
                fileWriter.append("true")
            } else if (input.equals("n")) {
                fileWriter.append("false")
            }
            fileWriter.appendln()

            fileWriter.flush()
        }

    }

}