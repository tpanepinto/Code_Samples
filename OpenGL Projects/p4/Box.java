/**
 * Box.java - a class implementation representing a Box object
 *           in OpenGL
 * Oct 16, 2013
 * rdb - derived from Box.cpp
 *
 * 10/28/14 rdb - revised to explicitly draw faces
 *              - drawPrimitives -> drawObject( GL2 )
 */

import javax.media.opengl.*;
import javax.media.opengl.glu.*;        // GL utility library

import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;

import java.nio.*;
import java.util.ArrayList;

import static javax.media.opengl.GL.*;  // static imports
import static javax.media.opengl.GL2.*;

public class Box extends Object3D {
    //--------- instance variables -----------------
    private FloatBuffer dataBuffer = null;
    private FloatBuffer colBuffer = null;

    private boolean print;

    private float[] normals;
    // vertex coordinates
    private float[] verts = { // 4-element homog coordinates
            // front face
            -0.5f, -0.5f, 0.5f, 1f, 0.5f, -0.5f, 0.5f, 1f,
            0.5f, 0.5f, 0.5f, 1f, -0.5f, 0.5f, 0.5f, 1f,
            // back face
            -0.5f, -0.5f, -0.5f, 1f, -0.5f, 0.5f, -0.5f, 1f,
            0.5f, 0.5f, -0.5f, 1f, 0.5f, -0.5f, -0.5f, 1f,
            // left face
            -0.5f, -0.5f, 0.5f, 1f, -0.5f, 0.5f, 0.5f, 1f,
            -0.5f, 0.5f, -0.5f, 1f, -0.5f, -0.5f, -0.5f, 1f,
            // right face
            0.5f, -0.5f, 0.5f, 1f, 0.5f, -0.5f, -0.5f, 1f,
            0.5f, 0.5f, -0.5f, 1f, 0.5f, 0.5f, 0.5f, 1f,
            // top face
            0.5f, 0.5f, 0.5f, 1f, 0.5f, 0.5f, -0.5f, 1f,
            -0.5f, 0.5f, -0.5f, 1f, -0.5f, 0.5f, 0.5f, 1f,
            // bottom face
            0.5f, -0.5f, 0.5f, 1f, -0.5f, -0.5f, 0.5f, 1f,
            -0.5f, -0.5f, -0.5f, 1f, 0.5f, -0.5f, -0.5f, 1f,

            // Now add vertex colors
            // front face: blue
            0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f,
            0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f,
            // back face: red
            1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f,
            // left face: magenta
            1f, 0f, 1f, 1f, 1f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f, 1f, 0f, 1f, 1f,
            // right face: cyan
            0f, 1f, 1f, 1f, 0f, 1f, 1f, 1f,
            0f, 1f, 1f, 1f, 0f, 1f, 1f, 1f,
            // top face: green
            0f, 1f, 0f, 1f, 0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f, 0f, 1f, 0f, 1f,
            // bottom face: yellow
            1f, 1f, 0f, 1f, 1f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f, 1f, 1f, 0f, 1f };


    private float[] nFront = { 0f, 0f, 1f, 0f };
    private float[] nBack = { 0f, 0f, -1f, 0f };
    private float[] nLeft = { -1f, 0f, 0f, 0f };
    private float[] nRight = { 1f, 0f, 0f, 0f };
    private float[] nTop = { -0f, 1f, 0f, 0f };
    private float[] nBotm = { 0f, -1f, 0f, 0f };


    //------------- constructor -----------------------
    public Box( boolean p ) {
        dataBuffer = makeDataBuffer( verts );
        print = p;
        makeNormals( p );
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

    private void makeNormals( boolean print ) {
        ArrayList<Coord> normalList = new ArrayList<>();

        normalList.add( new Coord( nFront[ 0 ], nFront[ 1 ], nFront[ 2 ], nFront[ 3 ] ) );
        normalList.add( new Coord( nBack[ 0 ], nBack[ 1 ], nBack[ 2 ], nBack[ 3 ] ) );
        normalList.add( new Coord( nLeft[ 0 ], nLeft[ 1 ], nLeft[ 2 ], nLeft[ 3 ] ) );
        normalList.add( new Coord( nRight[ 0 ], nRight[ 1 ], nRight[ 2 ], nRight[ 3 ] ) );
        normalList.add( new Coord( nTop[ 0 ], nTop[ 1 ], nTop[ 2 ], nTop[ 3 ] ) );
        normalList.add( new Coord( nBotm[ 0 ], nBotm[ 1 ], nBotm[ 2 ], nBotm[ 3 ] ) );

        int printCount = 0;
        if ( print )
            System.out.println( "----------Box Normals------------" );
        int count = 0;
        normals = new float[ normalList.size() * 4 ];
        for ( int i = 0; i < normalList.size(); i++ ) {
            normals[ count ] = normalList.get( i ).getX();
            count++;
            normals[ count ] = normalList.get( i ).getY();
            count++;
            normals[ count ] = normalList.get( i ).getZ();
            count++;



            if ( print && printCount < 20 ) {
                System.out.println( "Box Normal " + printCount + ": x = " + normalList.get( i ).getX() + " y = " + normalList.get( i ).getY() + " z= " + normalList.get( i ).getZ() );
                printCount++;
            }

            //System.out.println(newVerts);
        }
        float newVerts[] = new float[verts.length + normals.length];
        int vertCount = 0;
        for(int i = 0; i< verts.length; i++){
           newVerts[count] = verts[i];
            vertCount++;
        }
        for(int i = 0; i< normals.length; i++){
            newVerts[count] = normals[i];
            vertCount++;
        }
        verts = newVerts;

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
        gl.glVertexAttribPointer( vPosition, 4, GL_FLOAT, false, 0, 0L );
        JOGL.check( "redrawVBO: glVertexAttribPointer" );

        // Map  the colors to the second half of buffer
        gl.glVertexAttribPointer( vColor, 4, GL_FLOAT, false, 0,
                // each coord is 4 bytes; 1/2 array is verts
                // colors start at verts.length * 4 / 2
                ((verts.length * 4) / 2 ));
        JOGL.check( "redrawVBO: glVertexAttribPointer - color" );


        // 4b. Map the "vPosition" variable to the position in the buffer
        //     of the vertex coordinates. args: 
        //     shaderVarId, #coords, type, normalizedFlag, stride, index
        //        stride let's you intermix variables in 1 buffer.



        // 4c. Draw!
        gl.glDrawArrays( GL_QUADS, 0, verts.length / 4 );
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
}
