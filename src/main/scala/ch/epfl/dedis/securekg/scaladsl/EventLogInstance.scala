package ch.epfl.dedis.securekg.scaladsl

import java.time.Instant

import ch.epfl.dedis.byzcoin.{ByzCoinRPC, InstanceId}
import ch.epfl.dedis.eventlog
import ch.epfl.dedis.eventlog.Event
import ch.epfl.dedis.lib.darc.{DarcId, Signer}

import scala.collection.JavaConverters._
import scala.util.Try

class EventLogInstance private[scaladsl] (
    val underlying: ch.epfl.dedis.eventlog.EventLogInstance,
) {
  import scala.collection.JavaConverters._

  def log(events: TraversableOnce[Event], signers: TraversableOnce[Signer], counters: TraversableOnce[java.lang.Long]): Try[Seq[InstanceId]] = {

    val javaEvents = events.toSeq.asJava
    val javaSigners = signers.toSeq.asJava
    val javaCounters = counters.toSeq.asJava

    for {
      resultKeys <- Try( underlying.log(javaEvents, javaSigners, javaCounters) )
    } yield resultKeys.asScala
  }

  def log(event: Event, signers: TraversableOnce[Signer], counters: TraversableOnce[java.lang.Long]): Try[InstanceId] = {
    for {
      resultKeys <- log(List(event), signers, counters)
    } yield resultKeys.head
  }

  def get(key: InstanceId): Try[Event] = {
    Try( underlying.get( key ) )
  }

  def findAll(topic: String, from: Instant, to: Instant): List[Event] =
    underlying
      .search( topic, toNanos(from), toNanos(to))
      .events.asScala.toList

  private def toNanos(instant: Instant): Long = instant.toEpochMilli * 1000 * 1000

  //TODO: Map search method to get a nice single iterable
}

object EventLogInstance {
  def apply(bc: ByzCoinRPC, el: InstanceId): EventLogInstance = {
    val underlying = eventlog.EventLogInstance.fromByzcoin(bc, el)
    new EventLogInstance(underlying)
  }

  def apply(bc: ByzCoinRPC, darcBaseID: DarcId, signers: List[Signer], signerCtrs: List[java.lang.Long]): EventLogInstance = {
    val underlying = new eventlog.EventLogInstance(bc, darcBaseID, signers.asJava, signerCtrs.asJava)
    new EventLogInstance(underlying)
  }
  val contractID = eventlog.EventLogInstance.ContractId
  val logCmd = eventlog.EventLogInstance.LogCmd
}
