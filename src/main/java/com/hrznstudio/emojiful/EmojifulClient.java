package com.hrznstudio.emojiful;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.JsonElement;
import com.hrznstudio.emojiful.api.Emoji;
import com.hrznstudio.emojiful.api.EmojiFromGithub;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class EmojifulClient implements ClientModInitializer {

    private void preInitEmojis() {
        loadCustomEmojis();
        loadGithubEmojis();
    }

    private void loadCustomEmojis(){
        try {
            YamlReader reader = new YamlReader(new StringReader(Emojiful.readStringFromURL("https://raw.githubusercontent.com/InnovativeOnlineIndustries/emojiful-assets/master/Categories.yml")));
            ArrayList<String> categories = (ArrayList<String>) reader.read();
            for (String category : categories) {
                List<Emoji> emojis = Emojiful.readCategory(category);
                Emojiful.EMOJI_LIST.addAll(emojis);
                Emojiful.EMOJI_MAP.put(category, emojis);
            }
        } catch (YamlException e) {
            Emojiful.error = true;
        }
    }

    private void loadApiEmojis(){
        for (JsonElement categories : Emojiful.readJsonFromUrl("https://www.emojidex.com/api/v1/categories").getAsJsonObject().getAsJsonArray("categories")) {
            Emojiful.EMOJI_MAP.put(categories.getAsJsonObject().get("code").getAsString(), new ArrayList<>());
        }

    }

    private void loadGithubEmojis(){
        Emojiful.EMOJI_MAP.put("Github", new ArrayList<>());
        for (Map.Entry<String, JsonElement> entry : Emojiful.readJsonFromUrl("https://api.github.com/emojis").getAsJsonObject().entrySet()) {
            EmojiFromGithub emoji = new EmojiFromGithub();
            emoji.name = entry.getKey();
            emoji.strings = new ArrayList<>();
            emoji.strings.add(":" + entry.getKey() + ":");
            emoji.location = entry.getKey();
            emoji.url = entry.getValue().getAsString();
            Emojiful.EMOJI_MAP.get("Github").add(emoji);
            Emojiful.EMOJI_LIST.add(emoji);
        }
    }

    @Override
    public void onInitializeClient() {
        preInitEmojis();
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((spriteAtlasTexture, registry) -> {
            registry.register(new Identifier(Emojiful.MODID, "26a0"));
            registry.register(new Identifier(Emojiful.MODID, "26d4"));
        });
        EmojifulConfig.getInstance();
        Emojiful.LOGGER.info("Loaded {} emojis", Emojiful.EMOJI_LIST.size());
    }
}
