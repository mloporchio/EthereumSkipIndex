package skip;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;

/**
 * This program builds the chain index database starting from a file including the Bloom filters
 * of the blocks. The chain index database is a LevelDB key-value storage (see {@link ChainIndex}).
 * 
 * The inputs of this program are as follows.
 * <ol>
 *  <li><code>inputFile</code>: path of the binary file containing Bloom filters;</li>
 *  <li><code>indexDb</code>: path of the chain index database;</li>
 *  <li><code>numEntries</code>: number of entries to be computed for each BF skip index;</li>
 * </ol>
 * 
 * The program produces a LevelDB database where each block identifier is associated with
 * the corresponding block index (see {@link BlockIndex}).
 * 
 * @author Matteo Loporchio
 */
public class ChainIndexBuilder {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: ChainIndexBuilder <inputFile> <indexDb> <numEntries>");
            System.exit(1);
        }
        final String inputFile = args[0];
        final String indexPath = args[1];
        int numEntries = Integer.parseInt(args[2]);
        long totalTime = 0, creationTime = 0, totalStart = System.nanoTime(), creationStart = 0;
        //
        try (
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
            ChainIndex index = new ChainIndex(indexPath, true);
        ) {
            // Read the input file.
            int filterSize = in.readInt(), height = 0, blockId = -1;
            while (true) {
                try {
                    // Read the block identifier.
                    blockId = in.readInt();
                    // Read and build the current Bloom filter.
                    byte[] filterBytes = new byte[filterSize];
                    in.read(filterBytes);
                    BloomFilter filter = new BloomFilter(filterBytes);
                    // Construct the skip list for the current block.
                    creationStart = System.nanoTime();
                    Skip skip = build(index, numEntries, filterSize, blockId, height);
                    creationTime += (System.nanoTime() - creationStart);
                    // Build and write the descriptor.
                    BlockIndex desc = new BlockIndex(filter, skip);
                    index.put(blockId, desc);
                    height++;
                }
                catch (EOFException e) {break;}
            }
            totalTime = System.nanoTime() - totalStart;
            System.out.printf("Blocks written:\t%d\nTotal time:\t%d ns\nCreation time:\t%.3f ns\n", 
            height, totalTime, ((double) creationTime / (double) height));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Implementation of the BF skip index construction method.
     * @param chain chain index database
     * @param numEntries number of entries for the BF skip index
     * @param filterSize size of Bloom filters used in the BF skip index (in bytes)
     * @param blockId identifier of the block for which the BF skip index should be constructed
     * @param height height of the block for which the BF skip index should be constructed
     * @return BF skip index for the block
     */
    public static Skip build(ChainIndex chain, int numEntries, int filterSize, int blockId, int height) {
        // Initialize the BF skip index for the current block.
        BloomFilter[] entries = new BloomFilter[numEntries];
        for (int j = 0; j < numEntries; j++) 
            entries[j] = new BloomFilter(filterSize);
        // If the current height is zero, there is nothing to do.
        if (height == 0) return new Skip(entries);
        // Initialize the first entry as the filter of the predecessor block.
        BlockIndex curr = chain.get(blockId - 1);
        entries[0].merge(curr.filter);
        // Initialize the remaining entries.
        for (int j = 1; j < numEntries; j++) {
            if (height - (1 << j) <= 0) return new Skip(entries);
            BlockIndex b1 = chain.get(blockId - (1<<j)); 
            entries[j].merge(b1.skip.getEntry(j-1));
            BlockIndex b2 = chain.get(blockId - (1<<(j-1)));            
            entries[j].merge(b2.skip.getEntry(j-1));
        }
        return new Skip(entries);
    }
}