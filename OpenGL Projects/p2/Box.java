/**
 * Box.java - a class implementation representing a Box object
 *           in OpenGL
 * Oct 16, 2013
 * rdb - derived from Box.cpp
 */

public class Box extends Object3D
{
    //--------- instance variables -----------------
    float length;
    
    //------------- constructor -----------------------
    public Box()
    {
        length = 1;
    }
    
    //------------- drawPrimitives ---------------------------
    public void drawPrimitives()
    {           
        JOGL.glut.glutSolidCube( length );
    }
}
