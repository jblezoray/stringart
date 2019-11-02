package fr.jblezoray.stringart.cli;

public class InvalidArgumentException extends Exception {

  private static final long serialVersionUID = 1L;

  public InvalidArgumentException(String e) {
    super(e);
  }

}
