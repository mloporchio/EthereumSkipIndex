package skip;

import java.nio.ByteBuffer;

/**
 * A block index represents the indexing data structures contained
 * in each block. Specifically, a block is associated with a Bloom filter
 * summarizing the events triggered by its transactions and with a BF skip index
 * summarizing the events included in the predecessors.
 * 
 * @author Matteo Loporchio
 */
public class BlockIndex {
    /**
     * Bloom filter summarizing the events in the block.
     */
    public final BloomFilter filter;

    /**
     * BF skip index for the block.
     */
    public final Skip skip;

    /**
     * Constructs a block index.
     * @param filter Bloom filter for the current block
     * @param skip BF skip list of the current block
     */
    public BlockIndex(BloomFilter filter, Skip skip) {
        this.filter = filter;
        this.skip = skip;
    }

    /**
     * Returns the serialized size of the current block index (in bytes).
     * @return the serialized size of the block index
     */
    public int getSerializedSize() {
        int filterSize = filter.getSize();
        int numEntries = skip.getNumEntries(); 
        return 2 * Integer.BYTES + (1 + numEntries) * filterSize;
    }

    /**
     * Returns a serialized version of a block index.
     * @param index descriptor to be serialized
     * @return sequence of bytes representing the descriptor
     */
    public static byte[] serialize(BlockIndex index) {
        int filterSize = index.filter.getSize();
        int numEntries = index.skip.getNumEntries();
        int totalSize = 2 * Integer.BYTES + (1 + numEntries) * filterSize;
        ByteBuffer buf = ByteBuffer.allocate(totalSize);
        buf.putInt(filterSize).putInt(numEntries).put(index.filter.getBytes());
        for (int i = 0; i < numEntries; i++) {
            byte[] filterBytes = index.skip.getEntry(i).getBytes();
            buf.put(filterBytes);
        }
        return buf.array();
    }

    /**
     * Constructs a block index from its serialized version.
     * @param data sequence of bytes representing the index
     * @return an index corresponding to the byte sequence
     */
    public static BlockIndex deserialize(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        int filterSize = buf.getInt();
        int numEntries = buf.getInt();
        byte[] filterBytes = new byte[filterSize];
        buf.get(filterBytes);
        BloomFilter filter = new BloomFilter(filterBytes);
        BloomFilter[] entries = new BloomFilter[numEntries];
        for (int i = 0; i < numEntries; i++) {
            byte[] entryBytes = new byte[filterSize];
            buf.get(entryBytes);
            entries[i] = new BloomFilter(entryBytes);
        }
        return new BlockIndex(filter, new Skip(entries));
    }
}
