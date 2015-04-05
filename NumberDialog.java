
import java.awt.*;

public class NumberDialog extends Dialog {

private Button ok,close;
private NumberField nf;

public NumberDialog(Frame parent,String title,String label,double num)
{
        super(parent,title,true);
        GridBagLayout gridbag = new GridBagLayout();
        this.setLayout(gridbag);
        ok = new Button("OK");
        close = new Button("Cancel");
        nf = new NumberField(num,10);
        constrain(this,new Label(label),0,0,1,1,GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,0.0,1.0,10,10,10,10);
        constrain(this,nf,1,0,1,1,GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,1.0,1.0,10,10,10,10);
        constrain(this,ok,0,1,1,1,GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,1.0,0.0,10,10,10,10);
        constrain(this,close,1,1,1,1,GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,1.0,0.0,10,10,10,10);
        this.pack();
//        System.out.println(""+((parent.size().width - size().width)/2+","+
//                  (parent.size().height - size().height)/2));

        this.move(parent.location().x+(parent.size().width - size().width)/2,
                  parent.location().y+(parent.size().height - size().height)/2);
        this.show();
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
                GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);
   }
        
   public void constrain(Container cont,Component comp,int gx, int gy,
                int gw, int gh, int top, int left, int bottom, int right)
   {
        constrain(cont,comp,gx,gy,gw,gh,GridBagConstraints.NONE,
                GridBagConstraints.NORTHWEST,0.0,0.0,top,left,bottom,right);
   }

public boolean action(Event event, Object arg)
{
        if (event.target == close) {
                nf.setNumber(nf.getOrigNumber());
                this.hide();
                this.dispose();
                return true;
        }
        if (event.target == ok) {
                this.hide();
                this.dispose();
                return true;
        }
        return false;
}

public double getNumber()
{
        return (nf.getNumber());
}

public static void main(String args[])
{
        Frame f = new Frame("Testing NumberField");
        f.resize(600,400);
        f.move((800-600)/2,(600-400)/2);
        f.show();
        NumberDialog nd = new NumberDialog(f,"Enter","Enter No.",-36.56);
        double d;
        d = nd.getNumber();
        System.out.println(" "+d);
        System.exit(0);
}

}       // Class end
