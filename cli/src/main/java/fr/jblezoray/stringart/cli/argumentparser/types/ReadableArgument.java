package fr.jblezoray.stringart.cli.argumentparser.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ReadableArgument<T> extends Argument<T> {

  private static final Function<Argument<?>, Supplier<ArgumentDefinitionException>> VALUE_READER_REQUIRED_EXCEPTION = 
      (argument) -> () -> new ArgumentDefinitionException("Argument "+argument.getName()+" requires a value reader");
  
  private final Function<String, T> valueReader;
  private final Map<Predicate<T>, String> requirements;

  protected ReadableArgument(ReadableArgumentBuilder<T, ?, ?> builder) {
    super(builder);
    this.valueReader = builder.valueReader;
    this.requirements = Collections.unmodifiableMap(builder.requirements);
    if (valueReader==null) throw VALUE_READER_REQUIRED_EXCEPTION.apply(this).get();
  }

  public static abstract class ReadableArgumentBuilder<
      TYPE,
      ARG extends ReadableArgument<TYPE>,
      THIS extends ReadableArgumentBuilder<TYPE, ARG, THIS>
  > 
      extends ArgumentBuilder<TYPE, ARG, THIS> {

    protected Function<String, TYPE> valueReader;
    protected Map<Predicate<TYPE>, String> requirements = new HashMap<Predicate<TYPE>, String>();

    public THIS withReader(Function<String, TYPE> valueReader) {
      this.valueReader = valueReader; 
      return self();
    }
    
    public THIS withRequirement(Predicate<TYPE> requirement, String errorMessage) {
      this.requirements.put(requirement, errorMessage); 
      return self();
    }

  }
  

  public Function<String, T> getValueReader() {
    return valueReader;
  }
  
  public Map<Predicate<T>, String> getRequirements() {
    return requirements;
  }
  
}
