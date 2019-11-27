package fr.jblezoray.stringart.core;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics.Shape;

public class EdgeFactoryTest {

  
  @Test
  public void getEdge_minimumNailsDistance() {
    var sc = new StringCharacteristics(
        1.0f, 2.0f, 5, true, 2, Shape.CIRCLE);
    EdgeFactory ef = new EdgeFactory(sc);

    Assertions.assertTrue(ef.getEdge(2, true, 0, true).isPresent());// distance 2
    Assertions.assertTrue(ef.getEdge(2, true, 1, true).isEmpty());// distance 1
    Assertions.assertTrue(ef.getEdge(2, true, 2, true).isEmpty());// distance 0
    Assertions.assertTrue(ef.getEdge(2, true, 3, true).isEmpty());// distance 1
    Assertions.assertTrue(ef.getEdge(2, true, 4, true).isPresent());// distance 2

    Assertions.assertTrue(ef.getEdge(0, true, 4, true).isPresent());// distance 1
  }
  
  @Test
  public void getEdge_edgewayensabled() {
    var sc = new StringCharacteristics(
        1.0f, 2.0f, 5, true, 2, Shape.CIRCLE);
    EdgeFactory ef = new EdgeFactory(sc);

    Assertions.assertTrue(ef.getEdge(1, true, 2, true).isEmpty());
   
    Assertions.assertTrue(ef.getEdge(1, true, 4, true).isPresent());
    Assertions.assertTrue(ef.getEdge(1, true, 4, false).isPresent());
    Assertions.assertTrue(ef.getEdge(1, false, 4, true).isPresent());
    Assertions.assertTrue(ef.getEdge(1, false, 4, false).isPresent());
    
    Assertions.assertFalse(ef.getEdge(1, false, 3, true).equals(ef.getEdge(1, true, 3, true)));
    
    Assertions.assertTrue(ef.getEdge(3, true, 7, false).isEmpty());
    Assertions.assertTrue(ef.getEdge(-1, false, 3, false).isEmpty());
  }
  
  @Test
  public void getEdge_edgewaydisabled() {
    var sc = new StringCharacteristics(
        1.0f, 2.0f, 5, false, 1, Shape.CIRCLE);
    EdgeFactory ef = new EdgeFactory(sc);
    
    Assertions.assertTrue(ef.getEdge(1, true, 3, true).isPresent());
    Assertions.assertTrue(ef.getEdge(1, true, 3, false).isPresent());
    Assertions.assertTrue(ef.getEdge(1, false, 3, true).isPresent());
    Assertions.assertTrue(ef.getEdge(1, false, 3, false).isPresent());
    
    Assertions.assertTrue(ef.getEdge(1, false, 3, true).equals(ef.getEdge(1, true, 3, true)));
  }
  
  @Test
  public void streamEdges() {
    var sc = new StringCharacteristics(
        1.0f, 2.0f, 5, false, 1, Shape.CIRCLE);
    EdgeFactory ef = new EdgeFactory(sc);
    
    Stream<Edge> edges = ef.streamEdges(2, true);
    
    Assertions.assertEquals(4, edges.count());
    
  }
}
