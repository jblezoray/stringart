package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.hillclimb.StringArtHillClimb;
import fr.jblezoray.stringart.image.Image;

public class FileSaverListenerBuilder {

  private final File file;
  
  public FileSaverListenerBuilder(File file) {
    this.file = file;
  }

  public ListenerPredicateBuilder image(Function<StringArtHillClimb, Image> imageSupplier) {
    return (step, it, hc) -> {
      try {
        Image toSave = imageSupplier.apply(hc);
        EdgeImageIO.writeToFile(toSave, file);
        
      } catch (IOException e) {
        throw new RuntimeException("Cannot create image : " + e.getMessage());
      }
    };
  }

  public ListenerPredicateBuilder stringPath() {
    return (step, it, hc) -> {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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

}
