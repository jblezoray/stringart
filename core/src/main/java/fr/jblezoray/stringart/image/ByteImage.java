package fr.jblezoray.stringart.image;

public class ByteImage implements Image {

  private final byte[] bytes;
  private final ImageSize size;
  
  public ByteImage(ImageSize size, byte[] bytes) {
    this.size = size;
    this.bytes = bytes;
  }

  public ByteImage deepCopy() {
    return new ByteImage(this.size, this.bytes.clone());
  }
  
  public byte[] getRawBytes() {
    return bytes;
  }

  @Override
  public ByteImage asByteImage() {
    return this;
  }
  
  @Override
  public ImageSize getSize() {
    return this.size;
  }

  public int[][] asTwoDimensionalArray() {
    int[][] pixels = new int[this.size.w][this.size.h];
    for (int x=0; x<this.size.w; x++) {
      for (int y=0; y<this.size.h; y++) {
        pixels[x][y] = Byte.toUnsignedInt(bytes[y*this.size.w + x]);
      }
    }
    return pixels;
  }

  public ByteImage xDerivative() {
    byte[] derived = new byte[bytes.length];
    for (int y=0; y<size.h; y++) {
      for (int x=0; x<size.w-1; x++) {
        int i = y*size.w + x;
        int thiz = Byte.toUnsignedInt(bytes[i]);
        int next = Byte.toUnsignedInt(bytes[i+1]);
        derived[i] = (byte)(Math.abs(thiz - next));
        Math.abs(-(thiz+next)/2);
      }
      // approx last as penultimate 
      derived[(y+1)*size.w-1] = derived[(y+1)*size.w-2];
    }
    return new ByteImage(size, derived);
  }
  
  public ByteImage yDerivative() {
    byte[] derived = new byte[bytes.length];
    for (int x=0; x<size.w; x++) {
      int thiz = Byte.toUnsignedInt(bytes[x]);
      for (int y=0; y<size.h-1; y++) {
        int i = y*size.w + x;
        int below = Byte.toUnsignedInt(bytes[i+size.w]);
        derived[i] = (byte)(Math.abs(thiz - below));
        thiz = below;
      }
      // approx last as penultimate 
      derived[(size.h-1)*size.w+x] = derived[(size.h-2)*size.w+x];
    }
    return new ByteImage(size, derived);
  }
}
