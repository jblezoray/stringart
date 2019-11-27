package fr.jblezoray.stringart;

import java.io.File;
import java.util.Optional;

import fr.jblezoray.stringart.hillclimb.StringCharacteristics.Shape;

public class Configuration {

  /**
   * The reference image.
   * 
   * The prurpose of the program is to produce a string image that has the 
   * smallest difference with this image. 
   */
  private File goalImage;

  /**
   * The importance image.
   * 
   * Each pixel the importance image describes the influence of the
   * corresponding pixel in the reference image.  
   * A value of 0x00 implies that the pixel does not contributes to the 
   * fitness. A value of OxFF corresponds to the maximum influence possible.
   * Therefore, the brighter the zone are in the importanceMappingImage, the
   * more they represent important features of the reference image.
   * 
   * if not present, the approximation image will be determined automatically.
   */
  private Optional<File> importanceImage = Optional.empty();
      
  /** 
   * Enable to let the algorithm distinguish between the string going clockwise 
   * or anticlockwise. 
   * 
   * Disable to get faster processing, enable to lessen the moiré effect. 
   */
  private boolean edgeWayEnabled = true;
  
  private float canvasWidthMilimeters = 630.0f;
  
  private float threadThicknessMilimeters = 0.15f;
  
  private float nailDiameterMilimeters = 2.0f;
  
  private int nbNails = 200;
  
  private int minNailsDiff = Math.max(1, (int)nbNails/20);

  private Shape shape = Shape.CIRCLE;
  
  
  public void setShape(Shape shape) {
    this.shape = shape;
  }
  public File getGoalImage() {
    return goalImage;
  }
  public void setGoalImage(File goalImage) {
    this.goalImage = goalImage;
  }
  public Optional<File> getImportanceImage() {
    return importanceImage;
  }
  public void setImportanceImage(Optional<File> importanceImage) {
    this.importanceImage = importanceImage;
  }
  public boolean isEdgeWayEnabled() {
    return edgeWayEnabled;
  }
  public void setEdgeWayEnabled(boolean edgeWayEnabled) {
    this.edgeWayEnabled = edgeWayEnabled;
  }
  public float getCanvasWidthMilimeters() {
    return canvasWidthMilimeters;
  }
  public void setCanvasWidthMilimeters(float canvasWidthMilimeters) {
    this.canvasWidthMilimeters = canvasWidthMilimeters;
  }
  public float getThreadThicknessMilimeters() {
    return threadThicknessMilimeters;
  }
  public void setThreadThicknessMilimeters(float threadThicknessMilimeters) {
    this.threadThicknessMilimeters = threadThicknessMilimeters;
  }
  public float getNailDiameterMilimeters() {
    return nailDiameterMilimeters;
  }
  public void setNailDiameterMilimeters(float nailDiameterMilimeters) {
    this.nailDiameterMilimeters = nailDiameterMilimeters;
  }
  public int getNbNails() {
    return nbNails;
  }
  public void setNbNails(int nbNails) {
    this.nbNails = nbNails;
  }
  public int getMinNailsDiff() {
    return minNailsDiff;
  }
  public void setMinNailsDiff(int minNailsDiff) {
    this.minNailsDiff = minNailsDiff;
  }
  public Shape getShape() {
    return shape;
  }

}
