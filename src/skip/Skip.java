package skip;

/**
 * This class contains the implementation of the BF skip index, 
 * a data structure based on Bloom filters for answering
 * inter-block queries on blockchains in an efficient way.
 * 
 * @author Matteo Loporchio
 */
public class Skip {
    /**
     * Array of Bloom filters (i.e., the BF skip index entries).
     */
    private BloomFilter[] entries;

    /**
     * Constructs a new BF skip index with the given number of entries and filter size.
     * @param numEntries number of entries of the BF skip index
     * @param filterSize size of the Bloom filters (in bytes)
     */
    public Skip(int numEntries, int filterSize) {
        entries = new BloomFilter[numEntries];
        for (int i = 0; i < entries.length; i++) 
            entries[i] = new BloomFilter(filterSize);
    }

    /**
     * Constructs a new BF skip index from the given array of filters.
     * @param entries array of Bloom filters
     */
    public Skip(BloomFilter[] entries) {
        this.entries = entries;
    }

    /**
     * Returns the total number of entries in the current BF skip index.
     * @return number of entries in the BF skip index
     */
    public int getNumEntries() {
        return entries.length;
    }

    /**
     * Returns the i-th entry of the BF skip index.
     * @param i identifier of the entry
     * @return the i-th entry of the BF skip index
     */
    public BloomFilter getEntry(int i) {
        return entries[i];
    }
}