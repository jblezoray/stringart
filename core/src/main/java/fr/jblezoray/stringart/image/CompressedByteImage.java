package fr.jblezoray.stringart.image;

import java.io.ByteArrayOutputStream;


/**
 * A memory-optimized representation of an Image.   
 * 
 * The internal format is an array of bytes having each odd byte being a
 * numerator that indicates how many times the next byte must appear. For 
 * instance: <code>03 AE 01 04 02 FF</code> encodes the byte array  
 * <code>AE AE AE 04 FF FF</code>.  This method is efficient for compressing 
 * arrays iif the byte array has a lots of repeating elements.   
 *    
 * @return a compressed array.
 */
public class CompressedByteImage implements Image {
  
  private final byte[] compressedData;
  private final ImageSize size;

  public CompressedByteImage(ImageSize size, byte[] imageBytes) {
    this.size = size;
    this.compressedData = compressData(imageBytes);
  }
  
  private byte[] compressData(byte[] imageBytes) {
    if (imageBytes.length<=0) 
      throw new RuntimeException("invalid image: size is 0");
    
    int prevPixel = Byte.toUnsignedInt(imageBytes[0]);
    short counter = 1;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i=1; i<imageBytes.length; i++) {
      int pixel = Byte.toUnsignedInt(imageBytes[i]);
      if (pixel==prevPixel && counter<0xFF) {
        counter++;
      } else {
        baos.write(counter);
        baos.write(prevPixel);
        prevPixel = pixel;
        counter = 1;
      }
    }
    baos.write(counter);
    baos.write(prevPixel);
    return baos.toByteArray(); 
  }
  
  @Override
  public ImageSize getSize() {
    return this.size;
  }
  
  public byte[] getCompressedData() {
    return compressedData;
  }
  
  
  /**
   * Decompress the data as a ByteImage.
   */
  @Override
  public ByteImage asByteImage() {
    byte[] reconstructedData = new byte[this.size.nbPixels];
    
    int bytesIndex = 0;
    for (int i=0; i<this.compressedData.length; i+=2) {
      
      int howManyPixel = Byte.toUnsignedInt(this.compressedData[i]);
      byte pixel = this.compressedData[i+1];
      
      // 0xFF is the identity transformation
      if (pixel!=0xFF) {
        for (int j=bytesIndex; j<bytesIndex+(howManyPixel); j++) {
          reconstructedData[j] = pixel;
        }
      }
      bytesIndex += howManyPixel;
    }
    return new ByteImage(this.size, reconstructedData);
  }
  
}
