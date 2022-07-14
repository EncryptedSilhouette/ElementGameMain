package data.components;

import data.ObjectComponent;
import org.json.simple.JSONObject;

public class Renderer extends ObjectComponent {

    private int renderLayer;

    @Override
    public void init() {
        gameObject.isRenderable = true;
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

    public int getRenderLayer() {
        return renderLayer;
    }
}
