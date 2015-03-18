import java.awt.*;

/**
 * Created by tim on 10/11/14.
 *
 * a cake that is made of three cylinders
 */
public class Cake extends Object3D {

    private float height, radius;
    public Cake()
    {
        height = 0.3f;
        radius =0.2f;
    }

    @Override
    protected void drawPrimitives() {

    }


    @Override
    public void redraw(){



        float[] rgb1 = colors.get( 0 ).getComponents( null );
        float[] rgb2 = colors.get( 0 ).getComponents( null );
        float[] rgb3 = colors.get( 0 ).getComponents( null );


        if (colors.size() >= 2)
            rgb2 = colors.get( 1 ).getComponents( null );
        if (colors.size() >= 3)
            rgb3 = colors.get( 2 ).getComponents( null );


        JOGL.gl.glPushMatrix();
       // JOGL.gl.glLoadIdentity();
        JOGL.gl.glTranslatef(xLoc,yLoc,zLoc);
        JOGL.gl.glRotatef(dxRot+90, 1.0f, 0, 0);
        JOGL.gl.glRotatef(dyRot, 0, 1f, 0);
        JOGL.gl.glRotatef(dzRot, 0 ,0 ,1f);
        JOGL.gl.glScalef(xSize,ySize,zSize);
        JOGL.gl.glColor3f( rgb1[ 0 ], rgb1[ 1 ], rgb1[ 2 ] );
        JOGL.glut.glutSolidCylinder(radius,height,100,100);

        JOGL.gl.glPopMatrix();

        JOGL.gl.glPushMatrix();
        //JOGL.gl.glLoadIdentity();
        JOGL.gl.glTranslatef((xLoc),((yLoc+(height/2)))*ySize,zLoc);
        JOGL.gl.glRotatef(dxRot+90, 1.0f, 0, 0);
        JOGL.gl.glRotatef(dyRot, 0, 1f, 0);
        JOGL.gl.glRotatef(dzRot, 0 ,0 ,1f);
        JOGL.gl.glScalef(xSize/2,ySize/2,zSize/2);
        JOGL.gl.glColor3f( rgb2[ 0 ], rgb2[ 1 ], rgb2[ 2 ] );
        JOGL.glut.glutSolidCylinder(radius,height,100,100);
        JOGL.gl.glPopMatrix();

        JOGL.gl.glPushMatrix();
        //JOGL.gl.glLoadIdentity();
        JOGL.gl.glTranslatef((xLoc),(yLoc+(height-0.06f))*ySize,zLoc);
        JOGL.gl.glRotatef(dxRot+90, 1.0f, 0, 0);
        JOGL.gl.glRotatef(dyRot, 0, 1f, 0);
        JOGL.gl.glRotatef(dzRot, 0 ,0 ,1f);
        JOGL.gl.glScalef(xSize/4,ySize/4,zSize/4);
        JOGL.gl.glColor3f( rgb3[ 0 ], rgb3[ 1 ], rgb3[ 2 ] );
        JOGL.glut.glutSolidCylinder(radius,height,100,100);
        JOGL.gl.glPopMatrix();

    }
}
