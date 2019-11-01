package fr.jblezoray.stringart.hillclimb.listeners;

import java.util.Arrays;

public interface ListenerPredicateBuilder extends Listener {

  default Listener onEveryRound() {
    return (step, it, hc) -> this.notifyRoundResults(step, it, hc);
  }
  
  default Listener ifIsTrue(ListenerPredicate p) {
    return (step, it, hc) -> onAny(p);
  }
  
  default Listener onAny(ListenerPredicate... predicates) {
    return (step, it, hc) -> Arrays.stream(predicates)
          .filter(predicate -> predicate.check(step, it, hc))
          .findFirst()
          .ifPresent((p) -> this.notifyRoundResults(step, it, hc) ); 
  }

}
