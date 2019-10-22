package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.List;
import java.util.Optional;

import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.image.Image;

/**
 * This listener is invoked after each round.
 * @author jbl
 */
public interface IStringArtAlgoListener {

  void notifyRoundResults(
      Step operationDescription,
      int iteration, 
      Image curImg, 
      List<DirectedEdge> edges,
      Image importanceMappingImg, 
      Image refImg, 
      Optional<DirectedEdge> modifiedEdge, 
      double norm,
      int numberOfEdgesEvaluated, 
      long timeTook);
  
}
