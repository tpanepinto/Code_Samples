/**
 * Object3D.java - an abstract class representing an OpenGL graphical object
 *
 * 10/16/13 rdb derived from Object3D.cpp
 */
import java.io.*;
import java.util.*;
import java.awt.Color;

abstract public class Object3D
{
    //------------------ class variables ------------------------------
    static final int MAX_COLORS = 20;    // arbitrary number to keep an parameter

    //------------------ instance variables ------------------------------
    protected float xLoc, yLoc, zLoc;        // location (origin) of the object
    protected float xSize, ySize, zSize;     // size of the object
    protected float angle, dxRot, dyRot, dzRot; // rotation angle and axis
    
    protected ArrayList<Color> colors;    
    
    protected abstract void drawPrimitives();
    //------------------ Constructors ------------------------------------
    /**
     * Create a new object3D at position 0,0,0 of size 1,1,1
     */
    Object3D()
    {
        colors = new ArrayList<Color>();
        Color color = new Color( 1.0f, 0.0f, 0.0f ); //red is default
        colors.add( color );
        
        setLocation( 0, 0, 0 );
        setSize( 1, 1, 1 );
        setRotate( 0, 0, 0, 0);
    }
    
    //------------------ public methods -------------------------------
    //------------- redraw ---------------------------
    void redraw()
    {   
        //std::cout <<"++++++++++++++++ cube redraw+++++++++++++" << std::endl;
        JOGL.gl.glPushMatrix();
        float[] rgb = colors.get( 0 ).getComponents( null );
        JOGL.gl.glColor3f( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] );
        //testMaterial();
        JOGL.gl.glTranslatef(xLoc, yLoc, zLoc);

        JOGL.gl.glRotatef(dxRot, 1f, 0,0);
        JOGL.gl.glRotatef(dyRot, 0,1f,0);
        JOGL.gl.glRotatef(dzRot, 0,0,1f);
        JOGL.gl.glScalef(xSize,ySize,zSize);
        
        drawPrimitives();
        
        JOGL.gl.glPopMatrix();
    }
    private void testMaterial()
    {
        float no_mat[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float no_shininess[] = { 0.0f };
        float mat_red[] = {1.0f, 0.0f, 0.0f, 1.0f};
        JOGL.gl.glPushMatrix();
        //JOGL.gl.glColor3f(1.0f, 1.0f, 1.0f);
        JOGL.gl.glMaterialfv(JOGL.gl.GL_FRONT, JOGL.gl.GL_AMBIENT, mat_red, 0);
        JOGL.gl.glMaterialfv(JOGL.gl.GL_FRONT, JOGL.gl.GL_DIFFUSE, no_mat, 0);
        JOGL.gl.glMaterialfv(JOGL.gl.GL_FRONT, JOGL.gl.GL_SPECULAR, no_mat, 0);
        JOGL.gl.glMaterialfv(JOGL.gl.GL_FRONT, JOGL.gl.GL_SHININESS, no_shininess, 0);
        JOGL.gl.glMaterialfv(JOGL.gl.GL_FRONT, JOGL.gl.GL_EMISSION, no_mat, 0);
        JOGL.gl.glDisable( JOGL.gl.GL_COLOR_MATERIAL );
    } 
    /**
     * set the location of the object to the x,y,z position defined by the args
     */
    void setLocation( float x, float y, float z )
    {
        xLoc = x;
        yLoc = y;
        zLoc = z;
    }
    /**
     * return the value of the x origin of the shape
     */
    float getX()
    {
        return xLoc;
    }
    /**
     * return the value of the y origin of the shape
     */
    float getY()
    {
        return yLoc;
    }
    /**
     * return the value of the z origin of the shape
     */
    float getZ()
    {
        return zLoc;
    }
    /**
     * return the location as a Point3 object
     */
    Point3 getLocation()                // return location as a Point
    {
        return new Point3( xLoc, yLoc, zLoc );
    }
//----------------------- setColor methods ---------------------------
    /**
     * set the "nominal" color of the object to the specified color; this
     *   does not require that ALL components of the object must be the same
     *   color. Typically, the largest component will take on this color,
     *   but the decision is made by the child class.
     */
    void setColor( Color c )
    {
        setColor( 0, c );
    }
    
    /**
     * set the nominal color (index 0) to the specified color with floats
     */
    void setColor( float r, float g, float b )
    {
        setColor( 0, new Color( r, g, b ));
    }
    
    /**
     * set the index color entry to the specified color with floats
     */
    void setColor( int i, float r, float g, float b )
    {
        setColor( i, new Color( r, g, b ));
    }
    /**
     * set the i-th color entry to the specified color with Color
     */
    void setColor( int i, Color c )
    {
        if ( i < 0 || i > MAX_COLORS ) // should throw an exception!
        {
            System.err.println( "*** ERROR *** Object3D.setColor: bad index: " 
                                   + i + "\n" );
            return;
        }
        float[] rgb = c.getComponents( null );
        Color newColor = new Color( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] );
        if ( i >= colors.size() )  // need to add entries to vector
        {
            for ( int n = colors.size(); n < i; n++ ) // fill w/ black if needed
                colors.add( Color.BLACK );
            colors.add( newColor );  // put desired color at desired index
        }
        else
        {
            // now replace old entry 
            colors.set( i, newColor );
        }
    }
//------------------ setSize ----------------------------------------
    /**
     * set the size of the shape to be scaled by xs, ys, zs
     *    That is, the shape has an internal fixed size, the shape parameters
     *    scale that internal size.
     */
    void setSize( float xs, float ys, float zs )
    {
        xSize = xs;
        ySize = ys;
        zSize = zs;
    }
    
    /**
     * set the rotation parameters: angle, and axis specification
     */
    void setRotate( float a, float dx, float dy, float dz )
    {
        angle = a;
        dxRot = dx;
        dyRot = dy;
        dzRot = dz;
    }


    public void shapeTranslate(String id , float xyz)
    {
        if (id.equals("x"))
            xLoc = xyz;
        else if (id.equals("y"))
            yLoc = xyz;
        else if (id.equals("z"))
            zLoc = xyz;
        else
            System.err.println("There is no ID that matches: " + id);


    }


    public void shapeRotate(String id , float xyz)
    {
        if (id.equals("x"))
            dxRot = xyz;
        else if (id.equals("y"))
            dyRot = xyz;
        else if (id.equals("z"))
            dzRot = xyz;
        else
            System.err.println("There is no ID that matches: " + id);


    }

    public void shapeScale(String id , float xyz)
    {
        if (id.equals("x"))
            xSize = xyz;
        else if (id.equals("y"))
            ySize = xyz;
        else if (id.equals("z"))
            zSize = xyz;
        else
            System.err.println("There is no ID that matches: " + id);
    }
}
