// The shader control class.
// loads and starts/stops shaders.
//
// Downloaded on 10/26/13 by rdb from 
// http://www.guyford.co.uk/showpage.php?id=50&page=How_to_setup_and_load_GLSL_Shaders_in_JOGL_2.0
//
// Edits by rdb
// 1. Style
// 2. Added glGetError calls (via checkGLerrors)
// 3. Checked compile status of glCompileShader
// 4. Added static import of GL constants
//

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;        // GL utility library

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library

import java.io.*;
import java.nio.*;

import com.jogamp.opengl.util.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;

public class ShaderControl {
    private int vertexShaderProgram;
    private int fragmentShaderProgram;
    private int shaderprogram;
    public String[] vsrc;
    public String[] fsrc;

    // this will attach the shaders
    public void init( GL gl ) {
        try {
            attachShaders( gl );
        } catch ( Exception e ) {
            System.err.println( "Exception: in ShaderControl.init: " +
                    e.getClass().getName() );
            e.printStackTrace();
        }
    }

    // loads the shaders
    // in this example we assume that the shader is a file located in the applications JAR file.
    //
    public String[] loadShader( String name ) {
        System.out.println( "loading: " + name );
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = getClass().getResourceAsStream( name );
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            String line;
            while ( ( line = br.readLine() ) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            is.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return new String[]{ sb.toString() };
    }

    // This compiles and loads the shader to the video card.
    // If there is any kind of a problem, the program exits.
    //    it's hard to conceive of any value in continuing, but you
    //    can change the "fail" flag to false on the checkGLerror calls
    //    and remove the exit call.
    //
    private void attachShaders( GL gl1 ) throws Exception {
        GL2 gl = ( GL2 ) gl1;
        vertexShaderProgram = gl.glCreateShader( GL_VERTEX_SHADER );
        fragmentShaderProgram = gl.glCreateShader( GL_FRAGMENT_SHADER );
        gl.glShaderSource( vertexShaderProgram, 1, vsrc, null, 0 );
        gl.glCompileShader( vertexShaderProgram );

        int compiled[] = new int[ 1 ];

        gl.glGetShaderiv( vertexShaderProgram, GL_COMPILE_STATUS,
                compiled, 0 );
        if ( compiled[ 0 ] == GL_FALSE ) {
            // Can get a log from compile step: see makeShaderProgram in
            //   cppGLSL demo
            System.err.println( "***Error: Vertex shader compile failed: "
                    + vertexShaderProgram );
        }

        gl.glShaderSource( fragmentShaderProgram, 1, fsrc, null, 0 );
        gl.glCompileShader( fragmentShaderProgram );
        gl.glGetShaderiv( fragmentShaderProgram, GL_COMPILE_STATUS,
                compiled, 0 );
        if ( compiled[ 0 ] == GL_FALSE ) {
            // Compile log is available: see makeShaderProgram in cppGLSL
            System.err.println( "***Error: Fragment shader compile failed: "
                    + fragmentShaderProgram );
        }

        shaderprogram = gl.glCreateProgram();
        //
        gl.glAttachShader( shaderprogram, vertexShaderProgram );
        JOGL.checkGLerrors( "ShaderControl: attach vertex shader", true );
        gl.glAttachShader( shaderprogram, fragmentShaderProgram );
        JOGL.checkGLerrors( "ShaderControl: attach fragment shader", true );
        gl.glLinkProgram( shaderprogram );
        JOGL.checkGLerrors( "ShaderControl: link shader program", true );
        gl.glValidateProgram( shaderprogram );
        JOGL.checkGLerrors( "ShaderControl: validate shader program", true );
        IntBuffer intBuffer = IntBuffer.allocate( 1 );
        gl.glGetProgramiv( shaderprogram, GL_LINK_STATUS, intBuffer );

        if ( intBuffer.get( 0 ) != 1 ) {
            gl.glGetProgramiv( shaderprogram, GL_INFO_LOG_LENGTH,
                    intBuffer );
            int size = intBuffer.get( 0 );
            System.err.println( "Program link error: " );
            if ( size > 0 ) {
                ByteBuffer byteBuffer = ByteBuffer.allocate( size );
                gl.glGetProgramInfoLog( shaderprogram, size,
                        intBuffer, byteBuffer );
                for ( byte b : byteBuffer.array() ) {
                    System.err.print( ( char ) b );
                }
            } else
                System.err.println( "Unknown" );
            System.exit( 1 );
        }
        // do one file check for errors
        JOGL.checkGLerrors( "ShaderControl exiting", true );
    }

    // Function called when to activate the shader.
    // Once activated, it is applied to anything that drawn from
    // until you call the dontUseShader(GL) function.
    public int useShader( GL gl ) {
        ( ( GL2 ) gl ).glUseProgram( shaderprogram );
        JOGL.checkGLerrors( "ShaderControl: glUseProgram", false );
        return shaderprogram;
    }

    // when finished drawing everything you want using the shaders, 
    // call this to stop further shader interactions.
    public void dontUseShader( GL gl ) {
        ( ( GL2 ) gl ).glUseProgram( 0 );
        JOGL.checkGLerrors( "ShaderControl: glUseProgram( 0 )", false );
    }
}