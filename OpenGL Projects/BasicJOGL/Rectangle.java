import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library
import java.awt.Color;
/**
 * Rectangle.java - a class that represents a rectangle using the shape class as a parent class.
 *
 * @author tml62
 *
 *
 *
 */
public class Rectangle extends Shape{


    Point[] points; //array of points for the rectangle object
    //--------------------constructors--------------------------

    /**
     * Public constructor that takes in a location, height and width for the Rectangle
     */
    public Rectangle (Point location, float height, float width){

        points = new Point[4];
        this.setLocation(location.getX(), location.getY());

        //Bottom Left corner
        Point p = new Point(location.getX(), location.getY());
        points[0] = p;
        Point sp = new Point(location.getX(), location.getY()+ height);
        points[1] = sp;
        Point qp  = new Point(location.getX()+ width, location.getY()+ height);
        points[2] = qp;
        Point rp = new Point(location.getX() + width, location.getY());
        points[3] = rp;

        init();
    }
    /**
     * Constructor that makes a rectangle of definite size at a user defined location
     **/
    public Rectangle (Point location){

        points = new Point[4];
        this.setLocation(location.getX(), location.getY());

        //Bottom Left corner
        Point p = new Point(location.getX(), location.getY());
        points[0] = p;
        Point sp = new Point(location.getX(), location.getY()+ 0.2f);
        points[1] = sp;
        Point qp  = new Point(location.getX()+ 0.2f, location.getY()+ 0.2f);
        points[2] = qp;
        Point rp = new Point(location.getX() + 0.2f, location.getY());
        points[3] = rp;

        init();
    }
    /**
     * Constructor that takes in a point  for the lower left corner, takes in a height and width
     * and takes in a color for the fill
     */
    public Rectangle (Point location, float height, float width, Color fill)
    {
        points = new Point[4];
        this.setLocation(location.getX(), location.getY());

        //Bottom Left corner
        Point p = new Point(location.getX(), location.getY());
        points[0] = p;
        Point sp = new Point(location.getX(), location.getY()+ height);
        points[1] = sp;
        Point qp  = new Point(location.getX()+ width, location.getY()+ height);
        points[2] = qp;
        Point rp = new Point(location.getX() + width, location.getY());
        points[3] = rp;

        color = fill;
        setBound(false);
        setFill(true);
        init();
    }
    /**
     *Constructor that takes in a point  for the lower left corner, takes in a height and width
      *and takes in a color for the fill along with color for the boundry
     */
    public Rectangle (Point location, float height, float width, Color fill, Color boundry)
    {
        points = new Point[4];
        this.setLocation(location.getX(), location.getY());

        //Bottom Left corner
        Point p = new Point(location.getX(), location.getY());
        points[0] = p;
        Point sp = new Point(location.getX(), location.getY()+ height);
        points[1] = sp;
        Point qp  = new Point(location.getX()+ width, location.getY()+ height);
        points[2] = qp;
        Point rp = new Point(location.getX() + width, location.getY());
        points[3] = rp;

        setColor( fill );
        setBoundryColor( boundry );
        setBound(true);
        setFill(true);
        init();
    }

    /**
     *Public no parameter constructor for Recatngle, makes a quad of a definite dimension
     * */
    public Rectangle(){
        points = new Point[4];

        Point qPoint = new Point(0.0f,0.0f);
        points[0] = qPoint;
        qPoint = new Point(0.0f,0.2f);
        points[1] = qPoint;
        qPoint = new Point(0.2f,0.2f);
        points[2] = qPoint;
        qPoint = new Point(0.2f,0.0f);
        points[3] = qPoint;


        init();

    }

    //------------------init-------------------------
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
        gl.glBegin(GL2.GL_QUADS);

        for (int i =0; i< 4 ; i++)
        {
            gl.glVertex2f((points[i].getX()*xSize), (points[i].getY()*ySize));
        }
        gl.glEnd();
    }
    //-----------------boundShape-----------------------------
    /**
     *Makes a shape with the given vertices without fill. The color of the boundry can be changed.
     */
    public void boundShape(){

        gl.glColor3f( boundryColor.getRed(), boundryColor.getGreen(), boundryColor.getBlue());
        gl.glBegin(GL2.GL_LINE_LOOP);

        for (int i =0; i< 4 ; i++)
        {
            gl.glVertex2f((points[i].getX()*xSize), (points[i].getY()*ySize));
        }
        gl.glLineWidth( lineWidth );

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