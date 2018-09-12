package ch.epfl.dedis.securekg

import ch.epfl.dedis.lib.Roster
import ch.epfl.dedis.lib.SkipblockId
import ch.epfl.dedis.lib.exception.CothorityCryptoException
import ch.epfl.dedis.lib.exception.CothorityException
import ch.epfl.dedis.lib.omniledger.InstanceId
import ch.epfl.dedis.lib.omniledger.OmniledgerRPC
import ch.epfl.dedis.lib.omniledger.darc.DarcId
import ch.epfl.dedis.lib.omniledger.darc.Signer
import ch.epfl.dedis.lib.omniledger.darc.SignerEd25519
import javax.xml.bind.DatatypeConverter
import play.api.Configuration

import scala.io.Source

object Helpers {

  def readRosterFile(): String = {
    val source = Source.fromInputStream( getClass.getResourceAsStream("/roster.toml") )
    source.mkString
  }

  lazy val roster: Roster = Roster.FromToml(readRosterFile())

  lazy val config: Configuration = Configuration( com.typesafe.config.ConfigFactory.load() )

  lazy val skipChainId: SkipblockId = {
    new SkipblockId(DatatypeConverter.parseHexBinary(config.get[String]("cothority.skipChainId")))
  }

  lazy val signer: Seq[SignerEd25519] = {
    Seq(new SignerEd25519(DatatypeConverter.parseHexBinary(config.get[String]("cothority.signerPrivateKey"))))
  }

  lazy val darcId: DarcId = {
    new DarcId(DatatypeConverter.parseHexBinary(config.get[String]("cothority.darcId")))
  }

  lazy val eventLogId: InstanceId = {
    new InstanceId(DatatypeConverter.parseHexBinary(config.get[String]("cothority.eventLogId")))
  }

}
