package fr.jblezoray.stringart.image;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.core.EdgeImageIO;

public class HarrisCornerDetectionTest {
  
  @Test
  public void test_no_exceptions() throws IOException {
    ByteImage bi = EdgeImageIO.readResource("lenna.jpg");
    
    HarrisCornerDetection harris = new HarrisCornerDetection();
    harris.setK(0.05f);
    harris.setDumpDebugImages(true);
    harris.compute(bi);
  }
}
