package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.List;

import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.image.Image;

/**
 * This listener is invoked after each round.
 * @author jbl
 */
public interface IStringArtAlgoListener {

  void notifyRoundResults(
      String operationDescription,
      int iteration, 
      Image curImg, 
      List<DirectedEdge> edges,
      Image importanceMappingImg, 
      Image refImg, 
      DirectedEdge addedEdge, 
      double norm,
      int numberOfEdgesEvaluated, 
      long timeTook);
  
}
