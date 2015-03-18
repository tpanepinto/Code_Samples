/**
 * TextureDemo -- A class to create a very simple 2D scene with JOGL
 *
 * @author rdb
 * September 2013
 *
 *  Once this class has created these objects, it does not maintain a local 
 *  copy, but always references the objects through the JOGL variable. 
 *
 */

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class TextureDemo extends JFrame implements GLEventListener {
    //-------------------- class variables ----------------------------    
    private static int width = 1024, height = 768; // window w x h and defaults
    private static int drawW = 0, drawH = 0;  // drawing canvas w x h

    private static ArrayList<Shape> sceneObjs;
    private static GLCanvas glCanvas = null;
    private static TextureDemo me = null;

    //--------------------- instance variables ------------------------
    private FPSAnimator animator = null;
    private boolean animating = true;
    private int animationSpeed = 50;

    float angle = 0f; //for the spinning panel
    float replication = 1.0f;  // # copies of texture on quad in each direction
    float offset = 0.0f;       // offset of map origin as % of quad in x and y
    int deltaRep = 1;
    int revolution = 0;       // how many complete revolutions have been made.

    int stepsPer90 = 180;         // # steps to make to cover 90 degrees
    int stepsLeft = stepsPer90; // # steps left for current 90 deg segment
    float deltaAng = 90.0f / stepsPer90;
    float deltaOff = 0.15f;
    boolean spin = true;

    float x2yAspect = 1.0f; // keep texture mapping consistent with input image

    private GL2 gl = null;   // the GL2 encapsulation of the openGL state
    private GLUT glut = null; // the GLUT state
    private GLU glu = null; // the GLU  state

    private Texture myTexture = null;

    //------------------ getInstance() ---------------------------------

    /**
     * Return the 1 instance of TextureDemo; create if not there.
     */
    public static TextureDemo getInstance() {
        if ( me == null )
            me = new TextureDemo( width, height );
        return me;
    }

    //------------------ constructors ----------------------------------
    private TextureDemo( int w, int h ) {
        super( "TextureDemo demo" );
        width = w;
        height = h;

        this.setSize( width, height );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setupOpenGL();

        ControlPanel controlPanel = ControlPanel.getInstance();
        controlPanel.addDrawPanel( glCanvas );
        this.add( controlPanel );

        animator = new FPSAnimator( glCanvas, animationSpeed );
        //animator.start();

        this.setVisible( true );
    }
    //--------------------- setupOpenGL( int win ) -------------------------

    /**
     * Set up the open GL drawing window
     */
    void setupOpenGL() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities( glp );
        glCanvas = new GLCanvas( caps );

        // When a GL event occurs, need to tell the canvas to send the event
        //    to the TextureDemo object, which knows how to draw the scene.
        glCanvas.addGLEventListener( this );

        // This program doesn't need an animator since all image changes 
        //    occur because of interactions with the user and should
        //    get triggered as long as the GLCanvas.repaint method is called.
    }

    //--------------------- createTexture() -------------------------

    /**
     * Create a texture with the behavior we want.
     */
    void createTexture() {
        try {
            InputStream stream = getClass().getResourceAsStream( "Sunrise.jpg" );
            //InputStream stream = new FileInputStream( new File( "Sunrise.jpg" ));
            //TextureData data = TextureIO.newTextureData( stream, true, "jpg" );
            //myTexture = TextureIO.newTexture( data );
            myTexture = TextureIO.newTexture( stream, true, "jpg" );
        } catch ( IOException exc ) {
            exc.printStackTrace();
            System.exit( 1 );
        }
        myTexture.setTexParameteri( gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR );
        //myTexture.setTexParameteri( gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST );
        //myTexture.setTexParameteri( gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR );

        myTexture.setTexParameteri( gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR );
        myTexture.setTexParameteri( gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT );
        myTexture.setTexParameteri( gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT );
    }
    //+++++++++++++++  GLEventListener override methods ++++++++++++++++++++
    //-------------------- display -------------------------------------

    /**
     * Override the parent display method
     * In this framework, the display method is responsible for setting
     * up the projection specification, but the "render" method
     * is responsible for the View and Model specifications.
     * <p/>
     * This display method is reasonably application-independent;
     * It defines a pattern that can be reused with the exception
     * of the specifying the actual objects to render.
     */
    @Override
    public void display( GLAutoDrawable drawable ) {
        // 
        // Change back to model view matrix and initialize it
        JOGL.gl.glMatrixMode( GL2.GL_MODELVIEW );
        JOGL.gl.glLoadIdentity();

        render( drawable );
    }

    //--------------------- dispose ------------------------------
    @Override
    public void dispose( GLAutoDrawable arg0 ) {
        // nothing to dispose of...
    }

    //--------------------- init ------------------------------
    @Override
    public void init( GLAutoDrawable drawable ) {
        JOGL.gl = gl = drawable.getGL().getGL2();

        JOGL.gl.setSwapInterval( 1 );  // animation event occurs (maybe)
        //   only at end of frame draw.
        //  0 => render as fast as possible
        JOGL.glu = glu = new GLU();
        JOGL.glut = glut = new GLUT();

        gl.glClearColor( 0.4f, 0.4f, 0.5f, 1.0f );
        gl.glShadeModel( GL2.GL_SMOOTH );

        gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glEnable( GL2.GL_DOUBLEBUFFER );

        //This is in case the image has some transparency
        gl.glEnable( GL2.GL_BLEND );
        gl.glBlendFunc( GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA );
        createTexture();
    }
    //--------------------- reshape ----------------------------------------

    /**
     * Window has been resized, readjust internal information
     */
    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ) {
        System.out.println( "reshape" );
        JOGL.gl = drawable.getGL().getGL2();
        JOGL.gl.glViewport( 0, 0, w, h );

        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluPerspective( 40.0, w / ( float ) h, 1.0, 1000.0 );
        System.out.println( "Viewport size: " + w + " x " + h );
    }

    //++++++++++++++++++ GUI responder methods ++++++++++++++++++++++++++++++++
    public void setAnimation( boolean value ) {
        animating = value;
        if ( value )
            animator.start();
        else
            animator.stop();
        glCanvas.repaint();
    }

    public void setAnimationSpeed( int value ) {
        animationSpeed = value;
        animator.stop();
        animator.setFPS( value );
        animator.start();
        glCanvas.repaint();
    }

    //+++++++++++++++++++  private local methods ++++++++++++++++++++++++++++++++++
    //--------------------- updateSpin ------------------------------
    /*
     * Rotation update each frame. Cause the angle to change slowly and redisplay
     */
    void updateSpin() {
        if ( spin ) {
            angle += deltaAng;
            if ( --stepsLeft == 0 ) {
                replication += deltaRep;
                if ( ( int ) revolution % 2 == 0 ) // revolution # odd => change offset
                    offset += deltaOff;
                stepsLeft = stepsPer90;
            }

            if ( angle >= 360.0f ) // have we gone all the way around (again)
            {
                revolution++;
                angle = 0;
                offset = 0;
                deltaRep = -deltaRep;
            }
        }
    }

    //---------------------- render ---------------------------------------

    /**
     * Do the actual drawing
     */
    private void render( GLAutoDrawable drawable ) {
        updateSpin();
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();

        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        myTexture.enable( gl );
        myTexture.bind( gl );

        // We'll now map the texture to a single
        //   opengl quad (quadrilateral)
        //
        /* Draw textured quad */
        gl.glTranslatef( 0.0f, 0.0f, -5.f );
        gl.glRotatef( angle, 0f, 1.0f, 0f );

        // We'll scale the quad to make it big enough, but
        //   let's make sure its aspect ratio matches that
        //   of the texture; that way if we map the texture
        //   to square pieces of the quad BEFORE scaling,
        //   the scaled result will not distort the texture.
        //
        float yscale = 1.75f;
        float xscale = yscale * x2yAspect;
        gl.glScalef( xscale, yscale, 2.0f );

        // The quad will be drawn in the z=0 plane and will
        //   go from (-1,1,0) to (1,1,0)
        gl.glBegin( GL2.GL_POLYGON );
        //gl.glBegin( GL2.GL_QUADS );
        // Lower left quad corner
        gl.glTexCoord2f( offset, offset );
        gl.glVertex3f( -1.0f, -1.0f, 0.0f );

        // Lower right quad corner
        gl.glTexCoord2f( replication + offset, offset );
        gl.glVertex3f( 1.0f, -1.0f, 0.0f );

        // Upper right quad corner
        gl.glTexCoord2f( replication + offset, replication + offset );
        gl.glVertex3f( 1.0f, 1.0f, 0.0f );

        // Upper left quad corner
        gl.glTexCoord2f( offset, replication + offset );
        gl.glVertex3f( -1.0f, 1.0f, 0.0f );
        gl.glEnd();

        myTexture.disable( gl );

    }

    //++++++++++++++++++++++++++++ main ++++++++++++++++++++++++++++++++++++++
    public static void main( String[] args ) {
        int winW = 800, winH = 600;
        //TextureDemo scene = new TextureDemo( winW, winH );
        TextureDemo scene = TextureDemo.getInstance();
    }
}
