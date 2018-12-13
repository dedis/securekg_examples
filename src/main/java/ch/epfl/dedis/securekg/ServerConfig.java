package ch.epfl.dedis.securekg;

import ch.epfl.dedis.byzcoin.ByzCoinRPC;
import ch.epfl.dedis.byzcoin.InstanceId;
import ch.epfl.dedis.lib.Hex;
import ch.epfl.dedis.lib.Roster;
import ch.epfl.dedis.lib.SkipblockId;
import ch.epfl.dedis.lib.darc.DarcId;
import ch.epfl.dedis.lib.darc.Signer;
import ch.epfl.dedis.lib.darc.SignerEd25519;
import ch.epfl.dedis.lib.exception.CothorityCryptoException;
import ch.epfl.dedis.lib.exception.CothorityException;

/**
 * For testing with our deployed servers, you may use this class.
 * It contains an ByzCoinRPC object and a signer object which are already initialised.
 * An example of how it may be used is in the ch.epfl.dedis.securekg.Main class.
 */
public final class ServerConfig {
    /**
     * Gets the roster of the secure KG server.
     * @return the roster
     */
    public static Roster getRoster() {
        return Roster.FromToml("[[servers]]\n" +
                "  Address = \"tls://securekg.dedis.ch:18002\"\n" +
                "  Suite = \"Ed25519\"\n" +
                "  Public = \"13dc1a4f714422e7952cef38efd527925341efaa3a992398cb52fa3e0e6dd2b8\"\n" +
                "  Description = \"Conode_1\"\n" +
                "[[servers]]\n" +
                "  Address = \"tls://securekg.dedis.ch:18004\"\n" +
                "  Suite = \"Ed25519\"\n" +
                "  Public = \"705f2877119a39f366ea53f381e37234f9678dee5f17c9f20b11df7c6cdc0e64\"\n" +
                "  Description = \"Conode_2\"\n" +
                "[[servers]]\n" +
                "  Address = \"tls://securekg.dedis.ch:18006\"\n" +
                "  Suite = \"Ed25519\"\n" +
                "  Public = \"1084f8f919112931b18a545e14e4cb668ba0b6d4884f64b463fe3fa4493b8f0e\"\n" +
                "  Description = \"Conode_3\"\n");
    }

    /**
     * Gets the genesis skipblock ID of an existing ByzCoin service.
     * @return the genesis skipblock ID
     */
    public static SkipblockId getSkipchainId() throws CothorityCryptoException {
        // This is the hex id in the bc-$hex.cfg file.
        return new SkipblockId(Hex.parseHexBinary("0ab721ada6ac482a04db9d645c2528a1ff5bd62d785638c8b6ad1b0254af35fd"));
    }

    /**
     * Gets the signer that has "invoke:eventlog" and "spawn:eventlog" permissions.
     */
    public static Signer getSigner() {
        // output of "el create --keys"
        // Identity: ed25519:9203b8f065d7e8273175f3bb674b317c4a00bbfd9fa20675cccaac59585c4ec4
        // export PRIVATE_KEY=ea29d91778840fcc567d7bb4c4a82929923aa8ce1ea7ecf16b25106bc0362e00
        return new SignerEd25519(Hex.parseHexBinary("ea29d91778840fcc567d7bb4c4a82929923aa8ce1ea7ecf16b25106bc0362e00"));
    }

    /**
     * Gets the eventlog instance ID.
     * @return the instance ID.
     */
    public static InstanceId getEventlogId() throws CothorityCryptoException {
        // output of ./el create
        //export EL=06c30c5d5a83cef67dd94db576ad2c16ed7acf158dac4a92bdc0d92b07bdc030
        return new InstanceId(Hex.parseHexBinary("06c30c5d5a83cef67dd94db576ad2c16ed7acf158dac4a92bdc0d92b07bdc030"));
    }

    /**
     * Get the pre-configured ByzCoinRPC.
     * @return the ByzCoinRPC object
     */
    public static ByzCoinRPC getRPC() throws CothorityException {
        return ByzCoinRPC.fromByzCoin(getRoster(), getSkipchainId());
    }
}
