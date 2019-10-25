package fr.jblezoray.stringart.core;

import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.image.ImageSize;

public class FrameBorderEdgeDrawer extends EdgeDrawerWithCache {

  /** number of nails on the top border, excluding the one on the right corner */
  private int nbNailsOnTop;
  
  /** number of nails on the right border, excluding the one on the bottom corner */
  private int nbNailsOnRight;

  /** number of nails on the bottom border, excluding the one on the left corner */
  private int nbNailsOnBottom;

  /** number of nails on the left border, excluding the one on the top corner */
  private int nbNailsOnLeft;


  
  FrameBorderEdgeDrawer(ImageSize size, StringCharacteristics sc) {
    super(size, sc);
    if (totalNumberOfNails<4) throw new RuntimeException("Frame border shape requires at least 4 nails.");
    double hRatio = (double)size.h / (double)(size.h+size.w);
    double wRatio = 1.0 - hRatio;
    int nbIntermediateNailsOnVerticals   = (int)(hRatio * (totalNumberOfNails - 4) / 2);
    int nbIntermediateNailsOnHorizontals = (int)(wRatio * (totalNumberOfNails - 4) / 2);
    this.nbNailsOnTop = 1 + nbIntermediateNailsOnHorizontals;
    this.nbNailsOnBottom = 1 + nbIntermediateNailsOnHorizontals;
    this.nbNailsOnLeft = 1 + nbIntermediateNailsOnVerticals;
    this.nbNailsOnRight = 1 + nbIntermediateNailsOnVerticals;
    int remainder = totalNumberOfNails - this.nbNailsOnTop - this.nbNailsOnBottom
        - this.nbNailsOnLeft - this.nbNailsOnRight;
    while(remainder>0) {
      this.nbNailsOnTop += remainder>0 ? remainder-- : 0;
      this.nbNailsOnBottom += remainder>0 ? remainder-- : 0;
      this.nbNailsOnLeft += remainder>0 ? remainder-- : 0;
      this.nbNailsOnRight += remainder>0 ? remainder-- : 0;
    }
  }
  
  @Override
  protected int xNail2Position(int nailIndex) {
    return 
        nailIndex <= nbNailsOnTop 
        ? (int)((size.w-1) * ((double)nailIndex / nbNailsOnTop))
        : nailIndex <= nbNailsOnTop + nbNailsOnRight
        ? size.w-1
        : nailIndex <= nbNailsOnTop + nbNailsOnRight + nbNailsOnBottom 
        ? (int)((size.w-1) - (size.w-1) * ((double)(nailIndex - nbNailsOnTop - nbNailsOnRight) / nbNailsOnBottom ))
        : 0;
  }

  @Override
  protected int yNail2Position(int nailIndex) {
    return 
        nailIndex <= nbNailsOnTop 
        ? 0
        : nailIndex <= nbNailsOnTop + nbNailsOnRight
        ? (int)((size.h-1) - (size.h-1) * ((double)(nailIndex - nbNailsOnTop-1) / nbNailsOnRight))
        : nailIndex <= nbNailsOnTop + nbNailsOnRight + nbNailsOnBottom 
        ? size.h - 1
        : (int)((size.h-1) * ((double)(nailIndex - nbNailsOnTop - nbNailsOnRight - nbNailsOnBottom) / nbNailsOnLeft));
  }
  
  @Override
  protected int xNail2ThreadPosition(int nailIndex, boolean clockwise) {
    int nailX = xNail2Position(nailIndex);
    int nailY = yNail2Position(nailIndex);
    int pxDeviation = (nailY == 0 ^ clockwise ? 1 : -1) * this.nailPxRadiusInt / 2;
    return nailX + pxDeviation;
  }

  @Override
  protected int yNail2ThreadPosition(int nailIndex, boolean clockwise) {
    int nailX = xNail2Position(nailIndex);
    int nailY = yNail2Position(nailIndex);
    int pxDeviation = (nailX == 0 ^ clockwise ? 1 : -1) * this.nailPxRadiusInt / 2;
    return nailY + pxDeviation;
  }
  
}
