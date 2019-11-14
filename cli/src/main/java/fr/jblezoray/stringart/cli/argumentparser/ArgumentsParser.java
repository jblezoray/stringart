package fr.jblezoray.stringart.cli.argumentparser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import fr.jblezoray.stringart.cli.argumentparser.types.Argument;
import fr.jblezoray.stringart.cli.argumentparser.types.FlagArgument;
import fr.jblezoray.stringart.cli.argumentparser.types.NamedArgument;
import fr.jblezoray.stringart.cli.argumentparser.types.PositionArgument;
import fr.jblezoray.stringart.cli.argumentparser.types.ReadableArgument;

public class ArgumentsParser {

  private static final Function<Argument<?>, Supplier<InvalidArgumentException>> VALUE_REQUIRED_EXCEPTION = 
      (argument) -> () -> new InvalidArgumentException("Argument "+argument.getName()+" requires a value");

  private static final Function<String, Supplier<InvalidArgumentException>> UNKNOWN_ARGUMENT_EXCEPTION = 
      (argumentValue) -> () -> new InvalidArgumentException("Unknown argument : "+argumentValue);

  private static final Function<Argument<?>, Supplier<InvalidArgumentException>> MISSING_VALUE_EXCEPTION = 
      (argument) -> () -> new InvalidArgumentException("Missing argument : "+argument.getName());
  
  private final List<Argument<?>> arguments;
  private final Map<Argument<?>, Optional<String>> readArguments;
  private final Map<Argument<?>, Optional<?>> parsedArguments;

  public ArgumentsParser(Argument<?>... arguments) {
    this(Arrays.asList(arguments));
  }
  
  public ArgumentsParser(List<Argument<?>> arguments) {
    this.arguments = arguments;
    this.readArguments = new HashMap<>();
    this.parsedArguments = new HashMap<>();
  }

  public void parse(String argsLine) throws InvalidArgumentException {
    parse(argsLine==null||argsLine.isBlank() ? null : argsLine.trim().split("\\s+"));
  }

  public void parse(String[] args) throws InvalidArgumentException {
    String[] argsNN = Objects.requireNonNullElse(args, new String[0]);
    readArguments(Arrays.asList(argsNN));
    parseArguments();
    completeWithDefaultValues();
    checkForMissingValues();
    for (Argument<?> argument : parsedArguments.keySet()) {
      analyze(argument, parsedArguments.get(argument));
    }
  }
  

  private void readArguments(List<String> splitted) {
    ReadableArgument<?> previous = null;
    AtomicInteger currentPosition = new AtomicInteger(0);
    for (String element : splitted) {
      if (element==null) {
        continue;
      }
      
      // there is an argument waiting for his value.
      if (previous != null) {
        readArguments.put(previous, Optional.of(element));
        previous = null;
        continue; 
      } 
      
      Optional<Argument<?>> argument = asArgument(element);
      if (argument.isPresent()){
        Optional<ReadableArgument<?>> readableArg = asReadableArgument(argument.get());
        if (readableArg.isPresent()) {
          // wait for value: put appart to get value on next round of the 'for' loop.
          previous = readableArg.get();
        } else { 
          readArguments.put(argument.get(), Optional.empty());
        }
        
      } else {
        // this is a positionnal argument. 
        int position = currentPosition.getAndIncrement();
        PositionArgument<?> arg = arguments.stream()
            .map(a -> asPositionArgument(a))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(pa -> position == pa.getArgumentPosition())
            .findFirst()
            .orElseThrow(UNKNOWN_ARGUMENT_EXCEPTION.apply(element));
        readArguments.put(arg, Optional.of(element));
      }
    }
    
    if (previous!=null) {
      throw VALUE_REQUIRED_EXCEPTION.apply(previous).get();
    }
  }

  private Optional<Argument<?>> asArgument(String potentialArgName) {
    if (!potentialArgName.startsWith("-"))
      return Optional.empty();
    String asArgName = potentialArgName.substring(1);
    return arguments.stream()
          .filter(arg -> 
              arg.getName().equals(asArgName) ||
              asNamedArgument(arg)
                  .map(a->a.getAliases().contains(asArgName))
                  .orElse(false)
          )
          .findFirst();
  }

  @SuppressWarnings("rawtypes")
  private static Optional<NamedArgument<?>> asNamedArgument(Argument arg) {
    try {
      return Optional.of((NamedArgument)arg);
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }
  
  @SuppressWarnings("rawtypes")
  private static Optional<ReadableArgument<?>> asReadableArgument(Argument arg) {
    try {
      return Optional.of((ReadableArgument)arg);
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }
  
  @SuppressWarnings("rawtypes")
  private static Optional<PositionArgument<?>> asPositionArgument(Argument arg) {
    try {
      return Optional.of((PositionArgument)arg);
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }
  
  
  private void parseArguments() {
    for (Map.Entry<Argument<?>, Optional<String>> entry : readArguments.entrySet()) {
      Optional<ReadableArgument<?>> readableArg = asReadableArgument(entry.getKey());
      if (readableArg.isPresent()) {
        Function<String, ?> valueReader = readableArg.get().getValueReader();
        parsedArguments.put(entry.getKey(), entry.getValue().map(valueReader));
      } else {
        parsedArguments.put(entry.getKey(), Optional.empty());
      }
    }
  }
  
  private void completeWithDefaultValues() {
    this.arguments.stream()
        .filter((arg) -> this.parsedArguments.get(arg)==null)
        .filter((arg) -> arg.getDefaultValue().isPresent())
        .forEach((arg) -> this.parsedArguments.put(arg, arg.getDefaultValue()));
  }
  
  private void checkForMissingValues() {
    this.arguments.stream()
        .filter((arg) -> this.parsedArguments.get(arg)==null)
        .forEach((arg) -> {throw MISSING_VALUE_EXCEPTION.apply(arg).get();});
  }
  
  @SuppressWarnings("unchecked")
  private <A> void analyze(Argument<A> argument, Optional<?> value) {
    checkPredicates(argument, (Optional<A>) value);
    consume(argument, (Optional<A>) value);
  }
  
  @SuppressWarnings("unchecked")
  private <A> void checkPredicates(Argument<A> argument, Optional<A> value) {
    Optional<ReadableArgument<?>> readableArg = asReadableArgument(argument);
    if (readableArg.isEmpty())
      return;

    var requirements = readableArg.get().getRequirements();
    for (Predicate<? extends Object> predicate : requirements.keySet()) {
      A valueUnboxed = value.orElseThrow(VALUE_REQUIRED_EXCEPTION.apply(argument));
      if (!((Predicate<A>)predicate).test((A)valueUnboxed)) { 
        String errorMessage = requirements.get(predicate);
        throw new InvalidArgumentException(errorMessage);
      }
    }
  }

  private <A> void consume(Argument<A> argument, Optional<A> value) {
    if (value.isPresent()) {
      A valueUnboxed = value.orElseThrow(VALUE_REQUIRED_EXCEPTION.apply(argument));
      argument.getConsumer().accept(valueUnboxed);
      
    } else if (argument instanceof FlagArgument) {
      ((FlagArgument)argument).getConsumer().accept(true);
      
    } else {
      throw VALUE_REQUIRED_EXCEPTION.apply(argument).get();
    }
  }

}
