package ch.epfl.dedis.securekg.scaladsl

import java.time.{Duration, Instant}
import java.time.temporal.ChronoUnit.MILLIS

import ch.epfl.dedis.byzcoin.{ByzCoinRPC, SignerCounters}
import ch.epfl.dedis.eventlog.Event
import ch.epfl.dedis.lib.Hex
import ch.epfl.dedis.lib.darc.{Darc, Rules, SignerEd25519}
import ch.epfl.dedis.securekg._
import play.api.Logger

import scala.collection.JavaConverters._
import scala.concurrent.{Future, blocking}

class EventLogSpec extends AsyncBaseSpec {

  val logger: Logger = Logger( this.getClass )
  val testInstanceController: TestServerController = TestServerInit.getInstance
  val admin: SignerEd25519 = new SignerEd25519(Hex.parseHexBinary("76F40BEA4681B898E49D9657682703C0C3AA5D677A1DD259BDC60A66376B9E00"))
  val genesisDarc: Darc = {
    val darc = ByzCoinRPC.makeGenesisDarc(admin, testInstanceController.getRoster)
    darc.addIdentity("_name:eventlog", admin.getIdentity, Rules.OR)
    darc.addIdentity("spawn:eventlog", admin.getIdentity, Rules.OR)
    darc.addIdentity("invoke:" + EventLogInstance.contractID + "." + EventLogInstance.logCmd, admin.getIdentity, Rules.OR)
    darc
  }

  val byzcoin : ByzCoinRPC = new ByzCoinRPC(testInstanceController.getRoster, genesisDarc, Duration.of(1000, MILLIS))
  val adminCtrs: SignerCounters = byzcoin.getSignerCounters(List(admin.getIdentity.toString).asJava)
  val eventLog: EventLogInstance = {
    adminCtrs.increment()
    EventLogInstance(byzcoin, genesisDarc.getId, List(admin), adminCtrs.getCounters.asScala.toList)
  }

  "The genesis darc" should {
    "not change" in {
      Helpers.genesisDarcId shouldBe genesisDarc.getBaseId
    }
  }

  "The event log" should {
    "allow to post a new event" in {

      val topic = "my topic"

      adminCtrs.increment()

      for {
        key <- eventLog.log(new Event(topic, "some message"), List(admin), adminCtrs.getCounters.asScala)
        _ <- Future { blocking { Thread.sleep(2 * byzcoin.getConfig.getBlockInterval.toMillis) } }
        storedEvent <- eventLog.get(key)
        allStoredEvents <- eventLog.findAll(topic, Instant.now().minusSeconds(10), Instant.now().plusSeconds(10))
      } yield {
        allStoredEvents should contain(storedEvent)
        storedEvent.getTopic shouldBe "my topic"
        storedEvent.getContent shouldBe "some message"
      }
    }

    "allowing naming and resolving" in {
      adminCtrs.increment()
      val namingInst = NamingInstance(byzcoin, genesisDarc.getId, List(admin), adminCtrs.getCounters.asScala.toList)

      val name = "my event log"
      for {
        _ <- Future{ adminCtrs.increment() }
        _ <- namingInst.set(name, eventLog.underlying.getInstanceId, List(admin), adminCtrs.getCounters.asScala.toList, 10)
        iID <- Future {byzcoin.resolveInstanceID(genesisDarc.getBaseId, name) }
      } yield {
        iID shouldBe eventLog.underlying.getInstanceId
      }
    }
  }
}
