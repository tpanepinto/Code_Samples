/**
 * SceneManager: Manages the scenes and initiates drawing of current scene.
 *               Implements the Singleton class pattern, but uses a static
 *               interface to interact with GUI and Scene classes.
 *
 * @author rdb
 * @date 10/17/13
 *
 * 10/28/14 - import static opengl classes, so openGL constants don't
 *            need the GL2. prefix (in addition to the GL_ start)
 *          - Use GLAutoDrawable to access gl context, rather than JOGL
 *
 */

import java.util.*;

import java.awt.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.gl2.GLUT;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;

public class SceneManager implements GLEventListener {
    //------------------- class variables ---------------------------
    public static SceneManager me = null;
    private static boolean drawAxesFlag = true;
    private static boolean light0Flag = true;
    private static boolean glslFlag = true;

    private static ArrayList<Scene> allScenes;
    private static int curSceneIndex = -1;
    private static Scene curScene = null;
    private static float[] diffuseLight;
    private static GLCanvas canvas = null;

    //private static GL2  gl = null;   // Do we need this as global??

    //------------------- instance variables ------------------------

    // For testing, keep pointers to the created objects in a list.
    //    This makes it easy to just select previously created objects
    //    for multiple scenes, or to copy them and change transformations.
    private ArrayList<Object3D> someObjects;


    //------------------ constructor  --------------------------------
    public static SceneManager getInstance() {
        if ( me == null )
            me = new SceneManager();
        return me;
    }

    private SceneManager() {
        allScenes = new ArrayList<Scene>();
        someObjects = new ArrayList<Object3D>();
        diffuseLight = new float[]{ 0.7f, 0.7f, 0.7f, 1f };
    }
    //----------------- setCanvas -------------------------------------

    /**
     * Main program needs to tell sceneManager about the GL canvas to use
     */
    public void setCanvas( GLCanvas glCanvas ) {
        canvas = glCanvas;
    }
    //------------------------- addScene -------------------------------

    /**
     * Add a new scene to the scene collection
     */
    private void addScene( Scene newScene ) {
        allScenes.add( newScene );
        curScene = newScene;
        curSceneIndex = allScenes.size() - 1;
    }
    //------------------------- nextScene ------------------------------

    /**
     * update current scene to next one with wraparound
     * this is a static method to facilitate interaction with GUI
     */
    public static void nextScene() {
        curSceneIndex++;
        if ( curSceneIndex >= allScenes.size() )
            curSceneIndex = 0;    // wrap around
        canvas.repaint();
    }
    //------------------------- setDrawAxes( boolean ) -----------------

    /**
     * set the status of the axes drawing; called by GUI
     */
    public static void setDrawAxes( boolean onoff ) {
        curScene.normalEn = onoff;
        canvas.repaint();
    }
    //------------------------- setLight0( boolean ) ------------------

    /**
     * set the status of the axes drawing; called by GUI
     */
    public static void setLight0( boolean onoff ) {
        System.out.println( "light 0: " + onoff );
        light0Flag = onoff;
        canvas.repaint();
    }
    //------------------------- setGLSL( boolean ) ------------------

    /**
     * set the status of the glsl drawing; called by GUI
     */
    public static void setGLSL( boolean onoff ) {
        System.out.println( "glsl: " + onoff );
        glslFlag = onoff;
        canvas.repaint();
    }
    //------------------------- glslDraw() ------------------

    /**
     * retrieve glsl drawing status
     */
    public static boolean glslDraw() {
        return glslFlag;
    }
    //------------------------- drawAxes() ------------------

    /**
     * retrieve axes drawing status; called by Scene
     */
    public static boolean drawAxes() {
        return drawAxesFlag;
    }

    //------------ setLightColor ----------------------
    public static void setLightColor( Color c ) {
        c.getColorComponents( diffuseLight );
        canvas.repaint();
    }

    //------------ getLightColor ----------------------
    public static Color getLightColor() {
        return new Color( diffuseLight[ 0 ], diffuseLight[ 1 ],
                diffuseLight[ 2 ], diffuseLight[ 3 ] );
    }
    //+++++++++++++++  GLEventListener override methods ++++++++++++++++
    //-------------------- display -------------------------------------

    /**
     * Required method for GLEventListener interface.
     * In this framework, the display method is responsible for setting
     * up the projection specification, but the "render" method
     * is responsible for the View and Model specifications.
     * <p/>
     * This display method is reasonably application-independent;
     * It defines a pattern that can be reused with the exception
     * of the specifying the actual objects to render.
     */
    public void display( GLAutoDrawable drawable ) {
        curScene = allScenes.get( curSceneIndex );
        if ( curScene != null ) {
            sceneInit( drawable.getGL().getGL2() );
            curScene.display( drawable );
        } else
            System.err.println( "??? Trying to draw null scene" );
    }
    //--------------------- dispose ------------------------------    

    /**
     * Required method for GLEventListener interface.
     */
    public void dispose( GLAutoDrawable arg0 ) {
        // nothing to dispose of...
    }

    //--------------------- init ------------------------------

