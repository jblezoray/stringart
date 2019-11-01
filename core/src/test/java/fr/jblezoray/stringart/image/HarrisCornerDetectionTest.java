package fr.jblezoray.stringart.image;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.core.EdgeImageIO;

@Disabled("not a test")
public class HarrisCornerDetectionTest {
  
  @Test
  public void test() throws IOException {
    Configuration configuration = new Configuration();  

    ByteImage bi = EdgeImageIO.readFile(configuration.getGoalImagePath());
//    bi = bi.downsample(0.1);
    
    HarrisCornerDetection harris = new HarrisCornerDetection();
    harris.setK(0.05f);
    harris.setDumpDebugImages(true);
    Image result = harris.compute(bi);
    result.minFilter(0x30);
  }
}
