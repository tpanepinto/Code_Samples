/**
 * Coord -- a 3D homogenous coordinate; can be either a point or vector
 * <p/>
 * rdb 10/28/14
 */
public class Coord {
    //---------------- instance variables -----------------
    public float[] coords = { 0f, 0f, 0f, 1f };

    //--------------- Constructors --------------------------
    public Coord() {
    }

    public Coord( float ix, float iy, float iz ) {
        coords[ 0 ] = ix;
        coords[ 1 ] = iy;
        coords[ 2 ] = iz;
        coords[ 3 ] = 1f;
    }

    public Coord( float ix, float iy, float iz, float iw ) {
        coords[ 0 ] = ix;
        coords[ 1 ] = iy;
        coords[ 2 ] = iz;
        coords[ 3 ] = iw;
    }

    public Coord( float[] inCoords ) {
        int len = inCoords.length;
        if ( len != 4 )
            // throw exception??
            System.err.println( "Coord constructor needs 4-element array" );
        len = Math.min( len, 4 );
        for ( int i = 0; i < len; i++ )
            coords[ i ] = inCoords[ i ];
    }

    //---------------- toString -----------------------------
    public String toString() {
        return "<" + coords[ 0 ] + ", " + coords[ 1 ] + ", "
                + coords[ 2 ] + "," + coords[ 3 ] + ">";
    }

    public float getX() {
        return coords[ 0 ];
    }

    public float getY() {
        return coords[ 1 ];

    }

    public float getZ() {
        return coords[ 2 ];

    }

    public float getW() {
        return coords[ 3 ];
    }
}