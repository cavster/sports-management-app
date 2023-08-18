package services

import model.entities.Sport
import model.entities.ValueClasses.{DisplayName, Name, Order, Slug}

import java.io.{File, PrintWriter}
import scala.io.Source

class PersistenceService{
  val dataDir = new File("data")
  if (!dataDir.exists()) {
    dataDir.mkdirs()
  }
  val csvFilePath = s"${dataDir.getAbsolutePath}/sports_data.csv"

  def saveSportsToFile(sports: List[Sport]): Unit = {

    val writer = new PrintWriter(new File(csvFilePath))

    // Write CSV header
    writer.println("Sport Name,Display Name,Slug,Order,Event Name,Event Type,Event Status,Event Slug,Market Name,Market Display Name,Market Order,Market Schema,Num Columns,Selection Name,Selection Price,Selection Active,Selection Outcome")

    sports.foreach { sport =>
      sport.events.foreach { event =>
        event.markets.foreach { market =>
          market.selections.foreach { selection =>
            val line = s"${sport.name.value},${sport.displayName.value},${sport.slug.value},${sport.order.value}," +
              s"${event.name.value},${event.eventType},${event.status},${event.slug.value}," +
              s"${market.name.value},${market.displayName.value},${market.order.value},${market.schema.value},${market.columns.value}," +
              s"${selection.name.value},${selection.price.valueInPennies},${selection.active},${selection.outcome.value}"

            writer.println(line)
          }
        }
      }
    }

    writer.close()
  }


  def loadSportsFromFile(): List[Sport] = {
    val file = new File(csvFilePath)
    if (file.exists()) {
      val lines = Source.fromFile(file).getLines().toList
      println("lines " + lines)
      if (lines.nonEmpty) {
        val header :: dataLines = lines
        val sports = dataLines.map { line =>
          val fields = line.split(",").map(_.trim)
          if (fields.length >= 4) {
            val name = Name(fields(0))
            val displayName = DisplayName(fields(1))
            val slug = Slug(fields(2))
            val order = Order(fields(3).toInt)
            Sport(name, displayName, slug, order)
          } else {
            // Handle invalid lines or missing fields
            // You might want to log an error or handle it in a way that makes sense for your app
            // For now, returning a placeholder Sport with default values
            Sport(Name("asdasd"), DisplayName(""), Slug(""), Order(0))
          }
        }
        sports
      } else {
        Nil
      }
    } else {
      println("No file exists  at " + csvFilePath)
      Nil
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
