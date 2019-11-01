package fr.jblezoray.stringart.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;
import java.util.Map;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.image.ByteImage;
import fr.jblezoray.stringart.image.CompressedByteImage;
import fr.jblezoray.stringart.image.ImageSize;

abstract class EdgeDrawerWithCache implements EdgeDrawer {
  
  private Map<Edge, CompressedByteImage> cache;
  
  /**
   * Diameter of the nails.
   */
  protected final int nailPxRadiusInt;
  
  protected final ImageSize size;
  
  protected final int totalNumberOfNails;
  
  /**
   * Width of the lines (threads) when represented in the image.  
   */
  protected final float lineThicknessInPx;
  
  public EdgeDrawerWithCache(
      ImageSize size, 
      StringCharacteristics sc) { 
    this.cache = new HashMap<>();
    this.nailPxRadiusInt = Math.max(1, (int)sc.getNailDiameterInPx());
    this.lineThicknessInPx = sc.getLineThicknessInPx();
    this.size = size;
    this.totalNumberOfNails = sc.getNbNails(); 
  }
  
  @Override
  final public ByteImage drawAllNails() {
    
    // The implementation is obviously suboptimal, but we don't care as it's 
    // meant to be run sparcely.
    
    BufferedImage image = new BufferedImage(
        this.size.w, this.size.h, BufferedImage.TYPE_BYTE_GRAY);
    
    Graphics2D graphics2D = null;
    try {
      // create a new blank image. 
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, this.size.w, this.size.h);
      
      // draw all nails
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setColor(Color.BLACK);
      for (int i=0; i<this.totalNumberOfNails; i++) {
        int x = xNail2Position(i); 
        int y = yNail2Position(i);
        graphics2D.fillOval(
            x-nailPxRadiusInt/2, y-nailPxRadiusInt/2, 
            nailPxRadiusInt, nailPxRadiusInt);
      }
      
    } finally {
      if (graphics2D!=null) graphics2D.dispose();
    }
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    return new ByteImage(size, pixels);
  }

  /**
   * Rendering of an edge in the image.
   * 
   * This method is slow.  
   *   
   * @param nailA
   * @param nailAClockwise
   * @param nailB
   * @param nailBClockwise
   * @return
   */
  @Override
  final public CompressedByteImage drawEdge(Edge edge) {
    CompressedByteImage imageData;
    if (this.cache.containsKey(edge)) {
      imageData = this.cache.get(edge);
      
    } else {
      imageData = this.drawEdgeNoCache(edge);
      this.cache.put(edge, imageData);
    }
    return imageData;
  }

  private CompressedByteImage drawEdgeNoCache(Edge edge) {
    BufferedImage image = new BufferedImage(
        this.size.w, this.size.h, BufferedImage.TYPE_BYTE_GRAY);
    
    Graphics2D graphics2D = null;
    try {
      // create a new blank image. 
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, this.size.w, this.size.h);
      
      // draw line 
      graphics2D.setColor(Color.BLACK);
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setStroke(new BasicStroke(this.lineThicknessInPx));
      graphics2D.drawLine(
          xNail2ThreadPosition(edge.getNailA(), edge.isNailAClockwise()), 
          yNail2ThreadPosition(edge.getNailA(), edge.isNailAClockwise()), 
          xNail2ThreadPosition(edge.getNailB(), edge.isNailBClockwise()), 
          yNail2ThreadPosition(edge.getNailB(), edge.isNailBClockwise()));
      
    } finally {
      if (graphics2D!=null) graphics2D.dispose();
    }

    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    return new CompressedByteImage(size, pixels);
  }

  
  /**
   * returns a x position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  protected abstract int xNail2Position(int nailIndex);

  /**
   * returns a y position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  protected abstract int yNail2Position(int nailIndex);

  /**
   * returns a x position in the image for a string just after its revolution 
   * around a nail.
   *  
   * @param nailIndex the nail.
   * @param clockwise turn clockwise or anticlockwise.
   * @return
   */
  protected abstract int xNail2ThreadPosition(int nailIndex, boolean clockwise);

  /**
   * returns a y position in the image for a string just after its revolution 
   * around a nail.
   *  
   * @param nailIndex the nail.
   * @param clockwise turn clockwise or anticlockwise.
   * @return
   */
  protected abstract int yNail2ThreadPosition(int nailIndex, boolean clockwise);

}
