package fr.jblezoray.stringart.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics;

/**
 * The EdgeFactory maintains a collection of all the possible edges. 
 */
public class EdgeFactory {
  
  private final List<Edge> allPossibleEdges;
  
  /**
   * If enabled, then the way a thread turns around a nail is considered.
   */
  private final boolean wayEnabled;
  
  private final static boolean DEFAULT_WAY = false;
  
  /**
   *  If the 'edgeWayEnabled' boolean option is not setted to true, then only 
   *  the edges that goes clockwise are kept.  Otherwise, the predicates 
   *  considers that the string has make a 'turn' around the nail, and therefore
   *  arises at the other side.
   * 
   * @param minimumNailDistance
   * @param totalNumberOfNails
   * @param wayEnabled 
   */
  public EdgeFactory(StringCharacteristics sc) {
    this.allPossibleEdges = new ArrayList<>();
    for (int i=0; i<sc.getNbNails(); i++) {
      for (int j=i; j<sc.getNbNails(); j++) {
        var distance = Math.abs(j-i);
        if (distance >= sc.getMinimumNailsDistance()) {
          // one for each possible connection between two nails.
          if (sc.isEdgeWayEnabled()) {
            allPossibleEdges.add(new Edge(i, true,  j, true));
            allPossibleEdges.add(new Edge(i, false, j, true));
            allPossibleEdges.add(new Edge(i, true,  j, false));
            allPossibleEdges.add(new Edge(i, false, j, false));
          } else {
            allPossibleEdges.add(new Edge(i, DEFAULT_WAY, j, DEFAULT_WAY));
          }
        }
      }
    }
    this.wayEnabled = sc.isEdgeWayEnabled();
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
        .filter(edge -> nailA!=nailB
                     && edge.contains(nailA, this.wayEnabled ? nailAClockwise : DEFAULT_WAY) 
                     && edge.contains(nailB, this.wayEnabled ? nailBClockwise : DEFAULT_WAY))
        .findFirst();
  }
  
}
