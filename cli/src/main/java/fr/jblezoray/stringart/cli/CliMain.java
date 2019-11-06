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
      Argument.<File>build("The goal image.")
          .atPosition(0)
          .withRequiredCondition(f -> f.exists(), "Goal image does not exist")
          .andDo(CONFIGURATION::setGoalImage),
          
      Argument.<Optional<File>>build("An importance image, that ponderates the "
              + "pixels of the goal image")
          .withName("importanceImage", "i")
          .withRequiredCondition(f -> f.map(File::exists).orElse(true), "Importance image does not exist")
          .andDo(CONFIGURATION::setImportanceImage),
          
      Argument.<Boolean>build("")
          .withName("edgeWayEnabled", "e")
          .orDefault(() -> true)
          .andDo(CONFIGURATION::setEdgeWayEnabled),
          
      Argument.<File>build("Output file for rendering the graphical result")
          .withName("output", "o") 
          .orDefault(() -> new File("rendering.png"))
          .withRequiredCondition(f -> !f.exists(), "The rendering output file already exists.")
          .andDo(f -> CliMain.renderedImage = f),
          
      Argument.<File>build("Output file for a pixel to pixel difference between"
              + " the rendering and the goal image")
          .withName("diff", "d") 
          .orDefault(() -> new File("diff.png"))
          .withRequiredCondition(f -> !f.exists(), "The diff output file already exists.")
          .andDo(f -> CliMain.renderedImageDifference = f),
          
      Argument.<File>build("Output file for saving the string path")
          .withName("stringPath", "s") 
          .orDefault(() -> new File("stringPath.png"))
          .withRequiredCondition(f -> !f.exists(), "The stringpath output file already exists.")
          .andDo(f -> CliMain.renderedStringPath = f)
          
  );

  private static File renderedImage;
  private static File renderedImageDifference;
  private static File renderedStringPath;
  
  public static void main(String... args) 
      throws InvalidArgumentException, IOException {
    
    ARGUMENTS_PARSER.parse(args);
    
    var stringArt = new StringArt(CONFIGURATION);
    
    stringArt.addListener(Listener
        .writeTo(System.out)
        .debugLine()
        .onEveryRound());
    
    stringArt.addListener(Listener
        .saveToFile(renderedImage)
        .stringPath()
        .ifIsTrue(everyXRound(10)));

    stringArt.addListener(Listener
        .saveToFile(renderedImageDifference)
        .image(hc -> hc.getRenderedResult())
        .onAny(afterDelay(2), onStep(Step.FINAL)));

    stringArt.addListener(Listener
        .saveToFile(renderedStringPath)
        .image(hc -> hc.getRenderedResult()
            .differenceWith(hc.getReferenceImage())
            .multiplyWith(hc.getImportanceImage()))
        .onAny(afterDelay(2), onStep(Step.FINAL)));

    stringArt.start(new ArrayList<>());
    
    
  }
  
}
