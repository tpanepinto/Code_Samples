import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library
import java.awt.Color;
import java.util.ArrayList;
/**
 * Polygon.java - a class that represents a multisided shape using the shape class as a parent class.
 *
 * @author tml62
 *
 *
 *
 */
public class Polygon extends Shape{


    ArrayList<Point> points; //array of points for the quad object
//------------------Constructors------------------------------
    //Public constructor that takes in points to draw the shape then initializes
    public Polygon (ArrayList<Point> p){

        points = p;

        init();
    }
    /**
     * Constructor that takes in a point array to define vertices and takes in a color for the fill
     */
    public Polygon (ArrayList<Point> p, Color fill)
    {
        points = p;
        color = fill;
        setBound(false);
        setFill(true);
        init();
    }
    /**
     * Constructor that takes in a point array to define vertices and takes in a color for the fill and boundry color
     */
    public Polygon (ArrayList<Point> p, Color fill, Color boundry)
    {
        points = p;
        color = fill;
        boundryColor = boundry;
        setBound(true);
        setFill(true);
        init();
    }
    //---------------------init-----------------------
    //initializes gl
    private void init(){
        gl   = JOGL.gl;
        glu  = JOGL.glu;
        glut = JOGL.glut;

    }


    //----------------------fillShape-------------------------
    /**
    *Fills the shape with a defined color and a defines the vertex of the object to be drawn
    **/
    public void fillShape(){

        gl.glColor3f( color.getRed(), color.getGreen(), color.getBlue());
        gl.glBegin(GL2.GL_POLYGON);

        for (int i =0; i < points.size() ; i++)
        {
            gl.glVertex2f(xLoc + points.get(i).getX()*xSize, yLoc + points.get(i).getY()*ySize);
        }
        gl.glEnd();
    }
    //-----------------boundShape-----------------------------
    /**
     *Makes a shape with the given vertices without fill. The color of the boundry can be changed.
     */
    public void boundShape(){

        gl.glColor3f( boundryColor.getRed(), boundryColor.getGreen(), boundryColor.getBlue());
        gl.glLineWidth( lineWidth );
        gl.glBegin(GL2.GL_LINE_LOOP);

        for (int i =0; i < points.size() ; i++)
        {
            gl.glVertex2f(xLoc + points.get(i).getX()*xSize, yLoc + points.get(i).getY()*ySize);
        }
        gl.glEnd();
    }
    //-------------------redraw-----------------------------
    /**
     *   redraw method that actually tells gl what to draw on the screen
     */
    public void redraw(){




        if (fill && boundry)
        {
            fillShape();
            boundShape();
        }
        else if (fill && boundry==false)
        {

            fillShape();
        }
        else if (!fill && boundry )
        {
            boundShape();
        }

    }
}