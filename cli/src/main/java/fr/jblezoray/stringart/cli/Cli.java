package fr.jblezoray.stringart.cli;

import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.afterDelay;
import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.everyXRound;
import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.onStep;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.cli.argumentparser.ArgumentsParser;
import fr.jblezoray.stringart.cli.argumentparser.InvalidArgumentException;
import fr.jblezoray.stringart.cli.argumentparser.types.FlagArgument.FlagArgumentBuilder;
import fr.jblezoray.stringart.cli.argumentparser.types.NamedArgument.NamedArgumentBuilder;
import fr.jblezoray.stringart.cli.argumentparser.types.PositionArgument.PositionArgumentBuilder;
import fr.jblezoray.stringart.hillclimb.Step;
import fr.jblezoray.stringart.hillclimb.StringArt;
import fr.jblezoray.stringart.hillclimb.listeners.Listener;

public class Cli {

  private final Configuration configuration = new Configuration();
  private Consumer<Exception> fatalErrorHandler = exception -> {
    System.err.println(exception.getMessage());
  };
  
  private Boolean printHelp;
  private Boolean quietMode;
  private File renderedImage;
  private Optional<File> renderedImageDifference;
  private Optional<File> renderedStringPath;
  
  private Function<Configuration, StringArt> stringArtBuilder; 
  private StringArt stringArt;
  
  private final ArgumentsParser argumentsParser = new ArgumentsParser(
      new FlagArgumentBuilder()
          .withName("help")
          .withAliases("h")
          .withDescription("print help")
          .orDefault(() -> false)
          .andDo(h -> this.printHelp = h)
          .build(),
          
      new FlagArgumentBuilder()
          .withName("quiet")
          .withAliases("q")
          .withDescription("Quiet mode")
          .orDefault(() -> false)
          .andDo(q -> this.quietMode = q)
          .build(),
          
      new PositionArgumentBuilder<File>()
          .atPosition(0)
          .withName("goal image")
          .withDescription("The goal image.")
          .withReader(s->new File(s))
          .withRequirement(f -> f.exists(), "Goal image does not exist")
          .andDo(configuration::setGoalImage)
          .build(),
          
      new NamedArgumentBuilder<Optional<File>>()
          .withName("importanceImage")
          .withAliases("i")
          .withDescription("An importance image, that ponderates the pixels of the goal image")
          .withReader(s->Optional.of(new File(s)))
          .orDefault(() -> Optional.empty())
          .withRequirement(f -> f.map(File::exists).orElse(true), "Importance image does not exist")
          .andDo(configuration::setImportanceImage)
          .build(),
          
      new FlagArgumentBuilder()
          .withName("disableEdgeWay")
          .withDescription("disables the rendering of the way the string goes around edges. "
              + "Enables a ~4x faster rendering, but is less precise.")
          .orDefault(() -> false)
          .andDo(disableEdgeWay -> configuration.setEdgeWayEnabled(!disableEdgeWay))
          .build(),
          
      new NamedArgumentBuilder<File>()
          .withName("output")
          .withAliases("o")
          .withDescription("Output file for rendering the graphical result")
          .withReader(s->new File(s))
          .orDefault(() -> new File("output.png"))
          .withRequirement(f -> !f.exists(), "The output image file already exists.")
          .andDo(f -> this.renderedImage = f)
          .build(),
          
      new NamedArgumentBuilder<Optional<File>>()
          .withName("diff")
          .withAliases("d")
          .withDescription("Output file for a pixel to pixel difference between the "
              + "rendering and the goal image")
          .withReader(s->Optional.of(new File(s)))
          .orDefault(() -> Optional.empty())
          .withRequirement(of -> of.map(f -> !f.exists()).orElse(true), 
              "The diff output file already exists.")
          .andDo(f -> this.renderedImageDifference = f)
          .build(),
          
      new NamedArgumentBuilder<Optional<File>>()
          .withName("stringPath") 
          .withAliases("s")
          .withDescription("Output file for saving the string path")
          .withReader(s->Optional.of(new File(s)))
          .orDefault(() -> Optional.empty())
          .withRequirement(f -> f.map(file -> !file.exists()).orElse(true),
              "The stringpath output file already exists.")
          .andDo(f -> this.renderedStringPath = f)
          .build()
          
  );

  public Cli(Function<Configuration, StringArt> stringArtBuilder) {
    this.stringArtBuilder = stringArtBuilder;
  }
  
  public void setFatalErrorHandler(Consumer<Exception> fatalErrorHandler) {
    this.fatalErrorHandler = fatalErrorHandler;
  }

  public void start(String[] args) {
    
    try {
      this.argumentsParser.parse(args);
    } catch (InvalidArgumentException ie) {
      this.fatalErrorHandler.accept(ie);
      var help = this.argumentsParser.buildHelp();
      System.out.print(help);
      System.exit(-1);
    }
    
    if (printHelp) {
      String help = this.argumentsParser.buildHelp();
      System.out.print(help);
      System.exit(0);
    }

    try {
      this.stringArt = this.stringArtBuilder.apply(configuration);
      
      configureDebugListener();
      configureRenderedImageListener();
      configureDiffImageListener();
      configureRenderedStringPathListener();
      
      this.stringArt.start(new ArrayList<>());
      
    } catch (IOException e) {
      this.fatalErrorHandler.accept(e);
      System.exit(-1);
    }  
  }
  
  private void configureDebugListener() {
    if (!quietMode) {
      var l = Listener
          .writeTo(System.out)
          .debugLine()
          .onEveryRound();
      stringArt.addListener(l);
    }
  }
  
  private void configureRenderedImageListener() {
    var l = Listener
        .saveToFile(renderedImage)
        .image(hc -> hc.getRenderedResult())
        .onAny(everyXRound(10), onStep(Step.FINAL));
    stringArt.addListener(l);
  }

  private void configureDiffImageListener() {
    if (renderedImageDifference.isPresent()) {
      var l = Listener
          .saveToFile(renderedImageDifference.get())
          .image(hc -> hc.getRenderedResult()
          .differenceWith(hc.getReferenceImage())
          .multiplyWith(hc.getImportanceImage()))
          .onAny(afterDelay(2), onStep(Step.FINAL));
      stringArt.addListener(l);
    }
  }

  private void configureRenderedStringPathListener() {
    if (renderedStringPath.isPresent()) {
      var l = Listener
          .saveToFile(renderedStringPath.get())
          .stringPath()
          .onAny(afterDelay(2), onStep(Step.FINAL));
      stringArt.addListener(l);
    }
  }

}
