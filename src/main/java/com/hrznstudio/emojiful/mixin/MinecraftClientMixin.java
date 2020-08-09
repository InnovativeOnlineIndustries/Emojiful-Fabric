package com.hrznstudio.emojiful.mixin;

import com.hrznstudio.emojiful.Emojiful;
import com.hrznstudio.emojiful.render.EmojiFontRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow public TextRenderer textRenderer;
    @Shadow public abstract EntityRenderDispatcher getEntityRenderManager();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RunArgs args, CallbackInfo info) {
        if (!Emojiful.error) {
            this.textRenderer = new EmojiFontRenderer(MinecraftClient.getInstance(), MinecraftClient.getInstance().textRenderer);
            this.getEntityRenderManager().textRenderer = MinecraftClient.getInstance().textRenderer;
        }
    }
}
