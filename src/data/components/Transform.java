package data.components;

import data.ObjectComponent;
import org.json.simple.JSONObject;

import java.awt.*;

public class Transform extends ObjectComponent {

    private int x = 0, y = 0, width = 32, height = 32, scaleX = 1, scaleY = 1;

    @Override
    public void init() {
        gameObject.transform = this;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
    }

    @Override
    public void generate(JSONObject componentData) {

    }

    public int getMinX() {
        return x;
    }

    public int getMinY() {
        return y;
    }

    public int getMaxX() {
        return x + getWidth();
    }

    public int getMaxY() {
        return y + getHeight();
    }

    public int getWidth() {
        return width * scaleX;
    }

    public int getHeight() {
        return height * scaleY;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public int getPosX() {
        return x + getWidth() / 2;
    }

    public int getPosY() {
        return y + getHeight() / 2;
    }

    public Point getPosition() {
        return new Point(getPosX(), getPosY());
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosX(int posX) {
        x = posX - getWidth() / 2;
    }

    public void setPosY(int posY) {
        y = posY - getHeight() / 2;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setScaleX(int scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(int scaleY) {
        this.scaleY = scaleY;
    }

    public void setPosition(int posX, int posY) {
        x = posX - getWidth() / 2;
        y = posY - getHeight() / 2;
    }
}
