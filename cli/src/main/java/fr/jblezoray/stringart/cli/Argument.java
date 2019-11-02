package fr.jblezoray.stringart.cli;

public class Argument {

  private final String argumentName;
  private boolean hasValue = true;
  private boolean expectFile = false;
  private boolean fileExists = false;

  private Argument(String argumentName) {
    this.argumentName = argumentName;
  }
  
  public static Argument build(String argumentName) {
    return new Argument(argumentName);
  }
  
  public Argument noValue() {
    this.hasValue = false;
    return this;
  }

  private Argument expectFile() {
    this.expectFile = true;
    return this;
  }

  public Argument expectPresentFile() {
    this.fileExists = true;
    return expectFile();
  }

  public Argument expectAbsentFile() {
    this.fileExists = false;
    return expectFile();
  }

  public String getArgumentName() {
    return argumentName;
  }

  public boolean isHasValue() {
    return hasValue;
  }
  
  public boolean isExpectFile() {
    return expectFile;
  }

  public boolean isFileExists() {
    return fileExists;
  }
  
  @Override
  public String toString() {
    return "("+argumentName+")";
  }
  
}
