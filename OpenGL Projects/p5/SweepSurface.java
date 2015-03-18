import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import java.util.ArrayList;

/**
 * Created by tim on 12/6/14.
 * This Class sweeps a polygon across a plane to form a new polygon from the sweep
 *
 */
public class SweepSurface extends Object3D {

    //class variables
    private ArrayList<Point2D> polyVert = new ArrayList<>(  );
    private ArrayList<Point3> path = new ArrayList<>(  );
    private ArrayList<Point2D> scale = new ArrayList<>(  );
    private ArrayList<Float> rotation = new ArrayList<>(  );

    //constructor
    public SweepSurface( ArrayList<Point2D> origPoly, ArrayList<Point3> steps, ArrayList<Point2D> scalars, ArrayList<Float> rot) {

        polyVert = origPoly;
        path = steps;
        scale = scalars;
        rotation = rot;
    }
    //no input constructor most parameters have a set method
    public SweepSurface(){
        Point2D vert1 = new Point2D( 0, 0.5f );
        polyVert.add( vert1 );

        Point2D vert2 = new Point2D( 0, -0.5f );
        polyVert.add( vert2 );
        Point2D vert3 = new Point2D( 0.5f, -0.5f );
        polyVert.add( vert3 );
        Point2D vert4 = new Point2D( 0.5f, 0.5f );
        polyVert.add( vert4 );

        Point3 step1 = new Point3( 0,0,0 );
        path.add( step1 );
        Point3 step2 = new Point3( 0,0f,0.2f );
        path.add( step2 );
        Point3 step3 = new Point3( 0,0f,0.4f );
        path.add( step3 );
        Point3 step4 = new Point3( 0,0f,0.6f );
        path.add( step4 );

        Point2D scalar1 = new Point2D( 1f, 1f );
        scale.add( scalar1 );
        Point2D scalar2 = new Point2D( 1f, 1f );
        scale.add( scalar2 );
        Point2D scalar3 = new Point2D( 1f, 1f );
        scale.add( scalar3 );
        Point2D scalar4 = new Point2D( 1f, 1f );
        scale.add( scalar4 );


        rotation.add( 0f );
        rotation.add( 0f );
        rotation.add( 0f );
        rotation.add( 0f );


    }


