package skip;

/**
 * Contains information about the result of a type F query (see {@link Query}).
 * 
 * @author Matteo Loporchio
 */
public class QueryResult {
    /**
     * Identifier of the block containing the first occurrence.
     */
    public int id = -1;

    /**
     * Number blocks visited to find the solution.
     */
    public int count = 0;
}
