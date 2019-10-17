package fr.jblezoray.stringart.edge;

import org.junit.Assert;
import org.junit.Test;

import fr.jblezoray.stringart.edge.Edge;

public class EdgeTest {

  @Test
  public void test_equality_if_same_object() {
    Edge e1 = new Edge(1, true, 2, false);
    boolean eq = e1.equals(e1);
    Assert.assertTrue(eq);
  }
  
  @Test
  public void test_equality_if_identical() {
    Edge e1 = new Edge(1, true, 2, false);
    Edge e2 = new Edge(1, true, 2, false);
    boolean eq = e1.equals(e2);
    Assert.assertTrue(eq);
  }


  @Test
  public void test_equality_if_inverted() {
    Edge e1 = new Edge(1, true, 2, false);
    Edge e2 = new Edge(2, false, 1, true);
    boolean eq = e1.equals(e2);
    Assert.assertTrue(eq);
  }
}
