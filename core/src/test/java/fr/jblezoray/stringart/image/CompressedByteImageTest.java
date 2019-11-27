package fr.jblezoray.stringart.image;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.core.EdgeImageIO;

public class CompressedByteImageTest {

  @Test
  public void test_compress_decompress() throws IOException {
    ByteImage original = EdgeImageIO.readFile(new File("../samples/avatar/goal_image.png"));
    
    var compressed = new CompressedByteImage(original.getSize(), original.getRawBytes());
    
    Assertions.assertArrayEquals(original.getRawBytes(), compressed.asByteImage().getRawBytes());
    Assertions.assertEquals(original.getSize(), compressed.getSize());
  }
}
