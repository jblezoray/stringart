package fr.jblezoray.stringart.core;

import org.junit.Assert;
import org.junit.Test;

import fr.jblezoray.stringart.core.EdgeDrawer;
import fr.jblezoray.stringart.edge.Edge;
import fr.jblezoray.stringart.image.CompressedByteImage;
import fr.jblezoray.stringart.image.ImageSize;
import fr.jblezoray.stringart.image.UnboundedImage;

public class EdgeDrawerTest {
  
  @Test
  public void compressed_image_shall_encode_the_right_nb_of_bytes() {
    ImageSize size = new ImageSize(1000, 1000);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge edge = new Edge(1, false, 3, false, d);
    
    CompressedByteImage drawnEdge = d.drawEdge(edge);

    byte[] comp = drawnEdge.getCompressedData();
    int cpt = 0;
    for (int i=0; i<comp.length; i+=2) 
      cpt += Byte.toUnsignedInt(comp[i]);
    Assert.assertEquals(size.nbPixels, cpt);
  }
  
  @Test
  public void drawing_a_edge_on_a_white_image_equals_the_edge_itself() {
    ImageSize size = new ImageSize(10, 10);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge e = new Edge(1, false, 3, false, d);
    
    CompressedByteImage drawnEdge = d.drawEdge(e);
    UnboundedImage image = new UnboundedImage(size).add(drawnEdge);

//    System.out.println(toString(drawnEdge.getCompressedData(), 10));
//    System.out.println(toString(drawnEdge.asByteImage().getRawBytes(), 10));
//    System.out.println(toString(image.asByteImage().getRawBytes(), 10));
    Assert.assertArrayEquals(
        drawnEdge.asByteImage().getRawBytes(), 
        image.asByteImage().getRawBytes());
  }
  
  @Test
  public void drawing_a_edge_multiple_times_results_in_a_black_and_white_image() {
    ImageSize size = new ImageSize(10, 10);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge e = new Edge(1, false, 3, false, d);
    CompressedByteImage eImg = e.getDrawnEdgeData();
    
    UnboundedImage image = new UnboundedImage(size);
    for (int i=0; i<0xFF; i++) {
      image.add(eImg);
    }

//    System.out.println(toString(image.asByteImage().getRawBytes(), 10));
    for (byte b : image.asByteImage().getRawBytes()) {
      int ub = Byte.toUnsignedInt(b);
      Assert.assertTrue(ub==0xFF || ub==0x00);
    }
  }
  

  @Test
  public void a_drawn_edge_can_be_removed() {
    ImageSize size = new ImageSize(10, 10);
    EdgeDrawer d = new EdgeDrawer(size, 5, 1.0f, 2.0f);
    Edge e1 = new Edge(1, false, 3, false, d);
    Edge e2 = new Edge(2, false, 4, false, d);
    Edge e3 = new Edge(0, false, 3, false, d);

    UnboundedImage first = new UnboundedImage(size);
    first.add(e1.getDrawnEdgeData());
    first.add(e1.getDrawnEdgeData());
    first.add(e3.getDrawnEdgeData());
    UnboundedImage second = new UnboundedImage(size);
    second.add(e1.getDrawnEdgeData());
    second.add(e2.getDrawnEdgeData());
    second.add(e3.getDrawnEdgeData());
    second.add(e3.getDrawnEdgeData());
    second.add(e1.getDrawnEdgeData());
    second.remove(e2.getDrawnEdgeData());
    second.remove(e3.getDrawnEdgeData());
    
//    System.out.println(toString(first.asByteImage().getRawBytes(), 10));
//    System.out.println(toString(second.asByteImage().getRawBytes(), 10));
    
    Assert.assertArrayEquals(
        first.asByteImage().getRawBytes(), 
        second.asByteImage().getRawBytes());
  }
  

  @SuppressWarnings("unused")
  private String toString(byte[] bytes, int lineW) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<bytes.length; i++) {
      byte b = bytes[i];
      sb.append(String.format("%02x ", Byte.toUnsignedInt(b)));
      if (i%lineW==lineW-1)
        sb.append('\n');
    }
    return sb.toString();
  }

}
