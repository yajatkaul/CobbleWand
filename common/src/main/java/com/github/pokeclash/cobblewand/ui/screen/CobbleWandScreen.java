package com.github.pokeclash.cobblewand.ui.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.client.gui.trade.ModelWidget;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.github.pokeclash.cobblewand.mixin.PokemonAccessor;
import com.github.pokeclash.cobblewand.mixin.TeraTypesAccessor;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import com.github.pokeclash.cobblewand.ui.util.NumericEditBox;
import com.github.pokeclash.cobblewand.ui.util.SuggestionEditBox;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.stream.Collectors;

public class CobbleWandScreen extends Screen {
    private SuggestionEditBox nameField;
    private EditBox aspectField;
    private SuggestionEditBox natureField;
    private SuggestionEditBox teraField;

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
    private NumericEditBox scale;
    private NumericEditBox friendShip;
    private NumericEditBox dmaxLevel;

    private ModelWidget modelWidget;

    private Checkbox gmaxFactor;

    private final int BASE_WIDTH = 349;
    private final int BASE_HEIGHT = 205;
    private final int PORTRAIT_SIZE = 66;

    private final Pokemon startPokemon = new Pokemon();

    public CobbleWandScreen(String name) {
        super(Component.translatable(name));
    }

    @Override
    protected void init() {
        startPokemon.setSpecies(PokemonSpecies.random());

        int guiX = (this.width - BASE_WIDTH) / 2;
        int guiY = (this.height - BASE_HEIGHT) / 2;

        nameField = makeSuggestionEditBox(
                guiX + 73,
                guiY + 50,
                "Pokemon",
                PokemonSpecies.getSpecies()
                        .stream()
                        .map(s -> s.resourceIdentifier.getPath())
                        .toList()
        );

        teraField = makeSuggestionEditBox(
                guiX + 175,
                guiY + 50,
                "Tera",
                TeraTypesAccessor.getTypes().values()
                        .stream()
                        .map(t -> t.showdownId().toLowerCase(Locale.ROOT))
                        .toList(),
                70
        );

        gmaxFactor = makeCheckBox(
                guiX + 175,
                guiY + 77,
                "Gmax Factor"
        );

        aspectField = makeSuggestionEditBox(
                guiX + 73,
                guiY + 75,
                "Aspects",
                List.of()
        );

        move1 = makeMoveBox(guiX + 73, guiY + 115, "Move 1");
        move2 = makeMoveBox(guiX + 175, guiY + 115, "Move 2");
        move3 = makeMoveBox(guiX + 73, guiY + 140, "Move 3");
        move4 = makeMoveBox(guiX + 175, guiY + 140, "Move 4");

        // Left column
        hpEV = this.createEVBox(guiX + 290, guiY + 40, "HP EV");
        attackEV = this.createEVBox(guiX + 290, guiY + 60, "Atk EV");
        defenseEV = this.createEVBox(guiX + 290, guiY + 80, "Def EV");
        // Right column
        spAtkEV = this.createEVBox(guiX + 335, guiY + 40, "SpAtk EV");
        spDefEV = this.createEVBox(guiX + 335, guiY + 60, "SpDef EV");
        speedEV = this.createEVBox(guiX + 335, guiY + 80, "Spd EV");

        // Left column IVs
        hpIV = this.createIVBox(guiX + 290, guiY + 130, "HP IV");
        attackIV = this.createIVBox(guiX + 290, guiY + 150, "Atk IV");
        defenseIV = this.createIVBox(guiX + 290, guiY + 170, "Def IV");
        // Right column IVs
        spAtkIV = this.createIVBox(guiX + 335, guiY + 130, "SpAtk IV");
        spDefIV = this.createIVBox(guiX + 335, guiY + 150, "SpDef IV");
        speedIV = this.createIVBox(guiX + 335, guiY + 170, "Spd IV");

        level = makeNumericBox(guiX - 20, guiY + 165, "Lvl", 1, Cobblemon.config.getMaxPokemonLevel(), 50);

        scale = makeNumericBox(guiX + 32, guiY + 165, "Scale", 1, 100, 50);

        friendShip = makeNumericBox(guiX + 84, guiY + 165, "Frnd", 1, 255, 50);

        dmaxLevel = makeNumericBox(guiX + 136, guiY + 165, "DLvl", 0, Cobblemon.config.getMaxDynamaxLevel(), 50);

        natureField = makeSuggestionEditBox(
                guiX + 188,
                guiY + 165,
                "Nature",
                Natures.all().stream().map(n -> n.getName().getPath()).toList(),
                60
        );

        // Button
        this.addRenderableWidget(Button.builder(
                Component.literal("Set"),
                this::setPokemon
        ).bounds(guiX + 73, guiY + 190, 200, 20).build());

        RenderablePokemon renderablePokemon = startPokemon.asRenderablePokemon();
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

        natureField.renderSuggestions(guiGraphics, this.font);

        teraField.renderSuggestions(guiGraphics, this.font);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    private void setPokemon(Button button) {
        int x = (this.width - BASE_WIDTH) / 2;
        int y = (this.height - BASE_HEIGHT) / 2;

        if (!nameField.getValue().isBlank()) {
            Species pokemonSpecies = PokemonSpecies.getByName(nameField.getValue());
            if (pokemonSpecies != null) {
                startPokemon.setSpecies(pokemonSpecies);
            }
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

        if (scale.getIntValue() != -1) {
            startPokemon.setScaleModifier(scale.getIntValue());
        }

        if (friendShip.getIntValue() != -1) {
            ((PokemonAccessor) startPokemon).setFriendship(friendShip.getIntValue());
        }

        if (!natureField.getValue().isBlank()) {
            Nature nature = Natures.getNature(nameField.getValue());
            if (nature != null) {
                startPokemon.setNature(nature);
            }
        }

        if (dmaxLevel.getIntValue() != -1) {
            startPokemon.setDmaxLevel(dmaxLevel.getIntValue());
        }

        if (!teraField.getValue().isBlank()) {
            TeraType type = TeraTypes.get(teraField.getValue());
            if (type != null) {
                startPokemon.setTeraType(type);
            }
        }

        if (gmaxFactor.selected()) {
            startPokemon.setGmaxFactor(true);
        }

        RenderablePokemon renderablePokemon = startPokemon.asRenderablePokemon();

        NetworkManager.sendToServer(new PokemonSetPacket(startPokemon));
        modelWidget = new ModelWidget(x + 6, y + 27, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 2f, 325f, -10.0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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

    private NumericEditBox createEVBox(int x, int y, String name) {
        NumericEditBox temp = new NumericEditBox(
                this.font,
                x,
                y,
                50,
                20,
                Component.literal(name),
                0, 252
        );
        temp.setHint(Component.literal(name));
        this.addRenderableWidget(temp);
        return temp;
    }

    private NumericEditBox createIVBox(int x, int y, String name) {
        NumericEditBox temp = new NumericEditBox(
                this.font,
                x,
                y,
                50,
                20,
                Component.literal(name),
                0, 31
        );
        temp.setHint(Component.literal(name));
        this.addRenderableWidget(temp);
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
        this.addRenderableWidget(temp);
        return temp;
    }

    private NumericEditBox makeNumericBox(int x, int y, String name, int min, int max, int width) {
        NumericEditBox temp = new NumericEditBox(
                this.font,
                x,
                y,
                width,
                20,
                Component.literal(name),
                min, max
        );
        temp.setHint(Component.literal(name));
        this.addRenderableWidget(temp);
        return temp;
    }

    private SuggestionEditBox makeSuggestionEditBox(int x, int y, String name, List<String> list) {
        SuggestionEditBox temp = new SuggestionEditBox(
                this.font,
                x,
                y,
                100,
                20,
                Component.literal(name),
                list
        );
        temp.setHint(Component.literal(name));
        this.addRenderableWidget(temp);
        return temp;
    }

    private SuggestionEditBox makeSuggestionEditBox(int x, int y, String name, List<String> list, int width) {
        SuggestionEditBox temp = new SuggestionEditBox(
                this.font,
                x,
                y,
                width,
                20,
                Component.literal(name),
                list
        );
        temp.setHint(Component.literal(name));
        this.addRenderableWidget(temp);
        return temp;
    }

    private Checkbox makeCheckBox(int x, int y, String name) {
        Checkbox temp = Checkbox.builder(Component.literal(name), this.font)
                .pos(x, y)
                .build();
        this.addRenderableWidget(temp);
        return temp;
    }
}
