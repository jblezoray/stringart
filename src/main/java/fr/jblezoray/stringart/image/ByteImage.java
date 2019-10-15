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
}
