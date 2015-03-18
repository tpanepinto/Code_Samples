/**
 * Created by tim on 11/10/14.
 *
 * Based off box.java
 */



import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library

import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

import java.nio.*;
import java.util.ArrayList;

import static javax.media.opengl.GL.*;  // static imports
import static javax.media.opengl.GL2.*;

public class Pyramid extends Object3D {
    //--------- instance variables -----------------
    private FloatBuffer dataBuffer = null;
    private FloatBuffer colBuffer = null;
    private ArrayList<Coord> points = new ArrayList<>(  );
    private ArrayList<Coord> normals = new ArrayList<>(  );
    // vertex coordinates
    private float[] verts = { // 4-element homog coordinates
            // front face1
            0f, 1f, 0f, 1f, -1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            // back face
            0f, 1f, 0f, 1f, 1f, 0f, 1f, 1f,
            1f, 0f, -1f, 1f,
            // left face
            0f, 1f, 0f, 1f, 1f, 0f, -1f, 1f,
            -1f, 0f, -1f, 1f,
            // right face
            0f, 1f, 0f, 1f, -1f, 0f, -1f, 1f,
            -1f, 0f, 1f, 1f,
            // top face


            // Now add vertex colors
            // front face: blue
            0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f,
            0f, 0f, 1f, 1f,
            // back face: red
            1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            // left face: magenta
            1f, 0f, 1f, 1f, 1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            // right face: cyan
            0f, 1f, 1f, 1f, 0f, 1f, 1f, 1f,
            0f, 1f, 1f, 1f,
            // top face: green
    };





    //------------- constructor -----------------------
    public Pyramid(boolean print) {
        makePoints();
        makeNormals( print );
        dataBuffer = makeDataBuffer( verts );

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
    //------------ redrawVAO( GL2 ) ----------------------------

    /**
     * Draw the box using glsl and VertexArrayObjects.
     */
    protected void redrawVAO( GL2 gl ) {

    }
    //------------ redrawVBO( GL2 ) ----------------------------

    /**
     * Draw the box using glsl and VertexBufferObjects with VAOs.
     * Buffer version (VBO).
     * Need to 1) get glsl to allocate a buffer object in the gpu.
     * 2) tell the gpu that this new buffer is the current one.
     * 3) download cpu data to the gpu buffer
     * 4) tell the gpu to draw the data in the buffer.
     * Note that steps 1-3 can be done once for each object or scene,
     * which in some cases is a good idea. However, it may mean using gpu
     * memory for objects not currently being rendered.
     * <p/>
     * This version checks every GL call, with "no quit" option. You
     * won't need to do this all the time, but it's a good debugging
     * tool while you are first starting out.
     */
    protected void redrawVBO( GL2 gl ) {
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

        // 4b. Map the "vPosition" variable to the position in the buffer
        //     of the vertex coordinates. args:
        //     shaderVarId, #coords, type, normalizedFlag, stride, index
        //        stride let's you intermix variables in 1 buffer.
        gl.glVertexAttribPointer( vPosition, 4, GL_FLOAT, false, 0, 0L );
        JOGL.check( "redrawVBO: glVertexAttribPointer" );

        // Map  the colors to the second half of buffer
        gl.glVertexAttribPointer( vColor, 3, GL_FLOAT, false, 0,
                // each coord is 4 bytes; 1/2 array is verts
                // colors start at verts.length * 4 / 2
                verts.length * 2 );
        JOGL.check( "redrawVBO: glVertexAttribPointer - color" );

        // 4c. Draw!
        gl.glDrawArrays( GL_TRIANGLES, 0, verts.length / 3 );
        JOGL.check( "redrawVBO: drawArrays" );

        // unbind the buffer, so space can be re-used.
        gl.glBindBuffer( GL_ARRAY_BUFFER, 0 );
        JOGL.check( "redrawVBO: glBindBuffer" );
    }
    //------------ redrawObject( GL2 ) ----------------------------

    /**
     * Draw the box using old-style OpenGL.
     */
    protected void redrawObject( GL2 gl ) {
        gl.glBegin( GL2.GL_QUADS );
        {
            for ( int i = 0; i < verts.length; i += 4 ) {
                gl.glNormal3f( verts[ i ], verts[ i + 1 ], verts[ i + 2 ] );
                gl.glVertex4f( verts[ i ], verts[ i + 1 ], verts[ i + 2 ], verts[ i + 3 ] );
            }
        }
        gl.glEnd();
    }

    private void makeNormals( boolean print ) {

        Coord p1, p2, p3;

        int printCount = 0;
        if ( print )
            System.out.println( "----------Pyramid Normals------------" );
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
                System.out.println( "Pyramid Normal " + printCount + ": x = " + Nx + " y = " + Ny + " z= " + Nz );
                printCount++;
            }

        }
    }

    /**
     * Makes points from the point array to the vertex array
     */
    private void makePoints(){
        for(int i = 0; i < 48; i+=4){
            points.add( new Coord( verts[i], verts[i+1], verts[i+2], verts[i+3] ) );
        }
    }
}
