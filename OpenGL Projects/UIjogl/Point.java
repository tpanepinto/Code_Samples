
/*
Point class encapsulates the x and y coordinates for shapes
 */
public class Point {
    private float x;
    private float y;

    //Constructor for the point class the creates points at the orgin
    public Point(){
        x = 0.0f;
        y = 0.0f;
    }
    //Second constructor that takes in an x and y coordinate and uses that for the point
    public Point (float fx, float fy){
        x = fx;
        y = fy;

    }
    // get methods in order to get the points cordinates
    public float getX() {
        return x;
    }
    public float getY(){
        return y;
    }
}