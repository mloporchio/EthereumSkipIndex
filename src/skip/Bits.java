package skip;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;

/**
 * This class contains several utility methods for manipulating bit sequences
 * and producing bit representations of primitive data types.
 * 
 * @author Matteo Loporchio
 */
public final class Bits {

  /**
   *  Converts an array of bytes to a human-readable hexadecimal string.
   *  @param data array of bytes
   *  @return a hexadecimal string representing the content of the array
   */
  public static String toHex(byte[] data) {
    return BaseEncoding.base16().encode(data);
  }

  /**
   *  Constructs an array of bytes parsed from a hexadecimal string.
   *  @param str a hexadecimal string
   *  @return an array of bytes parsed from a hexadecimal string
   */
  public static byte[] fromHex(String str) {
    return BaseEncoding.base16().decode(str.toUpperCase());
  }
  
  /**
   *  Counts the number of bits equal to 1 in the given array of bytes.
   *  @param data the array of bytes
   *  @return number of ones in the array
   */
  public static int countOnes(byte[] data) {
    int result = 0;
    for (int i = 0; i < data.length; i++)
      result += Integer.bitCount(data[i] & 0xff);
    return result;
  }

  /**
   *  Converts an array of longs into an array of bytes.
   *  @param data array of longs
   *  @return an array containing all bytes of the long values
   */
  public static byte[] toByteArray(long[] data) {
    byte[] result = new byte[data.length * Long.BYTES];
    int l = 0;
    for (int i = 0; i < data.length; i++) {
      byte[] bytes = Longs.toByteArray(data[i]);
      for (int j = 0; j < bytes.length; j++) result[l+j] = bytes[j];
      l += bytes.length;
    }
    return result;
  }

  /**
   *  Converts an array of bytes into an array of longs.
   *  @param data array of bytes
   *  @return an array containing longs (obtained from the given bytes)
   */
  public static long[] toLongArray(byte[] data) {
    assert data.length % Long.BYTES == 0;
    long[] result = new long[data.length / Long.BYTES];
    for (int i = 0; i < data.length; i += Long.BYTES) {
      result[i / Long.BYTES] = Longs.fromBytes(data[i], data[i+1], data[i+2], data[i+3], 
      data[i+4], data[i+5], data[i+6], data[i+7]);
    }
    return result;
  }

}
