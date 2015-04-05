import java.io.*;
import java.util.*;
import java.awt.*;
import java.net.*;

public class SmibEac extends Frame
{                                          
   protected int num_windows=0;
   private static boolean appletmode = true, first=true;
   private int wide=700, high = 330;
   private EacPlot eacplot;

   public SmibEac()
     {
        super();
        num_windows++;
        int i=0;
//        if (!appletmode) resize(wide+20,high+125);
        resize(wide+40,high+215);
        setBackground(Color.white);
        eacplot = new EacPlot();
        this.add(eacplot);
        this.show();
        eacplot.show();

        MenuBar menubar = new MenuBar();
        this.setMenuBar(menubar);
        Menu file = new Menu("File");
        menubar.add(file);
        file.add("Eac Plot");
        file.add("Swing Curve");
        file.add("Pm Change");
        file.addSeparator();
        file.add("Full Size");
        file.add("Close");
//        this.pack();
        this.show();
     }

   public void update( Graphics g )
     {
        eacplot.repaint();
        paint( g );
     }

   public synchronized void paint( Graphics g)
   {
      int j=0,i=0;
      if (first)  {resize(wide+40,high+215);first=false;}
   }

   public boolean action(Event event, Object arg)
     {
	byte ret=-1;
        if (event.target instanceof MenuItem) {
            if (arg.equals("Eac Plot")) {
                  this.removeAll();
                  this.add(eacplot);
                  this.show();
                  eacplot.show();
            }
            if (arg.equals("Close"))  {
                  this.hide();
                  this.dispose();
                  if (!appletmode) System.exit(0);
                  return true;
            }
            if (arg.equals("Full Size"))  {
                  eacplot.resize(wide+40,high+215);
                  this.resize(wide+40,high+215);
                  repaint();
                  return true;
            }
        }
	return false;
     }

    public boolean handleEvent(Event e)
    {
       if (e.id == Event.WINDOW_DESTROY)
       {
          this.hide();
          this.dispose();
          if (!appletmode) System.exit(0);
          return true;
       }
       return super.handleEvent(e);
    }  

   public static void main( String[] args )
     {
	appletmode=false;
	System.out.println("Mode : "+appletmode);
        SmibEac eac = new SmibEac();
        eac.setTitle("SMIB Transient Stability Analysis");
     }  
}
