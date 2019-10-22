package fr.jblezoray.stringart;

import java.io.IOException;

import fr.jblezoray.stringart.hillclimb.StringArt;
import fr.jblezoray.stringart.hillclimb.listeners.DebugListener;
import fr.jblezoray.stringart.hillclimb.listeners.ImageSaverListener;
import fr.jblezoray.stringart.hillclimb.listeners.StringPathSaverListener;

public class Main {
  
  public static void main(String[] args) throws IOException {
    Configuration configuration = new Configuration();
    StringArt stringArt = new StringArt(configuration);
    stringArt.addListener(new DebugListener());
    stringArt.addListener(new ImageSaverListener(50, configuration.getRenderedImageName()));
//    stringArt.addListener(new ImageDifferenceSaverListener(50, configuration.getRenderedImageDifferenceName()));
    stringArt.addListener(new StringPathSaverListener(10, configuration.getRenderedStringPathFilename()));
    stringArt.start();
  }
  
}