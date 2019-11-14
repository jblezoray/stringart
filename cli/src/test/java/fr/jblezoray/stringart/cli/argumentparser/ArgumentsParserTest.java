package fr.jblezoray.stringart.cli.argumentparser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.cli.argumentparser.types.Argument;
import fr.jblezoray.stringart.cli.argumentparser.ArgumentsParser;
import fr.jblezoray.stringart.cli.argumentparser.InvalidArgumentException;
import fr.jblezoray.stringart.cli.argumentparser.types.ArgumentDefinitionException;
import fr.jblezoray.stringart.cli.argumentparser.types.FlagArgument;
import fr.jblezoray.stringart.cli.argumentparser.types.NamedArgument;
import fr.jblezoray.stringart.cli.argumentparser.types.NamedArgument.NamedArgumentBuilder;
import fr.jblezoray.stringart.cli.argumentparser.types.PositionArgument;

class ArgumentsParserTest {

  @Test
  public void argumentParser_named() throws InvalidArgumentException {
    var resultHandler = new AtomicReference<String>();
    var arg = new NamedArgumentBuilder<String>().withReader(s->s).withName("a").andDo(resultHandler::set).build();
    
    new ArgumentsParser(arg)
        .parse("-a aa");

    Assertions.assertEquals("aa", resultHandler.toString());
  }

  @Test
  public void argumentParser_alias() throws InvalidArgumentException {
    var resultHandler = new AtomicReference<String>();
    var arg = new NamedArgumentBuilder<String>().withReader(s->s).withName("x").withAliases("alias").andDo(resultHandler::set).build();
    
    new ArgumentsParser(arg)
        .parse("-alias aa");

    Assertions.assertEquals("aa", resultHandler.toString());
  }
  
  @Test
  public void argumentParser_positioned() throws InvalidArgumentException {
    var position0 = new AtomicReference<String>();
    var position1 = new AtomicReference<String>();
    List<Argument<?>> arguments = Arrays.asList(
        new PositionArgument.PositionArgumentBuilder<String>().withName("x").withReader(s->s).atPosition(0).andDo(position0::set).build(),
        new PositionArgument.PositionArgumentBuilder<String>().withName("x").withReader(s->s).atPosition(1).andDo(position1::set).build(),
        new FlagArgument.FlagArgumentBuilder().withName("b").andDo((x) -> {}).build(),
        new NamedArgumentBuilder<String>().withReader(s->s).withName("a").andDo((x) -> {}).build()
    );
    
    new ArgumentsParser(arguments)
        .parse("-a aa bb -b toto");

    Assertions.assertEquals("bb", position0.get());
    Assertions.assertEquals("toto", position1.get());
  }

  @Test
  public void argumentParser_nameRequired() {
    new PositionArgument.PositionArgumentBuilder<String>().withName("a").withReader(s->s).atPosition(0).andDo((x)->{}).build();
    Assertions.assertThrows(ArgumentDefinitionException.class, () -> 
      new PositionArgument.PositionArgumentBuilder<String>().withReader(s->s).atPosition(0).andDo((x)->{}).build()
    );
  }

  @Test
  public void argumentParser_positionRequired() {
    new PositionArgument.PositionArgumentBuilder<String>().withName("a").withReader(s->s).atPosition(0).andDo((x)->{}).build();
    Assertions.assertThrows(ArgumentDefinitionException.class, () -> 
      new PositionArgument.PositionArgumentBuilder<String>().withName("a").withReader(s->s).andDo((x)->{}).build()
    );
  }

  @Test
  public void argumentParser_flag() throws InvalidArgumentException {
    var resultHandler = new AtomicBoolean(false);
    var arg = new FlagArgument.FlagArgumentBuilder().withName("b").andDo(resultHandler::set).build();
    
    new ArgumentsParser(arg)
        .parse("-b");

    Assertions.assertEquals(true, resultHandler.get());
  }
  
  @Test
  public void argumentParser_null_empty() {
    new ArgumentsParser(Collections.emptyList()).parse("");
    new ArgumentsParser(Collections.emptyList()).parse("   \t\n");
    new ArgumentsParser(Collections.emptyList()).parse((String) null);
    new ArgumentsParser(Collections.emptyList()).parse((String[]) null);
  }
  
  @Test
  public void argumentParser_whitespaces() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        new NamedArgumentBuilder<String>().withReader(s->s).withName("a").andDo(a -> resultHandler.put("a", a)).build(),
        new NamedArgumentBuilder<String>().withReader(s->s).withName("b").andDo(b -> resultHandler.put("b", b)).build());
    
    new ArgumentsParser(arguments).parse("-a\naa\t\n-b  \rbb \n\r");
    
    Assertions.assertEquals(Map.of("a", "aa", "b", "bb"), resultHandler);
  }
  
  @Test
  public void argumentParser_nonFlagOptionAtTheEnd() throws InvalidArgumentException {
    List<Argument<?>> arguments = Arrays.asList(
        new NamedArgumentBuilder<String>().withReader(s->s).withName("a").andDo(a -> {}).build(),
        new NamedArgumentBuilder<String>().withReader(s->s).withName("b").andDo(b -> {}).build());

    Assertions.assertThrows(InvalidArgumentException.class, () -> {
      new ArgumentsParser(arguments).parse("-a aa -b");
    });
  }
  
  @Test
  public void argumentParser_single_dash() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        new PositionArgument.PositionArgumentBuilder<String>().withName("x").withReader(s->s).atPosition(0).andDo(x->{}).build(),
        new PositionArgument.PositionArgumentBuilder<String>().withName("x").withReader(s->s).atPosition(1).andDo(x->{}).build(),
        new FlagArgument.FlagArgumentBuilder().withName("b").andDo((b) -> resultHandler.put("b", b)).build(),
        new NamedArgumentBuilder<String>().withReader(s->s).withName("a").andDo((a) -> resultHandler.put("a", a)).build());
    
    new ArgumentsParser(arguments).parse("- -a - -b -");
    
    Assertions.assertEquals(Map.of("a", "-", "b", true), resultHandler);
  }

  @Test
  public void argumentParser_default_values() throws InvalidArgumentException {
    var resultHandler = new HashMap<>();
    List<Argument<?>> arguments = Arrays.asList(
        new NamedArgument.NamedArgumentBuilder<String>().withReader(x->x).withName("a").orDefault(()->"aa").andDo(a -> resultHandler.put("a", a)).build(),
        new PositionArgument.PositionArgumentBuilder<String>().withReader(x->x).withName("b").orDefault(()->"bb").atPosition(0).andDo(b -> resultHandler.put("b", b)).build(),
        new FlagArgument.FlagArgumentBuilder().withName("c").orDefault(() -> true).andDo(c -> resultHandler.put("c", c)).build(),
        new FlagArgument.FlagArgumentBuilder().withName("d").orDefault(() -> false).andDo(d -> resultHandler.put("d", d)).build()
        );
    
    new ArgumentsParser(arguments).parse("");
    
    Assertions.assertEquals(Map.of("a", "aa", "b", "bb", "c", true, "d", false), resultHandler);
  }
  
  @Test
  public void argumentParser_check_requirement() throws InvalidArgumentException {
    var arg = new NamedArgument.NamedArgumentBuilder<String>().withReader(x->x).withName("a").withRequirement((a)->false, "Boom").andDo(a -> {}).build();

    Assertions.assertThrows(InvalidArgumentException.class, () -> {
      new ArgumentsParser(arg).parse("-a");
    });
  }

  
}
