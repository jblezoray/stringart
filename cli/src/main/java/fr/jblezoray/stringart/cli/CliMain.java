package fr.jblezoray.stringart.cli;

import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.afterDelay;
import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.everyXRound;
import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.onStep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.hillclimb.Step;
import fr.jblezoray.stringart.hillclimb.StringArt;
import fr.jblezoray.stringart.hillclimb.listeners.Listener;

public class CliMain { 
  
  private static final Configuration CONFIGURATION = new Configuration();
  
  private static final ArgumentsParser ARGUMENTS_PARSER = new ArgumentsParser(
      
      Argument.<Boolean>build("Quiet mode")
          .withName("quiet", "q")
          .orDefault(() -> false)
          .andDo(q -> CliMain.quietMode = q),
          
      Argument.<File>build("The goal image.")
          .atPosition(0)
          .withRequirement(f -> f.exists(), "Goal image does not exist")
          .andDo(CONFIGURATION::setGoalImage),
          
      Argument.<Optional<File>>build("An importance image, that ponderates the "
              + "pixels of the goal image")
          .withName("importanceImage", "i")
          .withRequirement(f -> f.map(File::exists).orElse(true), "Importance image does not exist")
          .andDo(CONFIGURATION::setImportanceImage),
          
      Argument.<Boolean>build("")
          .withName("edgeWayEnabled", "e")
          .orDefault(() -> true)
          .andDo(CONFIGURATION::setEdgeWayEnabled),
          
      Argument.<File>build("Output file for rendering the graphical result")
          .withName("output", "o") 
          .orDefault(() -> new File("rendering.png"))
          .withRequirement(f -> !f.exists(), "The rendering output file already exists.")
          .andDo(f -> CliMain.renderedImage = f),
          
      Argument.<Optional<File>>build("Output file for a pixel to pixel difference between"
              + " the rendering and the goal image")
          .withName("diff", "d") 
          .orDefault(() -> Optional.empty())
          .withRequirement(f -> f.map(file -> !file.exists()).orElse(true), "The diff output file already exists.")
          .andDo(f -> CliMain.renderedImageDifference = f),
          
      Argument.<Optional<File>>build("Output file for saving the string path")
          .withName("stringPath", "s") 
          .orDefault(() -> Optional.empty())
          .withRequirement(f -> f.map(file -> !file.exists()).orElse(true), "The stringpath output file already exists.")
          .andDo(f -> CliMain.renderedStringPath = f)
          
  );


  private static Boolean quietMode;
  private static File renderedImage;
  private static Optional<File> renderedImageDifference;
  private static Optional<File> renderedStringPath;
  
  public static void main(String... args) 
      throws InvalidArgumentException, IOException {
    
    try {
      ARGUMENTS_PARSER.parse(args);
    } catch (InvalidArgumentException e) {
      System.err.println(e.getMessage());
      System.exit(-1);
    }
    
    var stringArt = new StringArt(CONFIGURATION);
    
    if (!quietMode) {
      stringArt.addListener(Listener
          .writeTo(System.out)
          .debugLine()
          .onEveryRound());
    }
    
    stringArt.addListener(Listener
        .saveToFile(renderedImage)
        .stringPath()
        .ifIsTrue(everyXRound(10)));

    if (renderedImageDifference.isPresent()) {
      stringArt.addListener(Listener
          .saveToFile(renderedImageDifference.get())
          .image(hc -> hc.getRenderedResult())
          .onAny(afterDelay(2), onStep(Step.FINAL)));
    }

    if (renderedStringPath.isPresent()) {
      stringArt.addListener(Listener
          .saveToFile(renderedStringPath.get())
          .image(hc -> hc.getRenderedResult()
              .differenceWith(hc.getReferenceImage())
              .multiplyWith(hc.getImportanceImage()))
          .onAny(afterDelay(2), onStep(Step.FINAL)));
    }

    stringArt.start(new ArrayList<>());
  }
  
}
