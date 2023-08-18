import model.entities.{Event, EventStatus, EventType, Sport}
import model.entities.ValueClasses.{DisplayName, Name, Order, Slug}
import services.{CommandLineService, PersistenceService}

object SportsManagement extends App{
println("Starting SportsManagement App")

  val persistenceService = new PersistenceService
  val commandLineService = new CommandLineService(persistenceService)

  //test first
  //Create sport
  //Create event
  //Create market
  //Create Selection
  //persistance
  //update just set to active inactive
  //delete
  //search
  while (true) {
    println("Select an action:")
    println("1. Add Sport")
    println("2. View All")
    println("3. Fill with sample data")
    println("9. Exit")
    val choice = scala.io.StdIn.readLine()

    choice match {
      case "1" => commandLineService.addSport()
      case "2" => commandLineService.viewAllSports()
      case "3" => commandLineService.fillWithSampleData()
      case "9" =>
        persistenceService.safelySaveSports(commandLineService.AllSports.toList)
        System.exit(0)
      case _ => println("Invalid choice")
    }
  }
  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    override def run(): Unit = {
      println("Saving sports data on exit...")
      persistenceService.safelySaveSports(commandLineService.AllSports.toList)
    }
  }))

}