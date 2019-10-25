package fr.jblezoray.stringart.hillclimb;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.jblezoray.stringart.core.EdgeDrawer;
import fr.jblezoray.stringart.core.EdgeDrawerFactory;
import fr.jblezoray.stringart.core.EdgeFactory;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.image.Image;
import fr.jblezoray.stringart.image.UnboundedImage;

public class StringArtHillClimb {

  private static final double MIN_NORM_DIFF = 1.0;
  private final EdgeDrawer edgeDrawer;
  private final EdgeFactory edgeFactory;
  private final Image referenceImg;
  private final Image importanceImg;
  private final List<DirectedEdge> edges;

  private UnboundedImage renderedResult;
  
  private double norm = Double.MAX_VALUE;
  private int numberOfEdgesEvaluated;
  private long timeTook;
  private Optional<DirectedEdge> modifiedEdge;
  
  
  public StringArtHillClimb(
      List<DirectedEdge> edges, 
      Image referenceImg, 
      Image importanceImg, 
      StringCharacteristics sc) {
    this.edges = edges;
    this.referenceImg = referenceImg;
    this.importanceImg = importanceImg;
    this.edgeDrawer = EdgeDrawerFactory.build(this.referenceImg.getSize(), sc);
    this.edgeFactory = new EdgeFactory(sc);
    this.renderedResult = new UnboundedImage(this.referenceImg.getSize())
        .add(this.edgeDrawer.drawAllNails());
    this.edges.forEach(e -> 
        this.renderedResult.add(this.edgeDrawer.drawEdge(e.getEdge())));
  }
  
  public List<DirectedEdge> getEdges() {
    return edges;
  }

  public UnboundedImage getRenderedResult() {
    return renderedResult;
  }
  
  public double getNorm() {
    return norm;
  }
  
  public int getNumberOfEdgesEvaluated() {
    return numberOfEdgesEvaluated;
  }
  
  public long getTimeTook() {
    return timeTook;
  }
  
  public Optional<DirectedEdge> addBestPossibleEdge() {
    AddEdgeScored e = getBestEdgeAdditionToReduceNorm();
    if (e.newNormIfAdded > this.norm - MIN_NORM_DIFF) 
      return Optional.empty();
    
    boolean isNailATheEnd = Optional.of(this.getEdges().size())
        .filter(s->s>=1)
        .map(size -> this.getEdges().get(size-1))
        .map(p -> e.edge.getNailB() == p.getEndNail())
        .orElse(true);
    DirectedEdge addedEdge = new DirectedEdge(e.edge, isNailATheEnd);

    this.norm = e.newNormIfAdded;
    this.renderedResult.add(this.edgeDrawer.drawEdge(addedEdge.getEdge()));
    this.edges.add(addedEdge);
    this.modifiedEdge = Optional.of(addedEdge);
    return Optional.of(addedEdge);
  }

