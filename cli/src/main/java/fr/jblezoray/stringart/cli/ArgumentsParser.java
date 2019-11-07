package fr.jblezoray.stringart.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

class ArgumentsParser {

  private static final Function<Argument<?>, Supplier<InvalidArgumentException>> VALUE_REQUIRED_EXCEPTION = 
      (argument) -> () -> new InvalidArgumentException("Argument "+humanReadableNameFor(argument)+" requires a value");

  private static final Supplier<InvalidArgumentException> NAME_OR_POSITION_REQUIRED_EXCEPTION = 
      () -> new InvalidArgumentException("Every Argument requires either a name or a position.");

  private static final Function<Argument<?>, Supplier<InvalidArgumentException>> FLAG_CAN_NOT_HAVE_PREDICATE = 
      (argument) -> () -> new InvalidArgumentException("Flag "+humanReadableNameFor(argument)+" can not have a predicate.");

  private static final Function<String, Supplier<InvalidArgumentException>> UNKNOWN_ARGUMENT_EXCEPTION = 
      (argumentValue) -> () -> new InvalidArgumentException("Unknown argument :"+argumentValue);
  
  private final List<Argument<?>> arguments;
  private final Map<Argument<?>, Optional<?>> parsedArguments;

  public ArgumentsParser(List<Argument<?>> arguments) {
    this.arguments = arguments;
    this.parsedArguments = new HashMap<>();
  }
  public ArgumentsParser(Argument<?>... arguments) {
    this.arguments = Arrays.asList(arguments);
    this.parsedArguments = new HashMap<>();
  }
  
  public void parse(String argsLine) throws InvalidArgumentException {
    String argsLineNN = Objects.requireNonNullElse(argsLine, "");
    parse(argsLineNN.split("\\s+"));
  }

  public void parse(String[] args) throws InvalidArgumentException {
    String[] argsNN = Objects.requireNonNullElse(args, new String[0]);
    parseToMap(Arrays.asList(argsNN));
    completeWithDefaultValues();
    for (Argument<?> argument : parsedArguments.keySet()) {
      analyze(argument, parsedArguments.get(argument));
    }
  }
  
  @SuppressWarnings("unchecked")
  private <A> void analyze(Argument<A> argument, Optional<?> value) {
    checkPredicates(argument, (Optional<A>) value);
    consume(argument, (Optional<A>) value);
  }
  
  private <A> void checkPredicates(Argument<A> argument, Optional<A> value) {
    Map<Predicate<A>, String> predicates = argument.getPredicates();
    if (argument.isFlag() && !predicates.isEmpty())
      throw FLAG_CAN_NOT_HAVE_PREDICATE.apply(argument).get();
    
    for (Predicate<A> predicate : predicates.keySet()) {
      A valueUnboxed = requireValue(argument, value);
      if (!predicate.test(valueUnboxed)) { 
        String errorMessage = predicates.get(predicate);
        throw new InvalidArgumentException(errorMessage);
      }
    }
  }

  private <A> A requireValue(Argument<A> argument, Optional<A> value) {
    return value.orElseThrow(VALUE_REQUIRED_EXCEPTION.apply(argument)); 
  }

  private static String humanReadableNameFor(Argument<?> argument) {
    return argument.getArgumentName().orElse(
        Integer.toString(
            argument
                .getArgumentPosition()
                .orElseThrow(NAME_OR_POSITION_REQUIRED_EXCEPTION)
        )
    );
  }
  
  private <A> void consume(Argument<A> argument, Optional<A> value) {
    A valueUnboxed = requireValue(argument, value);
    argument.getConsumer().accept(valueUnboxed);
  }

  private void parseToMap(List<String> splitted) {
    Argument<?> argument = null;
    AtomicInteger currentPosition = new AtomicInteger(0);
    for (String element : splitted) {
      if (element==null || element.length()==0) {
        continue;
      }
      if (argument != null) {
        parsedArguments.put(argument, Optional.of(element));
        argument = null;
        
      } else {
        Optional<Argument<?>> asValidOptionName = asArgument(element);
        if (asValidOptionName.isPresent()){
          if (asValidOptionName.get().isFlag()) {
            parsedArguments.put(asValidOptionName.get(), Optional.of(true));
            
          } else { 
            // put appart to get value on next round of the for loop.
            argument = asValidOptionName.get();
          }
          
        } else {
          int position = currentPosition.getAndIncrement();
          Optional<Argument<?>> arg = arguments.stream()
              .filter(a -> position == a.getArgumentPosition().orElseGet(() -> {return -1;}))
              .findFirst();
          if (arg.isEmpty()) throw UNKNOWN_ARGUMENT_EXCEPTION.apply(element).get();
          parsedArguments.put(arg.get(), Optional.of(element));
        }
      }
      
    }
    
    if (argument!=null) {
      if (argument.isFlag())
        parsedArguments.put(argument, Optional.empty());
      else 
        throw VALUE_REQUIRED_EXCEPTION.apply(argument).get();
    }
  }
  
  private void completeWithDefaultValues() {
    this.arguments.stream()
        .filter((arg) -> this.parsedArguments.get(arg)==null)
        .filter((arg) -> arg.getDefaultValue().isPresent())
        .forEach((arg) -> this.parsedArguments.put(arg, arg.getDefaultValue()));
  }

  private Optional<Argument<?>> asArgument(String potentialArgName) {
    Optional<Argument<?>> argument;
    if (potentialArgName.startsWith("-")) {
      String asArgName = potentialArgName.substring(1);
      argument = arguments.stream()
            .filter(arg -> arg.getArgumentName().isPresent())
            .filter(arg -> arg.getArgumentName().get().equals(asArgName))
            .findFirst();
    } else {
      argument = Optional.empty();
    }
    return argument;
  }

}
