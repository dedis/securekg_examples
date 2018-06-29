import java.util

import ch.epfl.dedis.lib.eventlog.Event
import ch.epfl.dedis.lib.omniledger.{InstanceId, OmniledgerRPC}
import ch.epfl.dedis.lib.omniledger.contracts.EventLogInstance
import ch.epfl.dedis.lib.omniledger.darc.Signer
import org.scalatest.FunSuite

class EventlogTest extends FunSuite {
  test("post one event") {
    val ol: OmniledgerRPC = ServerConfig.getOmniledgerRPC
    val admin: Signer = ServerConfig.getSigner
    val el: EventLogInstance = new EventLogInstance(ol, ServerConfig.getEventlogId)
    val key: InstanceId = el.log(new Event("hello", "goodbye"), util.Arrays.asList(admin))
    Thread.sleep(2 * ol.getConfig.getBlockInterval.toMillis)
    System.out.println("got event: " + el.get(key))

  }
}
