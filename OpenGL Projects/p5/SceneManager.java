/**
 * SceneManager: Manages all the scenes and initiates drawing of current scene.
 *               Implements the Singleton class pattern, but also uses a static
 *               interface to interact with ControlPanel and the Scene class
 *
 * @author rdb
 * @date 10/17/13
 *
 */

import java.util.*;

import java.awt.*;
import javax.media.nativewindow.util.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.gl2.GLUT;
import sun.nio.cs.ext.MacThai;

public class SceneManager implements GLEventListener {
    //------------------- class variables ---------------------------
    public static SceneManager me = null;
    private static boolean drawAxesFlag = true;
    private static ArrayList<Scene> allScenes;
    private static int curSceneIndex = -1;
    private static Scene curScene = null;
    private static float[] diffuseLight;
    private static float[] diffusePointLight;
    private static float[] diffuseDirectLight;
    private static float[] ambientPointLight;
    private static float[] ambientDirectLight;

    //private static
    private static GLCanvas canvas = null;
    private static ControlPanel cp = null;
    private static String button = "Scale";
    private static boolean light1Set = true;
    private static boolean light2Set = true;
    private static String lightButton = "Point Light Color";

    //------------------- instance variables ------------------------

    // For testing, we'll keep pointers to a bunch of created objects in a vector
    //    also. This makes it easy to just select previously created objects
    //    for multiple scenes, or to copy them and change transformations.
    private ArrayList<Object3D> someObjects;


    //------------------ constructor  --------------------------------
    public static SceneManager getInstance() {
        if ( me == null )
            me = new SceneManager();
        return me;
    }
    //-------SceneManager-------------------------------------

    private SceneManager() {
        allScenes = new ArrayList<Scene>();
        someObjects = new ArrayList<Object3D>();
        //lightColor = new Color( 0.7f, 0.7f, 0.7f );

        diffuseLight = new float[]{ 0.7f, 0.7f, 0.7f, 1f };
        diffuseDirectLight = new float[]{ 0.7f, 0.7f, 0.7f, 1f };
        diffusePointLight = new float[]{ 0.7f, 0.7f, 0.7f, 1f };

        ambientDirectLight = new float[]{ 0.3f, 0.3f, 0.3f, 1f };
        ambientPointLight = new float[]{ 0.3f, 0.3f, 0.3f, 1f };

        cp = ControlPanel.getInstance();
    }
    //----------------- setCanvas -------------------------------------

    /**
     * Main program needs to tell sceneManager about  the GL canvas to use
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
    //------------------------- nextScene -------------------------------

    /**
     * update current scene to next one with wraparound
     * this is a static method to facilitate interaction with ControlPanel
     */
    public static void nextScene() {
        curSceneIndex++;
        if ( curSceneIndex >= allScenes.size() )
            curSceneIndex = 0;    // wrap around
        curScene = allScenes.get( curSceneIndex );
        cp.setSceneTitle( curScene.getTitle() );
        //GLContext context = JOGL.gl.getContext();
        //context.makeCurrent();
        //curScene.redraw();
        canvas.repaint();
    }
    //------------------------- prevScene -------------------------------

    /**
     * update current scene to previous one with wraparound
     * this is a static method to facilitate interaction with ControlPanel
     */
    public static void prevScene() {

        curSceneIndex--;
        if ( curSceneIndex < 0 )
            curSceneIndex = allScenes.size() - 1;
        curScene = allScenes.get( curSceneIndex );
        cp.setSceneTitle( curScene.getTitle() );
        canvas.repaint();
    }
    //------------------------- setDrawAxes( boolean ) ------------------

    /**
     * set the status of the axes drawing; called by ControlPanel
     */
    public static void setDrawAxes( boolean onoff ) {
        drawAxesFlag = onoff;
        canvas.repaint();
    }
    //------------------------- drawAxes() ------------------

    /**
     * retrieve axes drawing status; called by Scene
     */
    public static boolean drawAxes() {
        return drawAxesFlag;
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
        curScene = allScenes.get( curSceneIndex );
        if ( curScene != null ) {
            sceneInit();
            curScene.display( drawable );
        } else
            System.err.println( "??? Trying to draw null scene" );
    }

    //--------------------- dispose ------------------------------
    @Override
    public void dispose( GLAutoDrawable arg0 ) {
        // nothing to dispose of...
    }

    //--------------------- init ------------------------------
    @Override
    public void init( GLAutoDrawable drawable ) {
        JOGL.gl = drawable.getGL().getGL2();

        JOGL.gl.setSwapInterval( 0 );  // animation event occurs (maybe)
        //   only at end of frame draw.
        //  0 => render as fast as possible
        JOGL.glu = new GLU();
        JOGL.glut = new GLUT();
        sceneInit();

        makeDiffTextureScenes();
        makeSingleScenes();  // make scenes with at least 1 example of each Object3D

        makeMultiObjectScenes();


        nextScene();
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
        System.out.println( "Viewport size: " + w + " x " + h );
    }
//-------------Make methods-------------------------------------------------------------------

