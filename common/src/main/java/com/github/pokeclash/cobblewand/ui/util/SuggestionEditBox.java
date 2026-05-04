package com.github.pokeclash.cobblewand.ui.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public class SuggestionEditBox extends EditBox {
    private List<String> allSuggestions;
    private List<String> filtered = List.of();

    public SuggestionEditBox(Font font,
                             int x, int y,
                             int width, int height,
                             Component message,
                             List<String> suggestions) {

        super(font, x, y, width, height, message);
        this.allSuggestions = suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.allSuggestions = suggestions;
        updateSuggestions();
    }

    private void updateSuggestions() {
        String input = this.getValue().toLowerCase(Locale.ROOT);

        filtered = allSuggestions.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(input))
                .limit(5)
                .toList();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean result = super.charTyped(codePoint, modifiers);
        updateSuggestions();
        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        updateSuggestions();
        return result;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isFocused()) {
            int y = this.getY() + 22;

            for (String suggestion : filtered) {
                if (mouseX >= this.getX() &&
                        mouseX <= this.getX() + this.getWidth() &&
                        mouseY >= y &&
                        mouseY <= y + 12) {

                    this.setValue(suggestion);
                    filtered = List.of();
                    return true;
                }
                y += 12;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void renderSuggestions(GuiGraphics guiGraphics, Font font) {
        if (!this.isFocused()) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);

        int y = this.getY() + 22;

        for (String suggestion : filtered) {

            guiGraphics.fill(
                    this.getX(),
                    y,
                    this.getX() + this.getWidth(),
                    y + 12,
                    0xFF2C2C2C
            );

            guiGraphics.drawString(
                    font,
                    suggestion,
                    this.getX() + 4,
                    y + 2,
                    0xFFFFFF
            );

            y += 12;
        }

        guiGraphics.pose().popPose();
    }
}