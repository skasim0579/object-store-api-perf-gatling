package ca.gc.aafc.objectstore.api.test.perf.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef.{ http, status, _ }
import scala.concurrent.duration._
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import scala.io.Source
import java.io.BufferedReader
import scala.io.Source.fromURL
import java.io.File

class FileUploadDownloadSimulation extends Simulation {
  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def serverHost: String = getProperty("server.host", "localhost")
  def serverPort: String = getProperty("server.port", "8081")
  println("serverHost: " + serverHost)
  println("serverPort: " + serverPort)

  val httpProtocol = http
    .baseUrl("http://" + serverHost + ":" + serverPort) // Here is the root for all relative URLs

  val scn =
    scenario("post-fileUploadDownload") // A scenario is a chain of requests and pauses
      .exec(http("Post-FileUpload")
        .post("/api/v1/file/mybucket2")
        .header("Content-Type", "image/png")
        .bodyPart(RawFileBodyPart("file", "example-png.png").contentType("image/png").fileName("example-png.png")).asMultipartForm
        .check(status.is(200))
        .check(jsonPath("$.fileIdentifier").saveAs("fileIdentifier")))
      .exec { session => println("fileIdentifier: " + session("fileIdentifier").as[String]); session }

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}