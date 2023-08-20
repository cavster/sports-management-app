package services

import model.entities.Sport
import play.api.libs.json._

import java.io.{File, PrintWriter}

class PersistenceService {

  import model.entities.MyJsonFormats._

  val dataDir = new File("data")
  if (!dataDir.exists()) {
    dataDir.mkdirs()
  }
  val jsonFilePath = s"${dataDir.getAbsolutePath}/sports_data.json"

  def sportsToJson(sports: List[Sport]): JsValue = Json.toJson(sports)

  def saveSportsToFile(sports: List[Sport]): Unit = {
    val jsonData = sportsToJson(sports)
    val jsonString = Json.prettyPrint(jsonData)

    val writer = new PrintWriter(new File(jsonFilePath))
    try {
      writer.write(jsonString)
    } finally {
      writer.close()
    }
  }

  def loadSportsFromFile(): List[Sport] = {
    val source = scala.io.Source.fromFile(jsonFilePath)
    try {
      val jsonString = source.mkString
      val json = Json.parse(jsonString)
      json.as[List[Sport]]
    } finally {
      source.close()
    }
  }


  def safelySaveSports(sports: List[Sport]): Unit = {
    try {
      saveSportsToFile(sports)
    } catch {
      case e: Exception =>
        println("An error occurred while saving sports data to file.")
        e.printStackTrace()
    }
  }
}
