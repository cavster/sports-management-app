package services

import model.entities.{Event, Market, Selection, Sport}
import services.SportsAppPrinter.{printEvent, printMarket, printSelection, printSport}

import scala.util.matching.Regex

class SearchFilterService {
  def searchSportsByName (sports:List[Sport],regex: Regex) = {

    val filteredSelections: Seq[Sport] = filterByNameRegex(sports, regex, (sport: Sport) => sport.name.value)
    filteredSelections.foreach(printSport)

  }

  def searchEventsByName (events:List[Event],regex: Regex) = {

    val filteredSelections = filterByNameRegex(events, regex, (event: Event) => event.name.value)
    filteredSelections.foreach(ev => println(printEvent(ev)))
  }
  def searchMarketsByName (markets:List[Market],regex: Regex) = {

    val filteredSelections = filterByNameRegex(markets, regex, (market: Market) => market.name.value)
    filteredSelections.foreach(ma => println(printMarket(ma)))
  }
  def searchSelectionsByName (selections:List[Selection],regex: Regex) = {

    val filteredSelections = filterByNameRegex(selections, regex, (selections: Selection) => selections.name.value)
    filteredSelections.foreach(sel => println(printSelection(sel)))
  }


  def filterByNameRegex[T](entities: List[T], regex: Regex, nameExtractor: T => String): List[T] = {
    entities.filter(entity => regex.findFirstIn(nameExtractor(entity)).isDefined)
  }
}
