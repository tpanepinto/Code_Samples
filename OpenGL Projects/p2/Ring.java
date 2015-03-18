import java.awt.*;

/**
 * Created by tim on 10/12/14.
 *
 * What a nice diamond ring
 */
public class Ring extends Object3D {


    @Override
    protected void drawPrimitives() {
        Torus t = new Torus(0.06f, 0.62f);
        t.setSize(xSize * 0.3f, ySize * 0.3f, zSize*0.3f);
        t.setColor(new Color(212, 175, 55));
        t.redraw();

        Dodecahedron stone = new Dodecahedron();
        stone.setSize(xSize * 0.03f, ySize * 0.03f, zSize*0.03f);
        stone.setColor(colors.get(0));
        stone.setLocation(xLoc,(yLoc + 0.24f)*ySize,zLoc);
        stone.redraw();
    }


}
