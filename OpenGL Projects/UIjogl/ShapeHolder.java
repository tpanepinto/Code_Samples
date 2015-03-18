

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by tim on 9/23/14.
 * Shape Holder holds all of the shapes that are in the scence
 */
public class ShapeHolder {
    protected ArrayList<Rectangle> rectArr;
    protected ArrayList<Triangle> eTriArr;
    protected ArrayList<Triangle> rTriArr;
    protected ArrayList<Quad> pgramArr, trapArr;
    protected ArrayList<Polygon> fpolyArr, epolyArr, nonPolyArr;
    protected ArrayList<Shape> shapes = new ArrayList<Shape>();
    Shape currShape;
    String currNewShape;
    float cblue, cred, cgreen;
    Color cBorderColor = Color.BLACK;
    boolean fill,border;
    protected int rectCount, eTriCount, rTriCount, pgramCount, trapCount, fpolyCount,epolyCount,nonPolyCount;

    public ShapeHolder(){
        rectArr = new ArrayList<Rectangle>();
        eTriArr = new ArrayList<Triangle>();
        rTriArr = new ArrayList<Triangle>();
        pgramArr = new ArrayList<Quad>();
        fpolyArr = new ArrayList<Polygon>();
        epolyArr = new ArrayList<Polygon>();
        nonPolyArr = new ArrayList<Polygon>();
        trapArr = new ArrayList<Quad>();
        //all counters set to zero
        rectCount = 0;
        eTriCount = 0;
        rTriCount = 0;
        pgramCount = 0;
        trapCount = 0;
        fpolyCount = 0;
        epolyCount = 0;
        nonPolyCount = 0;
        //set current shape to null
        Shape currShape = null;

        //default color red

        cred = 1;
        cgreen = 0;
        cblue = 0;

        //set fill to true
        fill = true;
        border = false;
        //current shape is a nonagon
        currNewShape = "nonPoly";
    }
//------------------addShape---------------

    /**
     *
     * Adds shape to the holder
     * */
    public void addShape(Shape s)
    {
        if ( s.getID().equals("RE")) {
            s.setID("RE" + (Integer.toString(rectCount)));
            rectArr.add((Rectangle) s);
            rectCount++;

        }
        else if (s.getID() == "ET")
        {
            s.setID("ET" + (Integer.toString(eTriCount)));
            eTriArr.add((Triangle) s);
            eTriCount++;
        }
        else if (s.getID().equals("RT"))
        {
            s.setID("RT" + (Integer.toString(rTriCount)));
            rTriArr.add((Triangle) s);
            rTriCount++;
        }
        else if (s.getID().equals("PG"))
        {
            s.setID("PG" + (Integer.toString(pgramCount)));
            pgramArr.add((Quad) s);
            pgramCount++;
        }
        else if (s.getID().equals("TR"))
        {
            s.setID("TR" + (Integer.toString(trapCount)));
            trapArr.add((Quad) s);
            trapCount++;
        }
        else if (s.getID().equals("FP"))
        {
            s.setID("FP" + (Integer.toString(fpolyCount)));
            fpolyArr.add((Polygon) s);
            fpolyCount++;
        }
        else if (s.getID().equals("EP"))
        {
            s.setID("EP" + (Integer.toString(epolyCount)));
            epolyArr.add((Polygon) s);
            epolyCount++;
        }
        else if (s.getID().equals("NP"))
        {
            s.setID("NP" + (Integer.toString(nonPolyCount)));

            nonPolyArr.add((Polygon) s);
            nonPolyCount++;
        }
        currShape = s;
        shapes.add(s);
    }
//------------------deleteShape---------------

    /**
     *
     * deletes the shape with the ID given
     * */
    public void deleteShape(String s)
    {
        /*
        if ( s.substring(0,2).equals("RE")) {
            for ( int i = 0; i < rectArr.size(); i++)
            {
                if ( rectArr.get(i).getID() == s )
                    rectArr.remove(i);
            }

        }
        else if (s.substring(0,2).equals("ET"))
        {
            for ( int i = 0; i < eTriArr.size(); i++)
            {
                if ( eTriArr.get(i).getID() == s )
                    eTriArr.remove(i);
            }
        }
        else if (s.substring(0,2).equals("RT"))
        {
            for ( int i = 0; i < rTriArr.size(); i++)
            {
                if ( rTriArr.get(i).getID() == s )
                    rTriArr.remove(i);
            }
        }
        else if (s.substring(0,2).equals("PG"))
        {
            for ( int i = 0; i < pgramArr.size(); i++)
            {
                if ( pgramArr.get(i).getID() == s )
                    pgramArr.remove(i);
            }
        }
        else if (s.substring(0,2).equals("TR"))
        {
            for ( int i = 0; i < trapArr.size(); i++)
            {
                if ( trapArr.get(i).getID() == s )
                    trapArr.remove(i);
            }
        }
        else if (s.substring(0,2).equals("FP"))
        {
            for ( int i = 0; i < fpolyArr.size(); i++)
            {
                if ( fpolyArr.get(i).getID() == s )
                    fpolyArr.remove(i);
            }
        }
        else if (s.substring(0,2).equals("EP"))
        {
            for ( int i = 0; i < epolyArr.size(); i++)
            {
                if ( epolyArr.get(i).getID() == s )
                    epolyArr.remove(i);
            }
        }
        else if (s.substring(0,2).equals("NP"))
        {
            for ( int i = 0; i < nonPolyArr.size(); i++)
            {
                if ( nonPolyArr.get(i).getID() == s )
                    nonPolyArr.remove(i);
            }
        }
*/
        for ( int i = 0; i < shapes.size(); i++)
        {
            if ( shapes.get(i).getID().equals(s) )
                shapes.remove(i);
        }
    }
//--------------------deleteAll-------------------------

    /**
     * Deletes all shapes in the scene and sets the counters to zero
     */
    public void deleteAll()
    {
        shapes.clear();
        rTriArr.clear();
        eTriArr.clear();
        rectArr.clear();
        pgramArr.clear();
        trapArr.clear();
        fpolyArr.clear();
        epolyArr.clear();
        nonPolyArr.clear();

        rTriCount = 0;
        eTriCount = 0;
        rectCount = 0;
        pgramCount = 0;
        trapCount = 0;
        fpolyCount = 0;
        epolyCount = 0;
        nonPolyCount = 0;



    }

//--------------------getShapes-------------------------

    /**
     * return all of the shapes stored
     */
    public ArrayList<Shape> getShapes(){

        return shapes;
    }


}
