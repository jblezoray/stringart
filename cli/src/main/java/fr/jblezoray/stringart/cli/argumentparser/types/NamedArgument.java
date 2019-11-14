package fr.jblezoray.stringart.cli.argumentparser.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NamedArgument<A> extends ReadableArgument<A> {

  private final List<String> aliases;

  private NamedArgument(NamedArgumentBuilder<A> builder) {
    super(builder);
    this.aliases = builder.aliases;
  }
  
  public static class NamedArgumentBuilder<TYPE> 
  extends ReadableArgumentBuilder<TYPE, NamedArgument<TYPE>, NamedArgumentBuilder<TYPE>> {

    protected List<String> aliases = Collections.emptyList(); 

    public NamedArgumentBuilder<TYPE> withAliases(String... aliases) {
      this.aliases = Arrays.asList(aliases);
      return this;
    }

    @Override
    public NamedArgument<TYPE> build() {
      return new NamedArgument<TYPE>(this);
    }
    
    @Override
    protected NamedArgumentBuilder<TYPE> self() {
      return this;
    }
  }

  public List<String> getAliases() {
    return aliases;
  }
}
