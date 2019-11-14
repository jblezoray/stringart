package fr.jblezoray.stringart.cli.argumentparser.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlagArgument extends Argument<Boolean> {

  private final List<String> aliases ;
  
  protected FlagArgument(FlagArgumentBuilder builder) {
    super(builder);
    this.aliases = Collections.unmodifiableList(builder.aliases);
  }

  public static class FlagArgumentBuilder 
  extends ArgumentBuilder<Boolean, FlagArgument, FlagArgumentBuilder> {

    protected List<String> aliases = Collections.emptyList();
    
    public FlagArgumentBuilder withAliases(String... aliases) {
      this.aliases = Arrays.asList(aliases);
      return self();
    }
    
    @Override
    protected FlagArgumentBuilder self() {
      return this;
    }
    
    @Override
    public FlagArgument build() {
      return new FlagArgument(this);
    }
  }
  
  public List<String> getAliases() {
    return aliases;
  }
}
