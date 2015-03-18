/**
 *@author tmlm62
 * Makes a subdivided iscohedron
 * Based off the code from http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html

 */

import com.jogamp.opengl.util.GLBuffers;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL2ES3.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;
import static javax.media.opengl.GL2GL3.GL_QUADS;

public class Sphere extends Object3D {

    //--------- instance variables -----------------
    float radius;
    float countB;
    float decimalTolerance = 0.00000001f;
    float numTriangles = 0;
    float numVert = 0;
    int Index = 0;
    ArrayList<Coord> normals = new ArrayList<>();
    ArrayList<Coord> points = new ArrayList<>();

    float normalArr[];
    float pointArr[];
    Coord verts[];

    float color[] = { 0f, 0f, 1.0f };

    boolean printNormals;
    private FloatBuffer dataBuffer = null;

    //------------- constructor -----------------------
    public Sphere() {


    }

    public Sphere( float c, float[] cr, boolean pv ) {
        countB = c;
        printNormals = pv;
        color = cr;

        float length = ( ( 1.0f + ( float ) Math.sqrt( 5.0 ) ) / 2.0f );
        //create the vertices
        verts = new Coord[ 12 ];
        verts[ 0 ] = vertMaker( new Coord( -1f, length, 0, 1.0f ) );
        verts[ 1 ] = vertMaker( new Coord( 1f, length, 0f, 1.0f ) );
        verts[ 2 ] = vertMaker( new Coord( -1f, -length, 0f, 1.0f ) );
        verts[ 3 ] = vertMaker( new Coord( 1f, -length, 0f, 1.0f ) );

        verts[ 4 ] = vertMaker( new Coord( 0.0f, -1f, length, 1.0f ) );
        verts[ 5 ] = vertMaker( new Coord( 0.0f, 1f, length, 1.0f ) );
        verts[ 6 ] = vertMaker( new Coord( 0f, -1f, -length, 1.0f ) );
        verts[ 7 ] = vertMaker( new Coord( 0f, 1f, -length, 1.0f ) );

        verts[ 8 ] = vertMaker( new Coord( length, 0.0f, -1.0f, 1.0f ) );
        verts[ 9 ] = vertMaker( new Coord( length, 0f, 1f, 1.0f ) );
        verts[ 10 ] = vertMaker( new Coord( -length, 0f, -1f, 1.0f ) );
        verts[ 11 ] = vertMaker( new Coord( -length, 0f, 1f, 1.0f ) );


        numTriangles = ( float ) Math.pow( 4, ( c + 1 ) );
        numVert = numTriangles * 3;

        icosahedron();
        divide_triangle( ( int ) c );
        makeNormals( pv );
        makeArrays();

        dataBuffer = makeDataBuffer( pointArr );
        System.out.println( pointArr.length );
    }

    /**
     * Makes the first step in the sphere
     */
    private void icosahedron() {

        makeVerts( 0, 11, 5 );
        makeVerts( 0, 5, 1 );
        makeVerts( 0, 1, 7 );
        makeVerts( 0, 7, 10 );
        makeVerts( 0, 10, 11 );

        makeVerts( 1, 5, 9 );
        makeVerts( 5, 11, 4 );
        makeVerts( 11, 10, 2 );
        makeVerts( 10, 7, 6 );
        makeVerts( 7, 1, 8 );

        makeVerts( 3, 9, 4 );
        makeVerts( 3, 4, 2 );
        makeVerts( 3, 2, 6 );
        makeVerts( 3, 6, 8 );
        makeVerts( 3, 8, 9 );


        makeVerts( 4, 9, 5 );
        makeVerts( 2, 4, 11 );
        makeVerts( 6, 2, 10 );
        makeVerts( 8, 6, 7 );
        makeVerts( 9, 8, 1 );


    }
//---------------------- makeDataBuffer() --------------------------

    /**
     * Build a FloatBuffer containing primitive vertices. This code builds
     * the buffer at construction time; but does not download to the
     * gpu until draw time.
     */
    protected FloatBuffer makeDataBuffer( float[] data ) {
        //
        // IMPORTANT: you should let OpenGL create the FloatBuffer; don't
        //      use the plain Java tools for that. That cost me many, many
        //      hours. There might be a way to make it work.
        FloatBuffer fBuffer
                = GLBuffers.newDirectFloatBuffer( data, 0, data.length );

        fBuffer.flip();  // prepare to read
        return fBuffer;
    }

