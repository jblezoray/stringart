package fr.jblezoray.stringart.core;

import fr.jblezoray.stringart.hillclimb.StringCharacteristics;
import fr.jblezoray.stringart.image.ImageSize;

public class CircleEdgeDrawer extends EdgeDrawerWithCache {

  CircleEdgeDrawer(ImageSize size, StringCharacteristics sc) {
    super(size, sc);
  }
  
  @Override
  protected int xNail2Position(int nailIndex) {
    double sinX = Math.sin(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(sinX*(this.size.w/2) + (this.size.w/2));
  }

  @Override
  protected int yNail2Position(int nailIndex) {
    double cosY = Math.cos(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(-cosY*(this.size.h/2) + (this.size.h/2));
  }
  
  @Override
  protected int xNail2ThreadPosition(int nailIndex, boolean clockwise) {
    double imgCenter = this.size.w / 2.0d;
    double nailDeviation = 2.0d*Math.PI*nailIndex/this.totalNumberOfNails;
    double wayDeviation = (double)this.nailPxRadiusInt/this.size.w;
    double way = clockwise ? -1.0d : 1.0d;
    return (int)((1 + Math.sin(nailDeviation + way * wayDeviation)) * imgCenter);
  }

  @Override
  protected int yNail2ThreadPosition(int nailIndex, boolean clockwise) {
    double imgCenter = this.size.h / 2.0d;
    double nailDeviation = 2.0d*Math.PI*nailIndex/this.totalNumberOfNails;
    double wayDeviation = (double)this.nailPxRadiusInt/this.size.h;
    double way = clockwise ? -1.0d : 1.0d;
    return (int)((1 - Math.cos(nailDeviation + way * wayDeviation)) * imgCenter);
  }
  
}
