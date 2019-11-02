package fr.jblezoray.stringart.cli;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class ArgumentsParser {

  private Map<Argument, Optional<String>> parsedArgs;
  
  private List<Argument> arguments;
  private String argsLine;
  private List<String> splitted; 

  public static Map<Argument, Optional<String>> parse(
      List<Argument> arguments,
      String argsLine) 
          throws InvalidArgumentException {
    var ap = new ArgumentsParser(arguments, argsLine);
    ap.splitArgsLine();
    ap.parseToMap();
    ap.checkFiles();
    return ap.parsedArgs;
  }

  private ArgumentsParser(List<Argument> arguments, String argsLine) {
    this.arguments = arguments;
    this.parsedArgs = new HashMap<>();
    this.argsLine = Objects.requireNonNullElse(argsLine, "");
  }

  private void splitArgsLine() {
    this.splitted = Arrays.asList(this.argsLine.split("\\s+"));
  }
  
  private void parseToMap() {
    Argument optionName = null;
    for (String element : splitted) {
      
      Optional<Argument> asValidOptionName = asArgument(element);
      
      if (optionName != null) {
        this.parsedArgs.put(optionName, Optional.of(element));
        optionName = null;
        
      } else if (asValidOptionName.isPresent()){
        if (!asValidOptionName.get().isHasValue()) {
          this.parsedArgs.put(asValidOptionName.get(), Optional.empty());
        } else { 
          optionName = asValidOptionName.get();
        }
      }
      
    }
    
    if (optionName!=null)
      this.parsedArgs.put(optionName, Optional.empty());
  }

  private Optional<Argument> asArgument(String potentialArgName) {
    Optional<Argument> argument;
    if (potentialArgName.startsWith("-")) {
      String asArgName = potentialArgName.substring(1);
      argument = arguments.stream()
            .filter(arg -> arg.getArgumentName().equals(asArgName))
            .findFirst();
    } else {
      argument = Optional.empty();
    }
    return argument;
  }

  private void checkFiles() throws InvalidArgumentException {
    for (Argument arg : this.parsedArgs.keySet()) {
      if (!arg.isExpectFile()) continue;
      
      if (this.parsedArgs.get(arg).isEmpty())
        throw new InvalidArgumentException("Argument "+arg.getArgumentName()+" expects a file value.");
      
      String argValue = this.parsedArgs.get(arg).get();
      var file = new File(argValue);
      if (arg.isFileExists() && !file.exists()) 
        throw new InvalidArgumentException("File "+argValue+" does not exist.");
      else if (!arg.isFileExists() && file.exists()) 
        throw new InvalidArgumentException("File "+argValue+" already exists.");
    }
  }
  
}
