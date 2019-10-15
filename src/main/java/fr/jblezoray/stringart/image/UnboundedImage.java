package fr.jblezoray.stringart.image;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * We here use integer to represent bytes, thus not limiting the range of 
 * image data to [0,255].
 * 
 * Therefore, any addition of a ByteImage in this image is reversible by 
 * substracting it.    
 */
public class UnboundedImage implements Image {

  private final int[] unboundedBytes;
  private final ImageSize size;
  
  /**
   * A cached rendering of the 'unboundedBytes' as a ByteImage.
   * 
   * This field MUST be reseted to null whenever unboundedBytes is modified.
   */
  private ByteImage clampedCopy;

  /** 
   * Blank image constructor (filed with 0xFF).  
   * @param size
   */
  public UnboundedImage(ImageSize size) {
    this.size = size;
    this.unboundedBytes = new int[this.size.nbPixels];
    Arrays.fill(this.unboundedBytes, 0xFF);
  }
  
  public UnboundedImage(ImageSize size, int[] bytes) {
    this.size = size;
    this.unboundedBytes = bytes;
  }
  
  /**
   * Constructs a deep copy.
   * @return
   */
  public UnboundedImage deepCopy() {
    return new UnboundedImage(
        this.size, 
        Arrays.copyOf(this.unboundedBytes, this.unboundedBytes.length));
  }
  
  
  /** 
   * Clamps all unbounded ints in [0x00, 0xFF], and build a copy from it. 
   */
  @Override
  public ByteImage asByteImage() {
    if (clampedCopy==null) {
      synchronized (this) {
        if (clampedCopy==null) {
          byte[] clampedBytes = new byte[this.size.nbPixels];
          for(int i=0; i<this.unboundedBytes.length; i++) {
            clampedBytes[i] = (byte)(0x00>this.unboundedBytes[i] ? 0x00 : 
                this.unboundedBytes[i]>0xFF ? 0xFF : this.unboundedBytes[i]);
          }
          this.clampedCopy = new ByteImage(size, clampedBytes);
        }
      }
    }
    return clampedCopy;
  }

  @Override
  public ImageSize getSize() {
    return this.size;
  }

  /**
   * perform a pixel to pixel addition of 'image' in this image.
   * @param image
   * @return itself.
   */
  public UnboundedImage add(CompressedByteImage image){
    this.clampedCopy = null;
    byte[] compressedData = image.getCompressedData();
    int bytesIndex = 0;
    for (int i=0; i<compressedData.length; i+=2) {
      
      int howManyPixel = Byte.toUnsignedInt(compressedData[i]);
      int pixel = Byte.toUnsignedInt(compressedData[i+1]);
      
      if (pixel!=0xFF) {
        for (int j=bytesIndex; j<bytesIndex+(howManyPixel); j++) {
          this.unboundedBytes[j] += pixel - 0xFF;
        }
      }
      bytesIndex += howManyPixel;
    }
    return this;
  }

  /**
   * perform a pixel to pixel deletion of 'image' from this image.
   * @param image
   * @return itself.
   */
  public UnboundedImage remove(CompressedByteImage image) {
    this.clampedCopy = null;
    byte[] compressedData = image.getCompressedData();
    int bytesIndex = 0;
    for (int i=0; i<compressedData.length; i+=2) {
      
      int howManyPixel = Byte.toUnsignedInt(compressedData[i]);
      int pixel = Byte.toUnsignedInt(compressedData[i+1]);
      
      if (pixel!=0xFF) {
        for (int j=bytesIndex; j<bytesIndex+(howManyPixel); j++) {
          this.unboundedBytes[j] -= pixel - 0xFF;
        }
      }
      bytesIndex += howManyPixel;
    }
    return this;
  }

  /**
   * perform a pixel to pixel addition of 'image' in this image.
   * @param image
   * @return itself.
   */
  public UnboundedImage add(ByteImage image){
    this.clampedCopy = null;
    byte[] data = image.getRawBytes();
    for (int i=0; i<data.length; i++) {
      int pixel = Byte.toUnsignedInt(data[i]);
      if (pixel!=0xFF) {
        this.unboundedBytes[i] += pixel - 0xFF;
      }
    }
    return this;
  }


  /**
   * perform a pixel to pixel addition of 'image' in this image.
   * @param image
   * @return itself.
   */
  public UnboundedImage add(UnboundedImage image) {
    this.clampedCopy = null;
    for (int i=0; i<image.unboundedBytes.length; i++) {
      this.unboundedBytes[i] += image.unboundedBytes[i] - 0xFF; 
    }
    return this;
  }

  public IntStream intStream() {
    return IntStream.of(this.unboundedBytes);
  }

  /**
   * for tests only.
   * @return
   */
  public int[] getRawIntegers() {
    return this.unboundedBytes;
  }

}
