package com.hrznstudio.emojiful.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class EmojiButton extends ButtonWidget {

    private int page;

    public EmojiButton(int x, int y, int width, int height, Text message, PressAction onPress, int page) {
        super(x, y, width, height, message, onPress);
        this.page = page;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float delta) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        DrawableHelper.fill(stack, x, y, x + width, y + height, Integer.MIN_VALUE);
        int j = 16777215;
        this.drawCenteredString(stack, MinecraftClient.getInstance().textRenderer, this.getMessage().asString(), (int) (this.x + this.width / 2 + MinecraftClient.getInstance().textRenderer.getWidth(this.getMessage().asString()) / 2) - 4, this.y + (this.height - 8) / 2 - 4, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.5f, 0.5f, 0.5f);
        this.drawCenteredString(stack, MinecraftClient.getInstance().textRenderer, new LiteralText((this.isHovered() ? Formatting.YELLOW : "") + this.getMessage().getString() + "\u00a7" +"-").asString(), (this.x + this.width / 2) * 2 , (this.y + (this.height - 8) / 2 + 8)*2 , j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        RenderSystem.scalef(1,1,1);
        RenderSystem.popMatrix();
    }

    public int getPage() {
        return page;
    }
}
