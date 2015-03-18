/**
 * Created by tim on 10/8/14.
 */
public class Cone extends Object3D{

    float base;
    float height;
//-------------------Constructors----------------
    public Cone(){
        base = 0.3f;
        height = 0.3f;

    }

    public Cone(float b, float h)
    {
        base = b;
        height = h;
    }

    @Override
    protected void drawPrimitives() {
        JOGL.glut.glutSolidCone(base, height,100,100);
    }
}
