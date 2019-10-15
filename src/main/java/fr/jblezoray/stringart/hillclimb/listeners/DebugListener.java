package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.List;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.edge.ScoredEdge;
import fr.jblezoray.stringart.image.Image;

public class DebugListener implements IStringArtAlgoListener {
  
  @Override
  public void notifyRoundResults(int iteration, Image curImg,  List<Edge> edges,
      Image importanceMappingImg, Image refImg, ScoredEdge scoredEdge) {
    String line = String.format(
        "iteration:%d ; norm:%7.0f ; added nail:%3d,%3d ; round time: %5dms (mean time per nail:%d*%2.3fms)",
        iteration,
        scoredEdge.getNorm(), 
        scoredEdge.getEdge().getNailA(), 
        scoredEdge.getEdge().getNailB(),
        scoredEdge.getTimeTook(), 
        scoredEdge.getNumberOfEdgesEvaluated(),
        scoredEdge.getTimeTook() / (float)scoredEdge.getNumberOfEdgesEvaluated());
    System.out.println(line);
  }

}
