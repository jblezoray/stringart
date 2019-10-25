package fr.jblezoray.stringart.core;

import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.image.ImageSize;

public class EdgeDrawerFactory {

  public static EdgeDrawer build(
      ImageSize size, 
      StringCharacteristics sc) {
    EdgeDrawer r;
    switch (sc.getShape()) {
    case FRAME_BORDER:
      r = new FrameBorderEdgeDrawer(size, sc);
      break;
    default:
    case CIRCLE: 
      r = new CircleEdgeDrawer(size, sc);
      break;
    }
    return r;
  }
  
}
