package ch.epfl.dedis.securekg

import com.typesafe.config.{Config, ConfigFactory}
import play.api.{Configuration, Logger}

class HelpersSpec extends AsyncBaseSpec {

  val config: Configuration = Helpers.config

  val logger: Logger = Logger( this.getClass )

  import scala.collection.JavaConverters._

  "The application config" should {
    "exist" in {
      config.entrySet should not be empty
    }

    "contain the skipChainId" in {
      val skipChainId = Helpers.skipChainId
      logger.debug(s"cothority.skipChainId=$skipChainId")
      skipChainId should not be null
    }

    "contain the signer" in {
      val signer = Helpers.signer
      logger.debug(s"cothority.signerPrivateKey=$signer")
      signer should not be null
    }

    "contain the eventLog id" in {
      val eventLogId = Helpers.eventLogId
      logger.debug(s"cothority.eventLogId=$eventLogId")
      eventLogId should not be null
    }
  }

  "The roster" should {
    "load correctly" in {
      val roster = Helpers.roster
      roster should not be null
    }
  }

}
