package ch.epfl.dedis.securekg.scaladsl

import java.time.{Duration, Instant}
import java.time.temporal.ChronoUnit.MILLIS

import ch.epfl.dedis.byzcoin.{ByzCoinRPC, SignerCounters}
import ch.epfl.dedis.eventlog.Event
import ch.epfl.dedis.lib.darc.{Darc, Rules, SignerEd25519}
import ch.epfl.dedis.securekg._
import play.api.Logger

import scala.collection.JavaConverters._
import scala.concurrent.{Future, blocking}

class EventLogSpec extends AsyncBaseSpec {

  val logger: Logger = Logger( this.getClass )
  val testInstanceController: TestServerController = TestServerInit.getInstance
  val admin: SignerEd25519 = new SignerEd25519
  val genesisDarc: Darc = {
    val darc = ByzCoinRPC.makeGenesisDarc(admin, testInstanceController.getRoster)
    darc.addIdentity("spawn:eventlog", admin.getIdentity, Rules.OR)
    darc.addIdentity("invoke:" + EventLogInstance.contractID + "." + EventLogInstance.logCmd, admin.getIdentity, Rules.OR)
    darc
  }
  val byzcoin : ByzCoinRPC = new ByzCoinRPC(testInstanceController.getRoster, genesisDarc, Duration.of(1000, MILLIS))
  val eventLog: EventLogInstance = {
    val adminCtrs: SignerCounters = byzcoin.getSignerCounters(List(admin.getIdentity.toString).asJava)
    EventLogInstance(byzcoin, genesisDarc.getId, List(admin), List(adminCtrs.head + 1))
  }

  "The event log" should {
    "allow to post a new event" in {

      val counters = byzcoin.getSignerCounters(List(admin.getIdentity.toString).asJava)
      counters.increment()

      val topic = "my topic"
      val eventKey = eventLog.log(new Event(topic, "some message"),
        List(admin), counters.getCounters.asScala)

      logger.debug(s"key=$eventKey")

      val wait = Future { blocking { Thread.sleep(2 * byzcoin.getConfig.getBlockInterval.toMillis) } }

      val futureStoredEvent = for {
        _ <- wait
        key <- Future.fromTry(eventKey)
      } yield eventLog.get(key).get

      def findAll: Future[List[Event]] = for {
        storedEvent <- futureStoredEvent
        _ <- wait
        events <- Future {
          eventLog.findAll(topic, Instant.now().minusSeconds(10), Instant.now().plusSeconds(10))
        }.flatMap {
          case found if found contains storedEvent => Future.successful(found)
          case _                                   => findAll
        }
      } yield events


      for {
        storedEvent     <- futureStoredEvent
        allStoredEvents <- findAll
      } yield {
        allStoredEvents should contain(storedEvent)

        storedEvent.getTopic shouldBe topic
        storedEvent.getContent shouldBe "some message"
      }
    }
  }

}
