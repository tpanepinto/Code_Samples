/**
 * UIjogl -- A class to create a very simple 2D scene with JOGL
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
import java.awt.event.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class UIjogl extends JFrame implements GLEventListener, MouseListener , MouseMotionListener
{
    //-------------------- class variables ----------------------------
    private static boolean guiWindow = true;  // set to true if want 2 windows
    
    private static int width, height;
    private static int realHeight;
    private static ArrayList<Shape> sceneObjs;
    private static GLCanvas  glCanvas = null;
    ControlPanelW cp;
     private static ShapeHolder holdShape = new ShapeHolder();
    private static Point mouseLoc;
    //------------------ constructors ----------------------------------
    public UIjogl( int w, int h )
    {
        super( "UIjogl demo" );
        width = w; height = h;
        
        this.setSize( width, height );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setupOpenGL();
       
        if ( guiWindow ) 
        {
            ControlPanelW controlPanelW = ControlPanelW.getInstance();
            cp = controlPanelW;
            this.add( glCanvas );
        }
        else
        {
            ControlPanel controlPanel = ControlPanel.getInstance(); 
            controlPanel.addDrawPanel( glCanvas );

            this.add( controlPanel );
        }



       this.setVisible( true );        
    }
    //--------------------- setupOpenGL( int win ) -------------------------
    /**
     * Set up the open GL drawing window
     */
    void setupOpenGL( )
    {
        GLProfile glp = GLProfile.getDefault();        
        GLCapabilities caps = new GLCapabilities( glp );
        glCanvas = new GLCanvas( caps );
        
        // When a GL event occurs, need to tell the canvas to send the event
        //    to the UIjogl object, which knows how to draw the scene.
        glCanvas.addGLEventListener( this );

        glCanvas.addMouseListener(this);
        glCanvas.addMouseMotionListener(this);
        // This program doesn't need an animator since all image changes 
        //    occur because of interactions with the user and should
        //    get triggered as long as the GLCanvas.repaint method is called.
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
        JOGL.glu.gluOrtho2D( 0.0, width, 0.0, height );
        
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
        System.out.println( "reshape" );
        JOGL.gl = drawable.getGL().getGL2();
        JOGL.gl.glViewport( 0, 0, w, h );
        System.out.println( "Viewport size: " + w + " x " + h );
        realHeight = h;
    }
    //+++++++++++++++++++ GUI responder hack ++++++++++++++++++++++++++++++++++++++
    //----------------------------changeEvent------------

    /**
     *
     *Provides the change for the XY translation on the canvas
     */
    public static void changeEvent( String event, int value )
    {

        if (holdShape.currShape != null) {
            Shape first = holdShape.currShape;
            if (event.equals("x"))
                first.setLocation(value, first.getY());
            else if (event.equals("y"))
                first.setLocation(first.getX(), value);
            else
                first.setSize(value, value);
        }

        glCanvas.repaint();
    }
