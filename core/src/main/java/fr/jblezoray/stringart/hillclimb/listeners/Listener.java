package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Function;

import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.hillclimb.Step;
import fr.jblezoray.stringart.hillclimb.StringArtHillClimb;
import fr.jblezoray.stringart.image.Image;

/**
 * This listener is invoked after each round.
 * @author jbl
 */
public interface Listener {

  void notifyRoundResults(
      Step step,
      int iteration, 
      StringArtHillClimb hc);
  
  interface FileSaverListenerBuilder {
    ListenerPredicateBuilder image(Function<StringArtHillClimb, Image> imageSupplier);
    ListenerPredicateBuilder stringPath();
  }

  interface WriterBuilder {
    ListenerPredicateBuilder debugLine();
  }
  
  static FileSaverListenerBuilder saveToFile(String filename) {
    return new FileSaverListenerBuilder() {

      public ListenerPredicateBuilder image(Function<StringArtHillClimb, Image> imageSupplier) {
        return (step, it, hc) -> {
          try {
            Image toSave = imageSupplier.apply(hc);
            EdgeImageIO.writeToFile(toSave, new File(filename));
            
          } catch (IOException e) {
            throw new RuntimeException("Cannot create image : " + e.getMessage());
          }
        };
      }

      public ListenerPredicateBuilder stringPath() {
        return (step, it, hc) -> {
            File f = new File(filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
              for (DirectedEdge e : hc.getEdges()) {
                String representation = 
                     e.getEdge().getNailA()+(e.getEdge().isNailAClockwise()?"+":"-")+
                    +e.getEdge().getNailB()+(e.getEdge().isNailBClockwise()?"+":"-");
                writer.write(representation+"\n");  
              }
            } catch (IOException e) {
              throw new RuntimeException("Cannot create string path file : " + e.getMessage());
            }
        };
      }
    };
  }
  
  static WriterBuilder writeTo(PrintStream out) {
    return new WriterBuilder() {
      
      @Override
      public ListenerPredicateBuilder debugLine() {
        return (step, it, hc) -> {
          out.format(
              "%s ; size:%s ; iteration:%d ; norm:%7.0f ; nbEdges:%4d ; "
              + "time: %5dms (time/edge:%d*%2.3fms)%s",
              step,
              hc.getRenderedResult().getSize(),
              it,
              hc.getNorm(), 
              hc.getEdges().size(), 
              hc.getTimeTook(), 
              hc.getNumberOfEdgesEvaluated(),
              hc.getTimeTook() / (float)hc.getNumberOfEdgesEvaluated(),
              System.lineSeparator());
        };
      }
      
    };
  }
  
  
}
