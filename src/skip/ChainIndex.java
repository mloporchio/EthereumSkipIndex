package skip;

import java.io.File;
import java.io.IOException;

import com.google.common.primitives.Ints;

import org.iq80.leveldb.*;
import static org.fusesource.leveldbjni.JniDBFactory.*;

/**
 * The chain index represents a database 
 * containing the indexing data structures for the Ethereum blocks.
 * The chain index is implemented as a LevelDB key-value database.
 * Specifically, given a key corresponding to a block identifier, 
 * the database stores the {@link BlockIndex} associated with the block.
 * We recall that a {@link BlockIndex} for a block consists simply 
 * of a Bloom filter summarizing the keys in the block and a BF skip index
 * to back-navigate the blockchain starting from the block itself.
 * 
 * @author Matteo Loporchio
 */
public class ChainIndex implements AutoCloseable {
    /**
     * The underlying LevelDB database.
     */
    private DB chainIndex;

    /**
     * Constructs a new chain index database.
     * @param chainIndexPath path of the database
     * @param createIfMissing whether the database should be created if not existing
     * @throws IOException if something goes wrong while opening the database
     */
    public ChainIndex(String chainIndexPath, boolean createIfMissing) throws IOException {
        File chainIndexFile = new File(chainIndexPath);
        Options chainOpt = new Options();
        chainOpt.createIfMissing(createIfMissing);
        this.chainIndex = factory.open(chainIndexFile, chainOpt);
    }

    /**
     * Returns the {@link BlockIndex} associated with the block.
     * @param id block identifier
     * @return the {@link BlockIndex} associated with the block
     */
    public BlockIndex get(int id) {
        byte[] key = Ints.toByteArray(id);
        return BlockIndex.deserialize(chainIndex.get(key));
    }

    /**
     * Adds a new {@link BlockIndex} to the database.
     * @param id block identifier
     * @param index {@link BlockIndex} to be associated with the block
     */
    public void put(int id, BlockIndex index) {
        byte[] key = Ints.toByteArray(id);
        chainIndex.put(key, BlockIndex.serialize(index));
    }

    /**
     * Closes the current database.
     */
    @Override
    public void close() throws Exception {
        chainIndex.close();
    }
}
