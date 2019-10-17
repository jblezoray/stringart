package fr.jblezoray.stringart.hillclimb;

public class StringCharacteristics {
  private final float lineThicknessInPx;
  private final float nailDiameterInPx;
  private final int nbNails;
  private final boolean edgeWayEnabled;
  private final int minimumNailsDistance;

  public StringCharacteristics(
      float lineThicknessInPx, 
      float nailDiameterInPx, 
      int nbNails, 
      boolean edgeWayEnabled,
      int minimumNailsDistance
  ) {
    this.lineThicknessInPx = lineThicknessInPx;
    this.nailDiameterInPx = nailDiameterInPx;
    this.nbNails = nbNails;
    this.edgeWayEnabled = edgeWayEnabled;
    this.minimumNailsDistance = minimumNailsDistance;
  }

  public float getLineThicknessInPx() {
    return lineThicknessInPx;
  }

  public float getNailDiameterInPx() {
    return nailDiameterInPx;
  }

  public int getNbNails() {
    return nbNails;
  }

  public boolean isEdgeWayEnabled() {
    return edgeWayEnabled;
  }

  public int getMinimumNailsDistance() {
    return minimumNailsDistance;
  }
  
  
}
