package services

import model.entities.{Event, Market, Selection, Sport}

import scala.util.matching.Regex

class SearchFilterService {
  def searchSportsByName (sports:List[Sport],regex: Regex) = {

    val filteredSelections = filterByNameRegex(sports, regex, (sport: Sport) => sport.name.value)
    println(filteredSelections)
  }

  def searchEventsByName (events:List[Event],regex: Regex) = {

    val filteredSelections = filterByNameRegex(events, regex, (event: Event) => event.name.value)
    println(filteredSelections)
  }
  def searchMarketsByName (markets:List[Market],regex: Regex) = {

    val filteredSelections = filterByNameRegex(markets, regex, (market: Market) => market.name.value)
    println(filteredSelections)
  }
  def searchSelectionsByName (selections:List[Selection],regex: Regex) = {

    val filteredSelections = filterByNameRegex(selections, regex, (selections: Selection) => selections.name.value)
    println(filteredSelections)
  }

  def filterByNameRegex[T](entities: List[T], regex: Regex, nameExtractor: T => String): List[T] = {
    entities.filter(entity => regex.findFirstIn(nameExtractor(entity)).isDefined)
  }

  //All (sports/events/markets) with a name satisfying a particular regex
}
