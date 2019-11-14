package fr.jblezoray.stringart.hillclimb;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.hillclimb.listeners.Listener;
import fr.jblezoray.stringart.image.ByteImage;
import fr.jblezoray.stringart.image.HarrisCornerDetection;
import fr.jblezoray.stringart.image.Image;

public class StringArt {

  private final Configuration configuration;
  
  private ByteImage referenceImg;
  private Image importanceImg;
  private Set<Listener> listeners = new HashSet<>();

  private int roundCounter =0;
  
  private Listener notifyAll = (step, count, hc) ->  
      listeners.forEach(l -> l.notifyRoundResults(step, count,  hc));
  
  public StringArt(Configuration configuration) {
    this.configuration = configuration;
  }
  
  private void init() throws IOException {
    this.referenceImg = EdgeImageIO.readFile(configuration.getGoalImage());

    if (configuration.getImportanceImage().isPresent()) {
      var impath = configuration.getImportanceImage().get();
      this.importanceImg = EdgeImageIO.readFile(impath);
      
    } else {
      this.importanceImg = new HarrisCornerDetection()
          .compute(this.referenceImg)
          .highPassFilter(500)
          .asByteImage()
          .minFilter(100);
    }
  }

  public void start(List<DirectedEdge> edges) throws IOException {
    init();
    
    double downsampleRatio = 1.0 / Math.pow(2.0, 6);
    
    StringArtHillClimb hc = null;
    
    while (downsampleRatio < 1.01) { 
      hc = this.hillClimbFactory(downsampleRatio, edges);
      this.notifyAll.notifyRoundResults(Step.SCALE, ++roundCounter, hc);
      
      this.startForScale(hc);
      
      downsampleRatio *= 2.0;
    }
    
    this.notifyAll.notifyRoundResults(Step.FINAL, roundCounter, hc);
  }
  

  public void startForScale(StringArtHillClimb hc) throws IOException {
    init(); 
    
    Step stepType = Step.ADD;
    Optional<DirectedEdge> modifiedEdge;
    int prevRoundCounter;
    do {
      prevRoundCounter = this.roundCounter;
      do {
        modifiedEdge = Step.ADD.equals(stepType) ? 
            hc.addBestPossibleEdge() : hc.removeWorstEdge();
        
        if (modifiedEdge.isPresent()) roundCounter++;
        
        this.notifyAll.notifyRoundResults(stepType, roundCounter, hc);
        
      } while (modifiedEdge.isPresent());
      
      stepType = Step.ADD.equals(stepType) ? Step.REMOVE : Step.ADD;
    } while (prevRoundCounter<this.roundCounter);
  }
  
  
  private StringArtHillClimb hillClimbFactory(
      double downsampleRatio, 
      List<DirectedEdge> edges) {
    Image referenceImgDownsized = this.referenceImg.downsample(downsampleRatio);
    Image importanceImgDownsized = this.importanceImg.downsample(downsampleRatio);
    
    float resolutionMmPerPx = configuration.getCanvasWidthMilimeters() / referenceImgDownsized.getSize().w;
    float lineThicknessInPx = configuration.getThreadThicknessMilimeters() / resolutionMmPerPx;
    float nailDiameterInPx = configuration.getNailDiameterMilimeters() / resolutionMmPerPx;
    
    var sc = new StringCharacteristics(lineThicknessInPx, 
        nailDiameterInPx, configuration.getNbNails(), configuration.isEdgeWayEnabled(),
        configuration.getMinNailsDiff(), configuration.getShape());
    
    return new StringArtHillClimb(edges, referenceImgDownsized, 
        importanceImgDownsized, sc);
  }
  
  public void addListener(Listener listener) {
    this.listeners.add(listener);
  }
  
}
