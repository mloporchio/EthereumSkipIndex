package skip;

import java.io.IOException;
import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.primitives.Bytes;

/**
 * This class contains the implementation of algorithms for event searching.
 * In particular, as detailed in our work, we focused on <em>type F queries</em>, 
 * i.e., when users are interested in computing the first occurrence of an event.
 * Two methods have been implemented in this regard:
 * <ol>
 *  <li>the sequential method, which iterates through all the blocks within the range until the desired key is found;</li>
 *  <li>the BF skip index-based method, which leverages these data structures to speed up the search.</li>
 * </ol>
 * 
 * @author Matteo Loporchio
 */
public final class Query {

    /**
     * Membership testing procedure for standard Bloom filters.
     * An event is included in a block if and only if the keys of the block
     * contain both the contract address and the event signature digest.
     */
    public static final BiPredicate<BloomFilter, Event> containsDefault = (bf, e) -> {
        return (bf.contains(e.address) && bf.contains(e.signature));
    };

    /**
     * Membership testing procedure for extended Bloom filters.
     * An event is included in a block if and only if the keys of the block
     * contain the concatenation of the contract address and the event signature digest.
     */
    public static final BiPredicate<BloomFilter, Event> containsExtended = (bf, e) -> {
        return bf.contains(Bytes.concat(e.address, e.signature));
    };

    /**
     * Implementation of the sequential search algorithm.
     * @param index chain index database
     * @param storage chain storage database
     * @param lower lower endpoint of the search interval
     * @param upper upper endpoint of the search interval
     * @param e event to be searched
     * @param membership predicate for Bloom filter membership testing
     * @return a {@link QueryResult} with information about the result of the query
     * @throws IOException in case of deserialization errors
     * @throws ClassNotFoundException in case of deserialization errors
     */
    public static QueryResult linearSearch(
        ChainIndex index, 
        ChainStorage storage, 
        int lower, 
        int upper, 
        Event e, 
        BiPredicate<BloomFilter,Event> membership
    ) throws IOException, ClassNotFoundException 
    {
        QueryResult result = new QueryResult();
        for (int i = upper; i >= lower; i--) {
            BlockIndex currIndex = index.get(i);
            result.count++;
            // If the current filter contains the event, we retrieve the block content
            // and then check if the event is really included in the block.
            if (membership.test(currIndex.filter, e)) {
                Set<Event> currEvents = storage.get(i);
                if (currEvents.contains(e)) {
                    result.id = i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Implementation of the efficient search algorithm based on BF skip indexes.
     * @param index chain index database
     * @param storage chain storage database
     * @param lower lower endpoint of the search range
     * @param upper upper endpoint of the search range
     * @param e event to be searched
     * @param membership predicate for checking event membership within Bloom filters
     * @return a {@link QueryResult} with information about the result of the query
     * @throws IOException in case of deserialization errors
     * @throws ClassNotFoundException in case of deserialization errors
     */
    public static QueryResult findFirst(
        ChainIndex index, 
        ChainStorage storage, 
        int lower,
        int upper, 
        Event e, 
        BiPredicate<BloomFilter,Event> membership
    ) throws IOException, ClassNotFoundException 
    {
        QueryResult result = new QueryResult();
        while (upper >= lower) {
            BlockIndex currIndex = index.get(upper);
            result.count++;
            if (membership.test(currIndex.filter, e)) {
                Set<Event> currEvents = storage.get(upper);
                if (currEvents.contains(e)) {
                    result.id = upper;
                    return result;
                }
            }
            int numEntries = currIndex.skip.getNumEntries();
            int jmax = maxJump(numEntries, lower, upper);
            for (int j = 0; j <= jmax; j++) {
                int lsub = Math.max(lower, upper - (1 << (j+1)) + 1);
                int usub = upper - (1 << j);
                if (membership.test(currIndex.skip.getEntry(j), e)) {
                    QueryResult partial = findFirst(index, storage, lsub, usub, e, membership);
                    result.count += partial.count;
                    if (partial.id != -1) {
                        result.id = partial.id;
                        return result;
                    }
                }
            }
            upper -= (1 << (jmax + 1));
        }
        return result;
    }

    /**
     * Auxiliary method for computing the maximum feasible jump.
     * @param numEntries number of entries in the current skip list
     * @param lower lower bound for the search range
     * @param upper upper bound for the search range (i.e., current block)
     * @return identifier of the maximum feasible jump, -1 if no jump can be selected
     */
    public static int maxJump(int numEntries, int lower, int upper) {
        for (int j = numEntries - 1; j >= 0; j--) {
            if (lower <= (upper - (1 << j))) return j;
        }
        return -1;
    }

}
