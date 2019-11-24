package fr.jblezoray.stringart.cli;

import java.util.function.Function;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.hillclimb.StringArt;

public class CliMain { 
  
  public static void main(String... args) {
    Function<Configuration, StringArt> stringArtBuilder = (conf) ->
        new StringArt(conf);
    new Cli(stringArtBuilder).start(args);
  }
  
}
