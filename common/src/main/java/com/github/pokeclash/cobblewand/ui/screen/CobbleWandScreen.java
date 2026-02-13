package com.github.pokeclash.cobblewand.ui.screen;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureAssignments;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.trade.ModelWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import com.github.pokeclash.cobblewand.ui.util.NumericEditBox;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class CobbleWandScreen extends Screen {
    private EditBox nameField;
    private EditBox aspectField;

    private NumericEditBox speedEV;
    private NumericEditBox attackEV;
    private NumericEditBox defenseEV;
    private NumericEditBox spAtkEV;
    private NumericEditBox spDefEV;
    private NumericEditBox hpEV;

    private NumericEditBox speedIV;
    private NumericEditBox attackIV;
    private NumericEditBox defenseIV;
    private NumericEditBox spAtkIV;
    private NumericEditBox spDefIV;
    private NumericEditBox hpIV;

    private List<String> filteredSuggestions = new ArrayList<>();

    private ModelWidget modelWidget;

    private int BASE_WIDTH = 349;
    private int BASE_HEIGHT = 205;
    private int PORTRAIT_SIZE = 66;

    public CobbleWandScreen(String name) {
        super(Component.translatable(name));
    }

    private NumericEditBox createEVBox(int x, int y, String name) {
        NumericEditBox temp = new NumericEditBox(
                this.font,
                x,
                y,
                40,
                20,
                Component.literal(name),
                0, 252
        );
        temp.setHint(Component.literal(name));
        return temp;
    }

    private NumericEditBox createIVBox(int x, int y, String name) {
        NumericEditBox temp = new NumericEditBox(
                this.font,
                x,
                y,
                40,
                20,
                Component.literal(name),
                0, 31
        );
        temp.setHint(Component.literal(name));
        return temp;
    }

    @Override
    protected void init() {
        int guiX = (this.width - BASE_WIDTH) / 2;
        int guiY = (this.height - BASE_HEIGHT) / 2;

        nameField = new EditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 40,
                100,
                20,
                Component.literal("Enter name")
        );
        nameField.setHint(Component.literal("Pokemon Species"));
        this.addRenderableWidget(nameField);
        aspectField = new EditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 15,
                100,
                20,
                Component.literal("Aspects")
        );
        aspectField.setHint(Component.literal("Aspects"));
        this.addRenderableWidget(aspectField);

        // Left column
        hpEV = this.createEVBox(guiX + 300, guiY + 40, "HP");
        this.addRenderableWidget(hpEV);
        attackEV = this.createEVBox(guiX + 300, guiY + 60, "Atk EV");
        this.addRenderableWidget(attackEV);
        defenseEV = this.createEVBox(guiX + 300, guiY + 80, "Def EV");
        this.addRenderableWidget(defenseEV);
        // Right column
        spAtkEV = this.createEVBox(guiX + 345, guiY + 40, "SpAtk EV");
        this.addRenderableWidget(spAtkEV);
        spDefEV = this.createEVBox(guiX + 345, guiY + 60, "SpDef EV");
        this.addRenderableWidget(spDefEV);
        speedEV = this.createEVBox(guiX + 345, guiY + 80, "Spd EV");
        this.addRenderableWidget(speedEV);

        // Left column IVs
        hpIV = this.createIVBox(guiX + 300, guiY + 130, "HP IV");
        this.addRenderableWidget(hpIV);
        attackIV = this.createIVBox(guiX + 300, guiY + 150, "Atk IV");
        this.addRenderableWidget(attackIV);
        defenseIV = this.createIVBox(guiX + 300, guiY + 170, "Def IV");
        this.addRenderableWidget(defenseIV);
        // Right column IVs
        spAtkIV = this.createIVBox(guiX + 345, guiY + 130, "SpAtk IV");
        this.addRenderableWidget(spAtkIV);
        spDefIV = this.createIVBox(guiX + 345, guiY + 150, "SpDef IV");
        this.addRenderableWidget(spDefIV);
        speedIV = this.createIVBox(guiX + 345, guiY + 170, "Spd IV");
        this.addRenderableWidget(speedIV);


        // Button
        this.addRenderableWidget(Button.builder(
                Component.literal("Set"),
                this::setPokemon
        ).bounds(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());

        RenderablePokemon renderablePokemon = new Pokemon().asRenderablePokemon();
        modelWidget = new ModelWidget(guiX + 6, guiY + 27, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 2f, 325f, -10.0);
    }

    private void setPokemon(Button button) {
        setPokemon(nameField.getValue());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        modelWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        if (nameField.isFocused()) {
            int y = nameField.getY() + 22;

            for (int i = 0; i < filteredSuggestions.size() && i < 5; i++) {
                String suggestion = filteredSuggestions.get(i);

                guiGraphics.fill(
                        nameField.getX(),
                        y,
                        nameField.getX() + 200,
                        y + 12,
                        0xFF2C2C2C
                );

                guiGraphics.drawString(
                        this.font,
                        suggestion,
                        nameField.getX() + 4,
                        y + 2,
                        0xFFFFFF
                );

                y += 12;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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

    private void updateSuggestions() {
        String input = nameField.getValue().toLowerCase();

        filteredSuggestions = PokemonSpecies.getSpecies()
                .stream()
                .map(s -> s.resourceIdentifier.getPath()) // convert Species → String
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                .toList();

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (nameField.isFocused()) {
            int y = nameField.getY() + 22;

            for (String suggestion : filteredSuggestions) {
                if (mouseX >= nameField.getX() &&
                        mouseX <= nameField.getX() + 200 &&
                        mouseY >= y &&
                        mouseY <= y + 12) {

                    nameField.setValue(suggestion);
                    filteredSuggestions = List.of();
                    return true;
                }
                y += 12;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void setPokemon(String species) {
        int x = (this.width - BASE_WIDTH) / 2;
        int y = (this.height - BASE_HEIGHT) / 2;

        Species pokemonSpecies = PokemonSpecies.getByName(species);
        Pokemon pokemon = new Pokemon();

        if (pokemonSpecies != null) {
            pokemon.setSpecies(pokemonSpecies);
        }
        if (!aspectField.getValue().isBlank()) {
            Set<String> aspects = Arrays.stream(aspectField.getValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            pokemon.setForcedAspects(aspects);
        }

        setEV(pokemon);
        setIV(pokemon);

        RenderablePokemon renderablePokemon = pokemon.asRenderablePokemon();

        NetworkManager.sendToServer(new PokemonSetPacket(pokemon));
        modelWidget = new ModelWidget(x + 6, y + 27, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 2f, 325f, -10.0);
    }

    private void setEV(Pokemon pokemon) {
        if (hpIV.getIntValue() != -1) {
            pokemon.setEV(Stats.HP, hpIV.getIntValue());
        }

        if (attackIV.getIntValue() != -1) {
            pokemon.setEV(Stats.ATTACK, attackIV.getIntValue());
        }

        if (defenseIV.getIntValue() != -1) {
            pokemon.setEV(Stats.DEFENCE, defenseIV.getIntValue());
        }

        if (spAtkIV.getIntValue() != -1) {
            pokemon.setEV(Stats.SPECIAL_ATTACK, spAtkIV.getIntValue());
        }

        if (spDefIV.getIntValue() != -1) {
            pokemon.setEV(Stats.SPECIAL_DEFENCE, spDefIV.getIntValue());
        }

        if (speedIV.getIntValue() != -1) {
            pokemon.setEV(Stats.SPEED, speedIV.getIntValue());
        }
    }

    private void setIV(Pokemon pokemon) {
        if (hpIV.getIntValue() != -1) {
            pokemon.setIV(Stats.HP, hpIV.getIntValue());
        }

        if (attackIV.getIntValue() != -1) {
            pokemon.setIV(Stats.ATTACK, attackIV.getIntValue());
        }

        if (defenseIV.getIntValue() != -1) {
            pokemon.setIV(Stats.DEFENCE, defenseIV.getIntValue());
        }

        if (spAtkIV.getIntValue() != -1) {
            pokemon.setIV(Stats.SPECIAL_ATTACK, spAtkIV.getIntValue());
        }

        if (spDefIV.getIntValue() != -1) {
            pokemon.setIV(Stats.SPECIAL_DEFENCE, spDefIV.getIntValue());
        }

        if (speedIV.getIntValue() != -1) {
            pokemon.setIV(Stats.SPEED, speedIV.getIntValue());
        }
    }
}
