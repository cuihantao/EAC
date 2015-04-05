import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.text.*;
import java.util.zip.*;

public class SwingCurve extends Applet implements Runnable
{                                          
   public static final String DEG = String.valueOf('\u00b0');
   public static final String DELTA = String.valueOf('\u00f8');
   public static final String PI_STR = String.valueOf('\u03c0');
   public static final double DEG_RAD =Math.PI/180.0;
   public static final double PI =Math.PI;
   private Thread runner;
   private boolean isgraph = false, done = false;
   private boolean first = true, degree=true, tsmargin=false;
   private static boolean appletmode = true;
   private int wide=700, high = 330;
   private TextField Pm, Pmax, Pmaxdf, Pmaxpf, Pc, Pcdf, Pcpf, G, Gdf, Gpf,
        deltac, deltacr, deltam;
   private Button redraw, clear, delcInc, delcDec,
        PepfInc, PepfDec, PedfInc, PedfDec;
   private Checkbox degBox, radBox, EacBox, TsBox;
   private CheckboxGroup degGroup, EacGroup;
   private Label degLabel, gLabel;
  // private ScrollPane pane;
   private Panel pane;
   private PlotGraph plotgraph;
   private GridBagLayout gridbag;
   private double pm=-1,del0=0,delc=0,deldf=2*PI,delcr=0,
                delpf=0,delm=0,pmax,pmaxdf,pmaxpf;
   private double pc=0,pcdf=0,pcpf=0,g=0,gdf=0,gpf=0;

   private Color PeColor=Color.darkGray, PedfColor=(Color.red).brighter(),
        PepfColor=(Color.cyan).darker(), PmColor=Color.blue,
        delcrColor=Color.red, delcColor=(Color.green).darker(),
        delmColor=Color.orange, AccColor = (Color.blue).brighter(),
        DecColor = Color.magenta;

