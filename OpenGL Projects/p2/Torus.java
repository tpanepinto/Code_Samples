/**
 * Created by tim on 10/8/14.
 */
public class Torus extends Object3D {
    float inner,outter;

    public Torus()
    {
        inner = 0.3f;
        outter = 0.5f;

    }



    public Torus(float i, float o){
        inner = i;
        outter = o;
    }

    @Override
    protected void drawPrimitives() {
        JOGL.glut.glutSolidTorus(inner, outter, 100, 100);
    }
}
