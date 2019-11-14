package fr.jblezoray.stringart.hillclimb.listeners;

import java.io.PrintStream;

public class WriterBuilder {
  
  private PrintStream out;

  public WriterBuilder(PrintStream out) {
    this.out = out;
  }

  public ListenerPredicateBuilder debugLine() {
    return (step, it, hc) -> {
      out.format(
          "%s ; size:%s ; iteration:%d ; norm:%7.0f ; nbEdges:%4d ; "
          + "time: %5dms (time/edge:%d*%2.3fms)%s",
          step,
          hc.getRenderedResult().getSize(),
          it,
          hc.getNorm(), 
          hc.getEdges().size(), 
          hc.getTimeTook(), 
          hc.getNumberOfEdgesEvaluated(),
          hc.getTimeTook() / (float)hc.getNumberOfEdgesEvaluated(),
          System.lineSeparator());
    };
  }
  
}
