package TwitterCovid

import io.circe.Decoder
import play.api.libs.json.Json
import io.circe.parser.decode
import CountryList._
import io.circe.generic.auto._

case object HashtagParser {
  case class Hashtag(Text:String, Start:Int, End:Int) {
    require(Text.length > 0, "the text is not long enough")
  }
  private def decodehashtag(tweet:String): String = Json.parse(tweet)("payload")("HashtagEntities").toString()
  implicit val HashtagDecoder: Decoder[List[Hashtag]] = Decoder[List[Hashtag]]

  def filterHashTag(tweet: String): Boolean ={
    decode(decodehashtag(tweet))(HashtagDecoder)
    match {
      case Left(error) => {
        println(error)
        false}
      case Right(value) => {
        println(value.map(v => v.Text))
        value.map(v => v.Text).count(text => countryList.contains(text.toUpperCase())) > 0}
    }
  }
}
