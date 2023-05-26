package skip;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class represents a generic Ethereum event.
 * For our purposes, an event is represented as a pair <code>(address, signature)</code>
 * where <code>address</code> is the address of the triggering contract
 * and <code>signature</code> is the Keccak-256 digest of the event signature.
 * 
 * @author Matteo Loporchio
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Size of an Ethereum address (in bytes).
     */
    public static final int ADDRESS_LENGTH = 20;

    /**
     * Size of a generic <em>log topic</em> (in bytes).
     * A log topic is a sequence of bytes describing an event.
     * According to the <a href="https://ethereum.github.io/yellowpaper/paper.pdf">Ethereum Yellow Paper</a>,
     * a log topic can be either a signature digest 
     * or the value of a parameter declared as <code>indexed</code>.
     */
    public static final int TOPIC_LENGTH = 32;

    /**
     * Address of the contract triggering the event.
     */
    public final byte[] address;

    /**
     * Keccak-256 cryptographic digest of the event signature.
     */
    public final byte[] signature;

    /**
     * Constructs a new event.
     * @param address event address
     * @param signature event signature hash
     */
    public Event(byte[] address, byte[] signature) {
        this.address = address;
        this.signature = signature;
    }

    /**
     * Constructs a new event.
     * @param address event address
     * @param signature event signature hash
     */
    public Event(String address, String signature) {
        this.address = Bits.fromHex(address);
        this.signature = Bits.fromHex(signature);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event)) return false;
        Event e = (Event) o;
        return Arrays.equals(address, e.address) && Arrays.equals(signature, e.signature);
    }

    @Override
    public int hashCode() {
        int h1 = Arrays.hashCode(address);
        int h2 = Arrays.hashCode(signature);
        return Objects.hash(h1, h2);
    }
}
