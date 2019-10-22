package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.image.Image;

public class ImageSaverListener implements IStringArtAlgoListener {
  
  private long prevTimestampMS = 0;
  private int secondsBetweenSaving;
  private String imageFilename;

  public ImageSaverListener(int secondsBetweenSaving, String imageFilename) {
    this.secondsBetweenSaving = secondsBetweenSaving;
    this.imageFilename = imageFilename;
  }
  
  @Override
  public void notifyRoundResults(
      Step operationDescription,
      int iteration, 
      Image curImg,  
      List<DirectedEdge> edges, 
      Image importanceMappingImg, 
      Image refImg, 
      Optional<DirectedEdge> modifiedEdge, 
      double norm, 
      int numberOfEdgesEvaluated, 
      long timeTook) {
    long curTimestampMS = new Date().getTime();
    if (curTimestampMS - prevTimestampMS > secondsBetweenSaving * 1_000) {
      prevTimestampMS = curTimestampMS;
      try {
        Image toSave = this.getImageToSave(curImg, refImg, importanceMappingImg);
        EdgeImageIO.writeToFile(toSave, new File(this.imageFilename));
        
      } catch (IOException e) {
        System.out.println("Cannot create results image : " + e.getMessage());
      }
    }
  }

  protected Image getImageToSave(Image curImg, Image refImg, Image importanceMappingImg) {
    return curImg;
  }
}