  public Optional<DirectedEdge> removeWorstEdge() {
    Optional<RemoveEdgeScored> e = getBestEdgeRemovalToReduceNorm();
    if (!e.isPresent() || e.get().newNormIfRemoved > this.norm - MIN_NORM_DIFF) 
      return Optional.empty();
    
    this.norm = e.get().newNormIfRemoved;
    modifiedEdge = Optional.of(this.edges.get(e.get().index));
    renderedResult.remove(this.edgeDrawer.drawEdge(this.edges.get(e.get().index).getEdge()));
    this.edges.remove(e.get().index);
    if (e.get().replacementForTheNextEdge.isPresent()) {
      renderedResult.remove(this.edgeDrawer.drawEdge(this.edges.get(e.get().index+1).getEdge()));
      renderedResult.add(this.edgeDrawer.drawEdge(e.get().replacementForTheNextEdge.get().getEdge()));
      this.edges.add(e.get().index, e.get().replacementForTheNextEdge.get());
    }
    return Optional.of(e.get().removedEdge);
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
  private AddEdgeScored getBestEdgeAdditionToReduceNorm() {
    int prevNail;
    boolean isPrevNailClockwise;
    if (this.edges.size() > 0) {
      DirectedEdge lastEdge = this.edges.get(this.edges.size()-1);
      prevNail = lastEdge.getEndNail();
      isPrevNailClockwise = lastEdge.isEndNailClockwise();
    } else {
      prevNail = 0;
      isPrevNailClockwise = true;
    }
    
    AtomicInteger numberOfEdges = new AtomicInteger();
    long before = System.currentTimeMillis();
    List<Edge> rawEdges = this.edges.stream()
        .map(DirectedEdge::getEdge)
        .collect(Collectors.toList()); 
    AddEdgeScored bestEdge = this.edgeFactory
        .streamEdges(prevNail, !isPrevNailClockwise)
        .filter(edge -> !rawEdges.contains(edge)) // not already in the image
        .map(edge -> getScoreIfAddedInImage(edge))
        .peek(edge -> numberOfEdges.incrementAndGet())
        .min((a, b) -> a.newNormIfAdded < b.newNormIfAdded ? -1 : 1)
        .orElseThrow(() -> new RuntimeException("Invalid state: no edge."));
    long after = System.currentTimeMillis();
    
    this.numberOfEdgesEvaluated = numberOfEdges.get();
    this.timeTook = after - before;
    return bestEdge;
  }
  
  private static class AddEdgeScored {
    Edge edge;
    double newNormIfAdded;
  }

  private Optional<RemoveEdgeScored> getBestEdgeRemovalToReduceNorm() {
    long before = System.currentTimeMillis();
    AtomicInteger numberOfEdges = new AtomicInteger();
    
    Optional<RemoveEdgeScored> worstEdge = IntStream.range(0, this.edges.size())
        .boxed()
        .map(edgeIndex -> getScoreIfRemovedFromImage(edgeIndex))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .peek(edge -> numberOfEdges.incrementAndGet())
        .min((a, b) -> a.newNormIfRemoved < b.newNormIfRemoved ? -1 : 1);
    long after = System.currentTimeMillis();
    
    this.numberOfEdgesEvaluated = numberOfEdges.get();
    this.timeTook = after - before;
    return worstEdge;
  }

  private Optional<RemoveEdgeScored> getScoreIfRemovedFromImage(int edgeIndex) {
    DirectedEdge edgeToRemove = this.edges.get(edgeIndex);
    
    Optional<DirectedEdge> nextEdge = Optional.ofNullable(
        edgeIndex+1 < this.edges.size() ? this.edges.get(edgeIndex) : null);
    
    Optional<DirectedEdge> replacement = nextEdge
        .map(e -> {
          int startNail = edgeToRemove.getStartNail();
          boolean isStartNailClockwise = edgeToRemove.isStartNailClockwise();
          int endNail = e.getEndNail();
          boolean isEndNailClockwise = e.isEndNailClockwise();
          return this.edgeFactory
              .getEdge(startNail, isStartNailClockwise, endNail, isEndNailClockwise)
              .map(edge -> new DirectedEdge(edge, edge.getNailA() == endNail))
              .orElse(null);
        });
    
    if (nextEdge.isPresent() && replacement.isEmpty()) {
      return Optional.empty();
    }
    
    UnboundedImage resultImage = renderedResult
        .deepCopy()
        .remove(this.edgeDrawer.drawEdge(edgeToRemove.getEdge()));
    nextEdge.ifPresent(ne -> resultImage.remove(this.edgeDrawer.drawEdge(ne.getEdge())));
    replacement.ifPresent(r -> resultImage.add(this.edgeDrawer.drawEdge(r.getEdge())));
    double norm = resultImage
        .differenceWith(referenceImg)
        .multiplyWith(importanceImg)
        .l2norm();
    
    var result = new RemoveEdgeScored();
    result.removedEdge = this.edges.get(edgeIndex);
    result.replacementForTheNextEdge = Optional.empty();
    result.index = edgeIndex;
    result.newNormIfRemoved = norm;
    return Optional.of(result);
  }

  private static class RemoveEdgeScored {
    int index;
    double newNormIfRemoved;
    DirectedEdge removedEdge;
    Optional<DirectedEdge> replacementForTheNextEdge;
  }
  
  /**
   * Builds a score for this edge when added in an image.
   * 
   * @param edge the edge to add.
   * @return The score of the resulting image if the edge is added.
   */
  private AddEdgeScored getScoreIfAddedInImage(Edge edge) {
    double norm = renderedResult
        .deepCopy()
        .add(this.edgeDrawer.drawEdge(edge))
        .differenceWith(referenceImg)
        .multiplyWith(importanceImg)
        .l2norm();
    var result = new AddEdgeScored();
    result.edge = edge;
    result.newNormIfAdded = norm;
    return result;
  }

  public Optional<DirectedEdge> getModifiedEdge() {
    return this.modifiedEdge;
  }
}
