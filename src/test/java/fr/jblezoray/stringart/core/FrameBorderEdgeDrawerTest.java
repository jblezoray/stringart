package fr.jblezoray.stringart.core;

import org.junit.Assert;
import org.junit.Test;

import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics.Shape;
import fr.jblezoray.stringart.image.ImageSize;

public class FrameBorderEdgeDrawerTest {

  @Test
  public void a_test() {
    int nbNails = 4;
    ImageSize size = new ImageSize(3, 4);
    StringCharacteristics sc = new StringCharacteristics(
        0.0f, 1.0f, nbNails, true, 0, Shape.FRAME_BORDER);
    
    var f = new FrameBorderEdgeDrawer(size, sc);
    byte[] rawBytes = f.drawAllNails().getRawBytes();
    
//    System.out.println(EdgeDrawerPrinter.toString(rawBytes, 3));
    Assert.assertArrayEquals(new byte[]{
      74, -1, 74,  
      -1, -1, -1,  
      -1, -1, -1,
      74, -1, 74, 
    }, rawBytes);
  }

  @Test
  public void a_test_2() {
    int nbNails = 7;
    ImageSize size = new ImageSize(12, 3);
    StringCharacteristics sc = new StringCharacteristics(
        0.0f, 1.0f, nbNails, true, 0, Shape.FRAME_BORDER);
    
    var f = new FrameBorderEdgeDrawer(size, sc);
    byte[] rawBytes = f.drawAllNails().getRawBytes();
    
//    System.out.println(EdgeDrawerPrinter.toString(rawBytes, 12));
    Assert.assertArrayEquals(new byte[]{
      74, -1, -1, 74, -1, -1, -1, 74, -1, -1, -1, 74, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  
      74, -1, -1, -1, -1, 74, -1, -1, -1, -1, -1, 74,  
    }, rawBytes);
  }
}
