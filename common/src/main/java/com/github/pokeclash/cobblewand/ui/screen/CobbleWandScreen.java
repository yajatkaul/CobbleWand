package com.github.pokeclash.cobblewand.ui.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.client.gui.trade.ModelWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import com.github.pokeclash.cobblewand.ui.util.NumericEditBox;
import com.github.pokeclash.cobblewand.ui.util.SuggestionEditBox;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.stream.Collectors;

public class CobbleWandScreen extends Screen {
    private SuggestionEditBox nameField;
    private EditBox aspectField;

    private SuggestionEditBox move1;
    private SuggestionEditBox move2;
    private SuggestionEditBox move3;
    private SuggestionEditBox move4;

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

    private NumericEditBox level;

    private ModelWidget modelWidget;

    private final int BASE_WIDTH = 349;
    private final int BASE_HEIGHT = 205;
    private final int PORTRAIT_SIZE = 66;

    private final Pokemon startPokemon = new Pokemon();

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

    private SuggestionEditBox makeMoveBox(int x, int y, String name) {
        SuggestionEditBox temp = new SuggestionEditBox(
                this.font,
                x,
                y,
                100,
                20,
                Component.literal(name),
                Moves.all()
                        .stream()
                        .map(m -> m.getName().toLowerCase(Locale.ROOT))
                        .toList()
        );
        temp.setHint(Component.literal(name));
        return temp;
    }

    @Override
    protected void init() {
        int guiX = (this.width - BASE_WIDTH) / 2;
        int guiY = (this.height - BASE_HEIGHT) / 2;

        nameField = new SuggestionEditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 40,
                200,
                20,
                Component.literal("Pokemon"),
                PokemonSpecies.getSpecies()
                        .stream()
                        .map(s -> s.resourceIdentifier.getPath())
                        .toList()
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

        move1 = new SuggestionEditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 + 10,
                100,
                20,
                Component.literal("Aspects"),
                Moves.all()
                        .stream()
                        .map(m -> m.getName().toLowerCase(Locale.ROOT))
                        .toList()
        );
        move1 = makeMoveBox(guiX + 73, guiY + 115, "Move 1");
        this.addRenderableWidget(move1);
        move2 = makeMoveBox(guiX + 175, guiY + 115, "Move 2");
        this.addRenderableWidget(move2);
        move3 = makeMoveBox(guiX + 73, guiY + 140, "Move 3");
        this.addRenderableWidget(move3);
        move4 = makeMoveBox(guiX + 175, guiY + 140, "Move 4");
        this.addRenderableWidget(move4);

        level = new NumericEditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 + 60,
                40,
                20,
                Component.literal("Level"),
                1, Cobblemon.config.getMaxPokemonLevel()
        );
        level.setHint(Component.literal("Level"));
        this.addRenderableWidget(level);

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
        ).bounds(this.width / 2 - 100, this.height / 2 + 90, 200, 20).build());

        RenderablePokemon renderablePokemon = new Pokemon().asRenderablePokemon();
        modelWidget = new ModelWidget(guiX + 6, guiY + 27, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 2f, 325f, -10.0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        modelWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        nameField.renderSuggestions(guiGraphics, this.font);

        move1.renderSuggestions(guiGraphics, this.font);
        move2.renderSuggestions(guiGraphics, this.font);
        move3.renderSuggestions(guiGraphics, this.font);
        move4.renderSuggestions(guiGraphics, this.font);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setPokemon(Button button) {
        int x = (this.width - BASE_WIDTH) / 2;
        int y = (this.height - BASE_HEIGHT) / 2;

        Species pokemonSpecies = PokemonSpecies.getByName(nameField.getValue());

        if (pokemonSpecies != null) {
            startPokemon.setSpecies(pokemonSpecies);
        }
        if (!aspectField.getValue().isBlank()) {
            Set<String> aspects = Arrays.stream(aspectField.getValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            startPokemon.setForcedAspects(aspects);
        }

        setEV(startPokemon);
        setIV(startPokemon);
        setMoves(startPokemon);

        if (level.getIntValue() != -1) {
            startPokemon.setLevel(level.getIntValue());
        }

        RenderablePokemon renderablePokemon = startPokemon.asRenderablePokemon();

        NetworkManager.sendToServer(new PokemonSetPacket(startPokemon));
        modelWidget = new ModelWidget(x + 6, y + 27, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 2f, 325f, -10.0);
    }

    private void setMoves(Pokemon pokemon) {
        Set<String> movesAdded = new HashSet<>();
        if (!move1.getValue().isBlank()) {
            MoveTemplate move = Moves.getByName(move1.getValue());
            if (move != null) {
                pokemon.getMoveSet().setMove(0, move.create());
                movesAdded.add(move.getName());
            }
        }
        if (!move2.getValue().isBlank()) {
            MoveTemplate move = Moves.getByName(move2.getValue());
            if (move != null && !movesAdded.contains(move.getName())) {
                pokemon.getMoveSet().setMove(1, move.create());
                movesAdded.add(move.getName());
            }
        }
        if (!move3.getValue().isBlank()) {
            MoveTemplate move = Moves.getByName(move3.getValue());
            if (move != null && !movesAdded.contains(move.getName())) {
                pokemon.getMoveSet().setMove(2, move.create());
                movesAdded.add(move.getName());
            }
        }
        if (!move4.getValue().isBlank()) {
            MoveTemplate move = Moves.getByName(move4.getValue());
            if (move != null && !movesAdded.contains(move.getName())) {
                pokemon.getMoveSet().setMove(3, move.create());
                movesAdded.add(move.getName());
            }
        }
    }

    private void setEV(Pokemon pokemon) {
        if (hpEV.getIntValue() != -1) {
            pokemon.setEV(Stats.HP, hpEV.getIntValue());
        }

        if (attackEV.getIntValue() != -1) {
            pokemon.setEV(Stats.ATTACK, attackEV.getIntValue());
        }

        if (defenseEV.getIntValue() != -1) {
            pokemon.setEV(Stats.DEFENCE, defenseEV.getIntValue());
        }

        if (spAtkEV.getIntValue() != -1) {
            pokemon.setEV(Stats.SPECIAL_ATTACK, spAtkEV.getIntValue());
        }

        if (spDefEV.getIntValue() != -1) {
            pokemon.setEV(Stats.SPECIAL_DEFENCE, spDefEV.getIntValue());
        }

        if (speedEV.getIntValue() != -1) {
            pokemon.setEV(Stats.SPEED, speedEV.getIntValue());
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
