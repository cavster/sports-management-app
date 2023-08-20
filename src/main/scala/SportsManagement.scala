import services.{CommandLineService, PersistenceService, SearchFilterService}

object SportsManagement extends App {
  println("Starting SportsManagement App")

  val persistenceService = new PersistenceService
  val searchFilterService = new SearchFilterService
  val commandLineService = new CommandLineService(persistenceService, searchFilterService)


  while (true) {
    println("Select an action:")
    println("1. Add Sport")
    println("2. View All")
    println("3. Fill with sample data")
    println("4. Search Sports by name")
    println("5. Search Events by name")
    println("6. Search Markets by name")
    println("7. Search Selections by name")
    println("8. Update Sport by name")
    println("9. Update Event by name")
    println("10. Update Market by name")
    println("11. Update Selections by name")
    println("12. Delete Sports by name")
    println("13. Delete Event by name")
    println("14. Delete Market by name")
    println("15. Delete Selections by name")
    println("16. Find Sports with min active Events")
    println("17. Find Events with min active Markets")
    println("18. Find Markets with min active Selections")
    println("19. Exit")
    val choice = scala.io.StdIn.readLine()

    choice match {
      case "1" => commandLineService.addSport()
      case "2" => commandLineService.viewAllSports()
      case "3" => commandLineService.fillWithSampleData()
      case "4" => commandLineService.searchSportByName()
      case "5" => commandLineService.searchEventByName()
      case "6" => commandLineService.searchMarketByName()
      case "7" => commandLineService.searchSelectionByName()
      case "8" => commandLineService.updateSportByName()
      case "9" => commandLineService.updateEventByNameInSport()
      case "10" => commandLineService.updateMarketByNameInEvent()
      case "11" => commandLineService.updateSelectionByNameInMarket()
      case "12" => commandLineService.deleteSportByName()
      case "13" => commandLineService.deleteEventByName()
      case "14" => commandLineService.deleteMarketByName()
      case "15" => commandLineService.deleteSelectionByName()
      case "16" => commandLineService.findSportsWithMinActiveEvents()
      case "17" => commandLineService.findEventsWithMinActiveMarkets()
      case "18" => commandLineService.findMarketsWithMinActiveSelections()
      case "19" =>
        persistenceService.safelySaveSports(commandLineService.allSports.toList)
        System.exit(0)
      case _ => println("Invalid choice")
    }
  }
  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    override def run(): Unit = {
      println("Saving sports data on exit...")
      persistenceService.safelySaveSports(commandLineService.allSports.toList)
    }
  }))

}