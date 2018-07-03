import ch.epfl.dedis.lib.eventlog.Event
import ch.epfl.dedis.lib.omniledger.contracts.EventLogInstance
import ch.epfl.dedis.lib.omniledger.OmniledgerRPC

import scala.collection.JavaConverters._
import org.scalatest.FunSuite

class EventlogTest extends FunSuite {
  test("post one event") {
    val config = new ServerConfig()

    val ol = new OmniledgerRPC(config.getRoster, config.getSkipchainId)
    val admin = config.getSigner
    val el = new EventLogInstance(ol, config.getEventlogId)
    val key = el.log(new Event("hello", "goodbye"), List(admin).asJava)

    Thread sleep 2 * ol.getConfig.getBlockInterval.toMillis

    println("got event: " + el.get(key))
  }
}
