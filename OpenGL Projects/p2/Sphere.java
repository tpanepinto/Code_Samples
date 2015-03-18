/**
 * Sphere.java - a class implementation representing a Box object
 *           in OpenGL
 * Oct 16, 2013
 * rdb - derived from Box.cpp
 */
    
    
import java.io.*;

public class Sphere extends Object3D
{
    //--------- instance variables -----------------
    float radius;
    
    //------------- constructor -----------------------
    public Sphere()
    {
        radius = 1;
    }
    
    //------------- drawPrimitives ---------------------------
    public void drawPrimitives()
    {   
        JOGL.glut.glutSolidSphere( radius, 360, 360 );
    }
}
