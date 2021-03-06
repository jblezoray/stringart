package fr.jblezoray.stringart.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.jblezoray.stringart.core.CircleEdgeDrawer;
import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.hillclimb.StringCharacteristics.Shape;
import fr.jblezoray.stringart.image.CompressedByteImage;
import fr.jblezoray.stringart.image.ImageSize;
import fr.jblezoray.stringart.image.UnboundedImage;

public class CircleEdgeDrawerTest {
  
  @Test
  public void compressed_image_shall_encode_the_right_nb_of_bytes() {
    ImageSize size = new ImageSize(1000, 1000);
    StringCharacteristics sc = new StringCharacteristics(
        1.0f, 2.0f, 5, true, 1, Shape.CIRCLE);
    CircleEdgeDrawer d = new CircleEdgeDrawer(size, sc);
    Edge edge = new Edge(1, false, 3, false);
    
    CompressedByteImage drawnEdge = d.drawEdge(edge);

    byte[] comp = drawnEdge.getCompressedData();
    int cpt = 0;
    for (int i=0; i<comp.length; i+=2) 
      cpt += Byte.toUnsignedInt(comp[i]);
    Assertions.assertEquals(size.nbPixels, cpt);
  }
  
  @Test
  public void drawing_a_edge_on_a_white_image_equals_the_edge_itself() {
    ImageSize size = new ImageSize(10, 10);
    StringCharacteristics sc = new StringCharacteristics(
        1.0f, 2.0f, 5, true, 1, Shape.CIRCLE);
    CircleEdgeDrawer d = new CircleEdgeDrawer(size, sc);
    Edge e = new Edge(1, false, 3, false);
    
    CompressedByteImage drawnEdge = d.drawEdge(e);
    UnboundedImage image = new UnboundedImage(size).add(drawnEdge);

    Assertions.assertArrayEquals(
        drawnEdge.asByteImage().getRawBytes(), 
        image.asByteImage().getRawBytes());
  }
  
  @Test
  public void drawing_a_edge_multiple_times_results_in_a_black_and_white_image() {
    ImageSize size = new ImageSize(10, 10);
    StringCharacteristics sc = new StringCharacteristics(
        1.0f, 2.0f, 5, true, 1, Shape.CIRCLE);
    CircleEdgeDrawer d = new CircleEdgeDrawer(size, sc);
    Edge e = new Edge(1, false, 3, false);
    CompressedByteImage eImg = d.drawEdge(e);
    
    UnboundedImage image = new UnboundedImage(size);
    for (int i=0; i<0xFF; i++) {
      image.add(eImg);
    }

    for (byte b : image.asByteImage().getRawBytes()) {
      int ub = Byte.toUnsignedInt(b);
      Assertions.assertTrue(ub==0xFF || ub==0x00);
    }
  }
  

  @Test
  public void a_drawn_edge_can_be_removed() {
    ImageSize size = new ImageSize(10, 10);
    StringCharacteristics sc = new StringCharacteristics(
        1.0f, 2.0f, 5, true, 1, Shape.CIRCLE);
    CircleEdgeDrawer d = new CircleEdgeDrawer(size, sc);
    Edge e1 = new Edge(1, false, 3, false);
    Edge e2 = new Edge(2, false, 4, false);
    Edge e3 = new Edge(0, false, 3, false);

    UnboundedImage first = new UnboundedImage(size);
    first.add(d.drawEdge(e1));
    first.add(d.drawEdge(e1));
    first.add(d.drawEdge(e3));
    UnboundedImage second = new UnboundedImage(size);
    second.add(d.drawEdge(e1));
    second.add(d.drawEdge(e2));
    second.add(d.drawEdge(e3));
    second.add(d.drawEdge(e3));
    second.add(d.drawEdge(e1));
    second.remove(d.drawEdge(e2));
    second.remove(d.drawEdge(e3));
    
    Assertions.assertArrayEquals(
        first.asByteImage().getRawBytes(), 
        second.asByteImage().getRawBytes());
  }

}
