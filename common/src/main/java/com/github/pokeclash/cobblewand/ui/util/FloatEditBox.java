package com.github.pokeclash.cobblewand.ui.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class FloatEditBox extends EditBox {

    private final float min;
    private final float max;

    public FloatEditBox(Font font, int x, int y, int width, int height,
                        Component message, float min, float max) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;

        // Set reasonable max length (including decimal point)
        this.setMaxLength(String.valueOf(max).length() + 2);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {

        String current = this.getValue();

        // Allow digits
        if (Character.isDigit(codePoint)) {
            boolean result = super.charTyped(codePoint, modifiers);
            clampToRange();
            return result;
        }

        // Allow one decimal point
        if (codePoint == '.' && !current.contains(".")) {
            return super.charTyped(codePoint, modifiers);
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        clampToRange();
        return result;
    }

    private void clampToRange() {
        String valueStr = this.getValue();

        if (valueStr.isEmpty() || valueStr.equals(".")) return;

        try {
            float value = Float.parseFloat(valueStr);

            if (value < min) {
                this.setValue(String.valueOf(min));
            } else if (value > max) {
                this.setValue(String.valueOf(max));
            }

        } catch (NumberFormatException ignored) {
            this.setValue(String.valueOf(min));
        }
    }

    public float getFloatValue() {
        if (this.getValue().isEmpty() || this.getValue().equals(".")) return -1f;
        return Float.parseFloat(this.getValue());
    }
}
