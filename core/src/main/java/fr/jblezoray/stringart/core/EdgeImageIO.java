package fr.jblezoray.stringart.core;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import fr.jblezoray.stringart.image.ByteImage;
import fr.jblezoray.stringart.image.Image;
import fr.jblezoray.stringart.image.ImageSize;

public class EdgeImageIO {

  /**
   * Read the image, and convert it if it's not of the expected type. 
   * @param imagePath
   * @return
   */
  public static ByteImage readFile(String imagePath) throws IOException {
    return readFile(new File(imagePath));
  }

  /**
   * Read the image, and convert it if it's not of the expected type. 
   * @param imageFileName
   * @return
   */
  public static ByteImage readResource(String imageFileName) throws IOException {
    ClassLoader classLoader = EdgeImageIO.class.getClassLoader();
    InputStream imageIS = classLoader.getResourceAsStream(imageFileName);
    BufferedImage image = ImageIO.read(imageIS);
    return convertToByteImage(image);
  }

  /**
   * Read the image, and convert it if it's not of the expected type. 
   * @param imageFile
   * @return
   */
  public static ByteImage readFile(File imageFile) throws IOException {
    BufferedImage image = ImageIO.read(imageFile);
    if (image == null) {
      throw new IOException("can not read image "+imageFile.getAbsolutePath());
    }
    return convertToByteImage(image);
  }

  private static ByteImage convertToByteImage(BufferedImage image) {
    ImageSize size = new ImageSize(image.getWidth(), image.getHeight());
    if (image.getType() != TYPE_BYTE_GRAY) {
      BufferedImage convertedImg = new BufferedImage(size.w, size.h, TYPE_BYTE_GRAY);
      convertedImg.getGraphics().drawImage(image, 0, 0, null);
      image = convertedImg;
    }
    byte[] bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    return new ByteImage(size, bytes);
  }
  
  
  /**
   * Write as grayscaled image.
   * @param image
   * @param f
   * @throws IOException
   */
  public static void writeToFile(Image image, File f) throws IOException {
    ImageSize size = image.getSize();
    byte[] bytes = image.asByteImage().getRawBytes();
    BufferedImage result = new BufferedImage(size.w, size.h, TYPE_BYTE_GRAY);
    result.getRaster().setDataElements(0, 0, size.w, size.h, bytes);
    
    ImageIO.write(result, "png", f);
  }
  
}
