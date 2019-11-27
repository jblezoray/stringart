package fr.jblezoray.stringart.core;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.image.ByteImage;


public class EdgeImageIOTest {

  @Test
  public void test_readFile() throws IOException {
    EdgeImageIO.readFile("src/test/resources/lenna.jpg");
  }
  
  @Test
  public void test_color_jpg() throws IOException {
    EdgeImageIO.readResource("lenna.jpg");
  }
  
  @Test
  public void test_not_square() throws IOException {
    EdgeImageIO.readResource("not_square.png");
  }
  
  @Test
  public void test_bw() throws IOException {
    EdgeImageIO.readResource("test_image.png");
  }
  
  @Test
  public void test_write() throws IOException {
    ByteImage bi = EdgeImageIO.readResource("test_image.png");
    File tempFile = File.createTempFile("TMP", ".tmp");
    
    EdgeImageIO.writeToFile(bi, tempFile);
    
    Assertions.assertTrue(tempFile.exists());
  }

}
