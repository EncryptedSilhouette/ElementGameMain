package data.components;

import core.ResourceManager;
import data.ObjectComponent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer extends ObjectComponent {
    private int renderLayer;
    private BufferedImage sprite;

    private Rectangle bounds;

    @Override
    public void init() {
        gameObject.renderer = this;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
    }

    @Override
    public void generate(JSONObject componentData) {
        sprite = ResourceManager.getTexture((String) componentData.get("texture_id"));
        renderLayer = (int) (long) componentData.get("render_layer");
    }

    public void render(Graphics2D graphics) {
        bounds = gameObject.transform.getBounds();
        graphics.drawImage(sprite, bounds.x, bounds.y, bounds.width, bounds.height, null);
    }

    public int getRenderLayer() {
        return renderLayer;
    }
}