    //------------- drawObject ---------------------------
    public void redrawObject( GL2 gl ) {
        JOGL.glut.glutSolidSphere( radius, 360, 360 );
    }

    //------------- redrawVAO ---------------------------
    public void redrawVAO( GL2 gl ) {
        //JOGL.glut.glutSolidSphere( radius, 360, 360 );
    }

    //------------- redrawVBO ---------------------------
    public void redrawVBO( GL2 gl ) {
        // 1. Ask for a buffer. Can get multiple buffers at once, so
        //    parameter to glGenBuffers call is an array.
        int[] bufferIds = new int[ 1 ]; // we need just 1 buffer.
        bufferIds[ 0 ] = 0; // avoids uninitialized warning
        gl.glGenBuffers( 1, bufferIds, 0 );
        JOGL.check( "redrawVBO: glGenBuffers" );

        // 2. Bind the buffer
        gl.glBindBuffer( GL_ARRAY_BUFFER, bufferIds[ 0 ] );
        JOGL.check( "redrawVBO: glBindBuffer" );

        // 3. Download data to buffer from a Java FloatBuffer, which is
        //    created at Shape object constructor time.
        //    size (2nd arg) is NUMBER OF BYTES, not number of floats.
        gl.glBufferData( GL_ARRAY_BUFFER, dataBuffer.capacity() * 4,
                dataBuffer, gl.GL_STATIC_DRAW );
        JOGL.check( "redrawVBO: glBufferData: " + dataBuffer.capacity() );

        // 4. Draw
        // 4a. Identify the "vertex" and "color" variables in vertex shader
        //
        int vPosition = gl.glGetAttribLocation( shaderProgram, "vPosition" );
        gl.glEnableVertexAttribArray( vPosition );
        JOGL.check( "redrawVBO: glEnableVertexAttribArray" );
        int vColor = gl.glGetAttribLocation( shaderProgram, "vColor" );
        gl.glEnableVertexAttribArray( vColor );
        JOGL.check( "redrawVBO: glEnableVertexAttribArray-color" );

        int vNormal = gl.glGetAttribLocation( shaderProgram, "vNormal" );
        gl.glEnableVertexAttribArray( vNormal );
        JOGL.check( "redrawVBO: glEnableVertexAttribArray-Normal" );

        // 4b. Map the "vPosition" variable to the position in the buffer
        //     of the vertex coordinates. args:
        //     shaderVarId, #coords, type, normalizedFlag, stride, index
        //        stride let's you intermix variables in 1 buffer.
        gl.glVertexAttribPointer( vPosition, 4, GL_FLOAT, false, 0, 0L );
        JOGL.check( "redrawVBO: glVertexAttribPointer" );

        gl.glVertexAttribPointer( vNormal, 4, GL_FLOAT, false, 0,

                pointArr.length * 4 );
        JOGL.check( "redrawVBO: glVertexAttribPointer - Normal" );

        // Map  the colors to the second half of buffer
        gl.glVertexAttribPointer( vColor, 4, GL_FLOAT, false, 0,
                // each coord is 4 bytes; 1/2 array is verts
                // colors start at verts.length * 4 / 2
                ( pointArr.length * 4 ) / 2 );
        JOGL.check( "redrawVBO: glVertexAttribPointer - color" );


        // 4c. Draw!
        gl.glDrawArrays( GL_TRIANGLES, 0, pointArr.length / 4 );
        JOGL.check( "redrawVBO: drawArrays" );


        // unbind the buffer, so space can be re-used.
        gl.glBindBuffer( GL_ARRAY_BUFFER, 0 );
        JOGL.check( "redrawVBO: glBindBuffer" );
    }

    /**
     *
     * applies the color to the sphere
     */
    private void applyColor( int r, int g, int b ) {

        color[ 0 ] = r;
        color[ 1 ] = g;
        color[ 3 ] = b;
    }

    /**
     * sub divides the iscohedron
     * @param divide
     */
    private void divide_triangle( int divide ) {
        for ( int i = 0; i < divide; i++ ) {

            ArrayList<Coord> newPoints = new ArrayList<>();

            int count = 0;
            while ( count < points.size() ) {
                Coord v1 = points.get( count );
                count++;
                Coord v2 = points.get( count );
                count++;
                Coord v3 = points.get( count );
                count++;

                Coord mp1 = midPoint( v1, v2 );
                Coord mp2 = midPoint( v2, v3 );
                Coord mp3 = midPoint( v3, v1 );

                newPoints.add( v1 );
                newPoints.add( mp1 );
                newPoints.add( mp3 );

                newPoints.add( v2 );
                newPoints.add( mp2 );
                newPoints.add( mp1 );

                newPoints.add( v3 );
                newPoints.add( mp3 );
                newPoints.add( mp2 );

                newPoints.add( v1 );
                newPoints.add( v2 );
                newPoints.add( v3 );


            }

            points = newPoints;
           // System.out.println( points.size() );
        }
    }

