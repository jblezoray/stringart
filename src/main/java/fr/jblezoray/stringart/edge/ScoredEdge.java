package fr.jblezoray.stringart.edge;

public class ScoredEdge  {
  private final Edge edge;
  private final double norm;
  private int numberOfEdgesEvaluated;
  private long timeTook;
  
  public ScoredEdge(Edge edge, double norm) {
    this.edge = edge;
    this.norm = norm;
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
  
}
