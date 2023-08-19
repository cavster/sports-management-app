package model.entities

import model.entities.EventStatus.EventStatus
import model.entities.EventType.EventType
import model.entities.ValueClasses.{Columns, DisplayName, Name, Order, Outcome, Price, Schema, Slug}
import play.api.libs.json.{Json, Reads, Writes}

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

case class Sport(name: Name, displayName: DisplayName, slug: Slug, order: Order, events: List[Event] = Nil) {
  val active: Boolean = events.exists(_.active)

  def addEvent(event: Event): Sport = copy(events = event :: events)
}
case class Event(name: Name, eventType: EventType, status: EventStatus, slug: Slug, markets: List[Market] = Nil) {
  val active: Boolean = markets.exists(_.active)
}

case class Market(name: Name, displayName: DisplayName, order: Order, schema: Schema, columns: Columns, selections: List[Selection] = Nil) {
  val active: Boolean = selections.exists(_.active)
}

case class Selection(name: Name, price: Price, active: Boolean, outcome: Outcome)

object EnumUtils {
  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] =
    Reads.enumNameReads(enum)
}
object MyJsonFormats {
  // Define implicit Reads for Selection, Market, Event, and Sport

  implicit val nameReads: Reads[Name] = Reads.of[String].map(Name)
  implicit val displayNameReads: Reads[DisplayName] = Reads.of[String].map(DisplayName)
  implicit val slugReads: Reads[Slug] = Reads.of[String].map(Slug)
  implicit val orderReads: Reads[Order] = Reads.of[Int].map(Order)
  implicit val priceReads: Reads[Price] = Reads.of[Int].map(Price)
  implicit val outcomeReads: Reads[Outcome] =Reads.of[String].map(Outcome)
  implicit val columnReads: Reads[Columns] =Reads.of[Int].map(Columns)
  implicit val schemaReads: Reads[Schema] =Reads.of[Int].map(Schema)

  implicit val eventTypeReads: Reads[EventType.Value] = EnumUtils.enumReads(EventType)
  implicit val eventStatusReads: Reads[EventStatus.Value] = EnumUtils.enumReads(EventStatus)

  implicit val nameWrites: Writes[Name] = Writes.of[String].contramap(_.value)
  implicit val displayNameWrites: Writes[DisplayName] = Writes.of[String].contramap(_.value)
  implicit val slugWrites: Writes[Slug] = Writes.of[String].contramap(_.value)
  implicit val orderWrites: Writes[Order] = Writes.of[Int].contramap(_.value)
  implicit val priceWrites: Writes[Price] =  Writes.of[Int].contramap(_.valueInPennies)
  implicit val outcomeWrites: Writes[Outcome] = Writes.of[String].contramap(_.value)
  implicit val columnWrites: Writes[Columns] = Writes.of[Int].contramap(_.value)
  implicit val schemaWrites: Writes[Schema] = Writes.of[Int].contramap(_.value)


  implicit val selectionReads: Reads[Selection] = Json.reads[Selection]
  implicit val marketReads: Reads[Market] = Json.reads[Market]
  implicit val eventReads: Reads[Event] = Json.reads[Event]
  implicit val sportReads: Reads[Sport] = Json.reads[Sport]

  // Define implicit Writes for Selection, Market, Event, and Sport
  implicit val selectionWrites: Writes[Selection] = Json.writes[Selection]
  implicit val marketWrites: Writes[Market] = Json.writes[Market]
  implicit val eventWrites: Writes[Event] = Json.writes[Event]
  implicit val sportWrites: Writes[Sport] = Json.writes[Sport]
}



