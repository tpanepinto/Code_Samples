/**
 * Point3
 * 
 * rdb 10/16/13
 */
public class Point3
{
    //---------------- instance variables -----------------
    public float x;
    public float y;
    public float z;
    
    //--------------- Constructors --------------------------
    public Point3()
    { 
        x = y = z = 0.0f;
    }
    public Point3( float ix, float iy, float iz )
    {
        x = ix;
        y = iy;
        z = iz;
    }
    //---------------- toString -----------------------------
    public String toString()
    {
        return "<" + x + ", " + y + ", " + z + ">";
    }
}