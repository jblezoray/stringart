package fr.jblezoray.stringart.hillclimb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.core.EdgeDrawer;
import fr.jblezoray.stringart.core.EdgeFactory;
import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.edge.ScoredEdge;
import fr.jblezoray.stringart.hillclimb.listeners.IStringArtAlgoListener;
import fr.jblezoray.stringart.image.Image;
import fr.jblezoray.stringart.image.ImageSize;
import fr.jblezoray.stringart.image.UnboundedImage;

public class StringArtHillClimb {

  private final Image refImg;
  
  private final Image importanceImg;
  
  private final ImageSize refImgSize;

  private final EdgeDrawer edgeDrawer;
  
  private final EdgeFactory edgeFactory;

  private Set<IStringArtAlgoListener> processingResultListeners = new HashSet<>();
  
  
  public StringArtHillClimb(Configuration c) throws IOException {
    this.refImg = EdgeImageIO.readFile(c.getGoalImagePath()).downsample(0.5);
    this.importanceImg = EdgeImageIO.readFile(c.getImportanceImagePath()).downsample(0.5);
    this.refImgSize = this.refImg.getSize();
    
    float resolutionMmPerPx = c.getCanvasWidthMilimeters() / this.refImgSize.w;
    float lineThicknessInPx = c.getThreadThicknessMilimeters() / resolutionMmPerPx;
    float nailDiameterInPx = c.getNailDiameterMilimeters() / resolutionMmPerPx;
    this.edgeDrawer = new EdgeDrawer(refImgSize, c.getNbNails(), 
        lineThicknessInPx, nailDiameterInPx);
    
    this.edgeFactory = new EdgeFactory(c.getMinNailsDiff(), c.getNbNails(), 
        c.isEdgeWayEnabled(), this.edgeDrawer);
  }
  
  
  public void start() {
    List<Edge> refImgAsEdges = new ArrayList<>();

    // create an image that represents the result.
    UnboundedImage curImg = new UnboundedImage(this.refImgSize);
    curImg.add(this.edgeDrawer.drawAllNails());
    
    // values kept from the previous round. 
    double prevNorm = Float.MAX_VALUE;
    int prevNail = 0; 
    boolean isPrevNailClockwise = false;
    
    // optimization algo.  
    AtomicInteger currentIteration = new AtomicInteger(0);
    ScoredEdge addedEdgeWithScore = null;
    do {
      addedEdgeWithScore = getBestEdgeAdditionToReduceNorm(
          prevNail, isPrevNailClockwise, curImg, refImgAsEdges);
      
      Edge addedEdge = addedEdgeWithScore.getEdge();
      curImg.add(addedEdge.getDrawnEdgeData());
      refImgAsEdges.add(addedEdge);

      // store the new prev nail for the next round.
      prevNorm = addedEdgeWithScore.getNorm();
      if (addedEdge.getNailA() == prevNail) {
        prevNail = addedEdge.getNailB();
        isPrevNailClockwise = addedEdge.isNailBClockwise();
      } else {
        prevNail = addedEdge.getNailA();
        isPrevNailClockwise = addedEdge.isNailAClockwise();
      }

      int iterationNumber = currentIteration.incrementAndGet();
      this.notifyResultToListeners(iterationNumber, refImgAsEdges, curImg, addedEdgeWithScore);
      
    } while (addedEdgeWithScore.getNorm()<=prevNorm); // stop if it does not reduces the norm.
  }


  /**
   * Tries to add all the possible edges, and keep only the one that results in 
   * a minimum score. 
   *  
   * @param prevNail
   * @param isPrevNailClockwise
   * @param curImg
   * @param edges
   * @return
   */
  private ScoredEdge getBestEdgeAdditionToReduceNorm(int prevNail, boolean isPrevNailClockwise, 
      UnboundedImage curImg, List<Edge> edges) {
    AtomicInteger numberOfEdges = new AtomicInteger();
    long before = System.currentTimeMillis();
    ScoredEdge scoredEdge = this.edgeFactory
        .streamEdges(prevNail, !isPrevNailClockwise)
        .filter(edge -> !edges.contains(edge)) // not already in the image
        .map(edge -> getScoreIfAddedInImage(edge, curImg))
        .peek(edge -> numberOfEdges.incrementAndGet())
        .min((a, b) -> a.getNorm()<b.getNorm() ? -1 : 1)
        .orElseThrow(() -> new RuntimeException("Invalid state: no edge."));
    long after = System.currentTimeMillis();
    scoredEdge.setNumberOfEdgesEvaluated(numberOfEdges.get());
    scoredEdge.setTimeTook(after-before);
    return scoredEdge;
  }


  /**
   * Builds a score for this edge when added in an image.
   * 
   * @param edge the edge to add.
   * @param curImg the current image (will be left untouched)
   * @return The score of the resulting image if the edge is added.
   */
  private ScoredEdge getScoreIfAddedInImage(Edge edge, UnboundedImage curImg) {
    double score = curImg
        .deepCopy()
        .add(edge.getDrawnEdgeData())
        .differenceWith(refImg)
        .multiplyWith(importanceImg)
        .l2norm();
    return new ScoredEdge(edge, score);
  }
  
  
  private void notifyResultToListeners(int iterationNumber, List<Edge> edges,
      UnboundedImage currentImage, ScoredEdge addedEdgeWithScore) {
    this.processingResultListeners.forEach(listener -> 
        listener.notifyRoundResults(iterationNumber, currentImage, edges,
            importanceImg, refImg, addedEdgeWithScore)
    );
  }
  

  public void addListener(IStringArtAlgoListener listener) {
    this.processingResultListeners.add(listener);
  }

}
