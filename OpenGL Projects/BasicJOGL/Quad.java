import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library
import java.awt.Color;
/**
 * Quad.java - a class that represents a four sided shape using the shape class as a parent class.
 *
 * @author tml62
 *
 *
 *
 */

public class Quad extends Shape{


    Point[] points; //array of points for the quad object
//----------------Constructors------------------------
    //Public constructor that takes in points to draw the shape then initializes
    public Quad (Point[] p){

        points = p;

        init();
    }

    /**
     * Constructor that takes in a point array to define vertices and takes in a color for the fill
     */
    public Quad (Point[] p, Color fill)
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
    public Quad (Point[] p, Color fill, Color boundry)
    {
        points = p;
        color = fill;
        boundryColor = boundry;
        setBound(true);
        setFill(true);
        init();
    }
    //Public no parameter constructor for quad, makes a quad of a definite dimension
    public Quad(){
        points = new Point[4];

//        points[0] = new Point(0.0f,0.0f);
//        points[2] = new Point(0.0f,0.2f);
//        points[1] = new Point(0.2f,0.2f);
//        points[3] = new Point(0.2f,0.0f);

        Point qPoint = new Point(0.0f,0.0f);
        points[0] = qPoint;
        qPoint = new Point(-0.2f,0.2f);
        points[1] = qPoint;
        qPoint = new Point(0.2f,0.2f);
        points[2] = qPoint;
        qPoint = new Point(0.4f,0.0f);
        points[3] = qPoint;
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

        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f( color.getRed(), color.getGreen(), color.getBlue());
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        for (int i =0; i< 4 ; i++)
        {
            gl.glVertex2f(xLoc + points[i].getX()*xSize, yLoc + points[i].getY()*ySize);
        }
        gl.glEnd();
    }
    //-----------------boundShape-----------------------------
    /**
     *Makes a shape with the given vertices without fill. The color of the boundry can be changed.
     */
    public void boundShape(){

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glColor3f( boundryColor.getRed(), boundryColor.getGreen(), boundryColor.getBlue());
        gl.glLineWidth( lineWidth );
        for (int i =0; i< 4 ; i++)
        {
            gl.glVertex2f(xLoc + points[i].getX()*xSize, yLoc + points[i].getY()*ySize);
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