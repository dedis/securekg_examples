package ch.epfl.dedis.securekg.scaladsl

import ch.epfl.dedis.lib.eventlog.Event
import ch.epfl.dedis.lib.omniledger.{InstanceId, OmniledgerRPC, contracts}
import ch.epfl.dedis.lib.omniledger.darc.{DarcId, Signer}

import scala.util.Try

class EventLogInstance private[scaladsl] (
    val underlying: ch.epfl.dedis.lib.omniledger.contracts.EventLogInstance
) {

  def id: InstanceId = underlying.getInstanceId

  def log(events: TraversableOnce[Event], signers: Signer*): Try[Seq[InstanceId]] = {
    import scala.collection.JavaConverters._

    val javaEvents = events.toSeq.asJava
    val javaSigners = signers.asJava

    for {
      resultKeys <- Try( underlying.log(javaEvents, javaSigners) )
    } yield resultKeys.asScala.toSeq
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
  def apply(ol: OmniledgerRPC, signers: Seq[Signer], darcId: DarcId): EventLogInstance = {
    import scala.collection.JavaConverters._
    val javaSigners = signers.asJava
    val underlying = new ch.epfl.dedis.lib.omniledger.contracts.EventLogInstance(ol, javaSigners, darcId)
    new EventLogInstance(underlying)
  }

  def apply(ol: OmniledgerRPC, id: InstanceId): EventLogInstance = {
    val underlying = new ch.epfl.dedis.lib.omniledger.contracts.EventLogInstance(ol, id)
    new EventLogInstance(underlying)
  }
}
