package data.components;

import core.Input;
import data.ObjectComponent;
import org.json.simple.JSONObject;

import java.awt.event.KeyEvent;

public class Player extends ObjectComponent {
    int speed = 5;
    Transform transform;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        transform = gameObject.transform;
    }

    @Override
    public void update() {
        int moveNorth = Input.getKeyState(KeyEvent.VK_W) ? -1 : 0,
            moveSouth = Input.getKeyState(KeyEvent.VK_S) ? 1 : 0,
            moveWest = Input.getKeyState(KeyEvent.VK_A) ? -1 : 0,
            moveEast = Input.getKeyState(KeyEvent.VK_D) ? 1 : 0;

        //Adds both the x and y direction variables, if both are not equal to zero, that means the object is being moved in both axes
        //This creates an issue since diagonal movement is faster than movement in a single axis.
        //So if the object is being moved in both directions then it will apply a diagonal multiplier to make the distance constant in any direction
        //This multiplier is an irrational number which is the ratio of the side of a square to its diagonal in simplified form, AKA the square's equivalent of PI
        float diagonalMulti = ((moveNorth + moveSouth != 0) && (moveWest + moveEast != 0)? 1.41421f : 1);
        transform.setPosition((int) (transform.getPosX() + (moveWest + moveEast) / diagonalMulti * speed),
                              (int) (transform.getPosY() + (moveNorth + moveSouth) / diagonalMulti * speed));

    }

    @Override
    public void generate(JSONObject componentData) {
        speed = (int)(long) componentData.get("speed");
    }
}
