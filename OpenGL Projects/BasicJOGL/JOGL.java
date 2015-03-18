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
 */
import javax.media.opengl.*;
//import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;        // GL utility library

//import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT; // GLUT library

public class JOGL
{
    //----------------------- class variables ---------------------------------
    public static GL2  gl = null;   // the GL2 encapsulation of the openGL state
    
    public static GLUT glut = null; // the GLUT state
    public static GLU  glu  = null; // the GLU  state
    
}