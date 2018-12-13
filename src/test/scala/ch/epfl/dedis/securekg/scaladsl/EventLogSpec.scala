package ch.epfl.dedis.securekg.scaladsl

import java.util.Collections

import ch.epfl.dedis.byzcoin.ByzCoinRPC
import ch.epfl.dedis.eventlog.Event
import ch.epfl.dedis.securekg.{AsyncBaseSpec, Helpers, ServerConfig}
import play.api.Logger

import scala.concurrent.{Future, blocking}

class EventLogSpec extends AsyncBaseSpec {

  val byzcoin : ByzCoinRPC = ServerConfig.getRPC

  val eventLog: EventLogInstance = EventLogInstance(byzcoin, Helpers.eventLogId)

  val logger: Logger = Logger( this.getClass )

  "The event log" should {
    "allow to post a new event" in {
      import scala.collection.JavaConverters._
      val admin = Helpers.signer(0)
      val counters = byzcoin.getSignerCounters(Collections.singletonList(admin.getIdentity().toString()))
      counters.increment()

      val eventKey = eventLog.log(new Event("my topic", "some message"),
        Helpers.signer, counters.getCounters.asScala)

      logger.debug(s"key=$eventKey")

      val wait = Future { blocking { Thread.sleep(2 * byzcoin.getConfig.getBlockInterval.toMillis) } }

      val futureStoredEvent = for {
        _ <- wait
        key <- Future.fromTry(eventKey)
      } yield eventLog.get(key).get

      for {
        storedEvent <- futureStoredEvent
      } yield {
        val topic = storedEvent.getTopic
        val content = storedEvent.getContent
        topic shouldBe "my topic"
        content shouldBe "some message"
      }
    }
  }

}
