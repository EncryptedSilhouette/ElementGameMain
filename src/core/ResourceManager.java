package core;

import data.GameObject;
import data.Scene;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ResourceManager {
    static HashMap<String, BufferedImage> textureRegistry = new HashMap<>();

    public static void load() {
        FileReader fileReader;
        JSONParser jsonParser;
        JSONArray contents;

        try {
            //Load valid keycodes
            fileReader = new FileReader("res\\data\\player_config.json");
            jsonParser = new JSONParser();
            contents = (JSONArray) jsonParser.parse(fileReader);
            for (Object keyObject: contents) {
                JSONObject key = (JSONObject) keyObject;
                Input.registerKey((String) key.get("id"),(int)(long) key.get("key_code"));
            }

            //Load textures
            fileReader = new FileReader("res\\data\\texture_dat.json");
            contents = (JSONArray) jsonParser.parse(fileReader);

            for (Object jsonObject: contents) {
                JSONObject texture = (JSONObject) jsonObject;
                String filePath = (String) texture.get("path");
                try { textureRegistry.put((String) texture.get("id"), ImageIO.read(new File(filePath))); }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(filePath);
                }
            }

            fileReader = new FileReader("res\\data\\entity_dat.json");
            contents = (JSONArray) jsonParser.parse(fileReader);
            for (Object fileData: contents) {
                fileReader = new FileReader((String) fileData);
                GameObject.registerObject((JSONObject) jsonParser.parse(fileReader));
            }

            fileReader = new FileReader("res\\data\\scene_dat.json");
            contents = (JSONArray) jsonParser.parse(fileReader);
            for (Object fileData: contents) {
                fileReader = new FileReader((String) fileData);
                Scene.registerScene((JSONObject) jsonParser.parse(fileReader));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getTexture(String id) {
        return textureRegistry.get(id);
    }
}
