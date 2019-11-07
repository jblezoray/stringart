package fr.jblezoray.stringart.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.cli.Argument.ArgumentBuilder.ArgumentDefinitionException;

class ArgumentsParserTest {

  @Test
  public void argumentParser_named() throws InvalidArgumentException {
    var resultHandler = new AtomicReference<String>();
    var arg = Argument.<String>build("").withName("a").andDo(resultHandler::set);
    
    new ArgumentsParser(Collections.singletonList(arg))
        .parse("-a aa");

    Assertions.assertEquals("aa", resultHandler.toString());
  }
  
  @Test
  public void argumentParser_positioned() throws InvalidArgumentException {
    var position0 = new AtomicReference<String>();
    var position1 = new AtomicReference<String>();
    List<Argument<?>> arguments = Arrays.asList(
        Argument.<String>build("").atPosition(0).andDo(position0::set),
        Argument.<String>build("").atPosition(1).andDo(position1::set), 
        Argument.<Boolean>build("").withName("b").asFlag().andDo((x) -> {}),
        Argument.<String>build("").withName("a").andDo((x) -> {})
    );
    
    new ArgumentsParser(arguments)
        .parse("-a aa bb -b toto");

    Assertions.assertEquals("bb", position0.get());
    Assertions.assertEquals("toto", position1.get());
  }

  @Test
  public void argumentParser_nameXorPosition() {
    Assertions.assertThrows(ArgumentDefinitionException.class, () -> {
      Argument.<String>build("both").withName("a").atPosition(0).andDo((a) -> {});
    });
    Assertions.assertThrows(ArgumentDefinitionException.class, () -> {
      Argument.<String>build("none").andDo((a) -> {});
    });
  }

  @Test
  public void argumentParser_flag() throws InvalidArgumentException {
    var resultHandler = new AtomicBoolean(false);
    var arg = Argument.build("").withName("b").asFlag().andDo(resultHandler::set);
    
    new ArgumentsParser(Collections.singletonList(arg))
        .parse("-b");

    Assertions.assertEquals(true, resultHandler.get());
  }
  
  @Test
  public void argumentParser_null() {
    new ArgumentsParser(Collections.emptyList()).parse((String) null);
    new ArgumentsParser(Collections.emptyList()).parse((String[]) null);
  }
  
  @Test
  public void argumentParser_whitespaces() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        Argument.build("").withName("a").andDo((a) -> resultHandler.put("a", a)),
        Argument.build("").withName("b").andDo((b) -> resultHandler.put("b", b)));
    
    new ArgumentsParser(arguments).parse("-a\naa\t\n-b  \rbb \n\r");
    
    Assertions.assertEquals(Map.of("a", "aa", "b", "bb"), resultHandler);
  }
  
  @Test
  public void argumentParser_nonFlagOptionAtTheEnd() throws InvalidArgumentException {
    List<Argument<?>> arguments = Arrays.asList(
        Argument.build("").withName("a").andDo((a) -> {}),
        Argument.build("").withName("b").andDo((b) -> {}));

    Assertions.assertThrows(InvalidArgumentException.class, () -> {
      new ArgumentsParser(arguments).parse("-a aa -b");
    });
  }
  
  @Test
  public void argumentParser_single_dash() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        Argument.build("").atPosition(0).andDo((x) -> {}),
        Argument.build("").atPosition(1).andDo((x) -> {}),
        Argument.build("").withName("a").andDo((a) -> resultHandler.put("a", a)),
        Argument.build("").withName("b").asFlag().andDo((b) -> resultHandler.put("b", b)));
    
    new ArgumentsParser(arguments).parse("- -a - -b -");
    
    Assertions.assertEquals(Map.of("a", "-", "b", true), resultHandler);
  }


  @Test
  public void argumentParser_flag_without_boolean() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        Argument.<String>build("").withName("a").asFlag().andDo((a) -> resultHandler.put("a", a)),
        Argument.build("").withName("b").asFlag().andDo((b) -> resultHandler.put("b", b))
        );
    
    new ArgumentsParser(arguments).parse("-a -b");
    
    Assertions.assertEquals(Map.of("a", true, "b", true), resultHandler);
  }
  
  @Test
  public void argumentParser_default_values() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        Argument.build("").withName("a").orDefault(()->"aa").andDo((a) -> resultHandler.put("a", a)),
        Argument.build("").atPosition(0).orDefault(()->"bb").andDo((b) -> resultHandler.put("b", b)),
        Argument.build("").withName("c").asFlag().orDefault(() -> true).andDo((c) -> resultHandler.put("c", c)),
        Argument.build("").withName("d").asFlag().orDefault(() ->false).andDo((d) -> resultHandler.put("d", d))
        );
    
    new ArgumentsParser(arguments).parse("");
    
    Assertions.assertEquals(Map.of("a", "aa", "b", "bb", "c", true, "d", false), resultHandler);
  }

  
}
