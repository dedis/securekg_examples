package ch.epfl.dedis.securekg.scaladsl

import ch.epfl.dedis.byzcoin.{ByzCoinRPC, InstanceId}
import ch.epfl.dedis.lib.darc.{DarcId, Signer}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.blocking

class NamingInstance private[scaladsl] (val underlying: ch.epfl.dedis.byzcoin.contracts.NamingInstance) {
  def set(name: String, iID: InstanceId, signers: TraversableOnce[Signer], counters: TraversableOnce[java.lang.Long])
         (implicit ec: ExecutionContext): Future[Unit] = {
    val javaSigners = signers.toSeq.asJava
    val javaCounters = counters.toSeq.asJava
    Future{
      underlying.set(name, iID, javaSigners, javaCounters)
    }
  }

  def set(name: String, iID: InstanceId, signers: TraversableOnce[Signer], counters: TraversableOnce[java.lang.Long], wait: Int)
         (implicit ec: ExecutionContext): Future[Unit] = {
    val javaSigners = signers.toSeq.asJava
    val javaCounters = counters.toSeq.asJava
    Future{
      blocking { underlying.setAndWait(name, iID, javaSigners, javaCounters, wait) }
    }
  }
}

object NamingInstance {
  def apply(bc: ByzCoinRPC, darcBaseID: DarcId, signers: List[Signer], signerCtrs: List[java.lang.Long]): NamingInstance = {
    val underlying = new ch.epfl.dedis.byzcoin.contracts.NamingInstance(bc, darcBaseID, signers.asJava, signerCtrs.asJava)
    new NamingInstance(underlying)
  }

  def apply(bc: ByzCoinRPC): NamingInstance = {
    val underlying = ch.epfl.dedis.byzcoin.contracts.NamingInstance.fromByzcoin(bc)
    new NamingInstance(underlying)
  }
}
