package ch.epfl.dedis.securekg

import ch.epfl.dedis.lib.Roster
import ch.epfl.dedis.lib.SkipblockId
import ch.epfl.dedis.lib.byzcoin.InstanceId
import ch.epfl.dedis.lib.byzcoin.darc.DarcId
import ch.epfl.dedis.lib.byzcoin.darc.SignerEd25519
import ch.epfl.dedis.lib.crypto.Hex
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
    new SkipblockId(Hex.parseHexBinary(config.get[String]("cothority.skipChainId")))
  }

  lazy val signer: Seq[SignerEd25519] = {
    Seq(new SignerEd25519(Hex.parseHexBinary(config.get[String]("cothority.signerPrivateKey"))))
  }

  lazy val darcId: DarcId = {
    new DarcId(Hex.parseHexBinary(config.get[String]("cothority.darcId")))
  }

  lazy val eventLogId: InstanceId = {
    new InstanceId(Hex.parseHexBinary(config.get[String]("cothority.eventLogId")))
  }
}
