package fr.jblezoray.stringart.cli.argumentparser.types;

public class ArgumentDefinitionException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  public ArgumentDefinitionException(String description) {
    super(description);
  }
  
}