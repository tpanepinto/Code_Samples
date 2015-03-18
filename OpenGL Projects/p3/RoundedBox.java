import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * RoundedBox.java
 * Is a child of the Box class and is a representation of a RoundedBox in gl where
 * each vertex has it's on set of normals thus leading to shading on the box faces
 * @author tml62
 */
public class RoundedBox extends Box {

    public RoundedBox() {
        length = 1;
    }

    public RoundedBox( float l ) {
        length = l;
    }

    /**
     * Redraws the cube
     */
    @Override
    public void redraw() {

        float[] rgb1 = colors.get( 0 ).getComponents( null );
        float[] rgb2 = colors.get( 0 ).getComponents( null );
        float[] rgb3 = colors.get( 0 ).getComponents( null );


        if ( colors.size() >= 2 )
            rgb2 = colors.get( 1 ).getComponents( null );
        if ( colors.size() >= 3 )
            rgb3 = colors.get( 2 ).getComponents( null );


        JOGL.gl.glPushMatrix();
        //TRS stuff

        if ( textureSet )
            texturedCube();
        else {


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

            makeCube();

            JOGL.gl.glPopMatrix();
        }


    }

    //-------------------texturedCube------------------

    /**
     * Sets all of the texture features up then makes the cube
     */
    private void texturedCube() {

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

        makeTexturedCube();

        JOGL.gl.glPopMatrix();
        shapeTexture.disable( JOGL.gl );

    }
//----------------------makeCube-------------------------------

    /**
     * Assembles a cube without a texture
     */
    private void makeCube() {
        makeFront();
        makeBack();
        makeLeft();
        makeRight();
        makeTop();
        makeBottom();
    }
//-----------------makeTexturedCube---------------------------------

    /**
     * Assembles a cube with a texture
     */
    private void makeTexturedCube() {
        makeFrontTextured();
        makeBackTextured();
        makeLeftTextured();
        makeRightTextured();
        makeTopTextured();
        makeBottomTextured();
    }
    //----------makeTop-----------------------------

    /**
     * Makes the top of the cube
     */
    private void makeTop() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );


        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, 1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, 1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, 1f, 1f );
        JOGL.gl.glEnd();
    }
    //---------------------makeBottom---------------------

    /**
     * Makes the bottom of the cube
     */
    private void makeBottom() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );


        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, -1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, -1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, -1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, -1f, 1f );
        JOGL.gl.glEnd();
    }
    //-------------------------makeLeft------------------------

    /**
     * Makes the left of the cube
     */
    private void makeLeft() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );


        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, 1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, -1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, -1f, 1f );
        JOGL.gl.glEnd();
    }
    //-------------------------makeRight------------------------

    /**
     * Makes the right of the cube
     */
    private void makeRight() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );


        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, -1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, -1f, -1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glEnd();
    }

    /**
     * Makes the Front of the cube
     */
    private void makeFront() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );


        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, -1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, -1f, 1f );

        JOGL.gl.glEnd();
    }
    //-------------------------makeBack------------------------

    /**
     * Makes the back of the cube
     */
    private void makeBack() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );


        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, 1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, 1f, -1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( -1f, -1f, -1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );
        JOGL.gl.glNormal3f( 1f, -1f, -1f );


        JOGL.gl.glEnd();
    }
//-------------Textured Box Pieces--------------------------------

    //-------------------------makeTopTextured------------------------

    /**
     * Makes the top of the cube with a texture applied to the side
     */
    private void makeTopTextured() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );

        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 0f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, 1f, -1 );
        JOGL.gl.glTexCoord2f( 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, 1f, -1f );
        JOGL.gl.glTexCoord2f( 0f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 0f, 0f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glEnd();
    }
    //-------------------------makeBottomTextured------------------------

    /**
     * Makes the bottom of the cube with a texture applied to the side
     */
    private void makeBottomTextured() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );

        JOGL.gl.glNormal3f( 1f, -1f, 1f );
        JOGL.gl.glTexCoord2f( 0f, 0f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, -1f, -1f );
        JOGL.gl.glTexCoord2f( 0f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, -1f, -1f );
        JOGL.gl.glTexCoord2f( 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, -1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 0f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glEnd();
    }

    //-------------------------makeLeftTextured------------------------

    /**
     * Makes the left of the cube with a texture applied to the side
     */
    private void makeLeftTextured() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );

        JOGL.gl.glNormal3f( -1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, 1f, -1f );
        JOGL.gl.glTexCoord2f( 0f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, -1f, -1f );
        JOGL.gl.glTexCoord2f( 0f, 0f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, -1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 0f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glEnd();
    }
    //-------------------------makeRightTextured------------------------

    /**
     * Makes the right of the cube with a texture applied to the side
     */
    private void makeRightTextured() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );

        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 0f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, -1f, 1f );
        JOGL.gl.glTexCoord2f( 0f, 0f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, -1f, -1f );
        JOGL.gl.glTexCoord2f( 1f, 0f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glEnd();
    }
    //-------------------------makeFrontTextured------------------------

    /**
     * Makes the Front of the cube with a texture applied to the side
     */
    private void makeFrontTextured() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );

        JOGL.gl.glNormal3f( 1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );


        JOGL.gl.glNormal3f( -1f, 1f, 1f );
        JOGL.gl.glTexCoord2f( 0f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, -1f, 1f );
        JOGL.gl.glTexCoord2f( 0f, 0f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, -1f, 1f );
        JOGL.gl.glTexCoord2f( 1f, 0f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ) );


        JOGL.gl.glEnd();
    }
    //-------------------------makeBackTextured------------------------

    /**
     * Makes the back of the cube with a texture applied to the side
     */
    private void makeBackTextured() {
        JOGL.gl.glBegin( GL2.GL_POLYGON );

        JOGL.gl.glNormal3f( 1f, 1f, -1f );
        JOGL.gl.glTexCoord2f( 0f, 1f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, 1f, -1f );
        JOGL.gl.glTexCoord2f( 1f, 1f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( -1f, -1f, -1f );
        JOGL.gl.glTexCoord2f( 1f, 0f );
        JOGL.gl.glVertex3f( ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );

        JOGL.gl.glNormal3f( 1f, -1f, -1f );
        JOGL.gl.glTexCoord2f( 0f, 0f );
        JOGL.gl.glVertex3f( ( 0 + ( length / 2f ) ), ( 0 - ( length / 2f ) ), ( 0 - ( length / 2f ) ) );


        JOGL.gl.glEnd();
    }


}
