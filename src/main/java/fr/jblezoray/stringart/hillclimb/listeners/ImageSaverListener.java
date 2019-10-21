package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.image.Image;

public class ImageSaverListener implements IStringArtAlgoListener {
  
  private int iterationsBetweenSaving;
  private String imageFilename;

  public ImageSaverListener(int iterationsBetweenSaving, String imageFilename) {
    this.iterationsBetweenSaving = iterationsBetweenSaving;
    this.imageFilename = imageFilename;
  }
  
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
    if (iteration%this.iterationsBetweenSaving != 0) return;
    try {
      Image toSave = this.getImageToSave(curImg, refImg, importanceMappingImg);
      EdgeImageIO.writeToFile(toSave, new File(this.imageFilename));
      
    } catch (IOException e) {
      System.out.println("Cannot create results image : " + e.getMessage());
    }
  }

  protected Image getImageToSave(Image curImg, Image refImg, Image importanceMappingImg) {
    return curImg;
  }
}
