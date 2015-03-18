/**
 * GLSL3D.java - basic 3D JOGL demo
 * Derived from GLSL3D.java, which was derived from Can Xiong code from
 *          Fall '12 and threeD.cpp demo
 * @author rdb
 * @date October 17, 2013
 * 10/27/14 - modified to use glsl
 *-------------------------------------------
 */

import java.awt.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.gl2.GLUT;

//----------------- class variables -------------------------------
public class GLSL3D extends JFrame {
    //-------------------- class variables ----------------------------
    private static int windowWidth = 800;  // default size
    private static int windowHeight = 750;

    //----------------- instance variables -----------------------------
    private int width, height;      // current window size
    private SceneManager sceneManager = null;
    private GLCanvas glCanvas = null;

    //------------------ constructors ----------------------------------
    public GLSL3D( int w, int h ) {
        super( "GLSL3D demo" );
        width = w;
        height = h;

        this.setSize( width, height );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        sceneManager = SceneManager.getInstance();

        setupOpenGL();

        GUI controlPanel = GUI.getInstance();
        controlPanel.addDrawPanel( glCanvas );
        this.add( controlPanel );

        this.setVisible( true );
    }
    //--------------------- setupOpenGL( int win ) ---------------------

    /**
     * Set up the open GL drawing window
     */
    void setupOpenGL() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities( glp );
        glCanvas = new GLCanvas( caps );

        // When a GL event occurs, tell the canvas to send the event
        //    to the GLSL3D object, which knows how to draw the scene.
        glCanvas.addGLEventListener( sceneManager );
        sceneManager.setCanvas( glCanvas );
        // This program doesn't need an animator since all image changes 
        //    occur because of interactions with the user and should
        //    get triggered as long as GLCanvas.repaint method is called.
    }

    //++++++++++++++++++++++++++++ main ++++++++++++++++++++++++++++++++
    public static void main( String[] args ) {
        GLSL3D scene = new GLSL3D( windowWidth, windowHeight );
    }
}
