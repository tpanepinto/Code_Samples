import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Shape.java - a simple abstract class to represent graphical objects in a 
 *              JOGL environment
 * 
 * @author rdb
 * Derived from Shape.cpp/h
 * 27 August 2013
 * ------- edit history -----------------------------------------
 * 07-22-14: Moved the static variables, GL2, GLUT, GLU from
 *               children classes to here; abstract parent.
 * 08-10-14: Made instance variables protected instead of package.
 * 
 */
public abstract class Shape 
{
    //---------------- class variables ------------------------
    public static GL2  gl = null;   // GL2 encapsulation of the openGL state
    
    public static GLUT glut = null; // the GLUT state
    public static GLU  glu  = null; // the GLU  state
    
    //------------------------- instance variables -----------------------
    protected float xLoc, yLoc;       // location (origin) of the object
    protected float xSize, ySize;     // size of the object
    protected Color color;            // color of object
    protected Color boundryColor;
    protected boolean fill;
    protected boolean boundry;
    protected float lineWidth;
    float red, green, blue;
    protected String ID;
    //---------------------------- Constructor ---------------------------
    public Shape()
    {
        setColor( 1,0,0 ); // default color is red
        setBoundryColor( Color.BLACK );
        setLocation( 0, 0 );
        setSize( 0.5f, 0.5f );
        setBound(false);
        setFill(true);
        setLineWidth(2.0f);

    } 
    
    //-------------------------- public methods --------------------------
    //----------------------------- redraw -------------------------------
    /**
     * Abstract redraw method must be defined by subclasses.
     */
    abstract public void redraw();
    abstract public void fillShape();
    abstract public void boundShape();
    //------------------ setLocation( float, float ) ------------------------
    /** 
     * Set the location of object to the x,y position defined by the args. 
     */ 
    public void setLocation( float x, float y ) 
    { 
        xLoc = x; 
        yLoc = y; 
    } 
    //------------------ getX() ------------------------
    /** 
     * Return the value of the x origin of the shape.
     */ 
    public float getX() 
    { 
        return xLoc; 
    }
    //------------------ getY() ------------------------
    /** 
     * Return the value of the y origin of the shape. 
     */ 
    public float getY() 
    { 
        return yLoc; 
    } 
    //------------------ setColor( Color ) -------------------
    /** 
     * Set the "nominal" color of the object to the specified color. This 
     *   does not require that ALL components of the object must be the same 
     *   color. Typically, the largest component will take on this color, 
     *   but the decision is made by the child class. 
     */   
    public void setColor( float r, float g, float b)
    { 
        red = r;
        green = g;
        blue = b;

    } 

    public void setBoundryColor( Color col){
        boundryColor = col;
    }

    //------------------ setSize( float, float ) ------------------------
    /**  
     * Set the size of the shape to be scaled by xs, ys. 
     *    That is, the shape has an internal fixed size, the shape parameters 
     *    scale that internal size. 
     */   
    public void setSize( float xs, float ys ) 
    { 
        xSize = xs; 
        ySize = ys; 
    }
    //------------------ setFill------------------------
    /**
     * Toggles the fill of the shape. When true the shape will be filled in when false the shape will not
     * be filled.
     */
    public void setFill ( boolean b)
    {
        fill = b;
    }
    //------------------ getFill ------------------------
    /**
     * Gets the state of whether the shape is filled or not
     */
    public boolean getFill( boolean b ){
        return fill;
    }
    //------------------setBound----------------------
    /**
     * Toggles the boundry of the shape. When true the shape will show a boundry.
     * When false the shape will not show the boundry
     */
    public void setBound ( boolean b)
    {
        boundry = b;
    }
    //------------------ getFill ------------------------
    /**
     * Gets the state of whether the shape is showing a boundry
     */
    public boolean getBound( boolean b ){ return boundry;}

    //------------------ setLineWidth ------------------------
    /**
     * Sets the width of the boundry lines
     */
    public void setLineWidth( float f )
    {
        lineWidth = f;
    }
    //------------------ getLineWidth ------------------------
    /**
     * Gets the width of the boundry lines
     */
    public float getLineWidth(){
        return lineWidth;
    }


    public String getID(){
        return ID;
    }

    public void setID( String s )
    {
        ID = s;
    }


}
