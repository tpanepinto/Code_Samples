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

//-------------- import static ---------------------------------------
// These imports allow you to avoid prefixing static variables and methods
//    in the GL and GL2 classes with the class prefix.
// Hence  
//      GL2.gl.glMatrixMode( GL2.GL_MODELVIEW )
// can be written as:
//      gl.glMatrixMode( GL_MODELVIEW );

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;

public class Scene {
    //------------------ class variables ------------------------------
    //---- draw Axes flag ------------------------
    static boolean drawAxes;    // package access

    //------------------ instance variables ------------------------------
    //---- objects collection -------
    ArrayList<Object3D> objects;

    //----gluLookat parameters -------
    float eyeX, eyeY, eyeZ; // gluLookat eye position
    float lookX, lookY, lookZ; // gluLookat look position
    float upX, upY, upZ; // up vector
    boolean normalEn = true;
    //----gluPerspective parameters ----
    float viewAngle, aspectRatio, near, far;

    //------------------ Constructors ------------------------------------

    /**
     * Initialize any values, register callbacks
     */
    public Scene() {
        objects = new ArrayList<Object3D>();
        resetView();
        drawAxes = true;
    }

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
    void drawCoordinateAxes( GL2 gl ) {
        float scale = 1.8f;  // convenient scale factor for experimenting with size
        gl.glPushMatrix();

        //gl.glDisable( GLLightingFunc.GL_LIGHTING );
        gl.glScalef( scale, scale, scale );
        float[] origin = { 0, 0, 0 };

        float[] xaxis = { 1, 0, 0 };
        float[] yaxis = { 0, 1, 0 };
        float[] zaxis = { 0, 0, 1 };

        gl.glLineWidth( 3 );

        gl.glBegin( GL2.GL_LINES );
        {
            gl.glColor3f( 1, 0, 0 ); // X axis is red.
            gl.glVertex3fv( origin, 0 );
            gl.glVertex3fv( xaxis, 0 );
            gl.glColor3f( 0, 1, 0 ); // Y axis is green.
            gl.glVertex3fv( origin, 0 );
            gl.glVertex3fv( yaxis, 0 );
            gl.glColor3f( 0, 0, 1 ); // z axis is blue.
            gl.glVertex3fv( origin, 0 );
            gl.glVertex3fv( zaxis, 0 );
        }
        gl.glEnd();
        gl.glPopMatrix();
        //gl.glEnable( GLLightingFunc.GL_LIGHTING );
    }

    //---------------------------------------------------------------------
    public void display( GLAutoDrawable drawable ) {
        // Don't need to use the GLAutoDrawable, since it has been 
        //   saved in the JOGL class, but should we? 
        // Why don't we just get the GL2 instance and pass it to redraw?
        // 
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();                // Reset Projection Matrix

        // Only do perspective for now
        JOGL.glu.gluPerspective( viewAngle, aspectRatio, near, far );

        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();                // Reset ModelView Matrix

        JOGL.glu.gluLookAt( eyeX, eyeY, eyeZ,
                lookX, lookY, lookZ,
                upX, upY, upZ );

        gl.glClear( GL2.GL_DEPTH_BUFFER_BIT
                | GL2.GL_COLOR_BUFFER_BIT );

        //if( SceneManager.drawAxes() )
        //drawCoordinateAxes( gl );

        // create a vector iterator to access and draw the objects
        for ( Object3D obj : objects ) {
            obj.normalEnable = normalEn;
            obj.redraw( gl );
        }
        gl.glFlush();                      // send all output to display 
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
}