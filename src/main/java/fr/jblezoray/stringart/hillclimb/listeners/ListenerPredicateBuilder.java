package fr.jblezoray.stringart.hillclimb.listeners;

public interface ListenerPredicateBuilder extends Listener {
  
  default Listener onEveryRound() {
    return (step, it, hc) -> this.notifyRoundResults(step, it, hc);
  }
  
  default Listener ifIsTrue(ListenerPredicate p) {
    return (step, it, hc) -> {
      if (p.check(step, it, hc))
        this.notifyRoundResults(step, it, hc);
    };
  }
  
  default Listener onAny(ListenerPredicate... predicates) {
    return (step, it, hc) -> {
      boolean anyMatch = false;
      for (ListenerPredicate predicate : predicates) {
        if (predicate.check(step, it, hc))
          anyMatch = true;
      }
      if (anyMatch) 
        this.notifyRoundResults(step, it, hc); 
    };
  }

}
