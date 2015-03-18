/**
 * JOGL -- this is a publc "static" class whose main goal is to encapsulate
 *         key global openGL references needed by the application to access
 *         the JOGL methods.
 *
 *         It's simpler than passing lots of parameters around.
 *
 *         This is an example of the Holder pattern
 *
 * @author rdb
 * @version 0.1 08/27/13
 * @version 1.0 10/24/14
 *         Added some basic functionality that is helpful for GLSL 
 *         programming: especially the GL errors debugging methods.
 */

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;        // GL utility library

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library

public class JOGL {
    //----------------------- class variables --------------------------
    public static GL2 gl = null;   // GL2 encapsulation of openGL state

    public static GLUT glut = null; // GLUT state
    public static GLU glu = null; // GLU  state

    public static boolean vaoSupported = false; // Can we use VAOs?
    public static boolean vaoMessagePrinted = false;

    //-------------------------- init ----------------------------------

    /**
     * Initialize the global variables useful for most openGL apps.
     */
    public static void init( GLAutoDrawable drawable ) {
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
    }
    //------------- checkGLerrors -----------------------------------

    /**
     * based on code from Bailey and Cunningham text, "Graphics Shaders"
     * <p/>
     * Returns the error code in case caller wants to do something with it.
     * However if the error code is GL.GL_NO_ERROR, it returns 0.
     */
    public static int checkGLerrors( String src, boolean exit ) {
        int gle = gl.glGetError();
        if ( gle != GL.GL_NO_ERROR ) {
            String msg = "**** GL error **** " + src + ": ";
            switch ( gle ) {
                case GL.GL_INVALID_ENUM:
                    msg += "Invalid enum";
                    break;
                case GL.GL_INVALID_VALUE:
                    msg += "Invalid value";
                    break;
                case GL.GL_INVALID_OPERATION:
                    msg += "Invalid operation";
                    break;
                //case GL.GL_STACK_OVERFLOW: // not recog after gl2.1
                case 0x0503: // not recog after gl2.1
                    msg += "Stack overflow";
                    break;
                //case GL.GL_STACK_UNDERFLOW: //not recog after gl2.1
                case 0x0504:
                    msg += "Stack underflow";
                    break;
                case GL.GL_OUT_OF_MEMORY:
                    msg += "Out of memory";
                    break;
                case GL.GL_INVALID_FRAMEBUFFER_OPERATION:
                    msg += "Bad frame buf op";
                    break;
                default:
                    msg += "Unknown error " + gle;
                    break;
            }
            System.err.printf( "%s: %x\n", msg, gle );
            if ( exit )
                System.exit( gle );
        }
        return gle;
    }
    //--------------------- check( String ) ---------------------------

    /**
     * This method just invokes checkGLerrors with "false" 2nd arg.
     * It's intended for debugging tests that are likely to be removed
     * from the code and you want to avoid typing long lines.
     */
    public static int check( String src ) {
        return checkGLerrors( src, false );
    }

}
