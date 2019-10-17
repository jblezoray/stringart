package fr.jblezoray.stringart.hillclimb;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import fr.jblezoray.stringart.core.EdgeDrawer;
import fr.jblezoray.stringart.core.EdgeFactory;
import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.edge.ScoredEdge;
import fr.jblezoray.stringart.image.Image;
import fr.jblezoray.stringart.image.UnboundedImage;

public class StringArtHillClimb {

  private final EdgeDrawer edgeDrawer;
  private final EdgeFactory edgeFactory;
  private final Image referenceImg;
  private final Image importanceImg;
  private final List<Edge> edges;

  private UnboundedImage renderedResult;
  private Optional<ScoredEdge> previous;
  
  public StringArtHillClimb(
      List<Edge> edges, 
      Image referenceImg, 
      Image importanceImg, 
      StringCharacteristics sc) {
    this.edges = edges;
    this.referenceImg = referenceImg;
    this.importanceImg = importanceImg;
    this.edgeDrawer = new EdgeDrawer(this.referenceImg.getSize(), sc);
    this.edgeFactory = new EdgeFactory(sc);
    this.renderedResult = new UnboundedImage(this.referenceImg.getSize())
        .add(this.edgeDrawer.drawAllNails());
    this.previous = Optional.empty();
    this.edges.forEach(e -> 
        this.renderedResult.add(this.edgeDrawer.drawEdge(e)));
  }

  public UnboundedImage getRenderedResult() {
    return renderedResult;
  }
  
  public ScoredEdge makeRound() {
    ScoredEdge addedEdgeWithScore = getBestEdgeAdditionToReduceNorm();
    Edge addedEdge = addedEdgeWithScore.getEdge();
    renderedResult.add(this.edgeDrawer.drawEdge(addedEdge));
    edges.add(addedEdge);
    previous = Optional.of(addedEdgeWithScore);
    return addedEdgeWithScore;
  }


  /**
   * Tries to add all the possible edges, and keep only the one that results in 
   * a minimum score. 
   *  
   * @param prevNail
   * @param isPrevNailClockwise
   * @param renderedResult
   * @param edges
   * @return
   */
  private ScoredEdge getBestEdgeAdditionToReduceNorm() {
    int prevNail = previous.map(ScoredEdge::getEndNail).orElse(0);
    boolean isPrevNailClockwise = previous.map(ScoredEdge::isEndNailClockwise).orElse(true);
    
    AtomicInteger numberOfEdges = new AtomicInteger();
    long before = System.currentTimeMillis();
    ScoredEdge scoredEdge = this.edgeFactory
        .streamEdges(prevNail, !isPrevNailClockwise)
        .filter(edge -> !edges.contains(edge)) // not already in the image
        .map(edge -> getScoreIfAddedInImage(edge))
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
   * @return The score of the resulting image if the edge is added.
   */
  private ScoredEdge getScoreIfAddedInImage(Edge edge) {
    double score = renderedResult
        .deepCopy()
        .add(this.edgeDrawer.drawEdge(edge))
        .differenceWith(referenceImg)
        .multiplyWith(importanceImg)
        .l2norm();
    boolean isNailATheEnd = previous
        .map(ScoredEdge::getEndNail)
        .map(nail -> edge.getNailB() == nail)
        .orElse(edge.getNailB()==0);
    return new ScoredEdge(edge, score, isNailATheEnd);
  }

}
