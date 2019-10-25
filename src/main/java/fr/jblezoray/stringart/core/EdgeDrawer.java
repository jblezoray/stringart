package fr.jblezoray.stringart.core;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.image.ByteImage;
import fr.jblezoray.stringart.image.CompressedByteImage;


/**
 * EdgeDrawer can render an Edge as an image (CompressedByteImage).
 * @author jbl
 */
public interface EdgeDrawer {

  /**
   * Draws all the nails in the image. 
   * 
   * @param original
   */
  ByteImage drawAllNails();
  CompressedByteImage drawEdge(Edge edge);
}
