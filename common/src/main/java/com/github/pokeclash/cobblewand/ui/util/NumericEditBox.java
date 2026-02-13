package com.github.pokeclash.cobblewand.ui.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumericEditBox extends EditBox {

    private final int min;
    private final int max;

    public NumericEditBox(Font font, int x, int y, int width, int height,
                          Component message, int min, int max) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;

        // Limit max length based on max value digits
        this.setMaxLength(String.valueOf(max).length());
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {

        // Allow only digits
        if (!Character.isDigit(codePoint)) {
            return false;
        }

        boolean result = super.charTyped(codePoint, modifiers);
        clampToRange();
        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        clampToRange();
        return result;
    }

    private void clampToRange() {
        if (this.getValue().isEmpty()) return;

        try {
            int value = Integer.parseInt(this.getValue());

            if (value < min) {
                this.setValue(String.valueOf(min));
            } else if (value > max) {
                this.setValue(String.valueOf(max));
            }

        } catch (NumberFormatException ignored) {
            this.setValue(String.valueOf(min));
        }
    }

    public int getIntValue() {
        if (this.getValue().isEmpty()) return -1;
        return Integer.parseInt(this.getValue());
    }
}
