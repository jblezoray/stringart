package fr.jblezoray.stringart;

import java.io.IOException;

import fr.jblezoray.stringart.hillclimb.StringArtHillClimb;
import fr.jblezoray.stringart.hillclimb.listeners.DebugListener;
import fr.jblezoray.stringart.hillclimb.listeners.ImageDifferenceSaverListener;
import fr.jblezoray.stringart.hillclimb.listeners.ImageSaverListener;
import fr.jblezoray.stringart.hillclimb.listeners.StringPathSaverListener;

public class Main {
  
  public static void main(String[] args) throws IOException {
    Configuration configuration = new Configuration();
    StringArtHillClimb stringArtAlgo = new StringArtHillClimb(configuration);
    stringArtAlgo.addListener(new DebugListener());
    stringArtAlgo.addListener(new ImageSaverListener(50, configuration.getRenderedImageName()));
    stringArtAlgo.addListener(new ImageDifferenceSaverListener(50, configuration.getRenderedImageDifferenceName()));
    stringArtAlgo.addListener(new StringPathSaverListener(10, configuration.getRenderedStringPathFilename()));
    stringArtAlgo.start();
  }
  
}
