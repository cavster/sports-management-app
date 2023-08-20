package services

import model.entities.{Event, Market, Selection, Sport}

object SportsAppPrinter {

  def printSelection(selection: Selection): String = {
    s"Selection: ${selection.name.value}, Price: ${selection.price.valueInPennies / 100.0}, Active: ${selection.active}, Outcome: ${selection.outcome.value}"
  }

  def printMarket(market: Market): String = {
    val selections = market.selections.map(printSelection).mkString("\n\t\t")
    s"Market: ${market.name.value}, Active: ${market.active}\n\tSelections:\n\t\t$selections"
  }

  def printEvent(event: Event): String = {
    val markets = event.markets.map(printMarket).mkString("\n\t")
    s"Event: ${event.name.value}, EventType: ${event.eventType}, Status: ${event.status}, Slug: ${event.slug.value}, Active: ${event.active}\n\tMarkets:\n\t$markets"
  }

  def printSport(sport: Sport): Unit = {
    val events = sport.events.map(printEvent).mkString("\n")
    println(s"Sport: ${sport.name.value}, DisplayName: ${sport.displayName.value}, Slug: ${sport.slug.value}, Order: ${sport.order.value}, Active: ${sport.active}\nEvents:\n$events")
  }

}


