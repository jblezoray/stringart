package fr.jblezoray.stringart.cli;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArgumentsParserTest {

  @Test
  public void argumentParser_nominalCase() throws InvalidArgumentException {
    List<Argument> arguments = Arrays.asList(
        Argument.build("a"),
        Argument.build("b"),
        Argument.build("c"),
        Argument.build("x").noValue());
    var r = ArgumentsParser.parse(arguments, "-a aa -b bb -c cc -x toto");
    Assertions.assertEquals(mapOf(arguments, "a", "aa", "b", "bb", "c", "cc", "x", null), r);
  }

  private Map<Argument, Optional<String>> mapOf(
      List<Argument> arguments, 
      String... mapValues) {
    var map = new HashMap<Argument, Optional<String>>();
    for (int i=0; i<mapValues.length-1; i+=2) {
      var k = asArgument(arguments, mapValues[i]);
      var v = Optional.ofNullable(mapValues[i+1]);
      map.put(k,  v);
    }
    return map;
  }

  private Argument asArgument(List<Argument> arguments, String argName) {
    return arguments.stream()
        .filter(arg -> arg.getArgumentName().equals(argName))
        .findFirst()
        .orElseThrow();
  }

  @Test
  public void argumentParser_null() throws InvalidArgumentException {
    var r = ArgumentsParser.parse(Collections.emptyList(), null);
    Assertions.assertEquals(Collections.emptyMap(), r);
  }
  
  @Test
  public void argumentParser_whitespaces() throws InvalidArgumentException {
    List<Argument> arguments = Arrays.asList(
        Argument.build("a"),
        Argument.build("b"));
    var r = ArgumentsParser.parse(arguments, "-a\naa\t\n-b  \rbb \n\r");
    Assertions.assertEquals(mapOf(arguments, "a", "aa", "b", "bb"), r);
  }
  
  @Test
  public void argumentParser_empty_option_at_the_end() throws InvalidArgumentException {
    List<Argument> arguments = Arrays.asList(
        Argument.build("a"),
        Argument.build("b").noValue());
    var r = ArgumentsParser.parse(arguments, "-a aa -b");
    Assertions.assertEquals(mapOf(arguments, "a", "aa", "b", null), r);
  }
  
  @Test
  public void argumentParser_single_dash() throws InvalidArgumentException {
    List<Argument> arguments = Arrays.asList(
        Argument.build("a"),
        Argument.build("b").noValue());
    var r = ArgumentsParser.parse(arguments,  "- -a - -b -");
    Assertions.assertEquals(mapOf(arguments, "a", "-", "b", null), r);
  }

  @Test
  public void argumentParser_with_file() throws Exception {
    // having
    var existingFile = new File("/tmp/sample");
    var absentFile = new File("/tmp/hopefully_this_file_does_not_exist");
    var existingPath = existingFile.getAbsolutePath();
    var absentPath = absentFile.getAbsolutePath();
    try {
      Assertions.assertFalse(absentFile.exists());
      existingFile.createNewFile();
      List<Argument> arguments = Arrays.asList(
          Argument.build("exists").expectPresentFile(),
          Argument.build("absent").expectAbsentFile());
      
      // when / then 
      ArgumentsParser.parse(arguments, "-exists "+existingPath);
      ArgumentsParser.parse(arguments, "-absent "+absentPath);
      Assertions.assertThrows(InvalidArgumentException.class, () -> {
        ArgumentsParser.parse(arguments, "-exists "+absentPath);
      });
      Assertions.assertThrows(InvalidArgumentException.class, () -> {
        ArgumentsParser.parse(arguments, "-absent "+existingPath);
      });
      
    } finally {
      Assertions.assertTrue(existingFile.delete());
    }
  }
  
}
