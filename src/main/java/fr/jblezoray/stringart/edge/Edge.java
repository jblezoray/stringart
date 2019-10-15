package fr.jblezoray.stringart.edge;

import java.util.Comparator;
import java.util.Objects;

import fr.jblezoray.stringart.core.EdgeDrawer;
import fr.jblezoray.stringart.image.CompressedByteImage;

public class Edge {

  private final int nailA;
  private final boolean nailAClockwise;
  private final int nailB;
  private final boolean nailBClockwise;
  private final EdgeDrawer edgeDrawer;
  
  /**
   * This value is lazily constructed. 
   */
  private CompressedByteImage imageData;
  
  public Edge(
      int nailA, boolean nailAClockwise, 
      int nailB, boolean nailBClockwise, 
      EdgeDrawer edgeDrawer) {
    if (nailA == nailB) { 
      throw new RuntimeException("cannot draw an edge if the two nails are identical");
    }
    // NailA is always the smallest.
    if (nailA < nailB) {
      this.nailA = nailA;
      this.nailAClockwise = nailAClockwise;
      this.nailB = nailB;
      this.nailBClockwise = nailBClockwise;
    } else {
      this.nailA = nailB;
      this.nailAClockwise = nailBClockwise;
      this.nailB = nailA;
      this.nailBClockwise = nailAClockwise;
    }
    this.edgeDrawer = edgeDrawer;
  }
  
  public int getNailA() {
    return nailA;
  }
  
  public int getNailB() {
    return nailB;
  }

  public boolean isNailAClockwise() {
    return nailAClockwise;
  }
  
  public boolean isNailBClockwise() {
    return nailBClockwise;
  }
  
  public CompressedByteImage getDrawnEdgeData() {
    // lazy initialization.
    if (imageData==null) {
      synchronized (this) {
        if (imageData==null) {
          this.imageData = this.edgeDrawer.drawEdge(this);
        }
      }
    }
      
    return imageData;
  }

  public boolean wayOf(int nail) {
    return this.nailA == nail ? this.nailAClockwise : this.nailBClockwise;
  }

  public boolean contains(int nail, boolean clockwise) {
    return (this.nailA==nail && this.nailAClockwise == clockwise) 
        || (this.nailB==nail && this.nailBClockwise == clockwise);
  }

  
  public final static Comparator<Edge> COMPARATOR = (e1, e2) ->
        (e1.getNailA() != e2.getNailA()) ? e1.getNailA() - e2.getNailA() : 
        (e1.getNailB() != e2.getNailB()) ? e1.getNailB() - e2.getNailB() : 
        (e1.isNailAClockwise() == e2.isNailAClockwise() && e1.isNailBClockwise() == e2.isNailBClockwise()) ? 0 : 
        1;
        
  @Override
  public boolean equals(Object o) {
    return (o instanceof Edge) && 0==COMPARATOR.compare(this, (Edge)o);
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(this);
  }
  
  @Override
  public String toString() {
    return "("
        +this.nailA+(this.nailAClockwise?'+':'-')+","
        +this.nailB+(this.nailBClockwise?'+':'-')+")";
  }
  
}