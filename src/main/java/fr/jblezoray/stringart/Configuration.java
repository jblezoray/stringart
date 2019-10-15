package fr.jblezoray.stringart;

public class Configuration {

  /**
   * The reference image.
   * 
   * The prurpose of the program is to produce a string image that has the 
   * smallest difference with this image. 
   */
  private String goalImagePath = "test2/einstein.png";

  /**
   * The importance image.
   * 
   * Each pixel the importance image describes the influence of the
   * corresponding pixel in the reference image.  
   * A value of 0x00 implies that the pixel does not contributes to the 
   * fitness. A value of OxFF corresponds to the maximum influence possible.
   * Therefore, the brighter the zone are in the importanceMappingImage, the
   * more they represent important features of the reference image.
   */
  private String importanceImagePath = "test2/einstein_features2.png";
  
  /**
   * Filename for the result.  
   */
  private String renderedImageName = "_rendering.png";

  /**
   * Filename for saving a pixel to pixel difference between the result and the 
   * reference image.  
   */
  private String renderedImageDifferenceName = "_diff.png";

  /** 
   * Filename for the saving the string path. 
   */
  private String renderedStringPathFilename = "_stringPath.txt";
  
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
  
  
  public String getGoalImagePath() {
    return goalImagePath;
  }
  public void setGoalImagePath(String goalImagePath) {
    this.goalImagePath = goalImagePath;
  }
  public String getImportanceImagePath() {
    return importanceImagePath;
  }
  public void setImportanceImagePath(String importanceImagePath) {
    this.importanceImagePath = importanceImagePath;
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
  public String getRenderedImageName() {
    return renderedImageName;
  }
  public void setRenderedImageName(String renderedImageName) {
    this.renderedImageName = renderedImageName;
  }
  public String getRenderedImageDifferenceName() {
    return renderedImageDifferenceName;
  }
  public void setRenderedImageDifferenceName(String renderedImageDifferenceName) {
    this.renderedImageDifferenceName = renderedImageDifferenceName;
  }
  public String getRenderedStringPathFilename() {
    return renderedStringPathFilename;
  }
  public void setRenderedStringPathFilename(String renderedStringPathFilename) {
    this.renderedStringPathFilename = renderedStringPathFilename;
  }
  
  
  
  

}
