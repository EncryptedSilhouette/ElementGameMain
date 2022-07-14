package core;

import data.GameObject;
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

public class ResourceLoader {
    static HashMap<String, BufferedImage> textureRegistry = new HashMap<>();

    public static void load() {
        FileReader fileReader;
        JSONParser jsonParser;
        JSONArray contents;

        try {
            fileReader = new FileReader("res\\textures.json");
            jsonParser = new JSONParser();
            contents = (JSONArray) jsonParser.parse(fileReader);

            for (Object jsonObject: contents) {
                JSONObject texture = (JSONObject) jsonObject;
                String filePath = (String) texture.get("file_path");

                try { textureRegistry.put((String) texture.get("id"), ImageIO.read(new File(filePath))); }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(filePath);
                }
            }

            fileReader = new FileReader("res\\entities");
            contents = (JSONArray) jsonParser.parse(fileReader);

            for (Object jsonObject: contents) {
                GameObject.registerObject((JSONObject) jsonObject);
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getTexture(String id) {
        return textureRegistry.get(id);
    }
}