    @Override
    protected void drawPrimitives() {

    }
    @Override
    public void redraw(){

        JOGL.gl.glPushMatrix();

        if ( textureSet )
        {
            shapeTexture.bind( JOGL.gl );
            shapeTexture.enable( JOGL.gl );
        }

        JOGL.gl.glTranslatef( xLoc, yLoc, zLoc );
        JOGL.gl.glRotatef( dxRot + 90, 1.0f, 0, 0 );
        JOGL.gl.glRotatef( dyRot, 0, 1f, 0 );
        JOGL.gl.glRotatef( dzRot, 0, 0, 1f );
        JOGL.gl.glScalef( xSize, ySize, zSize );
        float[] rgb = colors.get( 0 ).getComponents( null );

        if ( smoothObj )
            JOGL.gl.glShadeModel( GLLightingFunc.GL_SMOOTH );
        else
            JOGL.gl.glShadeModel( GLLightingFunc.GL_FLAT);

        if ( textureSet )
            JOGL.gl.glColor3f( 1, 1 , 1 );
        else
            JOGL.gl.glColor3f(  rgb[0], rgb[1], rgb[2] );


        if ( wireFrameOn )
            JOGL.gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE );
        else
            JOGL.gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);


        //make the polygons
        JOGL.gl.glBegin( GL2.GL_POLYGON );
        for ( int i = 0; i < path.size(); i++ ) {
            //   System.out.println( "This is i: " + i );
            float rot = rotation.get( i ) ;
            JOGL.gl.glBegin( GL2.GL_POLYGON );
            for ( int j = 0; j < polyVert.size(); j++ ) {
                float x = ( polyVert.get( j ).getX() * scale.get( i ).getX() ) + path.get( i ).x;
                float y = ( polyVert.get( j ).getY() * scale.get( i ).getY() ) + path.get( i ).y;
                float rx = (x * (float) Math.cos( Math.toRadians( rot ) ) ) - ( y * ( float ) Math.sin( Math.toRadians( rot ) ) ) ;
                float ry = (y * (float) Math.cos( Math.toRadians( rot ) ) ) + ( x * ( float ) Math.sin( Math.toRadians( rot ) ) ) ;
                float z = path.get( i ).z;

                float normX,normY,normZ;
                if (x < 0)
                    normX = -1f;
                else
                    normX = 1f;
                if (y < 0)
                    normY = -1f;
                else
                    normY = 1f;
                if (z < 0)
                    normZ = -1f;
                else
                    normZ = 1f;

                JOGL.gl.glNormal3f( normX, normY, normZ );
                JOGL.gl.glTexCoord3f( rx, ry, z );
                JOGL.gl.glVertex3f( rx , ry , z );


            }

            JOGL.gl.glEnd();
        }

        //then sweep them out
        if (triangulation) {
            JOGL.gl.glBegin( GL.GL_TRIANGLE_STRIP );


            // System.out.println( "Path Size: " + path.size() );
            for ( int i = 0; i < path.size() - 1; i++ ) {
                //   System.out.println( "This is i: " + i );
                float rot =  rotation.get( i );
                float nrot = rotation.get( i + 1 ) ;

                for ( int j = 0; j < polyVert.size(); j++ ) {
                    float x = ( polyVert.get( j ).getX() * scale.get( i ).getX() ) + path.get( i ).x;
                    float y = ( polyVert.get( j ).getY() * scale.get( i ).getY() ) + path.get( i ).y;
                    float z = path.get( i ).z;
                    float rx = ( x * ( float ) Math.cos( Math.toRadians( rot ) ) ) - ( y * ( float ) Math.sin( Math.toRadians( rot ) ) );
                    float ry = ( y * ( float ) Math.cos( Math.toRadians( rot ) ) ) + ( x * ( float ) Math.sin( Math.toRadians( rot ) ) );

                    float normX,normY,normZ;
                    if (x < 0)
                        normX = -1f;
                    else
                        normX = 1f;
                    if (y < 0)
                        normY = -1f;
                    else
                        normY = 1f;
                    if (z < 0)
                        normZ = -1f;
                    else
                        normZ = 1f;

                    JOGL.gl.glNormal3f( normX, normY, normZ );

                    JOGL.gl.glTexCoord3f( rx, ry, z );
                    JOGL.gl.glVertex3f( rx, ry, z );


                    float nx = ( polyVert.get( j ).getX() * scale.get( i + 1 ).getX() ) + path.get( i ).x;
                    float ny = ( polyVert.get( j ).getY() * scale.get( i + 1 ).getY() ) + path.get( i ).y;
                    float nrx = ( nx * ( float ) Math.cos( Math.toRadians( nrot ) ) ) - ( ny * ( float ) Math.sin( Math.toRadians( nrot ) ) );
                    float nry = ( ny * ( float ) Math.cos( Math.toRadians( nrot ) ) ) + ( nx * ( float ) Math.sin( Math.toRadians( nrot ) ) );
                    float nz = path.get( i + 1 ).z;

                    JOGL.gl.glTexCoord3f( nrx, nry, nz );
                    JOGL.gl.glVertex3f( nrx, nry, nz );



                }


            }
            JOGL.gl.glEnd();
        }

        if ( textureSet )
            shapeTexture.disable( JOGL.gl );

        JOGL.gl.glPopMatrix();

    }

    /**
     * Sets the path from the array list pasted in
     * @param pathin
     */
    public void setPath( ArrayList<Point3> pathin ){
        path = pathin;
    }

    /**
     * Sets the scalars from the array list the is pasted in
     * @param scalein
     */
    public void setScale( ArrayList<Point2D> scalein ){
        scale = scalein;
    }

    /**
     * Sets the rotation from the array list the is pasted in
     * @param scalein
     */
    public void setRotation( ArrayList<Float> scalein ){
        rotation = scalein;
    }
    private Point3 makeNormals( Point3 p1, Point3 p2, Point3 p3 ) {

        //Point3 p1, p2, p3;



        float Ux = p2.x - p1.x;
        float Uy = p2.y - p1.y;
        float Uz = p2.z - p1.z;

        float Vx = p3.x - p1.x;
        float Vy = p3.y - p1.y;
        float Vz = p3.z - p1.z;

        float Nx = ( Uy * Vz ) - ( Uz * Vy );
        float Ny = ( Uz * Vx ) - ( Ux * Vz );
        float Nz = ( Ux * Vy ) - ( Uy * Vx );


        return new Point3( Nx, Ny, Nz );
    }


}
