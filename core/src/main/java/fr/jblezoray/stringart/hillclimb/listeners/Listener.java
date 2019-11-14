package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.File;
import java.io.PrintStream;

import fr.jblezoray.stringart.hillclimb.Step;
import fr.jblezoray.stringart.hillclimb.StringArtHillClimb;

/**
 * This listener is invoked after each round.
 * @author jbl
 */
public interface Listener {

  void notifyRoundResults(
      Step step,
      int iteration, 
      StringArtHillClimb hc);
  
  static FileSaverListenerBuilder saveToFile(File file) {
    return new FileSaverListenerBuilder(file);
  }
  
  static WriterBuilder writeTo(PrintStream out) {
    return new WriterBuilder(out);
  }
  
  
}
