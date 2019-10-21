package fr.jblezoray.stringart.edge;

public class DirectedEdge {
  private final Edge edge;
  private final boolean isNailATheEnd;
  
  public DirectedEdge(Edge edge, boolean isNailATheEnd) {
    this.edge = edge;
    this.isNailATheEnd = isNailATheEnd;
  }
  
  public Edge getEdge() {
    return edge;
  }
  
  public int getEndNail() {
    return isNailATheEnd ? this.edge.getNailA() : this.edge.getNailB();
  }
  
  public int getStartNail() {
    return isNailATheEnd ? this.edge.getNailB() : this.edge.getNailA();
  }

  public boolean isEndNailClockwise() {
    return isNailATheEnd ? this.edge.isNailAClockwise() : this.edge.isNailBClockwise();
  }

  public boolean isStartNailClockwise() {
    return isNailATheEnd ? this.edge.isNailBClockwise() : this.edge.isNailAClockwise();
  }

}
