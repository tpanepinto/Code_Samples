/**
* ThreeD.java - basic 3D JOGL demo
* 
* Derived from ThreeD.java, which was derived from Can Xiong code from 
*          Fall '12 and threeD.cpp demo
* 
* @author rdb
* @date   October 17, 2013
*-------------------------------------------
* History
* 09/25/14: Added scene label support
*/

import java.awt.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.gl2.GLUT;

//----------------- class variables -------------------------------
public class ThreeD extends JFrame 
{
    //-------------------- class variables ----------------------------
    private static int windowWidth  = 800;  // default size
    private static int windowHeight = 750;

    //----------------- instance variables -----------------------------
    private int width, height;      // current window size
    private SceneManager sceneManager = null;     
    private GLCanvas  glCanvas = null;

    //------------------ constructors ----------------------------------
    public ThreeD( int w, int h )
    {
        super( "ThreeD demo" );
        width = w; height = h;
        
        this.setSize( width, height );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        sceneManager = SceneManager.getInstance();
        
        setupOpenGL();
        
        ControlPanel controlPanel = ControlPanel.getInstance(); 
        controlPanel.addDrawPanel( glCanvas );                           
        this.add( controlPanel );
        
        this.setVisible( true );        
    }
    //--------------------- setupOpenGL( int win ) ---------------------
    /**
     * Set up the open GL drawing window
     */
    void setupOpenGL( )
    {
        GLProfile glp = GLProfile.getDefault();        
        GLCapabilities caps = new GLCapabilities( glp );
        glCanvas = new GLCanvas( caps );
        
        // When a GL event occurs, stell the canvas to send the event
        //    to the ThreeD object, which knows how to draw the scene.
        glCanvas.addGLEventListener( sceneManager );
        sceneManager.setCanvas( glCanvas );
        
        // This program doesn't need an animator since all image changes 
        //    occur because of interactions with the user and should
        //    get triggered as long as GLCanvas.repaint is called.
    }
    //++++++++++++++++++++++++++++ main ++++++++++++++++++++++++++++++++
    public static void main( String[] args )
    {
        ThreeD scene = new ThreeD( windowWidth, windowHeight );
    }
}
