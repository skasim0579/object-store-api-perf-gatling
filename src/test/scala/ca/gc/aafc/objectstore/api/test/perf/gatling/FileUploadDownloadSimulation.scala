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
import java.io._

class FileUploadDownloadSimulation extends Simulation {
  var fileId: String = null;
  var bucket: String = "mybucket2";
  var jsonFileInStr: String = "";
  val url = getClass.getResource("/metadata1.json")

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def saveFileId(identifier: String): Unit = {
    fileId = identifier;
    println("fileIdentifier: " + identifier)
    println("fileId: " + fileId)
    if (url != null) {
      val source = Source.fromURL(url)
      jsonFileInStr = source.getLines().mkString
      jsonFileInStr = jsonFileInStr.replaceFirst("bucket1", bucket)
      jsonFileInStr = jsonFileInStr.replaceFirst("fileIdentifier1", fileId)
      println("jsonFileInStr: " + jsonFileInStr)
      val pw = new PrintWriter(new File("metadata2.json"))
      pw.write(jsonFileInStr)
      pw.close
    }
  }

  def writeToFile(response: String): Unit = {
    var writer = null
    try {
      var writer: BufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/home/abdulkadir/gat-ws/mypngfile1.png")))
      writer.write(response)
      writer.close()
    } catch {
      case e: IOException =>
    }
  }
  def serverHost: String = getProperty("server.host", "localhost")
  def serverPort: String = getProperty("server.port", "8081")
  println("serverHost: " + serverHost)
  println("serverPort: " + serverPort)
//    var fileIdentifier: String = "8809ed21-d5ef-4cb2-b51a-9a60ae09a89e"
//    saveFileId(fileIdentifier)

  val httpProtocol = http
    .baseUrl("http://" + serverHost + ":" + serverPort) // Here is the root for all relative URLs

  val scn =
    scenario("post-fileUploadDownload") // A scenario is a chain of requests and pauses
      // -----------------------------
      .exec(http("Post-FileUpload")
        .post("/api/v1/file/" + bucket)
        .header("Content-Type", "image/png")
        .bodyPart(RawFileBodyPart("file", "example-png.png").contentType("image/png").fileName("example-png.png")).asMultipartForm
        .check(status.is(200))
        .check(jsonPath("$.fileIdentifier").saveAs("fileIdentifier")))
      .exec { session => saveFileId(session("fileIdentifier").as[String]); session }
      // -----------------------------
      .exec(http("Post-metadata")
        .post("/api/v1/metadata")
        .header(HttpHeaderNames.ContentType, "application/vnd.api+json")
        .header(HttpHeaderNames.Accept, "application/vnd.api+json")
        .body(RawFileBody("./metadata2.json"))
        .check(status.is(201))
        .check(bodyString.saveAs("responseData2")))
      .exec { session => println("metadata-responseData: " + session("responseData2").as[String]); session }
  // -----------------------------
  //        .exec(http("Get-fileDownload")
  //          .get("/api/v1/file/" + bucket + "/" + fileId)
  //          .check(status.is(200))
  //          .check(bodyString.saveAs("responseBody")))
  //        .exec { session => writeToFile(session("responseBody").as[String]); session }

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}