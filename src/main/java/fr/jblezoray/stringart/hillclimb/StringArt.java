package fr.jblezoray.stringart.hillclimb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.hillclimb.listeners.IStringArtAlgoListener;
import fr.jblezoray.stringart.image.Image;
import fr.jblezoray.stringart.image.UnboundedImage;

public class StringArt {

  private final Image referenceImg;
  private final Image importanceImg;
  private final Configuration configuration;

  private Set<IStringArtAlgoListener> processingResultListeners = new HashSet<>();
  
  public StringArt(Configuration configuration) throws IOException {
    this.referenceImg = EdgeImageIO.readFile(configuration.getGoalImagePath());
    this.importanceImg = EdgeImageIO.readFile(configuration.getImportanceImagePath());
    this.configuration = configuration;
  }

  public void start() {
    double downsampleRatio = 1.0 / Math.pow(2.0, 6);
    int roundCounter =0;
    var edges = new ArrayList<DirectedEdge>();
    while (downsampleRatio < 1.01) {
      Image referenceImgDownsized = this.referenceImg.downsample(downsampleRatio);
      Image importanceImgDownsized = this.importanceImg.downsample(downsampleRatio);
      
      float resolutionMmPerPx = configuration.getCanvasWidthMilimeters() / referenceImgDownsized.getSize().w;
      float lineThicknessInPx = configuration.getThreadThicknessMilimeters() / resolutionMmPerPx;
      float nailDiameterInPx = configuration.getNailDiameterMilimeters() / resolutionMmPerPx;
      var sc = new StringCharacteristics(lineThicknessInPx, 
          nailDiameterInPx, configuration.getNbNails(), configuration.isEdgeWayEnabled(),
          configuration.getMinNailsDiff());
      var hillClimb = new StringArtHillClimb(edges, referenceImgDownsized, 
          importanceImgDownsized, sc);
      
      int previousNorm = Integer.MAX_VALUE;
      int thisNorm = Integer.MAX_VALUE-1;
      // try increasing the norm by adding edges.
      while (thisNorm < previousNorm) { 
        previousNorm = thisNorm;
        DirectedEdge addedEdge = hillClimb.addBestPossibleEdge();
        this.notifyResultToListeners("Add", ++roundCounter, edges, 
            hillClimb.getRenderedResult(), addedEdge, hillClimb.getNorm(), 
            hillClimb.getNumberOfEdgesEvaluated(), hillClimb.getTimeTook());
        thisNorm = (int) hillClimb.getNorm();
      }
      // try lowering norm by removing edges 
      while (thisNorm < previousNorm) { 
        previousNorm = thisNorm;
        DirectedEdge removedEdge = hillClimb.removeWorstEdge();
        this.notifyResultToListeners("Rem", ++roundCounter, edges,
            hillClimb.getRenderedResult(), removedEdge, hillClimb.getNorm(), 
            hillClimb.getNumberOfEdgesEvaluated(), hillClimb.getTimeTook());
        thisNorm = (int) hillClimb.getNorm();
      }

      downsampleRatio *= 2.0;
    }
    // TODO final event
  }
  
  
  private void notifyResultToListeners(String operationDescription, 
      int iterationNumber, List<DirectedEdge> edges,
      UnboundedImage currentImage, DirectedEdge addedEdge, double norm,
      int numberOfEdgesEvaluated, long timeTook) {
    this.processingResultListeners.forEach(listener -> 
        listener.notifyRoundResults(operationDescription, iterationNumber, 
            currentImage, edges, importanceImg, referenceImg, addedEdge, norm, 
            numberOfEdgesEvaluated, timeTook)
    );
  }
  
  
  public void addListener(IStringArtAlgoListener listener) {
    this.processingResultListeners.add(listener);
  }
}
