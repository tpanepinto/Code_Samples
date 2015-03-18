import java.awt.*;

/**
 * Created by tim on 10/12/14.
 *
 * Zoom-zoom this car has four torus tires and a rectangular body
 */

public class Car extends Object3D {

    public Car() {

    }

    @Override
    protected void drawPrimitives() {
        Box box = new Box();
        box.setLocation(xLoc,yLoc, zLoc);
        box.setColor(colors.get(0));
        box.shapeRotate("y", dyRot + 45);
        box.setSize(xSize * 0.5f, ySize *0.2f,zSize *0.5f);
        box.redraw();



        Torus tire1 = new Torus(0.04f,0.05f);
        tire1.setLocation((xLoc + (box.xSize - 0.15f)), yLoc, zLoc +(zSize *0.5f));
        tire1.setColor(new Color(98, 98, 98));
        tire1.shapeRotate("y", dyRot +45);
        tire1.redraw();

        Torus tire2 = new Torus(0.04f,0.05f);
        tire2.setLocation((xLoc + (box.xSize + 0.15f)), yLoc+0.05f, zLoc +(zSize *0.5f));
        tire2.setColor(new Color(98, 98, 98));
        tire2.shapeRotate("y", dyRot +45);
        tire2.redraw();


        Torus tire3 = new Torus(0.04f,0.05f);
        tire3.setLocation((xLoc - (box.xSize - 0.15f)), yLoc - 0.24f, zLoc - 0.2f );
        tire3.setColor(new Color(98, 98, 98));
        tire3.shapeRotate("y", dyRot + 45);
        tire3.redraw();

        Torus tire4 = new Torus(0.04f,0.05f);
        tire4.setLocation((xLoc - (box.xSize - 0.15f)), yLoc - 0.30f, zLoc - 0.55f );
        tire4.setColor(new Color(98, 98, 98));
        tire4.shapeRotate("y", dyRot + 45);
        tire4.redraw();

    }

}
