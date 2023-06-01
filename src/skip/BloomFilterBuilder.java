package skip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * This program reads a binary file containing a list of keys 
 * and outputs a binary file with a list of Bloom filters.
 * The output file includes one Bloom filter for each block in the input file.
 * Each filter summarizes the keys included in the corresponding block.
 * 
 * The inputs of this program are as follows:
 * 
 * <ol>
 *  <li><code>inputFile</code>: path of the input file (keys file) containing unique block keys.</li>
 *  <li><code>outputFile</code>: path of the output file (filter file) containing Bloom filters for the blocks.</li>
 *  <li><code>filterSize</code>: size of each Bloom filter (expressed in bytes).</li>
 * </ol>
 * 
 * The output file is a binary file with the following structure.
 * 
 * <ol>
 *  <li>The first 4 bytes represent the size of all Bloom filters (in bytes).</li>
 *  <li>
 *      Then there is a sequence of data chunks, each representing a block. 
 *      A chunk comprises the following fields. 
 *      <ol>
 *          <li>There are 4 bytes representing the identifier of the block.</li>
 *          <li>Then there are <code>filterSize</code> bytes representing the Bloom filter of the block.</li>
 *      </ol>
 *  </li>
 * </ol>
 * 
 * @author Matteo Loporchio
 */
public class BloomFilterBuilder {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: BloomFilterBuilder <inputFile> <outputFile> <filterSize>");
            System.exit(1);
        }
        final String inputFile = args[0];
        final String outputFile = args[1];
        int filterSize = Integer.parseInt(args[2]); // Expressed in bytes.
        long start = System.nanoTime();
        // Open input and output files.
        try (
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        ) {
            // Write the size of each filter to the output file.
            out.writeInt(filterSize);
            // Read the input file.
            int numBlocks = 0;
            byte[] addressBytes = new byte[Event.ADDRESS_LENGTH];
            byte[] topicBytes = new byte[Event.TOPIC_LENGTH];
            while (true) {
                try {
                    int blockId = in.readInt();
                    int numAddresses = in.readInt();
                    int numTopics = in.readInt();
                    BloomFilter bf = new BloomFilter(filterSize);
                    // Read all addresses and add them to the filter.
                    for (int i = 0; i < numAddresses; i++) {
                        in.read(addressBytes);
                        bf.put(addressBytes);
                    }
                    // Read all topics and add them to the filter.
                    for (int i = 0; i < numTopics; i++) {
                        in.read(topicBytes);
                        bf.put(topicBytes);
                    }
                    // Write the pair (blockId, filter) to the output file.
                    out.writeInt(blockId);
                    out.write(bf.getBytes());
                    numBlocks++;
                }
                catch (EOFException e) {break;}
            }
            // Print statistics.
            long elapsed = System.nanoTime() - start;
            System.out.printf("Blocks written:\t%d\nElapsed time:\t%d ns\n", numBlocks, elapsed);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}