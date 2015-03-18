/**
 * JOGLDemo -- A class to create a very simple 2D scene with JOGL
 * 
 * @author rdb
 * 
 * Derived loosely from code written by Derek Dupuis, Fall 2012. 
 *   The overall framework for Derek's code came from OpenGL demo code 
 *   he found on the web, but did not identify.
 * A few framework modifications were based on the tutorial by Justin
 *   Stoecker at 
 *   https://sites.google.com/site/justinscsstuff/jogl-tutorials
 * It looks like Derek's code may have come from the Stoecker tutorial,
 *   but there are a few differences. 
 * The version available to me was dated 2011.
 * 
 * The main class is responsible for creating the needed JOGL objects:
 *     gl:   the openGL Drawable reference
 *     glut: the GLUT reference
 *     glu:  the GL Utility reference
 *  and assigning them to the JOGL "holder" class.
 
 * 
 *  Once this class has created these objects, it does not maintain a local 
 *  copy, but always references the objects through the JOGL variable. 
 * 
 * @version 0.1 08/23/2013
 * @version 0.2 09/30/2013
 *     - discovered an error in display method; was initializing ModelView
 *       stack AFTER calling render.
 *     - added Animator creation, but don't use it????
 *     - added some comments
 */
import java.awt.Color;
import java.security.Policy;
import java.util.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.gl2.GLUT;

public class BasicJOGL extends JFrame implements GLEventListener
{
    //-------------------- instance variables --------------------------
    private int width, height;
    
    private ArrayList<Shape> sceneObjs;
        
    private GLCanvas canvas;
    
    //------------------ constructors ----------------------------------
    public BasicJOGL( int w, int h )
    {
        super( "BasicJOGL" );
        width = w;
        height = h;
        
        GLProfile glp = GLProfile.getDefault();        
        GLCapabilities caps = new GLCapabilities( glp );
        canvas = new GLCanvas( caps );        
        
        canvas.addGLEventListener( this );
        
        this.setSize( width, height );
        this.add( canvas );
        this.setVisible( true );
        
        // terminate the program when the window is asked to close
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
        
        // When a GL event occurs, need to tell the canvas to send the event
        //    to the BasicJOGL object, which knows how to draw the scene.
        // set up an Animator: will generate events to update the canvas 
        //   The update will either occur "as fast as possible", or 
        //   only after a complete frame has been drawn by the display
        //   hardware. See setSwapInterval call in "init" method.
        // Cpuld also use and FPSAnimator if you want to achieve a particular
        //   frame rate. This program doesn't need an animator at all since
        //   it just puts up 1 picture and then waits to be killed.
        //Animator animator = new Animator( canvas );
        //animator.start();
    }
    //+++++++++++++++  GLEventListener override methods ++++++++++++++++++++
    //-------------------- display -------------------------------------
    /**
     * Override the parent display method
     *    In this framework, the display method is responsible for setting
     *       up the projection specification, but the "render" method
     *       is responsible for the View and Model specifications.
     * 
     *    This display method is reasonably application-independent;
     *      It defines a pattern that can be reused with the exception
     *      of the specifying the actual objects to render.
     */
    @Override
    public void display( GLAutoDrawable drawable )
    {
       // set op projection matrix
        JOGL.gl.glMatrixMode( GL2.GL_PROJECTION );
        JOGL.gl.glLoadIdentity();
        
        // Set up a projection specification that will "see" objects 
        //    defined in a coordinate system plane and "window" that 
        //    approximates the size of the window.
        //   
        //JOGL.glu.gluOrtho2D( 0.0, width, 0.0, height );
        JOGL.glu.gluOrtho2D( -1, 1, -1, 1 );
        
        // 
        // Change back to model view matrix and initialize it
        JOGL.gl.glMatrixMode( GL2.GL_MODELVIEW );
        JOGL.gl.glLoadIdentity();   
        
        render( drawable );
    }
    //--------------------- dispose ------------------------------    
    @Override
    public void dispose( GLAutoDrawable arg0 )
    {
        // nothing to dispose of...
    }
    
