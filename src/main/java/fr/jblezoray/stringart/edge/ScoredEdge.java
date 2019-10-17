package fr.jblezoray.stringart.edge;

public class ScoredEdge  {
  private final Edge edge;
  private final double norm;
  private final boolean isNailATheEnd;
  private int numberOfEdgesEvaluated;
  private long timeTook;
  
  public ScoredEdge(Edge edge, double norm, boolean isNailATheEnd) {
    this.edge = edge;
    this.norm = norm;
    this.isNailATheEnd = isNailATheEnd;
  }
  
  public Edge getEdge() {
    return edge;
  }
  
  public double getNorm() {
    return norm;
  }

  public void setNumberOfEdgesEvaluated(int numberOfEdgesEvaluated) {
    this.numberOfEdgesEvaluated = numberOfEdgesEvaluated;
  }
  
  public int getNumberOfEdgesEvaluated() {
    return numberOfEdgesEvaluated;
  }

  public void setTimeTook(long timeTook) {
    this.timeTook = timeTook;
  }
  
  public long getTimeTook() {
    return timeTook;
  }

  public int getEndNail() {
    return isNailATheEnd ? this.edge.getNailA() : this.edge.getNailB();
  }

  public boolean isEndNailClockwise() {
    return isNailATheEnd ? this.edge.isNailAClockwise() : this.edge.isNailBClockwise();
  }

}
