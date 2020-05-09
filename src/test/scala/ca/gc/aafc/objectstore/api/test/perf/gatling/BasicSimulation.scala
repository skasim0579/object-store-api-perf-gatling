package ca.gc.aafc.objectstore.api.test.perf.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import java.util.Properties
import scala.io.Source
import java.io.BufferedReader
import scala.io.Source.fromURL

class BasicSimulation extends Simulation {
  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def serverHost: String = getProperty("server.host", "localhost")
  def serverPort: String = getProperty("server.port", "8081")
  //  val url = getClass.getResource("/server.properties")
  //  val properties: Properties = new Properties()
  //
  //  if (url != null) {
  //    val source = Source.fromURL(url)
  //    properties.load(source.bufferedReader())
  //  }
  //  val serverHost = properties.getProperty("server.host")
  //  val serverPort = properties.getProperty("server.port")
  println("serverHost: " + serverHost)
  println("serverPort: " + serverPort)
  val httpProtocol = http
    .baseUrl("http://" + serverHost + ":" + serverPort) // Here is the root for all relative URLs
    .acceptHeader(
      "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn =
    scenario("get-managed-attribute") // A scenario is a chain of requests and pauses
      .exec(
        http("request_1")
          .get("/api/v1/managed-attribute")
          .check(status.is(200)))
      .pause(7) // Note that Gatling has recorder real time pauses

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
