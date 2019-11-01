package fr.jblezoray.stringart.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteImageTest {


  @Test
  public void test_deriveX() {
    var size = new ImageSize(5, 2);
    ByteImage bi = new ByteImage(size, mkByteArray(
        0xFF, 0x00, 0x00, 0x00, 0x00, 
        0x00, 0x7F, 0xFF, 0x7F, 0x00));
    
    ByteImage derived = bi.xDerivative();
    
    Assertions.assertArrayEquals(mkByteArray(
        0xFF, 0x00, 0x00, 0x00, 0x00, 
        0x7F, 0x80, 0x80, 0x7F, 0x7F), 
        derived.getRawBytes());
  }
  
  @Test
  public void test_deriveY() {
    var size = new ImageSize(2, 5);
    ByteImage bi = new ByteImage(size, mkByteArray(
        0xFF, 0x00, 
        0x00, 0x7F, 
        0x00, 0xFF, 
        0x00, 0x7F, 
        0x00, 0x00));
    
    ByteImage derived = bi.yDerivative();
    
    Assertions.assertArrayEquals(mkByteArray(
        0xFF, 0x7F, 
        0x00, 0x80, 
        0x00, 0x80, 
        0x00, 0x7F, 
        0x00, 0x7F), 
        derived.getRawBytes());
  }
  
  private byte[] mkByteArray(int... vals) {
    byte[] b = new byte[vals.length];
    for (int i=0; i<vals.length; i++) {
      b[i] = (byte) vals[i];
    }
    return b;
  }
  
  
  
}
