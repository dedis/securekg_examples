package ch.epfl.dedis.securekg.scaladsl

import java.time.Duration
import java.time.temporal.ChronoUnit.MILLIS

import ch.epfl.dedis.byzcoin.{ByzCoinRPC, SignerCounters}
import ch.epfl.dedis.eventlog.Event
import ch.epfl.dedis.lib.darc.{Darc, Rules, SignerEd25519}
import ch.epfl.dedis.securekg._
import play.api.Logger

import scala.concurrent.{Future, blocking}
import scala.collection.JavaConverters._

class EventLogSpec extends AsyncBaseSpec {

  val logger: Logger = Logger( this.getClass )
  val testInstanceController: TestServerController = TestServerInit.getInstance
  val admin: SignerEd25519 = new SignerEd25519
  val genesisDarc: Darc = {
    val darc = ByzCoinRPC.makeGenesisDarc(admin, testInstanceController.getRoster)
    darc.addIdentity("_name:eventlog", admin.getIdentity, Rules.OR)
    darc.addIdentity("spawn:eventlog", admin.getIdentity, Rules.OR)
    darc.addIdentity("invoke:" + EventLogInstance.contractID + "." + EventLogInstance.logCmd, admin.getIdentity, Rules.OR)
    darc
  }
  val byzcoin : ByzCoinRPC = new ByzCoinRPC(testInstanceController.getRoster, genesisDarc, Duration.of(1000, MILLIS))
  val eventLog: EventLogInstance = {
    val adminCtrs: SignerCounters = byzcoin.getSignerCounters(List(admin.getIdentity.toString).asJava)
    adminCtrs.increment()
    EventLogInstance(byzcoin, genesisDarc.getId, List(admin), adminCtrs.getCounters.asScala.toList)
  }

  "The event log" should {
    "allow to post a new event" in {

      val counters = byzcoin.getSignerCounters(List(admin.getIdentity.toString).asJava)
      counters.increment()

      for {
        key <- eventLog.log(new Event("my topic", "some message"), List(admin), counters.getCounters.asScala)
        _ <- Future { blocking { Thread.sleep(2 * byzcoin.getConfig.getBlockInterval.toMillis) } }
        result <- eventLog.get(key)
      } yield {
        val topic = result.getTopic
        val content = result.getContent
        topic shouldBe "my topic"
        content shouldBe "some message"
      }
    }

    "be nameable" in {
      val counters = byzcoin.getSignerCounters(List(admin.getIdentity.toString).asJava)

      counters.increment()
      val namingInst = NamingInstance(byzcoin, genesisDarc.getId, List(admin), counters.getCounters.asScala.toList)

      val name = "my event log"
      for {
        _ <- Future{ counters.increment() }
        _ <- namingInst.set(name, eventLog.underlying.getInstanceId, List(admin), counters.getCounters.asScala.toList, 10)
        iID <- Future {byzcoin.resolveInstanceID(genesisDarc.getBaseId, name) }
      } yield {
        iID shouldBe eventLog.underlying.getInstanceId
      }
    }
  }
}
