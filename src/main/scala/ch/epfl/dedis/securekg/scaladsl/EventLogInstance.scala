package ch.epfl.dedis.securekg.scaladsl

import ch.epfl.dedis.lib.eventlog.Event
import ch.epfl.dedis.lib.byzcoin.{ByzCoinRPC, InstanceId}
import ch.epfl.dedis.lib.byzcoin.darc.{DarcId, Signer}

import scala.util.Try

class EventLogInstance private[scaladsl] (
    val underlying: ch.epfl.dedis.lib.byzcoin.contracts.EventLogInstance,
    val darc: DarcId
) {

  def id: InstanceId = underlying.getInstanceId

  def log(events: TraversableOnce[Event], signers: Signer*): Try[Seq[InstanceId]] = {
    import scala.collection.JavaConverters._

    val javaEvents = events.toSeq.asJava
    val javaSigners = signers.asJava

    for {
      resultKeys <- Try( underlying.log(javaEvents, darc, javaSigners) )
    } yield resultKeys.asScala
  }

  def log(event: Event, signers: Signer*): Try[InstanceId] = {
    for {
      resultKeys <- log(List(event), signers: _*)
    } yield resultKeys.head
  }

  def get(key: InstanceId): Try[Event] = {
    Try( underlying.get( key ) )
  }

  //TODO: Map search method to get a nice single iterable

}

object EventLogInstance {
  def apply(ol: ByzCoinRPC, signers: Seq[Signer], darcId: DarcId): EventLogInstance = {
    import scala.collection.JavaConverters._
    val javaSigners = signers.asJava
    val underlying = new ch.epfl.dedis.lib.byzcoin.contracts.EventLogInstance(ol, javaSigners, darcId)
    new EventLogInstance(underlying, darcId)
  }
}
