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

        makeSphereScenes();  // make scenes with at least 1 example of each Object3D
        makeCylinderScenes();
        makeMultiObjectScenes();
        makeDiffTextureScenes();

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

    //----------------- makeBox ----------------------------

    /**
     * A convenience function to create a Box with a uniform scale,
     * a specified color, and at 0,0,0.
     */
    Box makeBox( float scale, Color c ) {
        Box box = new Box();
        box.setColor( c );
        box.setLocation( 0, 0, 0 );
        box.setSize( scale, scale, scale );
        return box;
    }

    //----------------- makeBall ----------------------------

    /**
     * A convenience function to create a Sphere with a uniform scale,
     * a specified color, and at 0,0,0.
     */
    Sphere makeBall( float scale, Color c ) {

        Sphere ball = new Sphere();
        ball.setColor( c );
        ball.setLocation( 0, 0, 0 );
        ball.setSize( scale, scale, scale );

        return ball;
    }

    //----------------- makeRoundBox ----------------------------

    /**
     * A convenience function to create a Box with a uniform scale,
     * a specified color, and at 0,0,0.
     */
    RoundedBox makeRoundBox( float scale, Color c ) {
        RoundedBox box = new RoundedBox();
        box.setColor( c );
        box.setLocation( 0, 0, 0 );
        box.setSize( scale, scale, scale );
        return box;
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
     * make all the single sphere scenes
     */
    void makeSphereScenes() {
        //----------------sphere scenes------------------------------------------
        Scene sphereScene = new Scene( "Normal Sphere " );
        Object3D normSphere = makeBall( 0.5f, new Color( 1f, 0f, 1f ) ); // magenta
        sphereScene.addObject( normSphere );

        addScene( sphereScene );

        Scene ballScene = new Scene( "Regular Sphere with brass material properties" );
        Object3D ball = makeBall( 0.5f, new Color( 1f, 0f, 1f ) ); // magenta
        ball.setMatProp();
        ball.mp.setAmbient( new float[]{ 0.329412f, 0.223529f, 0.027451f, 1.0f } );
        ball.mp.setDiffuse( new float[]{ 0.780392f, 0.568627f, 0.113725f, 1.0f } );
        ball.mp.setSpecular( new float[]{ 0.992157f, 0.941176f, 0.807843f, 1.0f } );
        ball.mp.setShiny( 0.21794872f * 128 );
        ballScene.addObject( ball );

        addScene( ballScene );

        Scene globeScene = new Scene( "Sphere with Map " );
        Object3D globe = makeBall( 0.5f, new Color( 1f, 0f, 1f ) ); // magenta
        globe.createTexture( "mapImage.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_LINEAR );
        globeScene.addObject( globe );

        addScene( globeScene );


    }
    //-----------------------------makeCylinderScenes---------------------------------------------

    /**
     * make all the Cylinder sphere scenes
     */
    void makeCylinderScenes() {

        Scene cylinderScene = new Scene( "Cylinder Scene" );
        Object3D cyl = new Cylinder();
        cyl.setColor( new Color( 212, 175, 55 ) );

        cylinderScene.addObject( cyl );

        addScene( cylinderScene );

        Scene cylinderTexture = new Scene( "Cylinder with Map texture " );
        Object3D txtCyl = new Cylinder(); // magenta
        txtCyl.createTexture( "mapImage.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_LINEAR );
        txtCyl.setLocation( 0, 0, 0 );
        cylinderTexture.addObject( txtCyl );

        addScene( cylinderTexture );

        Scene mpCylScene = new Scene( "Regular Cylinder with Red Rubber material properties" );
        Object3D mpCyl = new Cylinder();
        mpCyl.setLocation( 0, 0.2f, 0 );
        mpCyl.setMatProp();
        mpCyl.mp.setAmbient( new float[]{ 0.05f, 0.0f, 0.0f, 1.0f } );
        mpCyl.mp.setDiffuse( new float[]{ 0.5f, 0.4f, 0.4f, 1.0f } );
        mpCyl.mp.setSpecular( new float[]{ 0.7f, 0.04f, 0.04f, 1.0f } );
        mpCyl.mp.setShiny( 0.078125f * 128.0f );

        mpCylScene.addObject( mpCyl );

        addScene( mpCylScene );


    }
//------------------------- makeMultiObjectScenes --------------------------

    /**
     * make the Box and rounded box scenes
     */
    void makeMultiObjectScenes() {
        Scene multi1 = new Scene( "Normal Red Box" );
        Object3D box = new Box();
        box.setLocation( 0f, 0f, 0f );
        box.setSize( 0.5f, 0.5f, 0.5f );
        //box.createTexture("mapImage.jpg", GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
        multi1.addObject( box );


        addScene( multi1 );

        Scene mpBoxScene = new Scene( "Box with Gold Material Properties" );
        Object3D mpBox = new Box();
        mpBox.setLocation( 0, 0.2f, 0 );
        mpBox.setMatProp();
        mpBox.mp.setAmbient( new float[]{ 0.24725f, 0.1995f, 0.0745f, 1.0f } );
        mpBox.mp.setDiffuse( new float[]{ 0.75164f, 0.60648f, 0.22648f, 1.0f } );
        mpBox.mp.setSpecular( new float[]{ 0.628281f, 0.555802f, 0.366065f, 1.0f } );
        mpBox.mp.setShiny( 0.4f * 128.0f );

        mpBoxScene.addObject( mpBox );

        addScene( mpBoxScene );


        Scene mpRBoxScene = new Scene( "Rounded Box with Silver Material Properties" );
        Object3D mpRBox = new RoundedBox();
        mpRBox.setLocation( 0, 0.2f, 0 );
        mpRBox.setMatProp();
        mpRBox.mp.setAmbient( new float[]{ 0.19225f, 0.19225f, 0.19225f, 1.0f } );
        mpRBox.mp.setDiffuse( new float[]{ 0.50754f, 0.50754f, 0.50754f, 1.0f } );
        mpRBox.mp.setSpecular( new float[]{ 0.508273f, 0.508273f, 0.508273f, 1.0f } );
        mpRBox.mp.setShiny( 0.4f * 128.0f );

        mpRBoxScene.addObject( mpRBox );

        addScene( mpRBoxScene );

    }
    //----------------makeDiffTexturesScenes-----------------------

    /**
     * make the scenes that have differing textures in them
     * this is where the texture test scenes are
     */
    void makeDiffTextureScenes() {
        Scene rboxTexture = new Scene( "Rounded Box Wood Texture Min Filter MIPMAP vs Linear" );
        Object3D txtRBox = new RoundedBox();
        txtRBox.createTexture( "wood.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR_MIPMAP_LINEAR, GL2.GL_LINEAR );
        txtRBox.setColor( new Color( 132, 183, 106 ) );
        txtRBox.setLocation( -1, 0, 0 );
        rboxTexture.addObject( txtRBox );


        Object3D txtRBox2 = new RoundedBox();
        txtRBox2.createTexture( "wood.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_LINEAR );
        txtRBox2.setColor( new Color( 132, 183, 106 ) );
        txtRBox2.setLocation( 1, 0, 0 );
        rboxTexture.addObject( txtRBox2 );

        addScene( rboxTexture );

        Scene textureSphereScene = new Scene( "Sphere Rock Texture MAG Filter Linear vs Nearest" );
        textureSphereScene.sceneMultSphere = true;
        Object3D textureSphere = new Sphere();
        textureSphere.createTexture( "rock.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_LINEAR );
        //textureSphere.setColor(new Color(132, 183, 106));
        textureSphere.setLocation( -1, 0, 0 );
        textureSphere.setSize( 0.5f, 0.5f, 0.5f );
        textureSphereScene.addObject( textureSphere );

        Object3D textureSphere2 = new Sphere();
        textureSphere2.createTexture( "rock.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_NEAREST );
        //textureSphere2.setColor(new Color( 132, 183, 106 ));
        textureSphere2.setLocation( 1, 0, 0 );
        textureSphere2.setSize( 0.5f, 0.5f, 0.5f );
        textureSphereScene.addObject( textureSphere2 );

        addScene( textureSphereScene );


        Scene BoxTextureScene = new Scene( "Box Wood Texture Wrap Clamp vs Repeat" );
        Object3D textureBox = new Box();
        textureBox.createTexture( "wood.jpg", GL2.GL_CLAMP, GL2.GL_CLAMP, GL2.GL_LINEAR, GL2.GL_LINEAR );
        textureBox.setLocation( -1, 0, 0 );
        textureBox.setSize( 0.5f, 0.5f, 0.5f );
        BoxTextureScene.addObject( textureBox );


        Object3D textureBox2 = new Box();
        textureBox2.createTexture( "wood.jpg", GL2.GL_REPEAT, GL2.GL_REPEAT, GL2.GL_LINEAR, GL2.GL_LINEAR );
        textureBox2.setLocation( 1, 0, 0 );
        textureBox2.setSize( 0.5f, 0.5f, 0.5f );
        BoxTextureScene.addObject( textureBox2 );

        addScene( BoxTextureScene );

    }

    public void render() {

    }


}