//----------------------------colorchange------------

    /**
     *
     *Provides the change for the color for the next shape
     */
    public static void colorChange(Color c)
    {
        holdShape.cred = c.getRed();
        holdShape.cgreen = c.getGreen();
        holdShape.cblue = c.getBlue();
    }
    //----------------------------boundchange------------

    /**
     *
     *Changes whether the bounds will be on the net shape
     */
    public static void boundChange( boolean b)
    {
        holdShape.border = b;
    }
    //----------------------------fillchange------------

    /**
     *
     *Changes whether the fill will be on the net shape
     */
    public static void fillChange( boolean b)
    {
        holdShape.fill = b;
    }
    //----------------------------shapechange------------

    /**
     *
     *Changes the next shape type
     */
    public static void shapeChange( String s )
    {
       holdShape.currNewShape = s;
    }
    //----------------------------shapeDelete------------

    /**
     *
     *deletes the shape with the id that the user enters
     */
    public static void shapeDelete( String str )
    {
        holdShape.deleteShape(str);
        glCanvas.repaint();
        System.out.println("Curr shapes: " + holdShape.shapes);

    }
    //----------------------------deleteAll------------

    /**
     *
     *deletes all of the shapes
     */
    public static void deleteAll()
    {
        sceneObjs.clear();
        holdShape.deleteAll();
        glCanvas.repaint();

    }
    //----------------------------borderColorChange------------

    /**
     *
     *Changes the color of the border of the next shape
     */
    public static void borderColorChange( Color c)
    {
        holdShape.cBorderColor = c;
    }
    //+++++++++++++++++++  private local methods ++++++++++++++++++++++++++++++++++
    //--------------------- makeScene ------------------------------
    private void makeScene()
    {
        sceneObjs = new ArrayList<Shape>();
     /*
        float[] x1 = { 0, 40, 20 };
        float[] y1 = { 0, 0, 75 };
        
        sceneObjs = new ArrayList<Shape>();
        

        Triangle tri = new Triangle();  //  red isosceles, centered at origin of side 1
        tri.setSize( 100, 100 );  // make it big enough to see!
        tri.setLocation(50, 50); // put its lower left corner at origin
        tri.setID("ET");
        holdShape.addShape(tri);
        sceneObjs = holdShape.getShapes();

        tri = new Triangle( x1, y1 );
        tri.setLocation( 10, 100 );
        tri.setColor( 0.0f, 1.0f, 1.0f ); // cyan
        sceneObjs.add( tri );
        
        tri = new Triangle( x1, y1 );
        tri.setLocation( 70, 150 );
        tri.setColor( 1.0f, 0.0f, 1.0f ); // magenta
        tri.setSize( 2.0f, 2.0f );
        sceneObjs.add( tri );


        Rectangle r = new Rectangle(new Point(0.2f,0.2f));
        sceneObjs.add(r);
      */
    }

    //---------------------- render ---------------------------------------
    /**
     * Do the actual drawing
     */
    private void render( GLAutoDrawable drawable )
    {
        JOGL.gl.glClear( GL2.GL_COLOR_BUFFER_BIT );

        sceneObjs = holdShape.getShapes();
        //System.out.println(sceneObjs);
        for ( Shape s: sceneObjs )
        {
            s.redraw( );
            System.out.println("Draw Shape" + s);
        }
    }
    
    //++++++++++++++++++++++++++++ main ++++++++++++++++++++++++++++++++++++++
    public static void main( String[] args )
    {
        int winW = 800, winH = 600;
        if ( args.length > 0 )
            guiWindow = !guiWindow;  // reverse the default setting of the guiWindow

        UIjogl scene = new UIjogl( winW, winH );


    }


    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }
    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e
     */
    @Override
        public void mousePressed(MouseEvent e) {

                mouseLoc = new Point((float) (e.getX()) ,  (float) (-(e.getY() - realHeight)));


               //makes a right triangle
               if (holdShape.currNewShape == "rtri")
                {
                    Triangle tri = new Triangle( "r");
                    tri.setID("RT");
                    tri.setLocation(mouseLoc.getX(), mouseLoc.getY());
                    //tri.setSize(10,10);
                    tri.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                    tri.setFill(holdShape.fill);
                    tri.setBound(holdShape.border);
                    tri.setBoundryColor(holdShape.cBorderColor);
                    holdShape.addShape(tri);

                }
               //makes an equilateral triangle
                else if ( holdShape.currNewShape.equals("etri"))
               {
                   Triangle tri = new Triangle( "e");
                   tri.setID("ET");
                   tri.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                   tri.setFill(holdShape.fill);
                   tri.setBound(holdShape.border);
                   tri.setLocation(mouseLoc.getX(), mouseLoc.getY());
                   tri.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(tri);

               }
               //makes a rectangle
               else if ( holdShape.currNewShape.equals("rect"))
               {
                   Rectangle rect = new Rectangle(mouseLoc);
                   rect.setID("RE");
                   rect.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                   rect.setFill(holdShape.fill);
                   rect.setBound(holdShape.border);
                   rect.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(rect);
               }
               //makes a parallelogram
            else if ( holdShape.currNewShape.equals("pgram"))
               {
                   Quad q = new Quad();
                   q.setColor(holdShape.cred, holdShape.cgreen, holdShape.cblue);
                   q.setFill(holdShape.fill);
                   q.setBound(holdShape.border);
                   q.setLocation(mouseLoc.getX(), mouseLoc.getY());
                   q.setID("PG");
                   q.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(q);

               }
               //makes trapezoid
            else if ( holdShape.currNewShape.equals("trap"))
               {
                   //points for quad
                   Point[] quadPoints = new Point[4];
                   Point qPoint = new Point(0,0);
                   quadPoints[0] = qPoint;
                   qPoint = new Point(100,150);
                   quadPoints[1] = qPoint;
                   qPoint = new Point(200,150);
                   quadPoints[2] = qPoint;
                   qPoint = new Point(300,0);
                   quadPoints[3] = qPoint;


                   Quad q = new Quad(quadPoints);
                   q.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                   q.setFill(holdShape.fill);
                   q.setBound(holdShape.border);
                   q.setLocation(mouseLoc.getX(), mouseLoc.getY());
                   q.setID("TR");
                   q.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(q);
               }
               //makes a pentagon
            else if( holdShape.currNewShape.equals("fPoly"))
               {
                   ArrayList<Point> polyPoints2 = new ArrayList<Point>();
                   Point pPoint = new Point(0,-100);
                   polyPoints2.add(pPoint);
                   pPoint = new Point(-95,-31);
                   polyPoints2.add(pPoint);
                   pPoint = new Point(-59,81);
                   polyPoints2.add(pPoint);
                   pPoint = new Point(59,81);
                   polyPoints2.add(pPoint);
                   pPoint = new Point(95,-31);
                   polyPoints2.add(pPoint);

                   Polygon poly = new Polygon(polyPoints2);
                   poly.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                   poly.setFill(holdShape.fill);
                   poly.setBound(holdShape.border);
                   poly.setLocation(mouseLoc.getX(), mouseLoc.getY());
                   poly.setID("FP");
                   poly.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(poly);

               }
               //makes an octagon
            else if ( holdShape.currNewShape.equals("ePoly"))
               {
                   ArrayList<Point> polyPoints3 = new ArrayList<Point>();
                   Point pPoint = new Point(0,-100);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-64,-77);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-98,-17);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-87,50);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-34,94);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(34,94);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(87,50);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(98,-17);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(64,-77);
                   polyPoints3.add(pPoint);

                   Polygon poly = new Polygon(polyPoints3);
                   poly.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                   poly.setFill(holdShape.fill);
                   poly.setBound(holdShape.border);
                   poly.setLocation(mouseLoc.getX(), mouseLoc.getY());
                   poly.setID("EP");
                   poly.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(poly);

               }
               //makes a nonagon
            else if ( holdShape.currNewShape.equals("nonPoly"))
               {
                   ArrayList<Point> polyPoints3 = new ArrayList<Point>();
                   Point pPoint = new Point(31,-95);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-31,-95);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-81,-59);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-100,0);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-81,59);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(-31,95);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(31,95);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(81,59);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(100, 0);
                   polyPoints3.add(pPoint);
                   pPoint = new Point(81,-59);
                   polyPoints3.add(pPoint);

                   Polygon poly = new Polygon(polyPoints3);
                   poly.setColor(holdShape.cred,holdShape.cgreen,holdShape.cblue);
                   poly.setFill(holdShape.fill);
                   poly.setBound(holdShape.border);
                   poly.setLocation(mouseLoc.getX(), mouseLoc.getY());
                   poly.setID("NP");
                   poly.setBoundryColor(holdShape.cBorderColor);
                   holdShape.addShape(poly);
               }
            else
               System.err.println("No SHAPE!");
        //changes slider to the current position of the current shape
        cp.setxSlider((int) mouseLoc.getX());
        cp.setySlider((int) mouseLoc.getY());

        cp.changeCurrShape(holdShape.currShape.getID());
        glCanvas.repaint();




        }

        /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {


    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p/>
     * Due to platform-dependent Drag&Drop implementations,
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
     * Drag&Drop operation.
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        float distance;
        //find the new mouse point
        /*
        Point newMouseLoc = new Point( e.getX(),(float) (-(e.getY() - realHeight)));

        if ( (Math.abs((newMouseLoc.getY() - mouseLoc.getY())) > 10) || (Math.abs(newMouseLoc.getX() - mouseLoc.getX()) > 10))
        {

            if ( Math.abs((newMouseLoc.getY() - mouseLoc.getY())) > (Math.abs(newMouseLoc.getX() - mouseLoc.getX())))
                distance = Math.abs((newMouseLoc.getY() - mouseLoc.getY())); //get the distance in y
            else
                distance = Math.abs(newMouseLoc.getX() - mouseLoc.getX()); //get the distance in x

            float sizeFactor = ((distance/ 100) ); //size factor to scale the shape
            holdShape.currShape.setSize(sizeFactor,sizeFactor);
            glCanvas.repaint();

        }
        */
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
