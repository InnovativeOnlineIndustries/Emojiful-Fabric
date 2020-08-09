package com.hrznstudio.emojiful.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class TranslucentButton extends ButtonWidget {

    public TranslucentButton(int widthIn, int heightIn, int width, int height, String text, PressAction onPress) {
        super(widthIn, heightIn, width, height, new LiteralText(text), onPress);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer fontrenderer = minecraft.textRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        DrawableHelper.fill(stack, x, y, x + width, y + height, Integer.MIN_VALUE);
        int j = 16777215;
        this.drawCenteredString(stack,fontrenderer, new LiteralText((this.isHovered() ? Formatting.YELLOW : "") + this.getMessage().getString()).asString(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

}
