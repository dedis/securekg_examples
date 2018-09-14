package ch.epfl.dedis.securekg;

import ch.epfl.dedis.lib.byzcoin.ByzCoinRPC;
import ch.epfl.dedis.lib.eventlog.Event;
import ch.epfl.dedis.lib.exception.CothorityException;
import ch.epfl.dedis.lib.byzcoin.InstanceId;
import ch.epfl.dedis.lib.byzcoin.contracts.EventLogInstance;
import ch.epfl.dedis.lib.byzcoin.darc.Signer;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            ByzCoinRPC ol = ServerConfig.getRPC();
            Signer admin = ServerConfig.getSigner();
            EventLogInstance el = new EventLogInstance(ol, ServerConfig.getEventlogId());
            InstanceId key = el.log(new Event("hello", "goodbye"), ServerConfig.getDarcId(), Arrays.asList(admin));
            Thread.sleep(2 * ol.getConfig().getBlockInterval().toMillis());
            System.out.println("got event: " + el.get(key));
        } catch (InterruptedException | CothorityException | InvalidProtocolBufferException e) {
            System.out.println("error: " + e);
        }
    }
}