    public SweepSurface makeSweepSurface3(){
        ArrayList<Point2D> polyVert = new ArrayList<>(  );
        ArrayList<Point3> path = new ArrayList<>(  );
        ArrayList<Point2D> scale = new ArrayList<>(  );
        ArrayList<Float> rotation = new ArrayList<>(  );

        Point2D objPoint1 = new Point2D( 0f, -1.0f );
        Point2D objPoint2 = new Point2D( -0.95f, -0.31f );
        Point2D objPoint3 = new Point2D( -0.59f, 0.81f );
        Point2D objPoint4 = new Point2D( 0.59f, 0.81f );
        Point2D objPoint5 = new Point2D( 0.95f, -0.31f );

        polyVert.add( objPoint1 );
        polyVert.add( objPoint2 );
        polyVert.add( objPoint3 );
        polyVert.add( objPoint4 );
        polyVert.add( objPoint5 );

        Point3 pathPoint1 = new Point3( 0,0,-0.5f);
        Point3 pathPoint2 = new Point3( 0,0,-0.2f);
        Point3 pathPoint3 = new Point3( 0,0,0f);
        Point3 pathPoint4 = new Point3( 0,0,0.4f);

        path.add( pathPoint1 );
        path.add( pathPoint2 );
        path.add( pathPoint3 );
        path.add( pathPoint4 );

        Point2D scalar1 = new Point2D( 1f, 1f );
        Point2D scalar2 = new Point2D( 1f, 0.7f );
        Point2D scalar3 = new Point2D( 1f, 0.5f );
        Point2D scalar4 = new Point2D( 1f, 1f );

        scale.add( scalar1 );
        scale.add( scalar2 );
        scale.add( scalar3 );
        scale.add( scalar4 );

        rotation.add( 0f );
        rotation.add( 0f );
        rotation.add( 0f );
        rotation.add( 0f );



        return new SweepSurface( polyVert, path, scale, rotation );
    }



    //--------------------- sceneInit ------------------------

    /**
     * Initialize scene, including especially the lights
     */
    public static void sceneInit() {
        JOGL.gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );//  black
        JOGL.gl.glClearDepth( 1.0 );              // clears depth buffer
        JOGL.gl.glEnable( GL2.GL_DEPTH_TEST );        // Enable depth testing
        JOGL.gl.glShadeModel( GL2.GL_SMOOTH );        // Enable smooth color shading
        JOGL.gl.glEnable( GL2.GL_NORMALIZE );         // Make all surface normals unit len
        JOGL.gl.glEnable( GL2.GL_COLOR_MATERIAL );    // Current color used for material

        //lighting set up
        JOGL.gl.glEnable( GL2.GL_LIGHTING );
        JOGL.gl.glEnable( GL2.GL_LIGHT0 );
        if ( light1Set )
            JOGL.gl.glEnable( GL2.GL_LIGHT1 );
        else
            JOGL.gl.glDisable( GL2.GL_LIGHT1 );
        if ( light2Set )
            JOGL.gl.glEnable( GL2.GL_LIGHT2 );
        else
            JOGL.gl.glDisable( GL2.GL_LIGHT2 );


