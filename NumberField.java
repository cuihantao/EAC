/** A class for implementing a numeric input box
*/

import java.awt.*;
import java.awt.event.*;

public class NumberField extends TextField implements TextListener{

private double num, origNum;

public NumberField()
{
        super();
        init(0);
}
public NumberField(double number)
{
        super(String.valueOf(number));
        init(number);
}
public NumberField(double number, int col)
{
        super(String.valueOf(number),col);
        init(number);
}

public void init(double number) 
{
         num = number;
         origNum = num;
         addTextListener (this);
}

public void textValueChanged(TextEvent e) {
        Double D=null;
        double d=0;
        String s = getText().trim();
        if (s.length()<1 || s.equals("-") || s.equals("+"))  s ="0";
        try {
                D = new Double(s);
        }
        catch (NumberFormatException ex) {
                setText(String.valueOf(num));
                setCaretPosition(getText().length());
                D = new Double(num);
        }
        finally {
               d = D.doubleValue();
               num = d; 
        }
        System.out.println(s+" : " + d);
}

public double getNumber()
{
        return (num);
}

public double getOrigNumber()
{
        return (origNum);
}

public void setNumber(double d)
{
        num = d;
        setText(String.valueOf(num));
        setCaretPosition(getText().length());
}

} //Class Number Field

