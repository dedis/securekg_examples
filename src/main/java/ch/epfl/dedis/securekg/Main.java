package ch.epfl.dedis.securekg;

import ch.epfl.dedis.byzcoin.ByzCoinRPC;
import ch.epfl.dedis.byzcoin.InstanceId;
import ch.epfl.dedis.byzcoin.SignerCounters;
import ch.epfl.dedis.eventlog.Event;
import ch.epfl.dedis.eventlog.EventLogInstance;
import ch.epfl.dedis.lib.darc.Signer;
import ch.epfl.dedis.lib.exception.CothorityException;

import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        try {
            ByzCoinRPC bc = ServerConfig.getRPC();
            Signer admin = ServerConfig.getSigner();

            SignerCounters counters = bc.getSignerCounters(Collections.singletonList(admin.getIdentity().toString()));
            counters.increment();

            EventLogInstance el = EventLogInstance.fromByzcoin(bc, ServerConfig.getEventlogId());
            InstanceId key = el.log(new Event("hello", "goodbye"),
                    Arrays.asList(admin), counters.getCounters());
            Thread.sleep(2 * bc.getConfig().getBlockInterval().toMillis());
            System.out.println("got event: " + el.get(key));
        } catch (InterruptedException | CothorityException e) {
            System.out.println("error: " + e);
        }
    }
}
