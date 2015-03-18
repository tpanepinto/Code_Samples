/**
 * Created by tim on 12/6/14.
 * Encapsulates a 2D point
 */
public class Point2D {
    private float x;
    private float y;

    //Constructor
    public Point2D( float px, float py){
        x = px;
        y = py;
    }

    /**
     * Gets the x value of the point
     * @return
     */
    public float getX(){
        return x;
    }
    /**
     * Gets the y value of the point
     * @return
     */
    public float getY(){
        return y;
    }

}
