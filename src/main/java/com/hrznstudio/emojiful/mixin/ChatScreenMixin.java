package com.hrznstudio.emojiful.mixin;

import com.hrznstudio.emojiful.Emojiful;
import com.hrznstudio.emojiful.gui.EmojiButton;
import com.hrznstudio.emojiful.gui.ParentButton;
import com.hrznstudio.emojiful.gui.TranslucentButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        int x = this.width - 50;
        int y = this.height - 40;
        int amountPage = 5*5;
        List<ButtonWidget> child = new ArrayList<>();
        List<ButtonWidget> allExtraChild = new ArrayList<>();
        Emojiful.EMOJI_MAP.keySet().forEach(s -> {
            List<EmojiButton> extraChild = new ArrayList<>();
            List<ButtonWidget> arrowButtons = new ArrayList<>();
            Emojiful.EMOJI_MAP.get(s).forEach(emoji -> {
                if (emoji != null && emoji.strings != null){
                    EmojiButton button = new EmojiButton(x - 82 - 42 - 42*(extraChild.size() % 5), y - 22 *((extraChild.size()%amountPage )/ 5), 40, 20, new LiteralText(emoji.strings.get(0)), p_onPress_2_ -> {
                        this.chatField.setText(this.chatField.getText() + " " + emoji.strings.get(0));
                    }, extraChild.size() / amountPage);
                    button.visible = false;
                    this.addButton(button);
                    extraChild.add(button);
                    allExtraChild.add(button);
                }
            });
            ParentButton button = new ParentButton(x - 82, y - 22 * child.size(), 80, 20, s.replace(".yml", ""), b -> {
                allExtraChild.forEach(ec -> ec.visible = false);
                arrowButtons.forEach(ec-> ec.visible = true);
                extraChild.forEach(ec -> ec.visible = false);
                for (EmojiButton emojiButton : extraChild) {
                    if (emojiButton.getPage() == ((ParentButton) b).getPage()){
                        emojiButton.visible = true;
                    }
                }
            });
            if (extraChild.size() > amountPage){
                TranslucentButton rightButton = new TranslucentButton(x - 82 - 42, y - 22*6, 40, 20, ">", p_onPress_1_ -> {
                    int page = button.getPage() + 1;
                    if (page > extraChild.size() / amountPage) page = 0;
                    button.setPage(page);
                });
                TranslucentButton leftButton = new TranslucentButton(x - 82 - 42 - 42*4, y - 22*6, 40, 20, "<", p_onPress_1_ -> {
                    int page = button.getPage() - 1;
                    if (page < 0) page = extraChild.size() / amountPage;
                    button.setPage(page);
                });
                rightButton.visible = false;
                leftButton.visible = false;
                arrowButtons.add(rightButton);
                arrowButtons.add(leftButton);
                allExtraChild.add(rightButton);
                allExtraChild.add(leftButton);
                this.addButton(rightButton);
                this.addButton(leftButton);
            }
            button.visible = false;
            this.addButton(button);
            child.add(button);
        });
        this.addButton(new TranslucentButton(x, y, 40, 20, "Emoji", press -> {
            child.forEach(button -> button.visible = true);
        }));
    }
}
