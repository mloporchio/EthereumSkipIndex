package skip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * This programs behaves similarly to {@link BloomFilterBuilder}, as it constructs a binary
 * file containing Bloom filters that summarize the keys included in the input blocks.
 * Unlike {@link BloomFilterBuilder}, this program builds the Bloom filter of each block
 * by inserting additional information. Specifically, for each event occurrence inside a block,
 * we add the concatenation of the address of the contract triggering the event and the event signature
 * digest. We refer to these new Bloom filters as <em>extended Bloom filters</em>.
 *
 * The inputs of this program are as follows:
 * 
 * <ol>
 *  <li><code>keysFile</code>: path of the keys file containing unique block keys.</li>
 *  <li><code>keysFile</code>: path of the events file containing unique event occurrences.</li>
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
public class BloomFilterBuilderExt {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: BloomFilterBuilderExt <keysFile> <eventsFile> <outputFile> <filterSize>");
            System.exit(1);
        }
        final String keysFile = args[0];
        final String eventsFile = args[1];
        final String outputFile = args[2];
        int filterSize = Integer.parseInt(args[3]); // Expressed in bytes.
        long start = System.nanoTime();
        // Open input and output files.
        try (
            DataInputStream keysIn = new DataInputStream(new BufferedInputStream(new FileInputStream(keysFile)));
            DataInputStream eventsIn = new DataInputStream(new BufferedInputStream(new FileInputStream(eventsFile)));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        ) {
            // Write the size of each filter to the output file.
            out.writeInt(filterSize);
            // Read the input file.
            int numBlocks = 0;
            byte[] addressBytes = new byte[Event.ADDRESS_LENGTH];
            byte[] topicBytes = new byte[Event.TOPIC_LENGTH];
            byte[] eventBytes = new byte[Event.ADDRESS_LENGTH + Event.TOPIC_LENGTH];
            while (true) {
                try {
                    int blockId = keysIn.readInt();
                    int eventId = eventsIn.readInt();
                    if (blockId != eventId) {
                        String errorMsg = String.format("Mismatching block identifier: expected %d, read %d", blockId, eventId);
                        throw new RuntimeException(errorMsg);
                    }
                    int numAddresses = keysIn.readInt();
                    int numTopics = keysIn.readInt();
                    int numEvents = eventsIn.readInt();
                    BloomFilter bf = new BloomFilter(filterSize);
                    // Read all addresses and add them to the filter.
                    for (int i = 0; i < numAddresses; i++) {
                        keysIn.read(addressBytes);
                        bf.put(addressBytes);
                    }
                    // Read all topics and add them to the filter.
                    for (int i = 0; i < numTopics; i++) {
                        keysIn.read(topicBytes);
                        bf.put(topicBytes);
                    }
                    // Read all events (= address + first topic) and add them to the filter.
                    for (int i = 0; i < numEvents; i++) {
                        eventsIn.read(eventBytes);
                        bf.put(eventBytes);
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