package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.edge.ScoredEdge;
import fr.jblezoray.stringart.image.Image;

public class StringPathSaverListener implements IStringArtAlgoListener {

  private int iterationsBetweenSaving;
  private String textFilename;
  
  public StringPathSaverListener(int iterationsBetweenSaving, String textFilename) {
    this.textFilename = textFilename;
    this.iterationsBetweenSaving = iterationsBetweenSaving;
  }
  
  @Override
  public void notifyRoundResults(int iteration, Image curImg, List<Edge> edges, 
      Image importanceMappingImg, Image refImg, ScoredEdge scoredEdge) {

    if (iteration%this.iterationsBetweenSaving != 0) return;
    
    File f = new File(textFilename);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
      for (Edge e : edges) {
        String representation = 
             e.getNailA()+(e.isNailAClockwise()?"+":"-")+
            +e.getNailB()+(e.isNailBClockwise()?"+":"-");
        writer.write(representation+"\n");  
      }
    } catch (IOException e) {
      System.out.println("Cannot create string path file: " + e.getMessage());
    }
  }

}
