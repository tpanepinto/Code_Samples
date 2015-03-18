import java.awt.*;

/**
 * Created by tim on 10/11/14.
 * This class is an object with a table and two cakes to be judged
 */
public class Contest extends Object3D{


    public Contest(){

    }


    @Override
    protected void drawPrimitives() {
        Box table = new Box();
        table.setLocation(xLoc,yLoc,zLoc);
        table.setSize(1, 0.05f, 1);
        table.setColor(colors.get(0));
        table.redraw();

        Cake cake1 = new Cake();
        cake1.setLocation(xLoc, yLoc + 0.3f, zLoc + 0.3f);
        cake1.setColor(new Color(46, 172, 224));
        cake1.redraw();


        Cake cake2 = new Cake();
        cake2.setLocation(xLoc + 0.3f, yLoc + 0.3f, zLoc);
        //cake1.setSize(0.5f, 0.2f, 0.5f);
        cake2.setColor(new Color(255, 251, 251));
        cake2.redraw();
    }







}
