import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Sphere.java
 * Is a child of object3d and is a representation of a cylinder in gl
 * @author tml62
 */

public class Cylinder extends Object3D {
    //--------- instance variables -----------------
    float radius;
    float height;

    //------------- constructor -----------------------
    public Cylinder() {
        radius = 1f;
        height = 1f;
    }

    public Cylinder( float l, float h ) {
        radius = l;
        height = h;
    }

    //------------- drawPrimitives ---------------------------
    public void drawPrimitives() {
        //JOGL.glut.glutSolidCube( length );
    }
    //----------------redraw-------------------------------

    /**
     * draws the cylinder object and applies the TRS transformations
     */
    public void redraw() {

        GLUquadric cyl = JOGL.glu.gluNewQuadric();
        float[] rgb1 = colors.get( 0 ).getComponents( null );
        float[] rgb2 = colors.get( 0 ).getComponents( null );
        float[] rgb3 = colors.get( 0 ).getComponents( null );


        if ( colors.size() >= 2 )
            rgb2 = colors.get( 1 ).getComponents( null );
        if ( colors.size() >= 3 )
            rgb3 = colors.get( 2 ).getComponents( null );


        if ( textureSet )
            texturedCylinder();
        else {
            JOGL.gl.glPushMatrix();
            //TRS stuff
            JOGL.gl.glTranslatef( xLoc, yLoc, zLoc );
            JOGL.gl.glRotatef( dxRot + 90, 1.0f, 0, 0 );
            JOGL.gl.glRotatef( dyRot, 0, 1f, 0 );
            JOGL.gl.glRotatef( dzRot, 0, 0, 1f );
            JOGL.gl.glScalef( xSize, ySize, zSize );
            JOGL.gl.glColor3f( rgb1[0], rgb1[1], rgb1[2] );

            if ( mpset )
                mp.setMatProp();
            else
                mp.clearMatProp();

            JOGL.glu.gluCylinder( cyl, radius, radius, height, 40, 40 );
            JOGL.gl.glPopMatrix();
        }

    }
//---------------texturedCylinder-------------------

    /**
     * Creates a cylinder with an applied texture
     */
    private void texturedCylinder() {

        GLUquadric cyl = JOGL.glu.gluNewQuadric();
        JOGL.glu.gluQuadricDrawStyle( cyl, GLU.GLU_FILL );
        JOGL.glu.gluQuadricTexture( cyl, true );
        JOGL.glu.gluQuadricNormals( cyl, GLU.GLU_SMOOTH );

        shapeTexture.bind( JOGL.gl );
        shapeTexture.enable( JOGL.gl );

        JOGL.gl.glPushMatrix();
        //TRS stuff
        JOGL.gl.glTranslatef( xLoc, yLoc, zLoc );
        JOGL.gl.glRotatef( dxRot + 90, 1.0f, 0, 0 );
        JOGL.gl.glRotatef( dyRot, 0, 1f, 0 );
        JOGL.gl.glRotatef( dzRot, 0, 0, 1f );
        JOGL.gl.glScalef( xSize, ySize, zSize );
        JOGL.gl.glColor3f( 1, 1, 1 );

        if ( mpset )
            mp.setMatProp();
        else
            mp.clearMatProp();

        JOGL.glu.gluCylinder( cyl, radius, radius, height, 40, 40 );

        JOGL.gl.glPopMatrix();
        shapeTexture.disable( JOGL.gl );
        JOGL.glu.gluDeleteQuadric( cyl );
        JOGL.gl.glPopMatrix();
    }
}
