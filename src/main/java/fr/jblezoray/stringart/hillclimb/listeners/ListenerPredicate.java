package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.Arrays;
import java.util.Date;

import fr.jblezoray.stringart.hillclimb.Step;
import fr.jblezoray.stringart.hillclimb.StringArtHillClimb;

public interface ListenerPredicate {
  
  Boolean check(Step step, Integer it, StringArtHillClimb hc);
  
  static ListenerPredicate everyXRound(int rounds) {
    return (step, it, hc) -> {
      return it%rounds == 0;
    };
  }
  
  static ListenerPredicate onStep(Step... steps) {
    return (step, it, hc) -> {
      return Arrays.binarySearch(steps, step)>=0;
    };
  }
  
  static ListenerPredicate afterDelay(int seconds) {
    return new ListenerPredicate() {
      private long prevTimestampMS = 0;
      @Override
      public Boolean check(Step step, Integer it, StringArtHillClimb hc) {
        long curTimestampMS = new Date().getTime();
        boolean timeIsUp = curTimestampMS - prevTimestampMS >= seconds * 1_000;
        if (timeIsUp) prevTimestampMS = curTimestampMS;
        return timeIsUp;
      }
    };
  }
}