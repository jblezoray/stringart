package fr.jblezoray.stringart.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import fr.jblezoray.stringart.edge.Edge;

/**
 * The EdgeFactory maintains a collection of all the possible edges. 
 */
public class EdgeFactory {
  
  private final List<Edge> allPossibleEdges;
  private final boolean wayEnabled;
  private final static boolean DEFAULT_WAY = false;
  
  /**
   *  If the 'edgeWayEnabled' boolean option is not setted to true, then only 
   *  the edges that goes clockwise are kept.  Otherwise, the predicates 
   *  considers that the string has make a 'turn' around the nail, and therefore
   *  arises at the other side.
   * 
   * @param minNailDiff
   * @param totalNumberOfNails
   * @param wayEnabled If enabled, then the way a thread turns around a nail
   *  is considered.
   * @param drawer
   */
  public EdgeFactory(
      int minNailDiff, 
      int totalNumberOfNails, 
      boolean wayEnabled,
      EdgeDrawer drawer) {
    this.allPossibleEdges = new ArrayList<>();
    for (int i=0; i<totalNumberOfNails; i++) {
      for (int j=i; j<totalNumberOfNails; j++) {
        if (Math.abs(j-i) > minNailDiff) {
          // one for each possible connection between two nails.
          if (wayEnabled) {
            allPossibleEdges.add(new Edge(i, true,  j, true,  drawer));
            allPossibleEdges.add(new Edge(i, false, j, true,  drawer));
            allPossibleEdges.add(new Edge(i, true,  j, false, drawer));
            allPossibleEdges.add(new Edge(i, false, j, false, drawer));
          } else {
            allPossibleEdges.add(new Edge(i, DEFAULT_WAY, j, DEFAULT_WAY, drawer));
          }
        }
      }
    }
    this.wayEnabled = wayEnabled;
  }
  
  public Stream<Edge> streamEdges(
      int nail, boolean nailClockwise) {
    return allPossibleEdges.stream()
        .parallel()
        .filter(edge -> edge.contains(nail, this.wayEnabled ? nailClockwise : DEFAULT_WAY));
  }

  public Optional<Edge> getEdge(
      int nailA, boolean nailAClockwise,
      int nailB, boolean nailBClockwise) {
    return allPossibleEdges.stream()
        .parallel()
        .filter(edge -> edge.contains(nailA, this.wayEnabled ? nailAClockwise : DEFAULT_WAY) 
                     && edge.contains(nailB, this.wayEnabled ? nailBClockwise : DEFAULT_WAY))
        .findFirst();
  }
  
}