   public void init( )
     {
	int i=0;
	gridbag = new GridBagLayout();
        if (!appletmode) resize(wide+20,high+125);
        setBackground(Color.white);
        Pm = new TextField("1.0",8);
        deltac= new TextField(""+precision(degree?45:PI/4,2),8);
        deltacr= new TextField(8);
        deltacr.setEditable(false);
        deltam= new TextField(8);
        deltam.setEditable(false);

        Pmax = new TextField("1.5",8);
        Pmaxdf = new TextField("0.9",8);
        Pmaxpf = new TextField("1.3",8);
        Pc = new TextField("0",8);
        Pcdf = new TextField("0",8);
        Pcpf = new TextField("0",8);
        G = new TextField("0",8);
        Gdf = new TextField("0",8);
        Gpf = new TextField("0",8);

	redraw = new Button("Update");
	clear = new Button("Clear");
        delcDec = new Button("Clearing Angle --");
        delcInc = new Button("Clearing Angle ++");
        PedfDec = new Button("Pmaxdf --");
        PedfInc = new Button("Pmaxdf ++");
        PepfDec = new Button("Pmaxpf --");
        PepfInc = new Button("Pmaxpf ++");

        degGroup = new CheckboxGroup();
        degBox = new Checkbox("Degrees",degGroup,true);
        radBox = new Checkbox("Radians",degGroup,false);
        degLabel = new Label(" Clearing Angle (Deg)");
        gLabel = new Label(" g (Deg)");

        EacGroup = new CheckboxGroup();
        EacBox = new Checkbox("EAC",EacGroup,true);
        TsBox = new Checkbox("TS Margin",EacGroup,false);
//        EacBox.setBackground(Color.white);
//        TsBox.setBackground(Color.white);

        plotgraph = new PlotGraph(wide,high);
//        plotgraph.sizeIt(wide,high);
//        plotgraph.init();
        plotgraph.setVgap(40);
//        plotgraph.setBackground(plotgraph.GraphBackground);
	plotgraph.show();

	Panel entrypanel = new Panel();
	entrypanel.setLayout(gridbag);
/*        
        constrain(entrypanel,degLabel,1,0,1,1,delcColor);
        constrain(entrypanel,new Label("Max. Angle for Stability ("+DELTA+"m)"),
                2,0,1,1,delmColor);
        constrain(entrypanel,new Label("Critical Clearing Angle ("+DELTA+"cr)"),
                3,0,1,1,delcrColor);
*/
        constrain(entrypanel,delcDec,0,0,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(entrypanel,delcInc,1,0,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(entrypanel,degLabel,2,0,1,1,delcColor);
        constrain(entrypanel,deltac,3,0,1,1,delcColor);
        constrain(entrypanel,new Label("Pmax"),4,0,1,1);
        constrain(entrypanel,new Label("Pc"),5,0,1,1);
        constrain(entrypanel,gLabel,6,0,1,1);
//        constrain(entrypanel,deltam,5,0,1,1,delmColor);
//        constrain(entrypanel,deltacr,5,0,1,1,delcrColor);
// Pre fault
        constrain(entrypanel,new Label(" Pm = "),0,1,1,1,PmColor);
        constrain(entrypanel,Pm,1,1,1,1,PmColor);
	constrain(entrypanel,
           new Label("Pre-Fault : Pe= Pc - Pmax.sin("+DELTA+"-g)"),
           2,1,2,1,PeColor);
        constrain(entrypanel,Pmax,4,1,1,1,PeColor);
        constrain(entrypanel,Pc,5,1,1,1,PeColor);
        constrain(entrypanel,G,6,1,1,1,PeColor);
//During fault
        constrain(entrypanel,PedfDec,0,2,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(entrypanel,PedfInc,1,2,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
	constrain(entrypanel,
             new Label("During-Fault : Pedf = Pcdf - Pmaxdf.sin("+DELTA+"-gdf)"),
             2,2,2,1,PedfColor);
        constrain(entrypanel,Pmaxdf,4,2,1,1,PedfColor);
        constrain(entrypanel,Pcdf,5,2,1,1,PedfColor);
        constrain(entrypanel,Gdf,6,2,1,1,PedfColor);
//Post fault
        constrain(entrypanel,PepfDec,0,3,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(entrypanel,PepfInc,1,3,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(entrypanel,
          new Label("Post-Fault : Pepf = Pcpf - Pmaxpf.sin("+DELTA+"-gpf)"),
          2,3,2,1,PepfColor);
        constrain(entrypanel,Pmaxpf,4,3,1,1,PepfColor);
        constrain(entrypanel,Pcpf,5,3,1,1,PepfColor);
        constrain(entrypanel,Gpf,6,3,1,1,PepfColor);

	Panel buttonpanel = new Panel();
	buttonpanel.setLayout(gridbag);
        constrain(buttonpanel,degBox,0,0,1,1);
        constrain(buttonpanel,radBox,1,0,1,1);

        constrain(buttonpanel,redraw,2,0,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
/*        constrain(buttonpanel,clear,1,0,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(buttonpanel,delcDec,2,0,1,1,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(buttonpanel,delcInc,3,0,1,1,GridBagConstraints.NONE,
                GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(buttonpanel,degLabel,2,0,1,1,delcColor); 
        constrain(buttonpanel,new Label("Max. Angle for Stability ("+DELTA+"m)"),
                1,0,1,1,delmColor);
        constrain(buttonpanel,deltam,2,0,1,1,delmColor);
        constrain(buttonpanel,new Label("Critical Clearing Angle ("+DELTA+"cr)"),
                3,0,1,1,delcrColor);
        constrain(buttonpanel,deltacr,4,0,1,1,delcrColor);
*/
//        constrain(buttonpanel,new Label("Angle : "),3,0,1,1,
//                GridBagConstraints.NONE,GridBagConstraints.CENTER,0.1,0.0,0,0,0,0);
        constrain(buttonpanel,EacBox,5,0,1,1);
        constrain(buttonpanel,TsBox,6,0,1,1);

	this.setLayout(gridbag);
	constrain(this,plotgraph,0,0,1,1,GridBagConstraints.BOTH,
                GridBagConstraints.CENTER,1.0,1.0,5,5,0,0);
	constrain(this,buttonpanel,0,1,1,1,GridBagConstraints.HORIZONTAL,
		GridBagConstraints.CENTER,1.0,0.0,5,5,5,5);
	constrain(this,entrypanel,0,2,1,1,GridBagConstraints.BOTH,
		GridBagConstraints.CENTER,0.0,0.0,5,5,5,5);
	plotgraph.resize(wide,high);
	plotgraph.show();

     }

   public void start( )
     {
	if( runner == null )
	  runner = new Thread( this );
	runner.start( );
     }
   public void stop( )
     {
	runner.stop( );
	runner = null;
     }
   public void run( )
     {
	while( true ) {
//           if (first) repaint();
	   try {
	      Thread.sleep( 100 );
	   }
	   catch( InterruptedException e ) {
	      System.err.println( e );
	   }
	}
     }

   public void update( Graphics g )
     {
	plotgraph.repaint();
	paint( g );
	paintAll(g);
     }

   public  void initiate()
   {
/*       Dimension d = pane.getViewportSize();
       Dimension dmap = plotgraph.getSize();
       Point p = pane.getScrollPosition();
       p.x = (int)((double)(dmap.width-d.width)/2.0);
       p.y = (int)((double)(dmap.height-d.height)/2.0);
       pane.setScrollPosition(p);*/
   }

   public synchronized void paint( Graphics g)
   {
      int j=0,i=0;
      if (first)  {computeValues();initiate();first=false;}
      if (!done) {Graph();done=true;}
   }

   public void areaShading(double del0, double delc, double delc2, double delm)
   {
        int k=0,nd=400;
        double xx=0, del=0, A1=0, A2=0;
	double[] xdata = new double[nd];
	double[] ydata = new double[nd];
//Area 1
        A1 = Int_Padf(delc) - Int_Padf(del0);
        xdata[0]=delc; ydata[0]=Pedf(delc);
        xdata[1]=delc; ydata[1]=pm;
	xdata[2]=del0; ydata[2]=pm;
        xdata[3]=del0; ydata[3]=Pedf(del0);
        nd = (int)(Math.abs(delc-del0)*50.0);
        if (nd<2) nd=2;
	for(k=0;k<nd;k++) {
                xx = del0+(double)k*(delc-del0)/(double)(nd-1);
                ydata[k+4]= Pedf(xx);
		xdata[k+4]= xx;
	}
        plotgraph.plotArea(xdata,ydata,nd+4,AccColor);

        if (delc<delpf) {
                A1 = A1 + Int_Papf(delpf) - Int_Papf(delc);
                xdata[0]=delpf; ydata[0]=pm;
		xdata[1]=delc; ydata[1]=pm;
                xdata[2]=delc; ydata[2]=Pepf(delc);
                nd = (int)(Math.abs(delpf-delc)*50.0);
                if (nd<2) nd=2;
                for(k=0;k<nd;k++) {
			xx = delc+(double)k*(delpf-delc)/(double)(nd-1);
                        ydata[k+3]= Pepf(xx);
			xdata[k+3]= xx;
		}
                plotgraph.plotArea(xdata,ydata,nd+3,AccColor);
	}
        plotgraph.plotText(2*DEG_RAD,1.06*pmax,"Acc. Area = "+
                precision(A1,3,degree)+(degree?" deg-pu":" rad-pu"),AccColor.brighter());

// Area 2
        A2 = -Int_Papf(delm) + Int_Papf(delc2);
        xdata[0]=delm; ydata[0]=Pepf(delm);
        xdata[1]=delm; ydata[1]=pm;
        xdata[2]=delc2; ydata[2]=pm;
        xdata[3]=delc2; ydata[3]=Pepf(delc2);
        nd = (int)(Math.abs(delm-del)*50.0);
        if (nd<2) nd=2;
        for(k=0;k<nd;k++) {
                xx = delc2+(double)k*(delm-delc2)/(double)(nd-1);
                ydata[k+4]= Pepf(xx);
                xdata[k+4]= xx;
	}
        plotgraph.plotArea(xdata,ydata,nd+4,DecColor);
        plotgraph.plotText(155*DEG_RAD,1.06*pmax,"Dec. Area = "+
               precision(A2,3,degree)+(degree?" deg-pu":" rad-pu"),DecColor.darker());
        if (tsmargin)
           plotgraph.plotText(155*DEG_RAD,pmax,"TS Margin = "+
               precision(A2-A1,3,degree)+(degree?" deg-pu":" rad-pu"),Color.black);
   }

   public void Graph()
   {
	int i=0,k=0,nd=0;
	double[] xdata,ydata;
        double xx=0,gmin, gmax;
        //computeValues();
	nd=360;
	xdata = new double[nd];
	ydata = new double[nd];
        plotgraph.setLabelX(" ");
        plotgraph.setLabelY(" Pe(pu) ");
        plotgraph.setXfactor(degree?1/DEG_RAD:1.0);
        gmax=(g>gpf?g:gpf);  gmax = (gmax>gdf?gmax:gdf);
        gmin=(g<gpf?g:gpf);  gmin = (gmin<gdf?gmin:gdf);
        System.out.println("gmin : "+gmin+", gmax : "+gmax);
        plotgraph.setScale(gmin,0,3.5+gmax,1.2*(pmax+pc),false);

// Acc. and Dec. Areas
        areaShading(del0,delc<deldf?delc:deldf,delc>delpf?delc:delpf,
                tsmargin?PI-delpf+2*gpf:delm);
// Power angle curves
	for(k=0;k<nd;k++) {
                xx = gmin+(double)k*(PI-gmin+gmax)/(double)(nd-1);
		ydata[k]= Pe(xx);
                xdata[k]= xx;
	}
	plotgraph.setData(xdata,ydata,nd);
        plotgraph.plot(PeColor);
	for(k=0;k<nd;k++) {
                xx = gmin+(double)k*(PI-gmin+gmax)/(double)(nd-1);
		ydata[k]= Pedf(xx);
	}
        plotgraph.addPlot(xdata,ydata,nd,PedfColor);
	for(k=0;k<nd;k++) {
                xx = gmin+(double)k*(PI-gmin+gmax)/(double)(nd-1);
		ydata[k]= Pepf(xx);
	}
        plotgraph.addPlot(xdata,ydata,nd,PepfColor);

// Pm and angle lines
        plotgraph.plotText(82.0*DEG_RAD+g,1.03*(pmax+pc),"Pmax = "+precision(pmax,2), PeColor);
        plotgraph.plotLine(gmin,pm,PI+gmax,pm, PmColor);
        plotgraph.plotText(2.0*DEG_RAD,1.03*pm,"Pm = "+precision(pm,2), PmColor);
        plotgraph.plotLine(del0,0,del0,pm, PeColor);
        plotgraph.plotText(del0-20*DEG_RAD,0.05,DELTA+"o = "+precision(del0,2,degree), PeColor);
        plotgraph.plotLine(PI-del0+2*g,0,PI-del0+2*g,pm, PeColor);
	
        plotgraph.plotLine(delpf,0,delpf,pm, PepfColor);
        plotgraph.plotText(delpf-4*DEG_RAD,-0.08,DELTA+"pf = "+precision(delpf,2,degree), PepfColor);
        plotgraph.plotLine(PI-delpf+2*gpf,0,PI-delpf+2*gpf,pm, PepfColor);
        plotgraph.plotText(PI-delpf-4*DEG_RAD,-0.08,DELTA+"pfm = "+precision(PI-delpf,2,degree), PepfColor);
        plotgraph.plotLine(delc,0,delc,Pepf(delc), delcColor);
        plotgraph.plotText(delc-2*DEG_RAD,-0.16,DELTA+"c = "+precision(delc,2,degree), delcColor);
        plotgraph.plotLine(delm,0,delm,Pepf(delm), delmColor);
        plotgraph.plotText(delm-10*DEG_RAD,-0.24,DELTA+"m = "+precision(delm,2,degree), delmColor);
        plotgraph.plotLine(delcr,0,delcr,Pepf(delcr), delcrColor);
        plotgraph.plotText(delcr-22*DEG_RAD,0.05,DELTA+"cr = "+precision(delcr,2,degree), delcrColor);

        plotgraph.plotLine(PI,0,PI,0.1*pmax,plotgraph.GraphAxesColor);
        plotgraph.plotText(176*DEG_RAD,-0.08,(degree?"180":PI_STR),plotgraph.GraphAxesColor);
        plotgraph.plotLine(PI/2,0,PI/2,0.1*pmax,plotgraph.GraphAxesColor);
        plotgraph.plotText(86*DEG_RAD,-0.08,(degree?"90":PI_STR+"/2"),plotgraph.GraphAxesColor);
        plotgraph.plotLine(0,0,0,0.1*pmax,plotgraph.GraphAxesColor);
        plotgraph.plotText(-1*DEG_RAD,-0.08,"0",plotgraph.GraphAxesColor);

        plotgraph.plotText(180*DEG_RAD,-0.16,DELTA+(degree?DEG:" Rad"),plotgraph.GraphAxesColor);
   }

   public double delta_m(double ang1, double ang2)
   {
        double a, b, J=0, del=0;
        if (Math.abs(ang2-del0)<1e-5) {ang2=1.001*ang2;System.out.println(""+del0+", "+ang2);}
        a=-Int_Padf(ang2)+Int_Papf(ang1)+Int_Padf(del0);
        if (ang2>Math.abs(Math.PI-deldf+2*gdf))
                a = a - Int_Padf(ang1) + Int_Padf(ang2);
        double delm=ang1;
        b = Int_Papf(delm);
        int i=0;
        while (Math.abs(a-b)>1e-6 && i<100) {
                i++;
                J = pm-Pepf(delm);
                b = Int_Papf(delm);
                del = (a-b)/(J+1e-99);
                delm += 0.5*del;
                if (delm>Math.PI-delpf+2*gpf)
                        delm=PI-delpf+2*gpf-PI/12;
                if (delm<ang1) delm = ang1 + PI/12;
        }
  //      System.out.println("count "+i+"  a = "+a+" b = "+b);
        if (i>=100) {
                delm = Math.PI-delpf+2*gpf;
                do {
                    b = Int_Papf(delm);
                    delm -= 0.00005;
                    i++;
                } while (Math.abs(a-b)>5e-5 && delm>ang1);
                delm -= 0.00005;
        }
//        System.out.println("count "+i+"  a = "+a+" b = "+b);
        return (delm);
   }

   public double delta_cr(double ang1, double ang2)
   {
        double a, b, J=0, del=0;
        boolean trouble = false;
        if (pmaxdf+pcdf>=pm) trouble=true;

        a=-Int_Padf(ang1)+Int_Papf(ang2)+(trouble?Int_Padf(deldf):0);
        double delm=ang1;
        b = Int_Papf(delm) - (trouble?0:Int_Padf(delm));
        int i=0;
        while (Math.abs(a-b)>1e-6 && i<100) {
                i++;
                J = -Pepf(delm)+(trouble?0:Pedf(delm));
                b = Int_Papf(delm) - (trouble?0:Int_Padf(delm));
                del = (a-b)/(J+1e-99);
                delm += 0.5*del;
                if (delm>ang2) delm=ang2-PI/12;
                if (delm<ang1) delm = ang1 + PI/12;
        }
//        System.out.println("count "+i+"  a = "+a+" b = "+b);
        if (i>=100) {
                delm = ang1;
                do {
                    b = Int_Papf(delm) - (trouble?0:Int_Padf(delm));
                    delm += 0.00005;
                    i++;
                } while (Math.abs(a-b)>5e-5 && delm<ang2);
                delm+=0.00005;
  //              System.out.println("i = "+i+"  a = "+a+" b = "+b);
        }
        return (delm);
   }

   public void computeValues()
   {
        pm = Math.abs(getValue(Pm));
        delc = Math.abs(getValue(deltac,degree));
	pmax = getValue(Pmax);
        pc = getValue(Pc);
        g = getValue(G, degree);
        if (pmax+pc<pm) {pmax = pm-pc;Pmax.setText(""+pmax);}
	pmaxdf = getValue(Pmaxdf);
        pcdf = getValue(Pcdf);
        gdf = getValue(Gdf, degree);
	pmaxpf = getValue(Pmaxpf);
        pcpf = getValue(Pcpf);
        gpf = getValue(Gpf,degree);
        if (pmaxpf+pcpf<pm) {pmaxpf = pm-pcpf;Pmaxpf.setText(""+pmaxpf);}
        if (pmaxpf+pcpf>pmax) {pmaxpf = pmax-pcpf;Pmaxpf.setText(""+pmaxpf);}
        if (pmaxdf+pcdf>pmaxpf) {pmaxdf = pmaxpf-pcdf;Pmaxdf.setText(""+pmaxdf);}
        if (pmaxdf+pcdf<0) {pmaxdf = 0;Pmaxdf.setText(""+pmaxdf);}
        if (pm<=pmax+pc) del0 = Math.asin((pm-pc)/pmax)+g;
        if (pm<=pmaxpf+pcpf) delpf = Math.asin((pm-pcpf)/pmaxpf)+gpf; else delpf = 2*PI;
        if (pm<=pmaxdf+pcdf) deldf = Math.asin((pm-pcdf)/pmaxdf)+gdf; else deldf = 2*PI;
        delcr = delta_cr(del0,PI-delpf+2*gpf);
        deltacr.setText(""+precision(degree?delcr/DEG_RAD:delcr,2));
        if (delc<del0) delc = del0;
        if (delc>delcr) delc = delcr;
        deltac.setText(""+precision(degree?delc/DEG_RAD:delc,2));
        if (delc>deldf) delm = delta_m(delc,deldf);
                else delm = delta_m(delc,delc);
        deltam.setText(""+precision(degree?delm/DEG_RAD:delm,2));
   }

   public double getValue(TextField tf,boolean degree)
   {
       String s = (tf.getText()).trim();
       double mm=1.0;
       try {mm = (Double.valueOf(s)).doubleValue();}
       catch (Exception ex) {
	     mm=1.0;
	     tf.setText(""+mm);
       }
       finally {if (degree) return(mm*DEG_RAD); else return(mm);}
   }
   public double getValue(TextField tf)
   {
        return(getValue(tf, false));
   }

   public double Pe(double ang)
   {
        return (pc+pmax*Math.sin(ang-g));
   }
   public double Pedf(double ang)
   {
        return (pcdf+pmaxdf*Math.sin(ang-gdf));
   }
   public double Pepf(double ang)
   {
        return (pcpf+pmaxpf*Math.sin(ang-gpf));
   }
   public double Int_Pa(double ang)
   {
        return (pm*ang-(pc*ang-pmax*Math.cos(ang-g)));
   }
   public double Int_Padf(double ang)
   {
        return (pm*ang-(pcdf*ang-pmaxdf*Math.cos(ang-gdf)));
   }
   public double Int_Papf(double ang)
   {
        return (pm*ang-(pcpf*ang-pmaxpf*Math.cos(ang-gpf)));
   }

   public boolean action(Event event, Object arg)
     {
	byte ret=-1;

       if (event.target == degBox) {
           if (degree) return true;
           degree = true;
           deltac.setText(""+delc/DEG_RAD);
           G.setText(""+g/DEG_RAD);
           Gdf.setText(""+gdf/DEG_RAD);
           Gpf.setText(""+gpf/DEG_RAD);
           degLabel.setText(" Clearing Angle (Deg)");
           gLabel.setText(" g (Deg) ");
           ret = 0;
	}
       if (event.target == radBox) {
           if (!degree) return true;
           degree = false;
           deltac.setText(""+delc);
           G.setText(""+g);
           Gdf.setText(""+gdf);
           Gpf.setText(""+gpf);
           degLabel.setText(" Clearing Angle (Rad)");
           gLabel.setText(" g (Rad) ");
           ret = 0;
	}
       if (event.target == EacBox) {
           if (!tsmargin) return true;
           tsmargin = false;
           ret = 0;
	}
       if (event.target == TsBox) {
           if (tsmargin) return true;
           tsmargin = true;
           ret = 0;
	}

	if (event.target == redraw) {
	   ret = 0;
	}
	if (event.target == clear) {
	   ret = 0;
        }
        if (event.target == delcInc) {
           delc+=1.0*DEG_RAD;
           deltac.setText(""+precision(degree?delc/DEG_RAD:delc,2));
	   ret = 0;
	}
        if (event.target == delcDec) {
           delc-=1.0*DEG_RAD;
           if (delc<del0) delc=del0;
           deltac.setText(""+precision(degree?delc/DEG_RAD:delc,2));
           ret = 0;
	}
        if (event.target == PedfInc) {
           pmaxdf+=0.1;
           Pmaxdf.setText(""+pmaxdf);
	   ret = 0;
	}
        if (event.target == PedfDec) {
           pmaxdf-=0.1;
           if (pmaxdf<0) pmaxdf=0;
           Pmaxdf.setText(""+pmaxdf);
	   ret = 0;
	}
        if (event.target == PepfInc) {
           pmaxpf+=0.1;
           Pmaxpf.setText(""+pmaxpf);
	   ret = 0;
	}
        if (event.target == PepfDec) {
           pmaxpf-=0.1;
           Pmaxpf.setText(""+pmaxpf);
	   ret = 0;
	}

	if (ret==0) {
           computeValues();
	   done = false;
	   repaint();
           return true;
	}
	return false;
     }

   public void constrain(Container cont,Component comp,int gx, int gy,
		int gw, int gh, int fill, int anchor, double wx, double wy,
		int top, int left, int bottom, int right)
   {
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = gx;  c.gridy = gy;
	c.gridwidth = gw; c.gridheight = gh;
	c.fill = fill; c.anchor = anchor;
	c.weightx = wx; c.weighty = wy;
	if (top+bottom+left+right>0)
		c.insets = new Insets(top,left, bottom,right);
	((GridBagLayout)cont.getLayout()).setConstraints(comp,c);
	cont.add(comp);
   }

   public void constrain(Container cont,Component comp,int gx, int gy,
		int gw, int gh)
   {
	constrain(cont,comp,gx,gy,gw,gh,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.0,0.0,0,0,0,0);
   }
	
   public void constrain(Container cont,Component comp,int gx, int gy,
		int gw, int gh, int top, int left, int bottom, int right)
   {
	constrain(cont,comp,gx,gy,gw,gh,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.0,0.0,top,left,bottom,right);
   }

   public void constrain(Container cont,Component comp,int gx, int gy,
		int gw, int gh, Color cl)
   {
	constrain(cont,comp,gx,gy,gw,gh,GridBagConstraints.NONE,
		GridBagConstraints.CENTER,0.0,0.0,0,0,0,0);
        comp.setForeground(cl);
        //comp.setBackground(cl);
   }

   public static double precision(double d,double factor,int decimal_places)
   {
      double fac = Math.pow(10.0,(double)decimal_places);
      return (Math.floor(d*factor*fac)/fac);
   }

   public static double precision(double d,int decimal_places)
   {
      double fac = Math.pow(10.0,(double)decimal_places);
      return (Math.floor(d*fac)/fac);
   }

   public static double precision(double d,int decimal_places, boolean b)
   {
      if (b) return(precision(d,1/DEG_RAD,decimal_places));
      else return(precision(d,decimal_places));
   }

   public static double precision(double d)
   {
      return (precision(d,2));
   }

   public static void main( String[] args )
     {
	appletmode=false;
	System.out.println("Mode : "+appletmode);
	AppletFrame.startApplet("SwingCurve","Swing Curve", args);
     }  
}
