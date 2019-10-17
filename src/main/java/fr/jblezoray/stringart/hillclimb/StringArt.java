package fr.jblezoray.stringart.hillclimb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.edge.ScoredEdge;
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
    List<Edge> edges = new ArrayList<Edge>();
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
      while (thisNorm < previousNorm) { // stop if it does not reduces the norm.
        previousNorm = thisNorm;
        ScoredEdge addedEdge = hillClimb.makeRound();
        this.notifyResultToListeners(++roundCounter, edges, hillClimb.getRenderedResult(), addedEdge);
        thisNorm = (int) addedEdge.getNorm();
      }
      
      // TODO try lowering norm by removing edges 
      
      downsampleRatio *= 2.0;
    }
    // TODO final event
  }
  
  
  private void notifyResultToListeners(int iterationNumber, List<Edge> edges,
      UnboundedImage currentImage, ScoredEdge addedEdgeWithScore) {
    this.processingResultListeners.forEach(listener -> 
        listener.notifyRoundResults(iterationNumber, currentImage, edges,
            importanceImg, referenceImg, addedEdgeWithScore)
    );
  }
  
  
  public void addListener(IStringArtAlgoListener listener) {
    this.processingResultListeners.add(listener);
  }
}
