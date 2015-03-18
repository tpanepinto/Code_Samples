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

public class SceneManager implements GLEventListener
{
    //------------------- class variables ---------------------------
    public static SceneManager me = null;
    private static boolean drawAxesFlag = true;
    private static ArrayList<Scene> allScenes; 
    private static int curSceneIndex = -1;
    private static Scene curScene = null;
    private static float[] diffuseLight;
    private static GLCanvas canvas = null;
    private static ControlPanel cp = null;
    private static String button = "S";
    //------------------- instance variables ------------------------
    
    // For testing, we'll keep pointers to a bunch of created objects in a vector
    //    also. This makes it easy to just select previously created objects
    //    for multiple scenes, or to copy them and change transformations.
    private ArrayList<Object3D> someObjects;
    
    
    //------------------ constructor  --------------------------------
    public static SceneManager getInstance()
    {
        if ( me == null )
            me = new SceneManager();
        return me;
    }
    private SceneManager()
    {
        allScenes = new ArrayList<Scene>();
        someObjects = new ArrayList<Object3D>(); 
        //lightColor = new Color( 0.7f, 0.7f, 0.7f );
        diffuseLight = new float[] { 0.0f, 0.7f, 0.7f, 1f };
        cp = ControlPanel.getInstance();
    }
    //----------------- setCanvas -------------------------------------
    /**
     * Main program needs to tell sceneManager about  the GL canvas to use
     */
    public void setCanvas( GLCanvas glCanvas )
    {
        canvas = glCanvas;
    }
    //------------------------- addScene -------------------------------
    /**
     * Add a new scene to the scene collection
     */
    private void addScene( Scene newScene ) 
    {
        allScenes.add( newScene );
        curScene = newScene;
        curSceneIndex = allScenes.size() - 1;
    }
    //------------------------- nextScene -------------------------------
    /**
     * update current scene to next one with wraparound
     * this is a static method to facilitate interaction with ControlPanel
     */
    public static void nextScene() 
    {
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
    public static void prevScene()
    {

        curSceneIndex--;
        if (curSceneIndex < 0)
            curSceneIndex = allScenes.size()-1;
        curScene = allScenes.get( curSceneIndex );
        cp.setSceneTitle( curScene.getTitle() );
        canvas.repaint();
    }
    //------------------------- setDrawAxes( boolean ) ------------------
    /**
     * set the status of the axes drawing; called by ControlPanel
     */
    public static void setDrawAxes( boolean onoff ) 
    {
        drawAxesFlag = onoff;
        canvas.repaint();
    }
    //------------------------- drawAxes() ------------------
    /**
     * retrieve axes drawing status; called by Scene
     */
    public static boolean drawAxes() 
    {
        return drawAxesFlag;
    }
    //------------ setLightColor ----------------------
    public static void setLightColor( Color c )
    {
        diffuseLight[ 0 ] = c.getRed() / 255.0f;
        diffuseLight[ 1 ] = c.getGreen()  / 255.0f;
        diffuseLight[ 2 ] = c.getBlue() / 255.0f;
        System.out.println( "New Color: " + c );
        sceneInit();
        canvas.repaint();
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
        curScene = allScenes.get( curSceneIndex );
        if ( curScene != null )
        {
            //sceneInit();
            curScene.display( drawable );
        }
        else
            System.err.println( "??? Trying to draw null scene" );
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
        
        JOGL.gl.setSwapInterval( 0 );  // animation event occurs (maybe)
                                       //   only at end of frame draw.
                                       //  0 => render as fast as possible
        JOGL.glu = new GLU();
        JOGL.glut = new GLUT();
        sceneInit();
        
        makeSimpleScenes();  // make scenes with at least 1 example of each Object3D
        makeMultiObjectScenes();
        nextScene();
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
    }

    //----------------- makeBox ----------------------------
    /**
     *  A convenience function to create a Sphere with a uniform scale,
     *    a specified color, and at 0,0,0.
     */
    Box makeBox( float scale, Color c )
    {
        Box box = new Box();
        box.setColor( c );
        box.setLocation( 0, 0, 0 );
        box.setSize( scale, scale, scale );
        return box;
    }
    //----------------- makeBall ----------------------------
    Sphere makeBall( float scale, Color c )
    {
        Sphere sp = new Sphere();
        sp.setLocation( 0, 0, 0 );
        sp.setSize( scale, scale, scale );
        sp.setColor( c );
        return sp;
    }
    //--------------------- sceneInit ------------------------
    /**
     * Initialize scene, including especially the lights
     */
    public static void sceneInit()
    {
        JOGL.gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );//  black
        JOGL.gl.glClearDepth( 1.0 );              // clears depth buffer
        JOGL.gl.glEnable( GL2.GL_DEPTH_TEST );        // Enable depth testing
        JOGL.gl.glShadeModel( GL2.GL_SMOOTH );        // Enable smooth color shading
        JOGL.gl.glEnable( GL2.GL_NORMALIZE );         // Make all surface normals unit len
        JOGL.gl.glEnable( GL2.GL_COLOR_MATERIAL );    // Current color used for material
        
        //lighting set up
        JOGL.gl.glEnable( GL2.GL_LIGHTING );
        JOGL.gl.glEnable( GL2.GL_LIGHT0 );
        
        //Set lighting intensity and color
        //GLfloat ambientLight[] = { 1f, 1f, 0.45f, 0.5f };
        float ambientLight[] = { 0.3f, 0.3f, 0.3f, 1f };
        JOGL.gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0 );
        
