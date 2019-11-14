package fr.jblezoray.stringart.cli.argumentparser.types;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Argument<T> {

  private static final Supplier<ArgumentDefinitionException> NAME_REQUIRED_EXCEPTION = 
      () -> new ArgumentDefinitionException("Every Argument requires a name.");

  private final String name;
  private final String description;
  private final Supplier<T> defaultValueSupplier;
  private final Consumer<T> consumer;

  protected Argument(ArgumentBuilder<T, ?, ?> builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.defaultValueSupplier = builder.defaultValueSupplier;
    this.consumer = builder.consumer;
    if (this.name==null) throw NAME_REQUIRED_EXCEPTION.get();
  }
  
  public static abstract class ArgumentBuilder<
      TYPE,
      ARG extends Argument<TYPE>,
      THIS extends ArgumentBuilder<TYPE, ARG, THIS>
  > {

    protected String name; 
    protected String description = "";
    protected Supplier<TYPE> defaultValueSupplier;
    protected Consumer<TYPE> consumer;
    
    public THIS withName(String name) {
      this.name = name;
      return self();
    }
    
    public THIS withDescription(String description) {
      this.description = description;
      return self();
    }
    
    public THIS orDefault(Supplier<TYPE> defaultValueSupplier) {
      this.defaultValueSupplier = defaultValueSupplier;
      return self();
    }

    public THIS andDo(Consumer<TYPE> consumer) {
      this.consumer = consumer;
      return self();
    }

    public abstract ARG build();
    
    protected abstract THIS self();
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
  
  public Optional<T> getDefaultValue() {
    return Optional.ofNullable(defaultValueSupplier).map(Supplier::get);
  }

  public Consumer<T> getConsumer() {
    return consumer;
  }
  
  @Override
  public String toString() {
    return "("+description+")";
  }
}