        //Set lighting intensity and color
        //GLfloat ambientLight[] = { 1f, 1f, 0.45f, 0.5f };
        float ambientLight[] = { 0.3f, 0.3f, 0.3f, 1f };
        JOGL.gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[]{ 0.2f, 0.2f, 0.2f, 1f }, 0 );
        JOGL.gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{ 0.2f, 0.2f, 0.2f, 1f }, 0 );


        //Set the light position
        //GLfloat lightPosition[] = {-1, 1, 1, 0};
        float lightPosition[] = { -1, 1, 1, 0 };
        //System.out.println("Scene Init");
        JOGL.gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0 );

        JOGL.gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambientPointLight, 0 );
        JOGL.gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffusePointLight, 0 );
        JOGL.gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{ -1.0f, 1.0f, 1.0f, 1.0f }, 0 );


        JOGL.gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_AMBIENT, ambientDirectLight, 0 );
        JOGL.gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_DIFFUSE, diffuseDirectLight, 0 );
        JOGL.gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_POSITION, new float[]{ 1.0f, 1.0f, 1.0f, 0 }, 0 );


    }
    //------------------------- changeEvent -----------------------------

    /**
     * change event for TRS parameters and the associated sliders
     */
    static void changeEvent( String id, int value ) {
        //curScene.changeEvent( id, button, value );

        float changeVal = ( ( float ) value ) / 360;
        if ( button.equals( "Rotate Scene" ) )
            curScene.sceneRotate( id, value );
        else if ( button.equals( "Translate" ) )
            curScene.sceneTranslate( id, changeVal );
        else
            curScene.changeEvent( id, button, value );

        canvas.repaint();
    }
    //-----------------changeLightColorEvent---------------------------------

    /**
     * Changes the light parameters to change the color of the light in the scene
     *
     * @param id
     * @param value
     */
    static void changeLightColorEvent( String id, int value ) {
        if ( lightButton.equals( "Point Light Color" ) ) //change point light parameters
        {
            switch ( id ) {
                case "r":
                    diffusePointLight[0] = ( value / 255.0f ) * 0.7f;
                    ambientPointLight[0] = ( value / 255.0f ) * 0.3f;
                    break;
                case "g":
                    diffusePointLight[1] = ( value / 255.0f ) * 0.7f;
                    ambientPointLight[1] = ( value / 255.0f ) * 0.3f;
                    break;
                case "b":
                    diffusePointLight[2] = ( value / 255.0f ) * 0.7f;
                    ambientPointLight[2] = ( value / 255.0f ) * 0.3f;
                    break;
            }
        } else if ( lightButton.equals( "Directional Light Color" ) ) //change directional light parameters
        {
            switch ( id ) {
                case "r":
                    diffuseDirectLight[0] = ( value / 255.0f ) * 0.5f;
                    ambientDirectLight[0] = ( value / 255.0f ) * 0.5f;

                    break;
                case "g":
                    diffuseDirectLight[1] = ( value / 255.0f ) * 0.5f;
                    ambientDirectLight[1] = ( value / 255.0f ) * 0.5f;
                    break;
                case "b":
                    diffuseDirectLight[2] = ( value / 255.0f ) * 0.5f;
                    ambientDirectLight[2] = ( value / 255.0f ) * 0.5f;
                    break;
            }
        }


        canvas.repaint();
    }
    //--------------lightRadioButtonChange

    /**
     * Sets the light that is going to be changed with the RGB Sliders
     *
     * @param id
     */
    static void lightRadioButtonChange( String id ) {
        lightButton = id;
    }
    //----------------lightChange---------------------------

    /**
     * changes the light that is turned on based on the chack boxs in the control panel
     *
     * @param id
     * @param set
     */
    static void lightChange( int id, boolean set ) {

        if ( id == 1 )
            light1Set = set;
        else if ( id == 2 )
            light2Set = set;

        canvas.repaint();

    }
   static void setTriangulation( boolean checked){
       curScene.setTri( checked );
       canvas.repaint();
   }
//----------radioButtonChange---------------------

    /**
     * changes the TRS settings based on the button selected
     *
     * @param id
     */
    static void radioButtonChange( String id ) {
        button = id;
        // setSliders();
    }

//--------------setSliders--------------------------

    /**
     * This method sets the sliders to the current value of the objects in the scene so there
     * is no bounce when using the sliders
     */
    static void setSliders() {

        if ( button.equals( "Translate" ) ) {
            cp.xSlider.setValue( ( int ) curScene.objects.get( 0 ).getX() * 360 );
            cp.ySlider.setValue( ( int ) curScene.objects.get( 0 ).getY() * 360 );
            cp.zSlider.setValue( ( int ) curScene.objects.get( 0 ).getZ() * 360 );

        } else if ( button.equals( "Rotate" ) ) {
            cp.xSlider.setValue( ( int ) curScene.objects.get( 0 ).getXAngle() );
            cp.ySlider.setValue( ( int ) curScene.objects.get( 0 ).getYAngle() );
            cp.zSlider.setValue( ( int ) curScene.objects.get( 0 ).getZAngle() );

        } else if ( button.equals( "Rotate Scene" ) ) {
            cp.xSlider.setValue( ( int ) curScene.angleX );
            cp.ySlider.setValue( ( int ) curScene.angleY );
            cp.zSlider.setValue( ( int ) curScene.angleZ );

        } else if ( button.equals( "Scale" ) ) {
            cp.xSlider.setValue( ( int ) ( curScene.objects.get( 0 ).getXSize() * 360 ) / 2 );
            cp.ySlider.setValue( ( int ) ( curScene.objects.get( 0 ).getXSize() * 360 ) / 2 );
            cp.zSlider.setValue( ( int ) ( curScene.objects.get( 0 ).getXSize() * 360 ) / 2 );

        }

    }

    //------------------------- makeSphereScenes --------------------------

    /**
     * make all the single scenes
     */
    void makeSingleScenes() {
        //----------------sphere scenes------------------------------------------
        Scene sweep = new Scene( "Box Sweep Object with flat shading" );
        Object3D sweep1 = new SweepSurface();

        sweep.addObject( sweep1 );
        addScene( sweep );

        Scene sweep2 = new Scene( "Box Sweep Object with smooth shading" );
        Object3D sweepo2 = new SweepSurface();
        Color c6 = new Color( 132, 183, 106 );
        sweepo2.setColor( c6 );
        sweepo2.setSmoothObj( true );
        sweep2.addObject( sweepo2 );
        addScene( sweep2 );


        Scene sweepScene2 = new Scene( "Sweep object with an arbitrary path" );

        SweepSurface sweepObj2 = new SweepSurface();
        Color c = new Color( 0, 0, 255 );
        sweepObj2.setColor( c );
        sweepObj2.setPath( makeArbPath() );
        sweepScene2.addObject( sweepObj2 );

        addScene( sweepScene2 );

        Scene sweepScene3 = new Scene( "Sweep Surface Pentagon made With Parametarized Constructor and scale applied to the second and third rib" );
        SweepSurface sweepObj3 = makeSweepSurface3();
        Color c2 = new Color( 43, 96, 207 );
        sweepObj3.setColor( c2 );
        sweepScene3.addObject( sweepObj3 );
        addScene( sweepScene3 );



        Scene sweepScene4 = new Scene( "Sweep object with rotation applied to the second and third rib" );

        SweepSurface sweepObj4 = new SweepSurface();
        Color c3 = new Color( 130, 130, 130 );
        sweepObj4.setColor( c3 );
        sweepObj4.setRotation( makeRot() );
        sweepScene4.addObject( sweepObj4 );

        addScene( sweepScene4 );


    }
