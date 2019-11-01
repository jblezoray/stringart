package fr.jblezoray.stringart;

import java.io.IOException;
import java.util.ArrayList;

import fr.jblezoray.stringart.hillclimb.Step;
import fr.jblezoray.stringart.hillclimb.StringArt;
import fr.jblezoray.stringart.hillclimb.listeners.Listener;
import static fr.jblezoray.stringart.hillclimb.listeners.ListenerPredicate.*;

public class Main {
  
  public static void main(String[] args) throws IOException {
    Configuration configuration = new Configuration();
    StringArt stringArt = new StringArt(configuration);
    
    stringArt.addListener(Listener
        .writeTo(System.out)
        .debugLine()
        .onEveryRound());
    
    stringArt.addListener(Listener
        .saveToFile(configuration.getRenderedStringPathFilename())
        .stringPath()
        .ifIsTrue(everyXRound(10)));

    stringArt.addListener(Listener
        .saveToFile(configuration.getRenderedImageName())
        .image(hc -> hc.getRenderedResult())
        .onAny(afterDelay(2), onStep(Step.FINAL)));

    stringArt.addListener(Listener
        .saveToFile(configuration.getRenderedImageDifferenceName())
        .image(hc -> hc.getRenderedResult()
            .differenceWith(hc.getReferenceImage())
            .multiplyWith(hc.getImportanceImage()))
        .onAny(afterDelay(2), onStep(Step.FINAL)));

    stringArt.start(new ArrayList<>());
  }
  
}