    /**
     * Required method for GLEventListener interface.
     */
    public void init( GLAutoDrawable drawable ) {
        System.out.println( "SceneManager.init" );
        GL2 gl = drawable.getGL().getGL2();
        gl.setSwapInterval( 1 );  // animation event occurs (maybe)
        //   only at end of frame draw.
        //  0 => render as fast as possible
        JOGL.init( drawable );

        Object3D.shaderProgram = buildShaders( gl );
        sceneInit( gl );

        makeSimpleScenes();          // make some simple scenes
        makeMultiObjectScenes();     // more complex scenes
        nextScene();
    }
    //--------------------- reshape ------------------------------------

    /**
     * Required method for GLEventListener interface.
     * Window has been resized, readjust internal information
     */
    public void reshape( GLAutoDrawable drawable, int x, int y, int w, int h ) {
        System.out.println( "reshape" );
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport( 0, 0, w, h );
        System.out.println( "Viewport size: " + w + " x " + h );

    }

    //-------------------- buildShaders --------------------------------

    /**
     * Open, compile and link the vertex and fragment shaders and build
     * the composite shader "program" to be used for this simple app.
     */
    int buildShaders( GL2 gl ) {
        ShaderControl shader = new ShaderControl();
        shader.fsrc = shader.loadShader( "flat.fsh" );    // fragment code
        shader.vsrc = shader.loadShader( "passThru.vsh" ); // vertex code

        shader.init( gl );
        int shaderProgram = shader.useShader( gl );
        return shaderProgram;
    }
    //----------------- makeBox ---------------------------------------

    /**
     * A convenience function to create a Sphere with a uniform scale,
     * a specified color, and at 0,0,0.
     */
    Box makeBox( float scale, Color c, boolean p ) {
        Box box = new Box( p );
        box.setColor( c );
        box.setLocation( 0, 0, 0 );
        box.setSize( scale, scale, scale );
        return box;
    }

    //----------------- makeBall --------------------------------------
    Sphere makeBall( float scale, Color c ) {
        Sphere sp = new Sphere( 6, new float[]{ 0, 0, 1 }, true );
        sp.setLocation( 0, 0, 0 );
        sp.setSize( scale, scale, scale );
        sp.setColor( c );
        return sp;
    }
    //--------------------- sceneInit ------------------------

    /**
     * Initialize scene, including especially the lights
     */
    public static void sceneInit( GL2 gl ) {
        //deleted all lighting stuff

        gl.glClearColor( 0.5f, 0.5f, 0.5f, 1.0f );//  black
        gl.glClearDepth( 1.0 );              // clears depth buffer
        gl.glEnable( GL2.GL_DEPTH_TEST );    // Depth testing
        //gl.glShadeModel( GL2.GL_SMOOTH );    // Smooth color shading
        //gl.glEnable( GL2.GL_NORMALIZE );     // make normls unit len


    }

    //------------------------- changeEvent -----------------------------
    static void changeEvent( String id, int value ) {
        if ( id.equals( "l" ) ) {
            // The light slider goes from 0 to 100, map this to 0..1
            // change light's Red component
            diffuseLight[ 0 ] = value / 100.0f;
        } else {
            curScene.changeEvent( id, "Rotate Object", value );
        }
        canvas.repaint();
    }

    //------------------------- makeSimpleScenes --------------------------

    /**
     * make all one object scenes
     */
    void makeSimpleScenes() {
        Box box1 = makeBox( 1, new Color( 1f, 0f, 1f ), false );  //unit magenta box
        someObjects.add( box1 );  // save it for future use

        Scene box1Scene = new Scene();
        box1Scene.addObject( box1 );
        addScene( box1Scene );

        Box box2 = makeBox( 0.5f, new Color( 0f, 1f, 1f ), true ); // small cyan box
        box2.setRotate( 45, 0f, 0f, 1f );
        someObjects.add( box2 );  // save it for future use

        Scene box2Scene = new Scene();
        box2Scene.addObject( box2 );
        addScene( box2Scene );

        Sphere sp = makeBall( 0.3f, new Color( 0.8f, 0.8f, 0f ) ); // yellow
        someObjects.add( sp );  // save it for future use

        Scene ballScene = new Scene();
        ballScene.addObject( sp );
        addScene( ballScene );

        Pyramid py = new Pyramid(true); // yellow
        someObjects.add( py );  // save it for future use

        Scene pyScene = new Scene();
        pyScene.addObject( py );
        addScene( pyScene );

    }
    //------------------------- makeMultiObjectScenes -------------------

    /**
     * make all one object scenes
     */
    void makeMultiObjectScenes() {
        Scene multi1 = new Scene();
        Object3D box = makeBox( 1, new Color( 1f, 0f, 1f ), false ); // magenta
        box.setLocation( 1f, 0f, 0f );
        box.setSize( 0.4f, 0.4f, 0.4f );
        multi1.addObject( box );

        Object3D box2 = makeBox( 0.5f, new Color( 0f, 1f, 1f ), false ); // cyan
        box2.setRotate( 115f, 0f, 1f, 0f ); // look at back side
        box2.setLocation( 0f, 0f, 1f );
        multi1.addObject( box2 );

        addScene( multi1 );
    }
    //--------------------- main -----------------------------------

    /**
     * Convenience main to invoke the app
     */
    public static void main( String[] args ) {
        GLSL3D.main( args );
    }
}
