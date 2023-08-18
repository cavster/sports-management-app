import model.entities.ValueClasses._
import model.entities._
import org.scalatest.funsuite.AnyFunSuite

class ModelSpec extends AnyFunSuite {
  test("Sport should add event correctly") {
    val sport = Sport(Name("Football"), DisplayName("Soccer"), Slug("football"), Order(1))
    val event = Event(Name("Match 1"), EventType.preplay, EventStatus.Preplay, Slug("match-1"))

    val sportWithEvent = sport.addEvent(event)

    assert(sportWithEvent.events.contains(event))
  }

  test("Event should correctly determine if it's active") {
    val selection1 = Selection(Name("Selection 1"), Price(100), active = true, Outcome("Outcome 1"))
    val market1 = Market(Name("Market 1"), DisplayName("Market 1"), Order(1), Schema(1), Columns(2), List(selection1))
    val market2 = Market(Name("Market 2"), DisplayName("Market 2"), Order(2), Schema(1), Columns(2), Nil)
    val eventWithActiveMarket = Event(Name("Match 1"), EventType.preplay, EventStatus.Preplay, Slug("match-1"), List(market1))
    val eventWithInactiveMarket = Event(Name("Match 2"), EventType.preplay, EventStatus.Preplay, Slug("match-2"), List(market2))

    assert(eventWithActiveMarket.active)
    assert(!eventWithInactiveMarket.active)
  }

  test("Market should correctly determine if it's active") {
    val selection1 = Selection(Name("Selection 1"), Price(100), active = true, Outcome("Outcome 1"))
    val selection2 = Selection(Name("Selection 2"), Price(200), active = false, Outcome("Outcome 2"))
    val marketWithActiveSelection = Market(Name("Market 1"), DisplayName("Market 1"), Order(1), Schema(1), Columns(2), List(selection1))
    val marketWithInactiveSelection = Market(Name("Market 2"), DisplayName("Market 2"), Order(2), Schema(1), Columns(2), List(selection2))

    assert(marketWithActiveSelection.active)
    assert(!marketWithInactiveSelection.active)
  }
}
