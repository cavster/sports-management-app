package services

import model.entities.ValueClasses._
import model.entities._
import services.SampleData.createSampleData
import services.SportsAppPrinter.printSport

import scala.collection.mutable.ListBuffer

class CommandLineService(persistenceService: PersistenceService,
                         searchFilterService: SearchFilterService) {
  private val sportsFromFile = persistenceService.loadSportsFromFile()

  var allSports = scala.collection.mutable.ListBuffer.empty[Sport] ++ sportsFromFile


  def addSport(): Unit = {
    println("Enter sport name:")
    val sportName = Name(scala.io.StdIn.readLine())

    println("Enter sport display name:")
    val sportDisplayName = DisplayName(scala.io.StdIn.readLine())

    println("Enter sport slug:")
    val sportSlug = Slug(scala.io.StdIn.readLine())

    println("Enter sport order:")
    val sportOrder = Order(safelyReadInt("Enter sport order:"))

    val sport = Sport(sportName, sportDisplayName, sportSlug, sportOrder)
    allSports += sport

    var events: List[Event] = Nil
    while (true) {
      val event = addEvent()
      events = event :: events

      println("Add another event? (yes/no):")
      val addAnotherEvent = scala.io.StdIn.readLine().toLowerCase
      if (addAnotherEvent != "yes") {
        allSports += sport.copy(events = events)
        return
      }
    }
    println(allSports)
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
    println(allSports)
    Event(eventName, eventType, eventStatus, eventSlug, markets.reverse)
  }

  def addMarket(): Market = {
    println("Enter market name:")
    val marketName = Name(scala.io.StdIn.readLine())

    println("Enter market display name:")
    val marketDisplayName = DisplayName(scala.io.StdIn.readLine())


    val marketOrder = Order(safelyReadInt("Enter market order:"))


    val marketSchema = Schema(safelyReadInt("Enter market schema:"))


    val numColumns = Columns(safelyReadInt("Enter number of columns:"))

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
    println(allSports)
    Market(marketName, marketDisplayName, marketOrder, marketSchema, numColumns, selections.reverse)
  }


  def addSelection(): Selection = {
    println("Enter selection name:")
    val selectionName = Name(scala.io.StdIn.readLine())

    val selectionPrice = Price(safelyReadInt("Enter selection price (in pennies):"))

    println("Is the selection active? (true/false):")
    val selectionActive = scala.io.StdIn.readBoolean()

    println("Enter selection outcome:")
    val selectionOutcome = Outcome(scala.io.StdIn.readLine())

    Selection(selectionName, selectionPrice, selectionActive, selectionOutcome)
  }

  def viewAllSports(): Unit = {
    if (allSports.isEmpty) {
      println("No sports available.")
    } else {
      println("All Sports:")
      allSports.foreach { sport =>

        SportsAppPrinter.printSport(sport)

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

  def searchSportByName() {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r
    searchFilterService.searchSportsByName(allSports.toList, regex)
  }

  def searchEventByName() {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r
    val allEvents = allSports.flatMap(_.events).toList
    searchFilterService.searchEventsByName(allEvents, regex)
  }

  def searchMarketByName() {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r
    val allMarkets: List[Market] = allSports.flatMap(_.events.flatMap(_.markets)).toList
    searchFilterService.searchMarketsByName(allMarkets, regex)
  }

  def searchSelectionByName() {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r
    val allMarkets: List[Market] = allSports.flatMap(_.events.flatMap(_.markets)).toList
    val allSections = allMarkets.flatMap(_.selections)
    searchFilterService.searchSelectionsByName(allSections, regex)
  }

  def deleteSportByName(): Unit = {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r
    val filteredSports = searchFilterService.filterByNameRegex(allSports.toList, regex, (sport: Sport) => sport.name.value)
    filteredSports.foreach { sport =>
      allSports -= sport
    }
  }
  def deleteEventByName(): Unit = {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r

    // Create a mutable list to collect the modified sports
    var modifiedSports = List.empty[Sport]

    allSports.foreach { sport =>
      val filteredEvents = sport.events.filter(event => regex.pattern.matcher(event.name.value).matches())

      if (filteredEvents.nonEmpty) {
        println(s"Deleting events matching pattern '$patternString' in sport '${sport.name.value}':")
        val updatedEvents = sport.events.filterNot(event => regex.pattern.matcher(event.name.value).matches())
        val updatedSport = sport.copy(events = updatedEvents)
        modifiedSports ::= updatedSport
        println(s"${filteredEvents.size} events deleted.")
      }
    }

    // Update the allSports list with the modified sports
    allSports = modifiedSports.reverse.to[ListBuffer]
  }
  def deleteMarketByName(): Unit = {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r

    // Create a mutable list to collect the modified events
    var modifiedEvents = List.empty[Event]

    allSports.foreach { sport =>
      sport.events.foreach { event =>
        val filteredMarkets = event.markets.filter(market => regex.pattern.matcher(market.name.value).matches())

        if (filteredMarkets.nonEmpty) {
          println(s"Deleting markets matching pattern '$patternString' in event '${event.name.value}' of sport '${sport.name.value}':")
          val updatedMarkets = event.markets.filterNot(market => regex.pattern.matcher(market.name.value).matches())
          val updatedEvent = event.copy(markets = updatedMarkets)
          modifiedEvents ::= updatedEvent
          println(s"${filteredMarkets.size} markets deleted.")
        }
      }
    }

    // Update the allSports list with the modified events
    allSports = allSports.map { sport =>
      sport.copy(events = sport.events.filterNot(event => modifiedEvents.exists(_.name == event.name)))
    }
  }
  def deleteSelectionByName(): Unit = {
    println("Enter a regex pattern:")
    val patternString = scala.io.StdIn.readLine()
    val regex = patternString.r

    // Create a mutable list to collect the modified selections
    var modifiedSelections = List.empty[Selection]

    allSports.foreach { sport =>
      sport.events.foreach { event =>
        event.markets.foreach { market =>
          val modifiedMarket = market.copy(
            selections = market.selections.filterNot(selection => regex.pattern.matcher(selection.name.value).matches())
          )

          val modifiedEvent = event.copy(
            markets = event.markets.map { m =>
              if (m.name == market.name) modifiedMarket else m
            }
          )

          val modifiedSport = sport.copy(
            events = sport.events.map { e =>
              if (e.name == event.name) modifiedEvent else e
            }
          )

          modifiedSelections = modifiedSelections ++ market.selections.filter(selection => regex.pattern.matcher(selection.name.value).matches())
          allSports = allSports.map { s =>
            if (s.name == sport.name) modifiedSport else s
          }

          println(s"${modifiedSelections.size} selections deleted.")
        }
      }
    }
  }

  def findSportsWithMinActiveEvents():Unit = {
    println("Enter the minimum number of active events:")
    val threshold = scala.io.StdIn.readInt()

   val sportsWithActiveEvents = allSports.filter { sport =>
      sport.events.count(_.active) >= threshold
    }
    sportsWithActiveEvents.foreach(printSport)
  }


  def findEventsWithMinActiveMarkets(): Unit = {
    println("Enter the minimum number of active markets:")
    val threshold = scala.io.StdIn.readInt()

    allSports.foreach { sport =>
      sport.events.foreach { event =>
        val activeMarketsCount = event.markets.count(_.active)
        if (activeMarketsCount >= threshold) {
          println(s"Event '${event.name.value}' in sport '${sport.name.value}' has $activeMarketsCount active markets.")
        }
      }
    }
  }
  def findMarketsWithMinActiveSelections(): Unit = {
    println("Enter the minimum number of active selections:")
    val threshold = scala.io.StdIn.readInt()

    allSports.foreach { sport =>
      sport.events.foreach { event =>
        event.markets.foreach { market =>
          val activeSelectionsCount = market.selections.count(_.active)
          if (activeSelectionsCount >= threshold) {
            println(s"Market '${market.name.value}' in event '${event.name.value}' of sport '${sport.name.value}' has $activeSelectionsCount active selections.")
          }
        }
      }
    }
  }


  def updateSportByName(): Unit = {
    println("Enter the current name of the sport you want to update:")
    val currentName = Name(scala.io.StdIn.readLine())

    // Find the sport in the list by its current name
    val sportToUpdateOption: Option[Sport] = allSports.find(_.name == currentName)

    sportToUpdateOption match {
      case Some(sportToUpdate) =>
        println(s"Updating Sport: ${sportToUpdate.name.value}")

        // Get the new name from the user
        println("Enter the new name:")
        val newName = Name(scala.io.StdIn.readLine())

        // Update the sport's name
        val updatedSport = sportToUpdate.copy(name = newName)

        // Update the allSports list
        allSports = allSports.map {
          case sport if sport == sportToUpdate => updatedSport
          case otherSport => otherSport
        }

        println(s"Sport updated. New name: ${updatedSport.name.value}")

      case None =>
        println(s"No sport found with the name: $currentName")
    }
  }

  def updateEventByNameInSport(): Unit = {
    println("Enter the name of the sport containing the event you want to update:")
    val sportName = Name(scala.io.StdIn.readLine())

    // Find the sport in the list by its name
    val sportToUpdateOption: Option[Sport] = allSports.find(_.name == sportName)

    sportToUpdateOption match {
      case Some(sportToUpdate) =>
        println(s"Enter the current name of the event you want to update:")
        val currentEventName = Name(scala.io.StdIn.readLine())

        // Find the event within the sport by its current name
        val eventToUpdateOption: Option[Event] = sportToUpdate.events.find(_.name == currentEventName)

        eventToUpdateOption match {
          case Some(eventToUpdate) =>
            println(s"Updating Event: ${eventToUpdate.name.value}")

            // Get the new name from the user
            println("Enter the new name:")
            val newEventName = Name(scala.io.StdIn.readLine())

            // Update the event's name
            val updatedEvent = eventToUpdate.copy(name = newEventName)

            // Update the sport's events list
            val updatedSport = sportToUpdate.copy(events = sportToUpdate.events.map {
              case event if event == eventToUpdate => updatedEvent
              case otherEvent => otherEvent
            })

            // Update the allSports list
            allSports = allSports.map {
              case sport if sport == sportToUpdate => updatedSport
              case otherSport => otherSport
            }

            println(s"Event updated. New name: ${updatedEvent.name.value}")

          case None =>
            println(s"No event found with the name: $currentEventName in the sport: ${sportToUpdate.name.value}")
        }

      case None =>
        println(s"No sport found with the name: $sportName")
    }
  }

  def updateMarketByNameInEvent(): Unit = {
    println("Enter the name of the sport containing the event with the market you want to update:")
    val sportName = Name(scala.io.StdIn.readLine())

    // Find the sport in the list by its name
    val sportToUpdateOption: Option[Sport] = allSports.find(_.name == sportName)

    sportToUpdateOption match {
      case Some(sportToUpdate) =>
        println("Enter the name of the event containing the market you want to update:")
        val eventName = Name(scala.io.StdIn.readLine())

        // Find the event within the sport by its name
        val eventToUpdateOption: Option[Event] = sportToUpdate.events.find(_.name == eventName)

        eventToUpdateOption match {
          case Some(eventToUpdate) =>
            println("Enter the current name of the market you want to update:")
            val currentMarketName = Name(scala.io.StdIn.readLine())

            // Find the market within the event by its current name
            val marketToUpdateOption: Option[Market] = eventToUpdate.markets.find(_.name == currentMarketName)

            marketToUpdateOption match {
              case Some(marketToUpdate) =>
                println(s"Updating Market: ${marketToUpdate.name.value}")

                // Get the new name from the user
                println("Enter the new name:")
                val newMarketName = Name(scala.io.StdIn.readLine())

                // Update the market's name
                val updatedMarket = marketToUpdate.copy(name = newMarketName)

                // Update the event's markets list
                val updatedEvent = eventToUpdate.copy(markets = eventToUpdate.markets.map {
                  case market if market == marketToUpdate => updatedMarket
                  case otherMarket => otherMarket
                })

                // Update the sport's events list
                val updatedSport = sportToUpdate.copy(events = sportToUpdate.events.map {
                  case event if event == eventToUpdate => updatedEvent
                  case otherEvent => otherEvent
                })

                // Update the allSports list
                allSports = allSports.map {
                  case sport if sport == sportToUpdate => updatedSport
                  case otherSport => otherSport
                }

                println(s"Market updated. New name: ${updatedMarket.name.value}")

              case None =>
                println(s"No market found with the name: $currentMarketName in the event: ${eventToUpdate.name.value}")
            }

          case None =>
            println(s"No event found with the name: $eventName in the sport: ${sportToUpdate.name.value}")
        }

      case None =>
        println(s"No sport found with the name: $sportName")
    }
  }
  def updateSelectionByNameInMarket(): Unit = {
    println("Enter the name of the sport containing the event with the market with the selection you want to update:")
    val sportName = Name(scala.io.StdIn.readLine())

    // Find the sport in the list by its name
    val sportToUpdateOption: Option[Sport] = allSports.find(_.name == sportName)

    sportToUpdateOption match {
      case Some(sportToUpdate) =>
        println("Enter the name of the event containing the market with the selection you want to update:")
        val eventName = Name(scala.io.StdIn.readLine())

        // Find the event within the sport by its name
        val eventToUpdateOption: Option[Event] = sportToUpdate.events.find(_.name == eventName)

        eventToUpdateOption match {
          case Some(eventToUpdate) =>
            println("Enter the name of the market containing the selection you want to update:")
            val marketName = Name(scala.io.StdIn.readLine())

            // Find the market within the event by its name
            val marketToUpdateOption: Option[Market] = eventToUpdate.markets.find(_.name == marketName)

            marketToUpdateOption match {
              case Some(marketToUpdate) =>
                println("Enter the current name of the selection you want to update:")
                val currentSelectionName = Name(scala.io.StdIn.readLine())

                // Find the selection within the market by its current name
                val selectionToUpdateOption: Option[Selection] = marketToUpdate.selections.find(_.name == currentSelectionName)

                selectionToUpdateOption match {
                  case Some(selectionToUpdate) =>
                    println(s"Updating Selection: ${selectionToUpdate.name.value}")

                    // Get the new name from the user
                    println("Enter the new name:")
                    val newSelectionName = Name(scala.io.StdIn.readLine())

                    // Update the selection's name
                    val updatedSelection = selectionToUpdate.copy(name = newSelectionName)

                    // Update the market's selections list
                    val updatedMarket = marketToUpdate.copy(selections = marketToUpdate.selections.map {
                      case selection if selection == selectionToUpdate => updatedSelection
                      case otherSelection => otherSelection
                    })

                    // Update the event's markets list
                    val updatedEvent = eventToUpdate.copy(markets = eventToUpdate.markets.map {
                      case market if market == marketToUpdate => updatedMarket
                      case otherMarket => otherMarket
                    })

                    // Update the sport's events list
                    val updatedSport = sportToUpdate.copy(events = sportToUpdate.events.map {
                      case event if event == eventToUpdate => updatedEvent
                      case otherEvent => otherEvent
                    })

                    // Update the allSports list
                    allSports = allSports.map {
                      case sport if sport == sportToUpdate => updatedSport
                      case otherSport => otherSport
                    }

                    println(s"Selection updated. New name: ${updatedSelection.name.value}")

                  case None =>
                    println(s"No selection found with the name: $currentSelectionName in the market: ${marketToUpdate.name.value}")
                }

              case None =>
                println(s"No market found with the name: $marketName in the event: ${eventToUpdate.name.value}")
            }

          case None =>
            println(s"No event found with the name: $eventName in the sport: ${sportToUpdate.name.value}")
        }

      case None =>
        println(s"No sport found with the name: $sportName")
    }
  }






  def fillWithSampleData() = allSports ++= createSampleData
}

