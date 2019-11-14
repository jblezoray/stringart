package fr.jblezoray.stringart.cli.argumentparser.types;

import java.util.function.Supplier;

public class PositionArgument<A> extends ReadableArgument<A> {

  private static final Supplier<ArgumentDefinitionException> POSITION_REQUIRED_EXCEPTION = 
      () -> new ArgumentDefinitionException("A position argument requires a position.");

  private final int argumentPosition;

  private PositionArgument(PositionArgumentBuilder<A> builder) {
    super(builder);
    this.argumentPosition = builder.position;
    if (this.argumentPosition<0) throw POSITION_REQUIRED_EXCEPTION.get();

  }

  public static class PositionArgumentBuilder<TYPE>
  extends ReadableArgumentBuilder<TYPE, PositionArgument<TYPE>, PositionArgumentBuilder<TYPE>> {

    protected int position = -1;
    
    public PositionArgumentBuilder<TYPE> atPosition(int position) {
      this.position = position;
      return this;
    }

    @Override
    public PositionArgument<TYPE> build() {
      return new PositionArgument<TYPE>(this);
    }
    
    @Override
    protected PositionArgumentBuilder<TYPE> self() {
      return this;
    }
    
  }

  public int getArgumentPosition() {
    return argumentPosition;
  }

}