        /****
        float diffuseLight[] = { lightColor.getRed(), 
                                 lightColor.getGreen(), 
                                 lightColor.getBlue(), 
                                 1f };
        /**/
        JOGL.gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0 );
        System.out.println( "DLight: <" + diffuseLight[ 0 ] + ", " 
                                       + diffuseLight[ 1 ] + ", " 
                                       + diffuseLight[ 2 ] + ">" );
        
        //Set the light position
        //GLfloat lightPosition[] = {-1, 1, 1, 0};
        float lightPosition[] = {-1, 1, 1, 0};
        JOGL.gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0 );
    }
    //------------------------- changeEvent -----------------------------
    static void changeEvent( String id, int value )
    {
        //curScene.changeEvent( id, button, value );

        float changeVal = ((float) value) / 360;
        if (button.equals("T"))
            curScene.sceneTranslate(id, changeVal);
        else if ( button.equals("R"))
            curScene.sceneRotate(id, value);
        else if ( button.equals("S"))
            curScene.sceneScale(id, ((Math.abs(changeVal))*2));



       // GLContext context = JOGL.gl.getContext();
        //context.makeCurrent();
        //curScene.sceneTransform();

        canvas.repaint();
    }
    static void radioButtonChange( String id)
    {
        button = id;
    }
    private void setSliders(){

        if (button.equals("T"))
        {
            //cp.xSlider.setValue(curScene.);
        }
    }

    //------------------------- makeSimpleScenes --------------------------
    /**
     *  make all one object scenes
     */
    void makeSimpleScenes()
    {
        Ring goldRing = new Ring();
        goldRing.setColor(new Color(43, 96, 207));
        someObjects.add(goldRing);

        Scene ringScene = new Scene("Golden Ring");
        ringScene.addObject(goldRing);

        addScene(ringScene);

        Ring goldRingRot = new Ring();
        goldRingRot.setColor(new Color(239, 242, 241));
        goldRingRot.shapeRotate("x", 90);
        someObjects.add(goldRingRot);

        Scene ringRotScene = new Scene("Golden Ring Rotated");
        ringRotScene.addObject(goldRingRot);

        addScene(ringRotScene);


        Ring goldRingRotSc = new Ring();
        goldRingRotSc.setColor(new Color(239, 242, 241));
        goldRingRotSc.shapeRotate("x", 90);
        goldRingRotSc.shapeScale("y", 2.0f);
        someObjects.add(goldRingRotSc);

        Scene ringRotScalScene = new Scene("Golden Ring Rotated Scaled");
        ringRotScalScene.addObject(goldRingRotSc);

        addScene(ringRotScalScene);


        Contest contest = new Contest();
        contest.setColor(new Color(43, 96, 207));
        someObjects.add(contest);

        Scene contestScene = new Scene("Cake Contest");
        contestScene.addObject(contest);
        addScene(contestScene);

        Contest contestRotTrans = new Contest();
        contestRotTrans.setColor(new Color(43, 96, 207));
        contestRotTrans.shapeRotate("y",90);
        contestRotTrans.shapeTranslate("x", 0.5f);
        someObjects.add(contestRotTrans);

        Scene contestRotTransScene = new Scene("Cake Contest Rotated and translated");
        contestRotTransScene.addObject(contestRotTrans);
        addScene(contestRotTransScene);

        Car car = new Car();
        car.setColor(new Color(43, 96, 207));
        someObjects.add(car);

        Scene carScene = new Scene("Car");
        carScene.addObject(car);
        addScene(carScene);


        Cake cake = new Cake();
        cake.setColor(new Color(62, 128, 46));
        someObjects.add(cake);

        Scene cakeScene = new Scene("cake");
        cakeScene.addObject(cake);
        addScene(cakeScene);

        Box box1 = makeBox( 1, new Color( 1f, 0f, 1f ));  //unit magenta box
        someObjects.add( box1 );  // save it for future use
        
        Scene box1Scene = new Scene("Small Cyan Box");
        box1Scene.addObject( box1 );

        addScene( box1Scene );
        
        Box box2 = makeBox( 0.5f, new Color( 0f, 1f, 1f )); // smaller cyan box
        box2.setRotate( 45, 0f, 0f, 1f );
        someObjects.add( box2 );  // save it for future use
        
        Scene box2Scene = new Scene("Bigger Box");
        box2Scene.addObject( box2 );
        addScene( box2Scene );

        Box box2dup = makeBox( 0.5f, new Color( 0f, 1f, 1f ));
        box2dup.shapeRotate( "z", 45 );
        someObjects.add(box2dup);  // save it for future use

        Scene box2dupScene = new Scene("Bigger Box");
        box2dupScene.addObject( box2dup );
        //box2dupScene.sceneTranslate(0.5f,0,0);
        addScene( box2dupScene );



     //-------------------------------------
        Sphere sp = makeBall( 0.45f, new Color( 0.8f, 0.8f, 0f )); // yellow ball
        someObjects.add( sp );  // save it for future use
        
        Scene ballScene = new Scene("Yellow Ball");
        ballScene.addObject( sp );
        addScene( ballScene );
//------------------Cones----------------------
        Cone cone = new Cone();
        someObjects.add(cone);

        Scene coneScene = new Scene("Cone Scene");
        coneScene.addObject(cone);
        addScene(coneScene);
//----------------cone-------------------------
        Cone cone2 = new Cone();
        cone2.setLocation(0.2f,0.0f,0.4f);
        someObjects.add(cone2);

        Scene coneScene2 = new Scene("Cone Scene new Coordinates");
        coneScene2.addObject(cone2);
        addScene(coneScene2);
//-----------------------------------------------------
        Cone cone3 = new Cone();
        cone3.shapeRotate("x", 90);
        someObjects.add(cone3);

        Scene coneScene3 = new Scene("Cone Scene Rotate");
        coneScene3.addObject(cone3);
        addScene(coneScene3);

    }
//------------------------- makeMultiObjectScenes --------------------------
    /**
     *  make all one object scenes
     */
    void makeMultiObjectScenes()
    {
        Scene multi1 = new Scene("Multi Scene");
        Object3D box = makeBox( 1, new Color( 1f, 0f, 1f )); // magenta
        box.setLocation( 1f, 0f, 0f );
        box.setSize( 0.4f, 0.4f, 0.4f );
        multi1.addObject( box );
        
        Object3D box2 = makeBox( 0.5f, new Color( 0f, 1f, 1f )); // cyan
        box2.setLocation( 0f, 0f, 1f );
        multi1.addObject( box2 );
        
        addScene( multi1 );

    }

    public void render(){

    }



}