import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Sphere.java
 * Is a child of object3d and is a representation of a sphere in gl
 * @author tml62
 */

public class Sphere extends Object3D {
    //--------- instance variables -----------------
    float radius;

    //------------- constructor -----------------------
    public Sphere() {
        radius = 1;
    }

    public Sphere( float l ) {
        radius = l;
    }

    //------------- drawPrimitives ---------------------------
    public void drawPrimitives() {
        //JOGL.glut.glutSolidCube( length );
    }

    public void redraw() {

        GLUquadric sphere = JOGL.glu.gluNewQuadric();
        float[] rgb1 = colors.get( 0 ).getComponents( null );
        float[] rgb2 = colors.get( 0 ).getComponents( null );
        float[] rgb3 = colors.get( 0 ).getComponents( null );


        if ( colors.size() >= 2 )
            rgb2 = colors.get( 1 ).getComponents( null );
        if ( colors.size() >= 3 )
            rgb3 = colors.get( 2 ).getComponents( null );

        if ( textureSet )
            textureSphere();
        else {

            JOGL.gl.glEnable( GL2.GL_COLOR_MATERIAL );

            JOGL.gl.glDisable( GL2.GL_COLOR_MATERIAL );
            JOGL.gl.glPushMatrix();

            //TRS stuff
            JOGL.gl.glTranslatef( xLoc, yLoc, zLoc );
            JOGL.gl.glRotatef( dxRot + 90, 1.0f, 0, 0 );
            JOGL.gl.glRotatef( dyRot, 0, 1f, 0 );
            JOGL.gl.glRotatef( dzRot, 0, 0, 1f );
            JOGL.gl.glScalef( xSize, ySize, zSize );

            if ( mpset )
                mp.setMatProp();
            else
                mp.clearMatProp();

            JOGL.gl.glColor3f( rgb1[0], rgb1[1], rgb1[2] );
            JOGL.glu.gluSphere( sphere, radius, 20, 20 );

            JOGL.gl.glPopMatrix();

        }

    }
//--------------textureShape--------------------

    /**
     * creates a sphere with a texture applied to it
     */
    private void textureSphere() {


        GLUquadric sphere = JOGL.glu.gluNewQuadric();
        JOGL.glu.gluQuadricDrawStyle( sphere, GLU.GLU_FILL );
        JOGL.glu.gluQuadricTexture( sphere, true );
        JOGL.glu.gluQuadricNormals( sphere, GLU.GLU_SMOOTH );

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

        JOGL.glu.gluSphere( sphere, radius, 20, 20 );

        JOGL.gl.glPopMatrix();

        shapeTexture.disable( JOGL.gl );
        JOGL.glu.gluDeleteQuadric( sphere );
        JOGL.gl.glPopMatrix();
    }
}
