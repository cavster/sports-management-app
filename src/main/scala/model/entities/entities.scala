package model.entities

import model.entities.EventStatus.EventStatus
import model.entities.EventType.EventType
import model.entities.ValueClasses.{Columns, DisplayName, Name, Order, Outcome, Price, Schema, Slug}

object EventType extends Enumeration {
  type EventType = Value
  val preplay, inplay = Value
}
object EventStatus extends Enumeration {
  type EventStatus = Value
  val Preplay, Inplay, Ended = Value
}


object ValueClasses {
  case class Name(value: String) extends AnyVal

  case class DisplayName(value: String) extends AnyVal

  case class Order(value: Int) extends AnyVal

  case class Schema(value: Int) extends AnyVal

  case class Columns(value: Int) extends AnyVal


  //Btw I am aware of the dangers of using a Double to represent money  ;)
  case class Price(valueInPennies: Int) extends AnyVal

  case class Outcome(value: String) extends AnyVal

  case class Slug(value: String) extends AnyVal


}

case class Selection(name: Name, price: Price, active: Boolean, outcome: Outcome)


case class Market(name: Name, displayName: DisplayName, order: Order, schema: Schema, columns: Columns, selections: List[Selection] = Nil) {
  val active: Boolean = selections.exists(_.active)
}

case class Event(name: Name, eventType: EventType, status: EventStatus, slug: Slug, markets: List[Market] = Nil) {
  val active: Boolean = markets.exists(_.active)
}

case class Sport(name: Name, displayName: DisplayName, slug: Slug, order: Order, events: List[Event] = Nil) {
  val active: Boolean = events.exists(_.active)

  def addEvent(event: Event): Sport = copy(events = event :: events)
}
