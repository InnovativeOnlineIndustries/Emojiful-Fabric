package com.hrznstudio.emojiful;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hrznstudio.emojiful.api.Emoji;
import com.hrznstudio.emojiful.api.EmojiFromEmojipedia;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Emojiful implements ModInitializer {
    public static final String MODID = "emojiful";
    public static final Logger LOGGER = LogManager.getLogger("Emojiful");

    public static final Map<String, List<Emoji>> EMOJI_MAP = new HashMap<>();
    public static final List<Emoji> EMOJI_LIST = new ArrayList<>();
    public static boolean error = false;

    public static void main(String[] s) throws YamlException {
        //YamlReader reader = new YamlReader(new StringReader(readStringFromURL("https://raw.githubusercontent.com/InnovativeOnlineIndustries/emojiful-assets/master/Categories.yml")));
        //ArrayList<String> categories = (ArrayList<String>) reader.read();
        //for (String category : categories) {
        //    List<Emoji> emojis = readCategory(category);
        //    EMOJI_LIST.addAll(emojis);
        //    EMOJI_MAP.put(category, emojis);
        //}
        for (JsonElement categories : readJsonFromUrl("https://www.emojidex.com/api/v1/categories").getAsJsonObject().getAsJsonArray("categories")) {
            EMOJI_MAP.put(categories.getAsJsonObject().get("code").getAsString(), new ArrayList<>());
        }
        for (JsonElement jsonElement : readJsonFromUrl("https://cdn.emojidex.com/static/utf_emoji.json").getAsJsonArray()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            EmojiFromEmojipedia emoji = new EmojiFromEmojipedia();
            emoji.name = obj.get("code").getAsString();
            emoji.strings = Arrays.asList(emoji.name);
            emoji.location = emoji.name;
            EMOJI_MAP.get(obj.get("category").getAsString()).add(emoji);
            EMOJI_LIST.add(emoji);
        }
        //{"code":"at","moji":"🇦🇹","unicode":"1f1e6-1f1f9","category":"symbols","tags":[],"link":null,"base":"at","variants":["at"],"score":0,"r18":false,"customizations":[],"combinations":[]}
    }

    public static List<Emoji> readCategory(String cat) throws YamlException {
        YamlReader categoryReader = new YamlReader(new StringReader(readStringFromURL("https://raw.githubusercontent.com/InnovativeOnlineIndustries/emojiful-assets/master/" + cat)));
        return Lists.newArrayList(categoryReader.read(Emoji[].class));
    }

    public static String readStringFromURL(String requestURL) {
        try {
            try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static JsonElement readJsonFromUrl(String url) {
        String jsonText = readStringFromURL(url);
        JsonElement json = new JsonParser().parse(jsonText);
        return json;
    }

    @Override
    public void onInitialize() {

    }
}
