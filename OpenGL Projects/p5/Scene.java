/**
 * Scene.java - a class to represent a scene: its objects and its view
 *
 * 10/16/13 rdb derived from Scene.cpp
 */

import java.util.*;
import java.io.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;
import javax.media.opengl.fixedfunc.*;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class Scene {
    //------------------ class variables ------------------------------
    //---- draw Axes flag ------------------------
    static boolean drawAxes;    // package access
    static boolean triangaliation = false;
    //------------------ instance variables ------------------------------
    //---- objects collection -------
    ArrayList<Object3D> objects;

    //----gluLookat parameters -------
    float eyeX, eyeY, eyeZ; // gluLookat eye position
    float lookX, lookY, lookZ; // gluLookat look position
    float upX, upY, upZ; // up vector

    //----gluPerspective parameters ----
    float viewAngle, aspectRatio, near, far;

    //---------------scene title

    String sceneTitle = null;

    //-----------------scene parameters--------------------
    float locX, locY, locZ = 0;
    float angleX, angleY, angleZ = 0;
    float scaleX, scaleY, scaleZ = 0;
    boolean sceneMultSphere = false;

    //------------------ Constructors ------------------------------------

    /**
     * Initialize any values, register  callbacks
     */
    public Scene( String title ) {
        objects = new ArrayList<Object3D>();
        resetView();
        drawAxes = true;
        sceneTitle = title;
    }
//-------------------getTitle--------------------------------

    /**
     * Gets the title of the scene
     */
    public String getTitle() {
        return sceneTitle;
    }
//---------------setTitle--------------------------------------

    /**
     * sets the scene title
     */
    public void setTitle( String title ) {
        sceneTitle = title;
    }

    /**
     * gets the change event from the sliders and changes the values accordingly
     */

    //----------- changeEvent -------------------------------------------
    static float delta = 0.1f;

    public void changeEvent( String id, String button, int value ) {

        if ( objects.size() > 0 ) {
            float changeVal = ( ( float ) value ) / 360;
            if ( button.equals( "Translate" ) ) {


                for ( int i = 0; i < objects.size(); i++ ) {
                    Object3D obj = objects.get( i );
                    obj.shapeTranslate( id, changeVal );
                }
            } else if ( button.equals( "Rotate Object" ) ) {

                for ( int i = 0; i < objects.size(); i++ ) {
                    Object3D obj = objects.get( i );
                    obj.shapeRotate( id, value );

                }

            } else if ( button.equals( "Scale" ) ) {


                for ( int i = 0; i < objects.size(); i++ ) {
                    Object3D obj = objects.get( i );
                    obj.shapeScale( id, ( ( Math.abs( changeVal ) ) * 2 ) );
                }
            }
        }


    }
    //-------------- resetView -----------------------------------------

    /**
     * restore the view to default settings
     */
    public void resetView() {
        setLookat( 10, 3, 10, // eye
                0, 0, 0,   // at
                0, 1, 0 ); // up

        setPerspective( 10, 1.33f, 0.1f, 100.0f ); //should calc windowWid / windowHt
    }

    //--------------------------------------------------------------------
    public void addObject( Object3D newObject ) {
        objects.add( newObject );
    }

    //--------------------------------------------------------------------
    public void clear() {
        objects.clear();
        redraw();
    }

    //---------------------------------------------------------------------

    /**
     * set lookat parameters
     */
    public void setLookat( float eyeX, float eyeY, float eyeZ,
                           float lookX, float lookY, float lookZ,
                           float upX, float upY, float upZ ) {
        this.eyeX = eyeX;
        this.eyeY = eyeY;
        this.eyeZ = eyeZ;
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
    }
    //---------------------------------------------------------------------

    /**
     * set perspective parameters
     */
    void setPerspective( float angle, float ratio, float near, float far ) {
        this.viewAngle = angle;
        this.aspectRatio = ratio;
        this.near = near;
        this.far = far;
    }

    //---------------- drawing coordinate axes -----------------------

    /**
     * Draw the world coord axes to help orient viewer.
     */
    void drawCoordinateAxes() {
        float scale = 1.8f;  // convenient scale factor for experimenting with size


        JOGL.gl.glDisable( GLLightingFunc.GL_LIGHTING );
        JOGL.gl.glScalef( scale, scale, scale );
        float[] origin = { 0, 0, 0 };

        float[] xaxis = { 1, 0, 0 };
        float[] yaxis = { 0, 1, 0 };
        float[] zaxis = { 0, 0, 1 };

        JOGL.gl.glLineWidth( 3 );

        JOGL.gl.glBegin( GL2.GL_LINES );
        JOGL.gl.glColor3f( 1, 0, 0 ); // X axis is red.
        JOGL.gl.glVertex3fv( origin, 0 );
        JOGL.gl.glVertex3fv( xaxis, 0 );
        JOGL.gl.glColor3f( 0, 1, 0 ); // Y axis is green.
        JOGL.gl.glVertex3fv( origin, 0 );
        JOGL.gl.glVertex3fv( yaxis, 0 );
        JOGL.gl.glColor3f( 0, 0, 1 ); // z axis is blue.
        JOGL.gl.glVertex3fv( origin, 0 );
        JOGL.gl.glVertex3fv( zaxis, 0 );
        JOGL.gl.glEnd();
        JOGL.gl.glPopMatrix();
        JOGL.gl.glEnable( GLLightingFunc.GL_LIGHTING );
    }

    //---------------------------------------------------------------------
    public void display( GLAutoDrawable drawable ) {
        //redraw( drawable ); 
        redraw();
    }
    //------------redraw----------------------------

    /**
     * redraws the current scene and applies the TRS transformations for the scene
     */
    public void redraw() {
        //JOGL.gl.glPushMatrix();
        JOGL.gl.glMatrixMode( GL2.GL_PROJECTION );
        JOGL.gl.glLoadIdentity();                // Reset The Projection Matrix


        // Only do perspective for now

        JOGL.glu.gluPerspective( viewAngle, aspectRatio, near, far );
        JOGL.gl.glMatrixMode( GL2.GL_MODELVIEW );

        JOGL.gl.glLoadIdentity();                // Reset The Projection Matrix

        // System.out.println("REDRAW");
        JOGL.glu.gluLookAt( eyeX, eyeY, eyeZ,
                lookX, lookY, lookZ,
                upX, upY, upZ );


        JOGL.gl.glClear( GL2.GL_DEPTH_BUFFER_BIT
                | GL2.GL_COLOR_BUFFER_BIT );

        if ( SceneManager.drawAxes() )
            drawCoordinateAxes();

        // create a vector iterator to access and draw the objects
        JOGL.gl.glPushMatrix();
        for ( Object3D obj : objects ) {

                JOGL.gl.glPushMatrix();
                JOGL.gl.glTranslatef( locX, locY, locZ );
                JOGL.gl.glRotatef( angleX, 1.0f, 0, 0 );
                JOGL.gl.glRotatef( angleY, 0, 1f, 0 );
                JOGL.gl.glRotatef( angleZ, 0, 0, 1f );
            obj.setWireFrameOn( triangaliation );
            obj.redraw();

        }

        JOGL.gl.glPopMatrix();
        JOGL.gl.glFlush();                         // send all output to display 
    }

    //------------------sceneTranslate-------------------

    /**
     * Sets the xyz location for the translation in the redraw function
     *
     * @param id
     * @param xyz
     */
    public void sceneTranslate( String id, float xyz ) {
        if ( id.equals( "x" ) )
            locX = xyz;
        else if ( id.equals( "y" ) )
            locY = xyz;
        else if ( id.equals( "z" ) )
            locZ = xyz;
        else
            System.err.println( "There is no ID that matches: " + id );


    }

    //-------------------sceneRotate-------------------

    /**
     * Sets the xyz angle for the rotation in the redraw function
     */
    public void sceneRotate( String id, float xyz ) {
        if ( id.equals( "x" ) )
            angleX = xyz;
        else if ( id.equals( "y" ) )
            angleY = xyz;
        else if ( id.equals( "z" ) )
            angleZ = xyz;
        else
            System.err.println( "There is no ID that matches: " + id );


    }

    public void sceneScale( String id, float xyz ) {
        if ( id.equals( "x" ) )
            scaleX = xyz;
        else if ( id.equals( "y" ) )
            scaleY = xyz;
        else if ( id.equals( "z" ) )
            scaleZ = xyz;
        else
            System.err.println( "There is no ID that matches: " + id );
    }
    //---------------- setDrawAxes( int )  -----------------------

    /**
     * 0 means don't draw the axes
     * non-zero means draw them
     */
    //-------------------------------------------
    public void setDrawAxes( boolean yesno ) {
        drawAxes = yesno;
    }

    //---------------- setDrawAxes( int )  -----------------------

    /**
     * 0 means don't draw the axes
     * non-zero means draw them
     */
    //-------------------------------------------
    public void setTri( boolean yesno ) {
        triangaliation = yesno;
    }
}