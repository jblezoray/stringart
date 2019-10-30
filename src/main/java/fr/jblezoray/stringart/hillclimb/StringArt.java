package fr.jblezoray.stringart.hillclimb;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.jblezoray.stringart.Configuration;
import fr.jblezoray.stringart.core.EdgeImageIO;
import fr.jblezoray.stringart.edge.DirectedEdge;
import fr.jblezoray.stringart.hillclimb.listeners.IStringArtAlgoListener;
import fr.jblezoray.stringart.hillclimb.listeners.Step;
import fr.jblezoray.stringart.image.ByteImage;
import fr.jblezoray.stringart.image.HarrisCornerDetection;
import fr.jblezoray.stringart.image.Image;
import fr.jblezoray.stringart.image.UnboundedImage;

public class StringArt {

  private final ByteImage referenceImg;
  private final Image importanceImg;
  private final Configuration configuration;

  private Set<IStringArtAlgoListener> processingResultListeners = new HashSet<>();
  
  private int roundCounter =0;
  
  public StringArt(Configuration configuration) throws IOException {
    this.configuration = configuration;
    
    this.referenceImg = EdgeImageIO.readFile(configuration.getGoalImagePath());

    if (configuration.getImportanceImagePath().isPresent()) {
      var impath = configuration.getImportanceImagePath().get();
      this.importanceImg = EdgeImageIO.readFile(impath);
      
    } else {
      this.importanceImg = new HarrisCornerDetection()
          .compute(this.referenceImg)
          .highPassFilter(500)
          .asByteImage()
          .minFilter(100);
    }
  }

  public void start(List<DirectedEdge> edges) {
    double downsampleRatio = 1.0 / Math.pow(2.0, 6);
    
    StringArtHillClimb hc = null;
    
    while (downsampleRatio < 1.01) { 
      hc = this.hillClimbFactory(downsampleRatio, edges);
      this.notifyResultToListeners(Step.SCALE, ++roundCounter, hc.getEdges(), 
          hc.getRenderedResult(), hc.getReferenceImage(), hc.getImportanceImage(),
          Optional.empty(), 0, hc.getNumberOfEdgesEvaluated(), hc.getTimeTook());
      
      this.startForScale(hc);
      
      downsampleRatio *= 2.0;
    }
    
    this.notifyResultToListeners(Step.FINAL, roundCounter, hc.getEdges(),
        hc.getRenderedResult(), hc.getReferenceImage(), hc.getImportanceImage(),
        Optional.empty(), hc.getNorm(), hc.getNumberOfEdgesEvaluated(), 
        hc.getTimeTook());
  }
  

  public void startForScale(StringArtHillClimb hc) {
    Step stepType = Step.ADD;
    Optional<DirectedEdge> modifiedEdge;
    int prevRoundCounter;
    do {
      prevRoundCounter = this.roundCounter;
      do {
        modifiedEdge = Step.ADD.equals(stepType) ? 
            hc.addBestPossibleEdge() : hc.removeWorstEdge();
        
        if (modifiedEdge.isPresent()) roundCounter++;
        
        this.notifyResultToListeners(stepType, roundCounter, hc.getEdges(), 
            hc.getRenderedResult(), hc.getReferenceImage(), hc.getImportanceImage(), 
            modifiedEdge, hc.getNorm(), hc.getNumberOfEdgesEvaluated(), 
            hc.getTimeTook());
        
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

  
  private void notifyResultToListeners(Step operationDescription, 
      int iterationNumber, List<DirectedEdge> edges,
      UnboundedImage currentImage, Image referenceImage, Image importanceImage, 
      Optional<DirectedEdge> modifiedEdge, double norm,
      int numberOfEdgesEvaluated, long timeTook) {
    this.processingResultListeners.forEach(listener -> 
        listener.notifyRoundResults(operationDescription, iterationNumber, 
            currentImage, edges, importanceImage, referenceImage, modifiedEdge, norm, 
            numberOfEdgesEvaluated, timeTook)
    );
  }
  
  
  public void addListener(IStringArtAlgoListener listener) {
    this.processingResultListeners.add(listener);
  }
}
