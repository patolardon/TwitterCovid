package TwitterCovid

import java.util.Properties
import HashtagParser._

import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._


object TwitterCovid extends App {


  // create config
  val config: Properties = {
    val properties = new Properties()
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "TwitterCovidHype")
    val bootstrapServers = if (args.length > 0) args(0) else "localhost:9092"
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, classOf[StringSerde].getName)
    properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[StringSerde].getName)
    properties
  }

  // create topology
  val streamsBuilder = new StreamsBuilder

  //input topic
  val inputTopic: KStream[Array[Byte], String] = streamsBuilder.stream[Array[Byte], String]("twitter_covid")
  // filter
  val filteredStream = inputTopic.filter((k: Array[Byte], jsonTweet: String) => filterHashTag(jsonTweet))
  filteredStream.to("country_tweets")

  // build the topology
  val kafkaStreams = new KafkaStreams(streamsBuilder.build(), config)

  //start the stream
  kafkaStreams.start()

}
