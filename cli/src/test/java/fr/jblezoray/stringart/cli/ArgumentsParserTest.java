package fr.jblezoray.stringart.cli;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ArgumentsParserTest {

  
  @Test
  public void argumentParser() {
    // having
    String argsLine = "-a aa -b bb toto -c cc"; 
    
    // when
    Map<String, String> args = ArgumentsParser.parse(argsLine);
    
    // then 
//    Assert.
  }
  
}