//------------makeArbPath---------------------------
    /**
     * makes an arbitrary sweep path
     * @return
     */
    public ArrayList<Point3> makeArbPath(){
        ArrayList<Point3> tmpPathPoints = new ArrayList<>(  );

        Point3 pathPoint1 = new Point3( 0,0,0 );
        Point3 pathPoint2 = new Point3( 0,0.3f,0.2f );
        Point3 pathPoint3 = new Point3( 0.2f,0,0.3f );
        Point3 pathPoint4 = new Point3( 0,-0.4f,0.5f );

        tmpPathPoints.add( pathPoint1 );
        tmpPathPoints.add( pathPoint2 );
        tmpPathPoints.add( pathPoint3 );
        tmpPathPoints.add( pathPoint4 );
        return tmpPathPoints;
    }

    /**
     * Makes an array for a rotation
     * @return
     */

    public ArrayList<Float> makeRot(){
        ArrayList<Float> tmpRot = new ArrayList<>(  );

        tmpRot.add( 0f );
        tmpRot.add( 90.0f );
        tmpRot.add( 250.0f );
        tmpRot.add( 360.0f );

        return tmpRot;
    }

//------------------------- makeMultiObjectScenes --------------------------

    /**
     * make the Box and rounded box scenes
     */
    void makeMultiObjectScenes() {
        Scene sweepScene3 = new Scene( "Sweep Surface Pentagons the one on the negative x-axis has smooth shading applied and the one on the positive x-axis has flat shading applied" );
        SweepSurface sweepObj3 = makeSweepSurface3();
        Color c2 = new Color( 43, 96, 207 );
        sweepObj3.setColor( c2 );
        sweepObj3.setLocation(1.0f, 0 ,0 );
        sweepObj3.setSize( 0.5f, 0.5f, 0.5f );
        sweepScene3.addObject( sweepObj3 );



        SweepSurface sweepObj2 = makeSweepSurface3();
        Color c1 = new Color( 43, 96, 207 );
        sweepObj2.setColor( c1 );
        sweepObj2.setLocation( - 1.0f, 0, 0 );
        sweepObj2.setSize( 0.5f, 0.5f, 0.5f );
        sweepObj2.setSmoothObj( true );
        sweepScene3.addObject( sweepObj2 );


        addScene( sweepScene3 );

    }
    //----------------makeDiffTexturesScenes-----------------------

    /**
     * make the scenes that have differing textures in them
     * this is where the texture test scenes are
     */
    void makeDiffTextureScenes() {
        Scene sweep = new Scene( "Base Sweep Object with Wire Frame and Triangulation and texture applied" );
        Object3D sweep1 = new SweepSurface();
        sweep1.createTexture( "wood.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR_MIPMAP_LINEAR, GL2.GL_LINEAR );


        sweep.addObject( sweep1 );
        addScene( sweep );

        Scene sweepScene = new Scene( "Same object as before but with GL Linear Mapping" );
        Object3D sweep5 = new SweepSurface();
        sweep5.createTexture( "wood.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_LINEAR );


        sweepScene.addObject( sweep5 );
        addScene( sweepScene );


    }

    public void render() {

    }


}