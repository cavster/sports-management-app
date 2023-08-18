package services

import model.entities.ValueClasses._
import model.entities._

object SampleData {
  def createSampleData(): List[Sport] = {
    val selection1 = Selection(Name("Team A"), Price(150), active = true, Outcome("Win"))
    val selection2 = Selection(Name("Team B"), Price(200), active = true, Outcome("Lose"))

    val market1 = Market(Name("Match Winner"), DisplayName("Match Winner"), Order(1), Schema(123), Columns(2), List(selection1, selection2))
    val market2 = Market(Name("Over/Under"), DisplayName("Over/Under"), Order(2), Schema(124), Columns(2), List(selection1, selection2))

    val event1 = Event(Name("Football Match"), EventType.preplay, EventStatus.Preplay, Slug("football-match"), List(market1))
    val event2 = Event(Name("Tennis Match"), EventType.preplay, EventStatus.Preplay, Slug("tennis-match"), List(market2))

    val sport1 = Sport(Name("Soccer"), DisplayName("Soccer"), Slug("soccer"), Order(1), List(event1))
    val sport2 = Sport(Name("Tennis"), DisplayName("Tennis"), Slug("tennis"), Order(2), List(event2))

    List(sport1, sport2)
  }
}
