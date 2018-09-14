package ch.epfl.dedis.securekg.scaladsl

import ch.epfl.dedis.lib.eventlog.Event
import ch.epfl.dedis.lib.byzcoin.ByzCoinRPC
import ch.epfl.dedis.securekg.{AsyncBaseSpec, Helpers}
import play.api.Logger

import scala.concurrent.{Future, blocking}

class EventLogSpec extends AsyncBaseSpec {

  val byzcoin : ByzCoinRPC = new ByzCoinRPC(Helpers.roster, Helpers.skipChainId)

  val eventLog: EventLogInstance = EventLogInstance(byzcoin, Helpers.signer, darcId = Helpers.darcId)

  val logger: Logger = Logger( this.getClass )

  "The event log" should {
    "allow to post a new event" in {
      val event = new Event("my topic", "some message")
      val eventKey = eventLog.log(event, Helpers.signer:_*)
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
