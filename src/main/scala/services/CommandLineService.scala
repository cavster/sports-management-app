package services

import model.entities.{Event, EventStatus, EventType, Market, Selection, Sport}
import model.entities.ValueClasses.{Columns, DisplayName, Name, Order, Outcome, Price, Schema, Slug}
import services.SampleData.createSampleData

class CommandLineService(persistenceService:PersistenceService) {
  val sportsFromFile =  persistenceService.loadSportsFromFile()

  var AllSports =  scala.collection.mutable.ListBuffer.empty[Sport] ++ sportsFromFile


  def addSport(): Unit = {
    println("Enter sport name:")
    val sportName = Name(scala.io.StdIn.readLine())

    println("Enter sport display name:")
    val sportDisplayName = DisplayName(scala.io.StdIn.readLine())

    println("Enter sport slug:")
    val sportSlug = Slug(scala.io.StdIn.readLine())

    println("Enter sport order:")
    val sportOrder = Order(    safelyReadInt("Enter sport order:"))

    val sport = Sport(sportName, sportDisplayName, sportSlug, sportOrder)
    AllSports += sport

    var events: List[Event] = Nil
    while (true) {
      val event = addEvent()
      events = event :: events

      println("Add another event? (yes/no):")
      val addAnotherEvent = scala.io.StdIn.readLine().toLowerCase
      if (addAnotherEvent != "yes") {
        AllSports += sport.copy(events = events)
        return
      }
    }
    println(AllSports)
  }


  def addEvent(): Event = {
    println("Enter event name:")
    val eventName = Name(scala.io.StdIn.readLine())

    val eventType = safelyReadEnum(EventType, "Select event type (preplay or inplay):")
    val eventStatus = safelyReadEnum(EventStatus, "Select event status (Preplay, Inplay, Ended):")


    println("Enter event slug:")
    val eventSlug = Slug(scala.io.StdIn.readLine())

    var markets: List[Market] = Nil
    while (true) {
      val market = addMarket()
      markets = market :: markets
      println(market)
      println("Add another market? (yes/no):")
      val addAnotherMarket = scala.io.StdIn.readLine().toLowerCase
      if (addAnotherMarket != "yes") {
        return Event(eventName, eventType, eventStatus, eventSlug, markets.reverse)
      }
    }
    println(AllSports)
    Event(eventName, eventType, eventStatus, eventSlug, markets.reverse)
  }

  def addMarket(): Market = {
    println("Enter market name:")
    val marketName = Name(scala.io.StdIn.readLine())

    println("Enter market display name:")
    val marketDisplayName = DisplayName(scala.io.StdIn.readLine())


    val marketOrder = Order(    safelyReadInt("Enter market order:"))


    val marketSchema = Schema(  safelyReadInt("Enter market schema:"))


    val numColumns = Columns(  safelyReadInt("Enter number of columns:"))

    var selections: List[Selection] = Nil
    while (true) {
      val selection = addSelection()
      selections = selection :: selections
       println(selection)
      println("Add another selection? (yes/no):")
      val addAnotherSelection = scala.io.StdIn.readLine().toLowerCase
      if (addAnotherSelection != "yes") {
        return Market(marketName, marketDisplayName, marketOrder, marketSchema, numColumns, selections.reverse)
      }
    }
    println(AllSports)
    Market(marketName, marketDisplayName, marketOrder, marketSchema, numColumns, selections.reverse)
  }


  def addSelection(): Selection = {
    println("Enter selection name:")
    val selectionName = Name(scala.io.StdIn.readLine())

    val selectionPrice = Price(  safelyReadInt("Enter selection price (in pennies):"))

    println("Is the selection active? (true/false):")
    val selectionActive = scala.io.StdIn.readBoolean()

    println("Enter selection outcome:")
    val selectionOutcome = Outcome(scala.io.StdIn.readLine())

    Selection(selectionName, selectionPrice, selectionActive, selectionOutcome)
  }
  def viewAllSports(): Unit = {
    if (AllSports.isEmpty) {
      println("No sports available.")
    } else {
      println("All Sports:")
      AllSports.foreach { sport =>

        println(s"Sport : ${SportsAppPrinter.printSport(sport)}")
        sport.events.foreach { event =>
          println(s"  Event : ${SportsAppPrinter.printEvent(event)}")
          event.markets.foreach { market =>
            println(s"    Market : ${SportsAppPrinter.printMarket(market)}")
            market.selections.foreach { selection =>
              println(s"      Selection : ${{SportsAppPrinter.printSelection(selection)}}")
            }
          }
        }
      }
    }
  }
  def safelyReadInt(prompt: String): Int = {
    try {
      println(prompt)
      scala.io.StdIn.readInt()
    } catch {
      case _: NumberFormatException =>
        println("Invalid input. Please enter a valid integer.")
        safelyReadInt(prompt)
    }
  }
  def safelyReadEnum[E <: Enumeration](enum: E, prompt: String): E#Value = {
    try {
      println(prompt)
      val input = scala.io.StdIn.readLine()
      enum.withName(input)
    } catch {
      case _: NoSuchElementException =>
        println("Invalid input. Please select a valid option.")
        safelyReadEnum(enum, prompt)
    }
  }

  def fillWithSampleData () = AllSports ++= createSampleData
}

