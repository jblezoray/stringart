package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.List;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.edge.ScoredEdge;
import fr.jblezoray.stringart.image.Image;

/**
 * This listener is invoked after each round.
 * @author jbl
 */
public interface IStringArtAlgoListener {

  void notifyRoundResults(
      int iteration, 
      Image curImg, 
      List<Edge> edges,
      Image importanceMappingImg, 
      Image refImg, 
      ScoredEdge scoredEdge);
  
}
