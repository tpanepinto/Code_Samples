/**
 * Triangle.java: version 1a 
 * 
 * @author rdb
 * @version 0.1 08/27/13
 * 
 *     This class defines a triangle object and is responsible for drawing it.
 * 
 *     The class simplifies the access to the JOGL class variables, 
 *     by making copies of them as Triangle class variables at construction time;
 *          this means we use references such as 
 *              gl.glBegin();
 *          instead of 
 *              JOGL.gl.glBegin();
 *     The possible disadvantage of this approach might occur if one or more of
 *     the JOGL class variables needs to be changed, we'd need to provide an
 *     opportunity to update the local variables.
 */

//import java.awt.geom.*;
//import java.awt.*;
import java.awt.Color;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library

public class Triangle extends Shape 
{
    //---------------- instance variables ------------------------
    //private Color _borderColor;
    //private Color _fillColor;
    //private int   _lineWidth = 2;
    
    private float[] dx;
    private float[] dy;
    
    
    //--------------------  constructors ---------------------------
    /**
     * Constructor
     */
    public Triangle()
    { 
        // default triangle is equilateral triangle with center at origin
        //    and base and height of size 1.
        float dxDefault[] = { -0.5f, 0.0f,  0.5f };
        float dyDefault[] = { -0.5f, 0.5f, -0.5f };
        
        initialize( dxDefault, dyDefault );
    }
    
    public Triangle( float[] x, float[] y )
    {
        initialize( x, y );
    }
    //-------------------------- initialize ------------------------
    public void initialize( float[] x, float[] y )
    {
        dx = new float[ 3 ];
        dy = new float[ 3 ];
        for ( int i = 0; i < x.length; i++ )
        {
            dx[ i ] = x[ i ];
            dy[ i ] = y[ i ];
        }
        // access global information about JOGL components
        gl   = JOGL.gl;
        glu  = JOGL.glu;
        glut = JOGL.glut;
    }
    
    

    //----------------------fillShape-------------------------
    /*
    *Fills the shape with a defined color and a defines the vertex of the object to be drawn
    **/
    public void fillShape(){

        gl.glColor3f( color.getRed(), color.getGreen(), color.getBlue() );
        gl.glBegin( GL2.GL_POLYGON );
        // The triangle is defined by positions relative to its location stored
        //   in the dx and dy arrays
        // The scale factor applies to the relative offset of each coordinate from the
        //    origin (which is xLoc, yLoc )

        for ( int i = 0; i < dx.length; i++ )
        {
            gl.glVertex2f( xLoc + dx[ i ] * xSize,
                    yLoc + dy[ i ] * ySize );
        }
        gl.glEnd();
    }
    //-----------------boundShape-----------------------------
    /*
     *Makes a shape with the given vertices without fill. The color of the boundry can be changed.
     */
    public void boundShape(){

        gl.glColor3f( boundryColor.getRed(), boundryColor.getGreen(), boundryColor.getBlue() );
        gl.glLineWidth( lineWidth );
        gl.glBegin( GL2.GL_LINE_LOOP );
        // The triangle is defined by positions relative to its location stored
        //   in the dx and dy arrays
        // The scale factor applies to the relative offset of each coordinate from the
        //    origin (which is xLoc, yLoc )

        for ( int i = 0; i < dx.length; i++ )
        {
            gl.glVertex2f( xLoc + dx[ i ] * xSize,
                    yLoc + dy[ i ] * ySize );
        }
        gl.glEnd();
    }
    //-------------------redraw-----------------------------
    /*
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
