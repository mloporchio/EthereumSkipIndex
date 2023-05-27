package skip;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.function.BiPredicate;

/**
 * This program simulates the execution of the sequential and skip index-based 
 * methods for searching the first occurrence of an event along the blockchain.
 * Each of the two methods is tested for a predefined number of executions 
 * using a data set of queries supplied as an input to the program.
 *  
 * The inputs of this program are as follows.
 * <ol>
 *  <li><code>indexDb</code>: path of the chain index database;</li>
 *  <li><code>storageDb</code>: path of the chain storage database;</li>
 *  <li><code>queryFile</code>: path of the CSV file containing the queries to be performed;</li>
 *  <li><code>resultFile</code>: path of output CSV file with the results;</li>
 *  <li><code>contract</code>: address of the contract triggering the event (must be a hex string with a <code>0x</code> prefix);</li>
 *  <li><code>eventSignature</code>: hash of the event signature (must be a hex string with a <code>0x</code> prefix);</li>
 *  <li><code>membership</code>: procedure to be used for testing whether an event is included in a Bloom filter (must be either <code>default</code> or <code>extended</code>);</li>
 * </ol>
 * 
 * The program outputs a CSV file containing the results of the experiment.
 * The output file has one row for each query in the input file and each row
 * consists of the following fields.
 * 
 * <ol>
 *  <li><code>upper</code>: upper endpoint of the query;</li>
 *  <li><code>lower</code>: lower endpoint of the query;</li>
 *  <li><code>solution</code>: identifier of the block containing the solution;</li>
 *  <li><code>distance</code>: distance from the solution starting from the upper endpoint;</li>
 *  <li><code>linearSolution</code>: identifier of the block found by the sequential approach;</li>
 *  <li><code>linearVisited</code>: number of blocks visited by the sequential approach;</li>
 *  <li><code>linearTime</code>: average time required by the sequential approach (expressed in nanoseconds);</li>
 *  <li><code>skipSolution</code>: identifier of the block found by the BF skip index-based approach;</li>
 *  <li><code>skipVisited</code>: number of blocks visited by the BF skip index-based approach;</li>
 *  <li><code>skipTime</code>: average time required by the BF skip index-based approach (expressed in nanoseconds).</li>
 * </ol>
 * 
 * @author Matteo Loporchio
 */
public class TestFindFirst {
    /**
     * Number of trials for each query algorithm simulation.
     */
    public static final int numExecutions = 5;

    public static void main(String[] args) {
        if (args.length < 7) {
            System.err.println("TestFindFirst <indexDb> <storageDb> <queryFile> <resultFile> <contract> <eventSignature> <membership>");
            System.exit(1);
        }
        final String indexPath = args[0];
        final String storagePath = args[1];
        final String queryFile = args[2];
        final String resultFile = args[3];
        final Event event = new Event(args[4].substring(2), args[5].substring(2));
        final BiPredicate<BloomFilter,Event> membership = ((args[6].equals("default")) ? Query.containsDefault : Query.containsExtended);
        try (
            ChainIndex index = new ChainIndex(indexPath, false);
            ChainStorage storage = new ChainStorage(storagePath, false);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(queryFile)));
            PrintWriter out = new PrintWriter(resultFile);
        ) {
            out.println("upper,lower,solution,distance,linearSolution,linearVisited,linearTime,skipSolution,skipVisited,skipTime");
            String query = null;
            while ((query = in.readLine()) != null) {
                String[] parts = query.split(",");
                int upper = Integer.parseInt(parts[0]);
                int lower = Integer.parseInt(parts[1]);
                int where = Integer.parseInt(parts[2]);
                int distance = upper - where;
                // Test linear approach.
                long totalLinearTime = 0;
                int linearSolution = 0, linearVisited = 0;
                for (int i = 0; i < numExecutions; i++) {
                    long start = System.nanoTime();
                    QueryResult result = Query.linearSearch(index, storage, lower, upper, event, membership);
                    totalLinearTime += (System.nanoTime() - start);
                    linearSolution = result.id;
                    linearVisited = result.count;
                } 
                totalLinearTime /= numExecutions;
                // Test BF skip approach.
                long totalSkipTime = 0;
                int skipSolution = 0, skipVisited = 0;
                for (int i = 0; i < numExecutions; i++) {
                    long start = System.nanoTime();
                    QueryResult result = Query.findFirst(index, storage, lower, upper, event, membership);
                    totalSkipTime += (System.nanoTime() - start);
                    skipSolution = result.id;
                    skipVisited = result.count;
                }
                totalSkipTime /= numExecutions;
                // Write the results.
                out.printf("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n", 
                upper, lower, where, distance,
                linearSolution, linearVisited, totalLinearTime, 
                skipSolution, skipVisited, totalSkipTime);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}