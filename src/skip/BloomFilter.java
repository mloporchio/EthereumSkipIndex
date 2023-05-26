package skip;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;

/**
 * This class contains the implementation of a generic Bloom filter.
 * This implementation has the following properties.
 * 
 * <ul>
 * 	<li>The filter uses a number of bits equal to a power of 2.</li>
 * 	<li>The filter uses the SHA-256 cryptographic hash function for inserting elements and checking their membership.</li>
 * 	<li>
 * 		The procedure for inserting/checking membership is as follows: 
 * 		<ol>
 * 			<li>calculate the cryptographic digest of the element using the SHA-256 hash function;</li>
 * 			<li>take the first 96 bits of the resulting digest and split them into 3 chunks of 32 bits each;</li>
 * 			<li>for each chunk, reduce the corresponding integer modulo the filter size and set/check the corresponding position.</li>
 * 		</ol>
 * 	</li>
 * </ul>
 * 
 * @author Matteo Loporchio
 */
public class BloomFilter {
	/**
	 * The hash function used by the Bloom filter.
	 */
    public static final HashFunction hf = Hashing.sha256();

	/**
	 * Number of bytes used to represent the bit array.
	 */
    private final int size;

	/**
	 * Number of hash functions used by the filter.
	 */
    private final int numHash = 3;

	/**
	 * Maximum size of the output of each hash function.
	 */
    private final int chunkSize = Integer.BYTES;

	/**
	 * Bit array used for representing the filter.
	 */
    private long[] bits;

    /**
     * Constructs a new Bloom filter with the given size.
	 * NOTICE: the size must be a multiple of 8.
     * @param size number of bytes used by the filter
     */
    public BloomFilter(int size) {
		assert (size % Long.BYTES == 0);
        this.size = size;
        this.bits = new long[size / Long.BYTES];
    }

    /**
     * Constructs a new Bloom filter from its byte representation.
	 * NOTICE: the size of the input array must be a multiple of 8.
     * @param data serialized representation of the filter
     */
    public BloomFilter(byte[] data) {
		assert (data.length % Long.BYTES == 0);
        this.size = data.length;
        this.bits = Bits.toLongArray(data);
    }

	/**
	 * Returns the size (in bytes) of the current Bloom filter.
	 * @return the number of bytes used by this filter
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the backing array of the Bloom filter.
	 * @return the backing array of the Bloom filter
	 */
	public long[] getBitSet() {
		return bits;
	}

	/**
	 * Returns the backing array of the filter as a sequence of bytes. 
	 * @return all bytes of the backing array of the filter 
	 */
	public byte[] getBytes() {
		return Bits.toByteArray(bits);
	}

    /**
     * Inserts an element into the filter.
     * @param data the sequence of bytes representing the object
     */
	public void put(byte[] data) {
		//if (data == null) return;
	    byte[] d = hf.hashBytes(data).asBytes();
	    for (int i = 0; i < numHash * chunkSize; i += chunkSize) {
	    	int h = Ints.fromBytes(d[i], d[i+1], d[i+2], d[i+3]);
	    	set(Integer.remainderUnsigned(h, size * Byte.SIZE));
	    }
	}

	/**
	 *  Checks if a given element has already been inserted in the filter.
	 *  @param data the array of bytes representing the element
	 *  @return true if the element might have been added, false if it has not been inserted
	 */
	public boolean contains(byte[] data) {
		//if (data == null) return false;
		byte[] d = hf.hashBytes(data).asBytes();
		for (int i = 0; i < numHash * chunkSize; i += chunkSize) {
			int h = Ints.fromBytes(d[i], d[i+1], d[i+2], d[i+3]);
			if (!get(Integer.remainderUnsigned(h, size * Byte.SIZE))) return false;
		}
		return true;
	}

	/**
	 * In-place merge of two Bloom filters using bitwise OR.
	 * @param bf filter to be merged with the current one
	 */
	public void merge(BloomFilter bf) {
		//assert (bf != null && bf.numBits == numBits);
		long[] bfBitSet = bf.getBitSet();
		for (int i = 0; i < bits.length; i++) bits[i] |= bfBitSet[i];
	}

    /**
	 * Sets the i-th bit of the Bloom filter.
	 * @param i position of the bit
	 */
	private void set(int i) {
        bits[i >>> 6] |= (1L << (Long.SIZE - i - 1));
	}

	/**
	 * Returns the value of the i-th bit of the filter.
	 * @param i position of the bit
	 * @return value of the bit
	 */
	private boolean get(int i) {
        return ((bits[i >>> 6] & (1L << (Long.SIZE - i - 1))) != 0);
	}
}