    /**
     * calculates the midpoint of the triangle for the normal
     * @param coo1
     * @param coo2
     * @return
     */
    private Coord midPoint( Coord coo1, Coord coo2 ) {
        float x = ( coo1.getX() + coo2.getX() ) / 2.0f;
        float y = ( coo1.getY() + coo2.getY() ) / 2.0f;
        float z = ( coo1.getZ() + coo2.getZ() ) / 2.0f;
        return vertMaker( new Coord( x, y, z, 1.0f ) );
    }

    /**
     * makes the normals for the sphere
     * @param print
     */
    private void makeNormals( boolean print ) {

        Coord p1, p2, p3;

        int printCount = 0;
        if ( print )
            System.out.println( "----------Sphere Normals------------" );
        for ( int i = 0; i < points.size(); i++ ) {
            p1 = points.get( i );
            i++;
            p2 = points.get( i );
            i++;
            p3 = points.get( i );

            float Ux = p2.getX() - p1.getX();
            float Uy = p2.getY() - p1.getY();
            float Uz = p2.getZ() - p1.getZ();

            float Vx = p3.getX() - p1.getX();
            float Vy = p3.getY() - p1.getY();
            float Vz = p3.getZ() - p1.getZ();

            float Nx = ( Uy * Vz ) - ( Uz * Vy );
            float Ny = ( Uz * Vx ) - ( Ux * Vz );
            float Nz = ( Ux * Vy ) - ( Uy * Vx );

            normals.add( new Coord( Nx, Ny, Nz, 1f ) );
            if ( print && printCount < 20 ) {
                System.out.println( "Sphere Normal " + printCount + ": x = " + Nx + " y = " + Ny + " z= " + Nz );
                printCount++;
            }

        }
    }

    /**
     * Makes the arrays needed for the vertex and the normals so that it can be put into a buffer
     */
    private void makeArrays() {
        normalArr = new float[ normals.size() * 4 ];
        int count = 0;
        for ( int i = 0; i < normals.size(); i++ ) {
            normalArr[ count ] = normals.get( i ).getX();
            count++;
            normalArr[ count ] = normals.get( i ).getY();
            count++;
            normalArr[ count ] = normals.get( i ).getZ();
            count++;
            normalArr[ count ] = normals.get( i ).getW();
            count++;

        }

        pointArr = new float[ ( ( points.size() * 4 ) * 2 ) ];// + (normalArr.length) ];

        count = 0;
        for ( int i = 0; i < points.size(); i++ ) {
            pointArr[ count ] = points.get( i ).getX();
            count++;
            pointArr[ count ] = points.get( i ).getY();
            count++;
            pointArr[ count ] = points.get( i ).getZ();
            count++;
            pointArr[ count ] = points.get( i ).getW();
            count++;
        }
        /*
        for ( int i = 0; i < normals.size() ; i++ ) {
            pointArr[ count ] = normals.get( i ).getX();
            count++;
            pointArr[ count ] = normals.get( i ).getY();
            count++;
            pointArr[ count ] = normals.get( i ).getZ();
            count++;
            pointArr[ count ] = normals.get( i ).getW();
            count++;
        }*/
        for ( int i = 0; i < points.size(); i++ ) {
            pointArr[ count ] = color[ 0 ];
            count++;
            pointArr[ count ] = color[ 1 ];
            count++;
            pointArr[ count ] = color[ 2 ];
            count++;
            pointArr[ count ] = 1f;
            count++;
        }


    }

    /**
     * Makes the original vertices for the iscohedron
     * @param i
     * @param j
     * @param k
     */
    private void makeVerts( int i, int j, int k ) {
        points.add( verts[ i ] );
        points.add( verts[ j ] );
        points.add( verts[ k ] );
    }

    /**
     * makes the verticies normalized
     * @param coo
     * @return
     */
    private Coord vertMaker( Coord coo ) {
        float length = ( float ) Math.sqrt( coo.getX() * coo.getX() + coo.getY() * coo.getY() + coo.getZ() * coo.getZ() );
        return new Coord( coo.getX() / length, coo.getY() / length, coo.getZ() / length );
    }

}
