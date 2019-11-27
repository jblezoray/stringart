package fr.jblezoray.stringart.image;

public class ImageTestUtils {

  
  static byte[] mkByteArray(int... vals) {
    byte[] b = new byte[vals.length];
    for (int i=0; i<vals.length; i++) {
      b[i] = (byte) vals[i];
    }
    return b;
  }
  
}
