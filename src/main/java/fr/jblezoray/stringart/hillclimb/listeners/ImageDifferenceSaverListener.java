package fr.jblezoray.stringart.hillclimb.listeners;

import fr.jblezoray.stringart.image.Image;

public class ImageDifferenceSaverListener extends ImageSaverListener {
  
  public ImageDifferenceSaverListener(int iterationsBetweenSaving, String imageFilename) {
    super(iterationsBetweenSaving, imageFilename);
  }

  @Override
  protected Image getImageToSave(Image curImg, Image refImg, Image importanceMappingImg) {
    return curImg
        .differenceWith(refImg)
        .multiplyWith(importanceMappingImg);
  }

}
