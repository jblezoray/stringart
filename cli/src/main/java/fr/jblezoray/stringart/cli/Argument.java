package fr.jblezoray.stringart.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Argument<A> {

  private String description;
  
  private Optional<String> argumentName = Optional.empty();
  private OptionalInt argumentPosition = OptionalInt.empty();
  
  private List<String> aliases = Collections.emptyList();
  private Supplier<A> defaultValueSupplier = null;
  private Consumer<A> consumer = null;
  private Map<Predicate<A>, String> predicates = new HashMap<>();
  private boolean isFlag = false;

  private Argument() {}
  
  public static <A> ArgumentBuilder<A> build(String description) {
    return new ArgumentBuilder<A>(description);
  }

  public static class ArgumentBuilder<B> {
    
    private Argument<B> ongoing;

    static class ArgumentDefinitionException extends RuntimeException {
      private static final long serialVersionUID = 1L;
      public ArgumentDefinitionException(String description) {
        super(description);
      }
    }

    public ArgumentBuilder(String description) {
      this.ongoing = new Argument<B>();
      this.ongoing.description = description;
    }

    @SuppressWarnings("unchecked")
    public <BOOL extends Boolean> ArgumentBuilder<? extends BOOL> asFlag() {
      this.ongoing.isFlag = true;
      return (ArgumentBuilder<BOOL>)this;
    }

    public ArgumentBuilder<B> orDefault(Supplier<B> defaultValueSupplier) {
      this.ongoing.defaultValueSupplier = defaultValueSupplier;
      return this;
    }
    
    public ArgumentBuilder<B> withName(String argumentName, String... aliases) {
      if (this.ongoing.argumentPosition.isPresent())
        throw new ArgumentDefinitionException("A named argument can not have a position");
      this.ongoing.argumentName = Optional.of(argumentName);
      this.ongoing.aliases = Arrays.asList(aliases);
      return this;
    }
    
    public ArgumentBuilder<B> atPosition(int argumentPosition) {
      if (this.ongoing.argumentName.isPresent())
        throw new ArgumentDefinitionException("A named argument can not have a position");
      this.ongoing.argumentPosition = OptionalInt.of(argumentPosition);
      return this;
    }

    public ArgumentBuilder<B> withRequiredCondition(Predicate<B> predicate, String errorMessage) {
      this.ongoing.predicates.put(predicate, errorMessage); 
      return this;
    }

    public Argument<B> andDo(Consumer<B> consumer) {
      this.ongoing.consumer = consumer;
      // ensure immutability of mutables.
      this.ongoing.aliases = Collections.unmodifiableList(this.ongoing.aliases);
      this.ongoing.predicates = Collections.unmodifiableMap(this.ongoing.predicates);
      if (!(this.ongoing.argumentName.isPresent() ^ this.ongoing.argumentPosition.isPresent())) {
        throw new ArgumentDefinitionException("An argument must define either a name or a position.");
      }
      return this.ongoing;
    }
    
  }
  
  public Optional<String> getArgumentName() {
    return argumentName;
  }

  public List<String> getAliases() {
    return aliases;
  }

  public OptionalInt getArgumentPosition() {
    return argumentPosition;
  }

  public Optional<A> getDefaultValue() {
    return Optional.ofNullable(defaultValueSupplier).map(Supplier::get);
  }

  public Consumer<A> getConsumer() {
    return consumer;
  }

  public String getDescription() {
    return description;
  }

  public Map<Predicate<A>, String> getPredicates() {
    return predicates;
  }
  
  @Override
  public String toString() {
    return "("+argumentName+")";
  }

  public boolean isFlag() {
    return isFlag;
  }
}
