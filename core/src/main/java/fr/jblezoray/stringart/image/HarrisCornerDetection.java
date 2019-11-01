package fr.jblezoray.stringart.image;

import java.io.File;
import java.io.IOException;

import fr.jblezoray.stringart.core.EdgeImageIO;

/**
 * A naive implementation of a Harris Corner Detection Algorithm.
 * 
 * see :
 * - https://en.wikipedia.org/wiki/Harris_Corner_Detector
 * - https://algo.developpez.com/actu/3637
 * 
 * @author jbl
 */
public class HarrisCornerDetection {
  
  public static final class Corner {
    int x, y;
  }

  private float k = 0.05f;
  private boolean dumpDebugImages = false;

  private int[][] pixels;
  private int width;
  private int height;

  private int[][] xDerivative;
  private int[][] yDerivative;

  private int[][] xxDerivative;
  private int[][] xyDerivative;
  private int[][] yyDerivative;
  
  private int[][] xxMoment;
  private int[][] xyMoment;
  private int[][] yyMoment;
  
  private int[][] cornerResponse;

  /**
   * An empirical value
   * @param k between 0.04 and 0.06
   */
  public void setK(float k) {
    this.k = k;
  }
  
  /**
   * when true, will write image files for each step. 
   * @param dumpDebugImages
   */
  public void setDumpDebugImages(boolean dumpDebugImages){
    this.dumpDebugImages = dumpDebugImages;
  }
  
  /**
   * Compute Harris Corner Detector.
   * 
   * @param bi input image.
   * @return Harris Corner Detector response.
   */
  public UnboundedImage compute(ByteImage bi) {
    this.pixels = bi.asTwoDimensionalArray();
    this.width = bi.getSize().w;
    this.height = bi.getSize().h;
    
    this.xDerivative = computeXDerivative(this.pixels);
    this.yDerivative = transpose(computeXDerivative(transpose(this.pixels)));
    debugDumpImage(xDerivative, "_xDerivative.png");
    debugDumpImage(yDerivative, "_yDerivative.png");

    this.xxDerivative = multiply(this.xDerivative, this.xDerivative);
    this.xyDerivative = multiply(this.xDerivative, this.yDerivative);
    this.yyDerivative = multiply(this.yDerivative, this.yDerivative);
    debugDumpImage(xxDerivative, "_xxDerivative.png");
    debugDumpImage(xyDerivative, "_xyDerivative.png");
    debugDumpImage(yyDerivative, "_yyDerivative.png");
    
    computeSecondMomentMatrix(5);
    debugDumpImage(xxMoment, "_xxMoment.png");
    debugDumpImage(xyMoment, "_xyMoment.png");
    debugDumpImage(yyMoment, "_yyMoment.png");
    
    computeCornerResponse();
    debugDumpImage(cornerResponse, "_cornerResponse.png");
    
    return new UnboundedImage(new ImageSize(width, height), cornerResponse);
  }

  private void computeSecondMomentMatrix(int windowSize) {
    this.xxMoment = new int[width][height];
    this.xyMoment = new int[width][height];
    this.yyMoment = new int[width][height];
    
    // for each pixel
    for (int x=0; x<width; x++) {
      for (int y=0; y<height; y++) {
        
        int xxm=0, xym=0, yym=0; 
        
        // for each pixel of the window around the pixel
        for(int dx=-windowSize; dx<windowSize; dx++) {
          int wx = x+dx;
          wx = (wx<0) ? 0 : (wx>=width) ? width-1 : wx;  
          for(int dy=-windowSize; dy<windowSize; dy++) {
            int wy = y+dy;
            wy = (wy<0) ? 0 : (wy>=height) ? width-1 : wy;

            xxm += this.xxDerivative[wx][wy];
            xym += this.xyDerivative[wx][wy];
            yym += this.yyDerivative[wx][wy];
          }
        }
        
        this.xxMoment[x][y] = xxm;
        this.xyMoment[x][y] = xym;
        this.yyMoment[x][y] = yym;
        
      }
    }
  }

  private void debugDumpImage(int[][] pixels, String imageName) {
    if (!dumpDebugImages) return;
    var size = new ImageSize(width, height);
    var img = new UnboundedImage(size, pixels);
    var file = new File(imageName);
    try {
      EdgeImageIO.writeToFile(img, file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static int[][] computeXDerivative(int[][] pixels) {
    var w = pixels.length;
    var h = pixels[0].length;
    var xDerivative = new int[w][h];
    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        xDerivative[x][y] = computeSobelFeldmanGradiant(pixels, x, y);
      }
    }
    return xDerivative;
  }

  private static int computeSobelFeldmanGradiant(int[][] pixels, int x, int y) {
    int w = pixels.length;
    int h = pixels[0].length;
    int x0 = (x-1<0)?0:x-1;
    int y0 = (y-1<0)?0:y-1;
    int x2 = (x+1==w)?x:x+1;
    int y2 = (y+1==h)?y:y+1;
    return
          ( +  3*pixels[x0][y0] -  3*pixels[x2][y0]
            + 10*pixels[x0][y ] - 10*pixels[x2][y ]
            +  3*pixels[x0][y2] -  3*pixels[x2][y2]  ) / 32;
  }

  private static int[][] transpose(int[][] pixels) {
    var w = pixels.length;
    var h = pixels[0].length;
    var t = new int[h][w];
    for (int x=0; x<w; x++) {
      for (int y=0; y<h; y++) {
        t[y][x] = pixels[x][y];
      }
    }
    return t;
  }
  
  private int[][] multiply(int[][] a, int[][] b) {
    var product = new int[width][height];
    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        product[x][y] = a[x][y] * b[x][y];
      }
    }
    return product;
  }
  
  private void computeCornerResponse() {
    this.cornerResponse = new int[width][height];
    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        // det(M) - k * trace(M)^2
        double m00 = this.xxMoment[x][y]; 
        double m01 = this.xyMoment[x][y];
        double m10 = this.xyMoment[x][y];
        double m11 = this.yyMoment[x][y];
        double mesure = (m00*m11 - m01*m10) - k * (m00+m11) * (m00+m11);
        if (mesure<=0.0)
          mesure = - mesure;
        cornerResponse[x][y] = (int)(255 * Math.log(1+mesure) / Math.log(1+255));
      }
    }
  }
}
