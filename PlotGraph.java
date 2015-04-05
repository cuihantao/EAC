/*
 * PlotGraph.java
 * Farrukh Shahzad
 *
*/
//package com.elexor.steve;
import java.awt.*;
import java.applet.*;

public class PlotGraph extends Panel
{
        public Color GraphColor=Color.yellow;
        public Color GraphBackground=Color.white;
        public Color GraphAxesColor=Color.black;
        public Color GraphBorderColor=Color.red;
        private Image image=null;
        private Graphics os=null;
        private String labelX="X-axis",labelY="Y-axis";
        private boolean AutoScale=true;
        private double fromX=0,toX=0,fromY=0,toY=0;
        private double xdata[], ydata[];
        private Rectangle r=null;
        private int ndata=0;
        private int hgap=50,vgap=20;
        private int xoff,yoff;
        private double xscale, yscale, xfac=1.0;
        private boolean scaled, showAxes=true;

        public PlotGraph(int wd, int ht)
        {
          super();
          r = new Rectangle(0,0,wd,ht);
          System.out.println(""+r.width+","+r.height);
          int i=0;
          resize(r.width,r.height);
          ndata=10;
          xdata = new double[ndata];
          ydata = new double[ndata];
          for (i=0;i<ndata;i++) {
                ydata[i] = (double) i;
                xdata[i] = (double) i;
          }
          scaled = false;
          setBackground(GraphBackground);
        }

        public Dimension getPreferredSize()
        {
                return new Dimension(r.width,r.height);
        }

        public void setData(int[] data, int N)
        {
          int i=0;
          ndata = N;
          xdata = new double[ndata];
          ydata = new double[ndata];
          for (i=0;i<ndata;i++) {
                ydata[i] = (double) data[i];
                xdata[i] = (double) i;
          }
          if (AutoScale || !scaled) scaling();
        }

        public void setData(double[] X, double[] Y, int N)
        {
          int i=0;
          ndata = N;
          xdata = new double[ndata];
          ydata = new double[ndata];
          for (i=0;i<ndata;i++) {
                xdata[i] = X[i];
                ydata[i] = Y[i];
          }
          if (AutoScale || !scaled) scaling();
        }

        private void scaling()
        {
          int i;
          fromX=1e100;toX=-1e100;
          fromY=1e100;toY=-1e100;

          for(i=0;i<ndata;i++) {
                        if (ydata[i]>toY) toY=ydata[i];
                        if (ydata[i]<fromY) fromY=ydata[i];
                        if (xdata[i]>toX) toX=xdata[i];
                        if (xdata[i]<fromX) fromX=xdata[i];
          }
          xoff=r.x+hgap;
          yoff=r.y+r.height-vgap;
          xscale = (double) ((r.width-hgap)/(toX-fromX));
          yscale = (double) ((r.height-vgap)/(toY-fromY));
          scaled = true;
        }

        public void setup()
        {
          image = this.createImage(r.width,r.height);
          if (image!=null) {
           os = image.getGraphics();
          //os.setColor(GraphBackground);
         // os.fillRect(r.x,r.y,r.width,r.height);
          os.setColor(GraphBorderColor);
          os.drawRoundRect(r.x,r.y,r.width-1,r.height-1,15,15);

          os.setColor(GraphAxesColor);
          os.drawLine(xoff,r.y,xoff,yoff);
          os.drawLine(xoff,yoff,r.x+r.width,yoff);
          os.drawString(labelX,(r.x+r.width-hgap)/2,yoff+2*vgap/3);
          os.drawString(labelY,r.x+hgap/10,(r.y+r.height-vgap)/2);
          if (showAxes) {
           os.drawString(""+precision(xfac*fromX,3),xoff,yoff+2*vgap/3);
           os.drawString(""+precision(fromY,3),r.x+hgap/10,yoff);
           os.drawString(""+precision(xfac*toX,3),r.x+r.width-hgap/2,yoff+2*vgap/3);
           if (toY>fromY) os.drawString(""+precision(toY,3),r.x+hgap/10,r.y+15);
          }
         }
        }
        public void update(Graphics g)
        { paint(g);
        }
        public void paint(Graphics g)
        {
         if (image!=null) {g.drawImage(image,0,0,this);
          System.out.println(" Size "+r.width+","+r.height);
         }
//         g.drawLine(10,10,100,100);
//         if (image!=null) g.drawLine(100,100,200,200);
        }

