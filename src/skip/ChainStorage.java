package skip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.primitives.Ints;

import org.iq80.leveldb.*;
import static org.fusesource.leveldbjni.JniDBFactory.*;

/**
 * The {@link ChainStorage} represents a LevelDB database 
 * containing information about Ethereum block events. 
 * Keys correspond to block identifiers and the associated
 * values are sets of events (i.e., the events included in the block).
 * 
 * @author Matteo Loporchio
 */
public class ChainStorage implements AutoCloseable {
    /**
     * The underlying LevelDB database.
     */
    private DB chainStorage;

    /**
     * Creates a new {@link ChainStorage} event database.
     * @param chainStoragePath path of the database
     * @param createIfMissing whether the database should be created if not existing
     * @throws IOException if something goes wrong during the creation
     */
    public ChainStorage(String chainStoragePath, boolean createIfMissing) throws IOException {
        File chainStorageFile = new File(chainStoragePath);
        Options chainOpt = new Options();
        chainOpt.createIfMissing(createIfMissing);
        this.chainStorage = factory.open(chainStorageFile, chainOpt);
    }

    /**
     * Returns the set of events included in the given block.
     * @param blockId identifier of the block
     * @return set of events included in the block
     * @throws IOException if deserialization goes wrong
     * @throws ClassNotFoundException if deserialization goes wrong
     */
    public Set<Event> get(int blockId) throws IOException, ClassNotFoundException {
        byte[] contentBytes = chainStorage.get(Ints.toByteArray(blockId));
        return deserialize(contentBytes);
    }

    /**
     * Inserts a new set of events for a block in the database.
     * @param blockId identifier of the block
     * @param content set of events for the block
     * @throws IOException if serialization goes wrong
     */
    public void put(int blockId, Set<Event> content) throws IOException {
        byte[] contentBytes = serialize(content);
        chainStorage.put(Ints.toByteArray(blockId), contentBytes);
    }

    /**
     * Closes the {@link ChainStorage} database.
     */
    @Override
    public void close() throws IOException {
        chainStorage.close();
    }

    /**
     * Static method for serializing a set of events (using Java serialization).
     * @param content set of events
     * @return a byte representation of the set of events
     * @throws IOException if something goes wrong during serialization
     */
    public static byte[] serialize(Set<Event> content) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(content);
        byte[] result = byteStream.toByteArray();
        objectStream.close();
        byteStream.close();
        return result;
    }

    /**
     * Static method for deserializing a set of events (using Java serialization).
     * @param data sequence of bytes representing the serialized set
     * @return a set of events
     * @throws IOException if something goes wrong during deserialization
     * @throws ClassNotFoundException if something goes wrong during deserialization
     */
    @SuppressWarnings("unchecked")
    public static Set<Event> deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        ObjectInputStream objectStream = new ObjectInputStream(byteStream);
        Set<Event> result = (LinkedHashSet<Event>) objectStream.readObject();
        objectStream.close();
        byteStream.close();
        return result;
    }
}
