package fr.jblezoray.stringart.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static fr.jblezoray.stringart.image.ImageTestUtils.*;

public class ImageTest {

  private static final Image IMG = new ByteImage(
      new ImageSize(5, 2), 
      mkByteArray(
        0xFF, 0x00, 0x00, 0x00, 0x00, 
        0x00, 0x7F, 0xFF, 0x7F, 0x00)
  );
  
  private static final Image IMG_2 = new ByteImage(
      new ImageSize(5, 2), 
      mkByteArray(
        0x10, 0x7F, 0xFF, 0x00, 0x00, 
        0x00, 0x7F, 0x00, 0x7F, 0x00)
  );

  @Test
  public void test_multiplyWith() {
    Image multiplied = IMG.multiplyWith(IMG_2);
    
    Assertions.assertArrayEquals(mkByteArray(
        0x10, 0x00, 0x00, 0x00, 0x00, 
        0x00, 0x3F, 0x00, 0x3F, 0x00),
        ((ByteImage)multiplied).getRawBytes());
  }

  @Test
  public void test_differenceWith() {
    Image difference = IMG.differenceWith(IMG_2);
    
    Assertions.assertArrayEquals(mkByteArray(
        0xEF, 0x7F, 0xFF, 0x00, 0x00, 
        0x00, 0x00, 0xFF, 0x00, 0x00),
        ((ByteImage)difference).getRawBytes());
  }

  @Test
  public void test_minFilter() {
    Image filtered = IMG.minFilter(0x11);
    
    Assertions.assertArrayEquals(mkByteArray(
        0xFF, 0x11, 0x11, 0x11, 0x11, 
        0x11, 0x7F, 0xFF, 0x7F, 0x11),
        ((ByteImage)filtered).getRawBytes());
  }
  
  
  @Test
  public void test_downsample() {
    ByteImage downsample = IMG.downsample(0.5);
    
    Assertions.assertArrayEquals(mkByteArray(
        0x48, 0x2E), 
        downsample.getRawBytes());
  }

  @Test
  public void test_l2norm() {
    double l2norm = IMG.l2norm();
    
    Assertions.assertEquals(402.0, l2norm, 1.0);
  }
  
}