    //--------------------- init ------------------------------
    @Override
    public void init( GLAutoDrawable drawable )
    {
        System.err.println( "init" );
        JOGL.gl = drawable.getGL().getGL2();
        
        JOGL.gl.setSwapInterval( 1 );  // animation event occurs (maybe)
                                       //   only at end of frame draw.
                                       //  0 => render as fast as possible
        JOGL.glu = new GLU();
        JOGL.glut = new GLUT();
        JOGL.gl.glClearColor(0.75294f,0.75294f,0.75294f,0.0f);
        makeScene();
    }
    //--------------------- reshape ----------------------------------------
    /**
     * Window has been resized, readjust internal information
     */
    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h )
    {
        JOGL.gl = drawable.getGL().getGL2();
        JOGL.gl.glViewport( 0, 0, w, h );
        System.out.println( "Viewport size: " + w + " x " + h );
    }
    
    //+++++++++++++++++++  private local methods ++++++++++++++++++++++++++++++++++
    //--------------------- makeScene ------------------------------
    private void makeScene()
    {
        float[] x1 = { 0, 0.4f, 0.2f };
        float[] y1 = { 0, 0, 0.7f };
        
        sceneObjs = new ArrayList<Shape>();
//-------------------------triangles-----------------------------
        Triangle tri = new Triangle( x1, y1 );
        tri.setLocation( 0, 0 ); // put its lower left corner at origin
        tri.setColor( Color.CYAN ); // cyan
        tri.setBound(true);
        tri.setBoundryColor(Color.MAGENTA);
        sceneObjs.add( tri );

        tri = new Triangle( x1, y1 );
        tri.setLocation( -0.5f, -0.25f );
        tri.setColor(Color.MAGENTA);    //  magenta
        tri.setSize(0.7f, 1.1f);
        tri.setBound(true);
        tri.setFill(false);
        tri.setBoundryColor(Color.BLUE);
        tri.setLineWidth(3.0f);
        sceneObjs.add( tri );

        tri = new Triangle( x1, y1 );      // red by default
        tri.setLocation( 0.3f, -0.2f );
        sceneObjs.add( tri );
//-----------------quads------------------
        //points for quad
        Point[] quadPoints = new Point[4];
        Point qPoint = new Point(0.0f,0.0f);
        quadPoints[0] = qPoint;
        qPoint = new Point(0.1f,0.15f);
        quadPoints[1] = qPoint;
        qPoint = new Point(0.2f,0.15f);
        quadPoints[2] = qPoint;
        qPoint = new Point(0.3f,0.0f);
        quadPoints[3] = qPoint;


        Quad q = new Quad(quadPoints);
        q.setLocation(-0.9f,-0.5f);
        q.setColor(Color.RED);
        q.setSize(0.5f, 0.5f);
        q.setBound(true);
        sceneObjs.add( q );


        q = new Quad();
        q.setLocation(0.0f,-0.3f);
        q.setColor(Color.GREEN);
        q.setSize(1.0f, 1.0f);
        q.setBound(true);
        q.setFill(true);
        q.setBoundryColor(Color.RED);
        sceneObjs.add( q );
//-----------------Rectangles-----------------
        Point recLoc = new Point(-0.8f,0.5f);
        Rectangle rec = new Rectangle(recLoc,0.3f,0.3f);
        rec.setBoundryColor(Color.MAGENTA);
        rec.setBound(true);
        rec.setFill(false);
        sceneObjs.add(rec);

        rec = new Rectangle();
        rec.setColor(Color.BLUE);
        sceneObjs.add(rec);

        recLoc = new Point(0.4f, -0.7f);
        rec = new Rectangle(recLoc,0.3f,0.3f,Color.BLUE, Color.RED);

        sceneObjs.add(rec);
//--------------------Polygons-------------------------------
        ArrayList<Point> polyPoints = new ArrayList<Point>();
        Point pPoint = new Point(0.6f,0.6f);
        polyPoints.add(pPoint);
        pPoint = new Point(0.73f,0.8f);
        polyPoints.add(pPoint);
        pPoint = new Point(0.75f,0.8f);
        polyPoints.add(pPoint);
        pPoint = new Point(0.8f,0.74f);
        polyPoints.add(pPoint);
        pPoint = new Point(0.8f,0.6f);
        polyPoints.add(pPoint);
        pPoint = new Point(0.6f,0.6f);
        polyPoints.add(pPoint);

        Polygon poly = new Polygon(polyPoints);
        poly.setColor(Color.BLACK);
        poly.setLocation(0.0f, 0.0f);
        poly.setBound(true);
        poly.setBoundryColor(Color.GREEN);
        poly.setLineWidth(2.5f);
        sceneObjs.add(poly);
        //poly.setSize(0.7f,0.7f);

        ArrayList<Point> polyPoints2 = new ArrayList<Point>();
        pPoint = new Point(0.0f,-0.10f);
        polyPoints2.add(pPoint);
        pPoint = new Point(-0.095f,-0.031f);
        polyPoints2.add(pPoint);
        pPoint = new Point(-0.059f,0.081f);
        polyPoints2.add(pPoint);
        pPoint = new Point(0.059f,0.081f);
        polyPoints2.add(pPoint);
        pPoint = new Point(0.059f,-0.031f);
        polyPoints2.add(pPoint);



        poly = new Polygon(polyPoints2);
        poly.setLocation(-0.8f,-0.8f);
        //poly.setSize(0.1f,0.1f);
        poly.setBound(true);
        poly.setFill(false);
        poly.setBoundryColor(Color.YELLOW);
        sceneObjs.add(poly);


        ArrayList<Point> polyPoints3 = new ArrayList<Point>();
        pPoint = new Point(0.0f,-0.10f);
        polyPoints3.add(pPoint);
        pPoint = new Point(-0.064f,-0.077f);
        polyPoints3.add(pPoint);
        pPoint = new Point(-0.098f,-0.017f);
        polyPoints3.add(pPoint);
        pPoint = new Point(-0.087f,0.050f);
        polyPoints3.add(pPoint);
        pPoint = new Point(-0.034f,0.094f);
        polyPoints3.add(pPoint);
        pPoint = new Point(0.034f,0.094f);
        polyPoints3.add(pPoint);
        pPoint = new Point(0.087f,0.050f);
        polyPoints3.add(pPoint);
        pPoint = new Point(0.098f,-0.017f);
        polyPoints3.add(pPoint);
        pPoint = new Point(0.064f,-0.077f);
        polyPoints3.add(pPoint);



        poly = new Polygon(polyPoints3);
        poly.setLocation(-0.3f,-0.6f);
        poly.setSize(0.3f,0.3f);
        poly.setColor(Color.BLUE);
        sceneObjs.add(poly);

 }
        
    //---------------------- render ---------------------------------------
    /**
     * Do the actual drawing
     */
    private void render( GLAutoDrawable drawable )
    {
        System.err.println( "render" );
        JOGL.gl.glClear( GL2.GL_COLOR_BUFFER_BIT );
        
        for ( Shape s: sceneObjs )
        {
            s.redraw( );
        }
    }
    
    //++++++++++++++++++++++++++++ main ++++++++++++++++++++++++++++++++++++++
    public static void main( String[] args )
    {
        int winW = 800, winH = 600;
        BasicJOGL scene = new BasicJOGL( winW, winH );
    }
}
