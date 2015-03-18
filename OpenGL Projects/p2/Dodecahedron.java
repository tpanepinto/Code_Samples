/**
 * Created by tim on 10/12/14.
 *
 * makes a basic dodecahedron
 */
public class Dodecahedron extends Object3D {


    public Dodecahedron(){}


    @Override
    protected void drawPrimitives() {
        JOGL.glut.glutSolidDodecahedron();
    }
}
