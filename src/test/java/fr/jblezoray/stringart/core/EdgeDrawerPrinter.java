package fr.jblezoray.stringart.core;

public class EdgeDrawerPrinter {

  public static String toString(byte[] bytes, int lineW) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<bytes.length; i++) {
      byte b = bytes[i];
      sb.append(String.format("%02x ", Byte.toUnsignedInt(b)));
      if (i%lineW==lineW-1)
        sb.append('\n');
    }
    return sb.toString();
  }
}
