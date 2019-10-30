package fr.jblezoray.stringart;

import java.io.IOException;
import java.util.ArrayList;

import fr.jblezoray.stringart.hillclimb.StringArt;
import fr.jblezoray.stringart.hillclimb.listeners.DebugListener;
import fr.jblezoray.stringart.hillclimb.listeners.ImageDifferenceSaverListener;
import fr.jblezoray.stringart.hillclimb.listeners.ImageSaverListener;
import fr.jblezoray.stringart.hillclimb.listeners.StringPathSaverListener;

public class Main {
  
  public static void main(String[] args) throws IOException {
    Configuration configuration = new Configuration();
    StringArt stringArt = new StringArt(configuration);
    
    stringArt.addListener(new DebugListener());
    stringArt.addListener(new ImageSaverListener(2, configuration.getRenderedImageName()));
    stringArt.addListener(new ImageDifferenceSaverListener(2, configuration.getRenderedImageDifferenceName()));
    stringArt.addListener(new StringPathSaverListener(10, configuration.getRenderedStringPathFilename()));
    stringArt.start(new ArrayList<>());
  }
  
}
