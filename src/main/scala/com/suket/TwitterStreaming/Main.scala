package com.suket.TwitterStreaming

import org.apache.log4j.{Logger, Level}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext, Seconds}




object Main {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("TwitterStreaming").setMaster("local[*]")
    
    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials
    System.setProperty("twitter4j.oauth.consumerKey", "uMV8R8ijOAFzEH6XONOJ4MUto")
    System.setProperty("twitter4j.oauth.consumerSecret", "KdLwxBQiWcNEvKjm4lmTkGquvefiqC7HkCPdjdknjio3FVvQVK")
    System.setProperty("twitter4j.oauth.accessToken", "2965768122-2GTtVaxVYHhX2ASM3OkJiTMkeTtJkyxoddvrTEq")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "jHAOvY765IdmxzPLn8gdLuWE5bWgnNTlNSwX1RGH5SMnY")

    val ssc = new StreamingContext(conf, Seconds(1))
    
    val twitterDStream = TwitterDStream(ssc)
    	.map(s => String.format("%16s: %s", "@" + s.getUser.getScreenName, s.getText))
    	
    args match {
      case Array() =>
        // Disable logging to make messages more clear
        Logger.getLogger("org").setLevel(Level.OFF)
        Logger.getLogger("akka").setLevel(Level.OFF)
        twitterDStream.print
      case Array(output) => twitterDStream.saveAsTextFiles(s"output/$output", "txt")
      case _ => throw new IllegalArgumentException("Expecting at most one argument");
    }
    ssc.start()
    ssc.awaitTermination()
  }
}
