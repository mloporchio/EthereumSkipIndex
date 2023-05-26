package skip;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This program builds the chain storage database starting from the binary file including 
 * all event occurrences of the blocks. The chain storage database is implemente as
 * a LevelDB key-value storage (see {@link ChainStorage}).
 * 
 * The inputs of this program are as follows.
 * <ol>
 *  <li><code>inputFile</code>: path of the binary file containing event occurrences;</li>
 *  <li><code>outputFile</code>: path of the output chain storage database;</li>
 * </ol>
 * 
 * The program produces a LevelDB database where each block identifier is associated with
 * the corresponding set of event occurrences.
 * 
 * @author Matteo Loporchio
 */
public class ChainStorageBuilder {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ChainStorageBuilder <inputFile> <outputFile>");
            System.exit(1);
        }
        final String inputFile = args[0];
        final String outputFile = args[1];
        long start = System.nanoTime();
        try (
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
            ChainStorage storage = new ChainStorage(outputFile, true);
        ) {
            // Read the input file.
            int numBlocks = 0;
            while (true) {
                try {
                    int blockId = in.readInt();
                    int numEvents = in.readInt();
                    Set<Event> events = new LinkedHashSet<>();
                    for (int i = 0; i < numEvents; i++) {
                        byte[] addressBytes = new byte[Event.ADDRESS_LENGTH];
                        byte[] topicBytes = new byte[Event.TOPIC_LENGTH];
                        in.read(addressBytes);
                        in.read(topicBytes);
                        events.add(new Event(addressBytes, topicBytes));
                    }
                    // Write the pair (blockId, set of events) to the output database.
                    storage.put(blockId, events);
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
