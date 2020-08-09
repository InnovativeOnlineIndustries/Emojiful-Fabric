package com.hrznstudio.emojiful.gui;

public class ParentButton extends TranslucentButton {

    private int page;

    public ParentButton(int widthIn, int heightIn, int width, int height, String text, PressAction onPress) {
        super(widthIn, heightIn, width, height, text, onPress);
        this.page = 0;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        onPress.onPress(this);
    }
}
