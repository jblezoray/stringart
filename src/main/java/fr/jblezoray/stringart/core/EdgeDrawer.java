package fr.jblezoray.stringart.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.image.ByteImage;
import fr.jblezoray.stringart.image.CompressedByteImage;
import fr.jblezoray.stringart.image.ImageSize;

/**
 * EdgeDrawer can render an Edge as an image (CompressedByteImage).
 * @author jbl
 */
public class EdgeDrawer {

  private final ImageSize size;
  private final int totalNumberOfNails;
  
  /**
   * Width of the lines (threads) when represented in the image.  
   */
  private final float lineThicknessInPx;

  /**
   * Diameter of the nails.
   */
  private final int nailPxRadiusInt;
  
  public EdgeDrawer(ImageSize size, int totalNumberOfNails, 
      float lineThicknessInPx, float nailDiameterInPx) {
    this.size = size;
    this.totalNumberOfNails = totalNumberOfNails; 
    this.lineThicknessInPx = lineThicknessInPx;
    this.nailPxRadiusInt = Math.max(1, (int)nailDiameterInPx);
  }
  
  /**
   * Draws all the nails in the image. 
   * 
   * The implementation is obviously suboptimal, but we don't care as it's meant
   * to be run only once.
   * 
   * @param original
   */
  public ByteImage drawAllNails() {
    
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
   * returns a x position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  private int xNail2Position(int nailIndex) {
    double sinX = Math.sin(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(sinX*(this.size.w/2) + (this.size.w/2));
  }


  /**
   * returns a y position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  private int yNail2Position(int nailIndex) {
    double cosY = Math.cos(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(-cosY*(this.size.h/2) + (this.size.h/2));
  }

  
  /**
   * Rendering of the image.
   * 
   * This method is slow.  
   *   
   * @param nailA
   * @param nailAClockwise
   * @param nailB
   * @param nailBClockwise
   * @return
   */
  public CompressedByteImage drawEdge(Edge edge) {
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
   * returns a x position in the image for a string just after its revolution 
   * around a nail.
   *  
   * @param nailIndex the nail.
   * @param clockwise turn clockwise or anticlockwise.
   * @return
   */
  private int xNail2ThreadPosition(int nailIndex, boolean clockwise) {
    double imgCenter = this.size.w / 2.0d;
    double nailDeviation = 2.0d*Math.PI*nailIndex/this.totalNumberOfNails;
    double wayDeviation = (double)this.nailPxRadiusInt/this.size.w;
    double way = clockwise ? -1.0d : 1.0d;
    return (int)((1 + Math.sin(nailDeviation + way * wayDeviation)) * imgCenter);
  }


  /**
   * returns a y position in the image for a string just after its revolution 
   * around a nail.
   *  
   * @param nailIndex the nail.
   * @param clockwise turn clockwise or anticlockwise.
   * @return
   */
  private int yNail2ThreadPosition(int nailIndex, boolean clockwise) {
    double imgCenter = this.size.h / 2.0d;
    double nailDeviation = 2.0d*Math.PI*nailIndex/this.totalNumberOfNails;
    double wayDeviation = (double)this.nailPxRadiusInt/this.size.h;
    double way = clockwise ? -1.0d : 1.0d;
    return (int)((1 - Math.cos(nailDeviation + way * wayDeviation)) * imgCenter);
  }
  
  
}
