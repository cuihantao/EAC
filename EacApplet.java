import java.util.*;
import java.awt.*;
import java.applet.*;
import java.net.*;

public class EacApplet extends Applet implements Runnable
{                                          
   private Button Start;
   private Label wait;
   private Thread runner;
   private static boolean appletmode=true, first=true;
   private int nw = 0;

   public void init()
     {
        if (!appletmode) resize(200,80);
        Start = new Button("Run");
        wait = new Label("Click 'Run Button' to Execute",Label.CENTER);
        this.add(Start);
        this.add(wait);
        /*try 
        {
          Object a =  Class.forName(className).newInstance();
        }
        catch (ClassNotFoundException e) { return; }
        catch (InstantiationException e) { return; }
        catch (IllegalAccessException e) { return; }
        */
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
           if (first) { repaint();first = false;}
           try {     
	      Thread.sleep( 100 );
	   }
	   catch( InterruptedException e ) {
	      System.err.println( e );
	   }
	}
     }

   public boolean action(Event event, Object arg)
     {

       if (event.target == Start) {
          nw++;
          wait.setText("Please Wiat.. Loading !");
          try 
          {
             Object a =  Class.forName("EacPlot").newInstance();
          }
          catch (ClassNotFoundException e) {  }
          catch (InstantiationException e) {  }
          catch (IllegalAccessException e) {  }
          //EacPlot eac = new EacPlot("EAC Plot "+nw);
          wait.setText("Click 'Run Button' to Execute");
          return true;
	}
	return false;
     }

  public static void main( String[] args )
     {
	appletmode=false;
	System.out.println("Mode : "+appletmode);
        AppletFrame.startApplet("EacApplet","EAC Plot Applet", args);
     }  
}