        public void plot(Color cl)
        {
          int x1=0,x2=0,y1=0,y2=0,i=0;
          if (image==null) setup();
          os.setColor(cl);
          for(i=0;i<ndata;i++) {
                x2 = xoff + (int)((xdata[i]-fromX)*xscale);
                y2 = yoff - (int)((ydata[i]-fromY)*yscale);
                if (x2>r.x+r.width) x2 = r.x+r.width;
                if (x2<xoff) x2 = xoff;
                if (y2<r.y) y2 = r.y;
                if (y2>(yoff)) y2 = yoff;
                if (i==0) {x1=x2;y1=y2;}
                else {os.drawLine(x1,y1,x2,y2); x1 = x2;   y1 = y2;}
          }
        }

        public void addPlot(double x[], double y[],int nd, Color cl)
        {
          int x1=0,x2=0,y1=0,y2=0,i=0;
          if (!scaled) { setData(x,y,nd);if (image==null) setup();}
          os.setColor(cl);      
          for(i=0;i<nd;i++) {
                x2 = xoff + (int)((x[i]-fromX)*xscale);
                y2 = yoff - (int)((y[i]-fromY)*yscale);
                if (x2>r.x+r.width) x2 = r.x+r.width;
                if (x2<xoff) x2 = xoff;
                if (y2<r.y) y2 = r.y;
                if (y2>yoff) y2 = yoff;
                if (i==0) {x1=x2;y1=y2;}
                else {os.drawLine(x1,y1,x2,y2); x1 = x2;   y1 = y2;}
          }
        }

        public void plotLine(double x1, double y1,
                double x2, double y2,Color cl)
        {
          os.setColor(cl);
          os.drawLine(xoff+(int)((x1-fromX)*xscale),yoff-(int)((y1-fromY)*yscale),
                xoff+(int)((x2-fromX)*xscale),yoff-(int)((y2-fromY)*yscale));
        }

        public void plotText(double x, double y, String text,Color cl)
        {
          os.setColor(cl);
          os.drawString(text,xoff+(int)((x-fromX)*xscale),
                yoff-(int)((y-fromY)*yscale));
        }

        public void plotArea(double x[], double y[],int nd, Color cl)
        {
          int i=0;
          int[] xx = new int[nd];
          int[] yy = new int[nd];
          os.setColor(cl);
          for(i=0;i<nd;i++) {
                xx[i] = xoff + (int)((x[i]-fromX)*xscale);
                yy[i] = yoff - (int)((y[i]-fromY)*yscale);
          }
          Polygon p = new Polygon(xx,yy,nd);
          os.fillPolygon(p);
        }

        public void setScale(double x1,double y1,double x2,double y2)
        {
          AutoScale = false;
          fromX = x1;
          fromY = y1;
          toX = x2;
          toY = y2;
          xoff=r.x+hgap;
          yoff=r.y+r.height-vgap;
          xscale = (double) ((r.width-hgap)/(toX-fromX));
          yscale = (double) ((r.height-vgap)/(toY-fromY));
          scaled = true;
          setup();
        }

        public void setScale(double x1,double y1,double x2,double y2, boolean axes)
        {
           showAxes = axes;
           setScale(x1,y1,x2,y2);
        }

        public void setAutoScale(boolean b)      {  AutoScale = b;  }
        public boolean isAutoScale()    {     return(AutoScale);    }

        public void setLabelX(String s)   {    labelX = s;     }
        public String getLabelX()    {    return(labelX);    }

        public void setLabelY(String s)    {   labelY = s;   }
        public String getLabelY()    {    return(labelY);     }

        public void setHgap(int hw)    {   hgap = hw;   }
        public int getHgap()    {    return(hgap);     }

        public void setVgap(int hw)    {   vgap = hw;   }
        public int getVgap()    {    return(vgap);     }

        public void setXfactor(double xf)    {   xfac = xf;   }
        public double getXfactor()    {    return(xfac);     }

        public static double precision(double d,int decimal_places)
        {
                double fac = Math.pow(10.0,(double)decimal_places);
                return ((int)(d*fac)/fac);
        }

}  // class end.
