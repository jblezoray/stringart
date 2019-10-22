package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.List;

import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.image.Image;

public class DebugListener implements IStringArtAlgoListener {
  
  @Override
  public void notifyRoundResults(
      String operationDescription,
      int iteration, 
      Image curImg,  
      List<DirectedEdge> edges, 
      Image importanceMappingImg, 
      Image refImg, 
      DirectedEdge addedEdge, 
      double norm, 
      int numberOfEdgesEvaluated, 
      long timeTook) {
    String line = String.format(
        "%s ; size:%s ; iteration:%d ; norm:%7.0f ; nail:%3d,%3d ; time: %5dms (time/edge:%d*%2.3fms)",
        operationDescription,
        curImg.getSize(),
        iteration,
        norm, 
        addedEdge.getEdge().getNailA(), 
        addedEdge.getEdge().getNailB(),
        timeTook, 
        numberOfEdgesEvaluated,
        timeTook / (float)numberOfEdgesEvaluated);
    System.out.println(line);
  }

}
