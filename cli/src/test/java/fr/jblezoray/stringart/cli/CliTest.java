package fr.jblezoray.stringart.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.hillclimb.StringArt;

public class CliTest {
  
  private Cli cli;

  private Exception exceptionThrown;
  
  
  @BeforeEach
  public void before() {
    this.exceptionThrown = null;
    Function<Configuration, StringArt> stringArtMockBuilder = (conf) -> 
        new StringArt(conf) {
          @Override
          public void start(List<DirectedEdge> edges){
            // noop;
          }
        };
    this.cli = new Cli(stringArtMockBuilder);
    this.cli.setFatalErrorHandler(exception -> this.exceptionThrown = exception);
  }
  
  
  @Test
  public void test_only_mandatory() throws IOException {
    // having
    File existingFile = File.createTempFile("___", "");
    var args = existingFile.getAbsolutePath();
    var argsArray = args.split(" ");
    
    // when
    cli.start(argsArray);
    
    // then
    this.exceptionThrown.printStackTrace();
    Assertions.assertNull(this.exceptionThrown);
  }
  
  @Test
  public void test_all_arguments() throws IOException {
    // having
    String existingFile = File.createTempFile("___", "").getAbsolutePath();
    var args = "-q "+existingFile+" -i "+existingFile+" -o output.png -d diff.png -s stringpath.png";
    var argsArray = args.split(" ");
    
    // when
    cli.start(argsArray);
    
    // then
    this.exceptionThrown.printStackTrace();
    Assertions.assertNull(this.exceptionThrown);
  }
}
