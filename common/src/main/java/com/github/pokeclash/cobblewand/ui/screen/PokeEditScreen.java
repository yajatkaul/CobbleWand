package com.github.pokeclash.cobblewand.ui.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.client.gui.trade.ModelWidget;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.github.pokeclash.cobblewand.mixin.PokemonAccessor;
import com.github.pokeclash.cobblewand.mixin.TeraTypesAccessor;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonAddPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonEditPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import com.github.pokeclash.cobblewand.ui.util.FloatEditBox;
import com.github.pokeclash.cobblewand.ui.util.NumericEditBox;
import com.github.pokeclash.cobblewand.ui.util.SuggestionEditBox;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.stream.Collectors;

public class PokeEditScreen extends Screen {
    private final int BASE_WIDTH = 349;
    private final int BASE_HEIGHT = 205;
    private final int PORTRAIT_SIZE = 66;
    private final Pokemon pokemon;
    private final Pokemon showPokemon;
    private SuggestionEditBox speciesField;
    private EditBox aspectField;
    private SuggestionEditBox natureField;
    private SuggestionEditBox abilityField;
    private SuggestionEditBox teraField;
    private SuggestionEditBox move1;
    private SuggestionEditBox move2;
    private SuggestionEditBox move3;
    private SuggestionEditBox move4;
    private SuggestionEditBox heldItemField;
    private SuggestionEditBox genderField;
    private SuggestionEditBox pokeBallField;
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
    private FloatEditBox scale;
    private NumericEditBox friendShip;
    private NumericEditBox dmaxLevel;
    private ModelWidget modelWidget;
    private Checkbox gmaxFactor;
    private Checkbox statue;
    private final UUID uuid;

    private Item renderItem = Items.AIR;
    public PokeEditScreen(String name, Pokemon pokemon, RegistryAccess registryAccess, UUID uuid) {
        super(Component.translatable(name));
        this.pokemon = pokemon;
        this.showPokemon = pokemon.clone(true, registryAccess);
        this.uuid = uuid;
    }

    @Override
    protected void init() {
        int guiX = (this.width - BASE_WIDTH) / 2;
        int guiY = (this.height - BASE_HEIGHT) / 2;

        speciesField = makeSuggestionEditBox(
                guiX + 73,
                guiY + 28,
                "Pokemon",
                PokemonSpecies.getSpecies()
                        .stream()
                        .map(s -> s.resourceIdentifier.getPath())
                        .toList()
        );
        speciesField.setResponder((val) -> setPokemon(false));

        heldItemField = makeSuggestionEditBox(
                guiX + 175,
                guiY + 28,
                "Held Item",
                BuiltInRegistries.ITEM
                        .stream()
                        .map(i -> BuiltInRegistries.ITEM.getKey(i).toString())
                        .toList(),
                200
        );
        heldItemField.setResponder((val) -> setItemRender(false));

        teraField = makeSuggestionEditBox(
                guiX + 175,
                guiY + 51,
                "Tera",
                TeraTypesAccessor.getTypes().values()
                        .stream()
                        .map(ShowdownIdentifiable::showdownId)
                        .toList(),
                70
        );

        abilityField = makeSuggestionEditBox(
                guiX + 73,
                guiY + 51,
                "Ability",
                Abilities.all()
                        .stream()
                        .map(AbilityTemplate::getName)
                        .toList()
        );

        genderField = makeSuggestionEditBox(
                guiX + 247,
                guiY + 51,
                "G",
                Arrays.stream(Gender.values())
                        .toList().stream()
                        .map(Gender::getShowdownName)
                        .toList(),
                20
        );

        pokeBallField = makeSuggestionEditBox(
                guiX + 247,
                guiY + 7,
                "Pokeball",
                PokeBalls.all()
                        .stream()
                        .map((p) -> p.getName().getPath())
                        .toList(),
                128
        );
        pokeBallField.setResponder((val) -> setPokeball(false));

        gmaxFactor = makeCheckBox(
                guiX + 175,
                guiY + 75,
                "Gmax Factor"
        );

        statue = makeCheckBox(
                guiX + 175,
                guiY + 95,
                "Statue"
        );

        aspectField = makeSuggestionEditBox(
                guiX + 73,
                guiY + 75,
                "Aspects",
                List.of()
        );
        aspectField.setResponder((val) -> setPokemon(false));

        move1 = makeMoveBox(guiX + 73, guiY + 115, "Move 1");
        move2 = makeMoveBox(guiX + 175, guiY + 115, "Move 2");
        move3 = makeMoveBox(guiX + 73, guiY + 140, "Move 3");
        move4 = makeMoveBox(guiX + 175, guiY + 140, "Move 4");

        // Left column
        hpEV = this.createEVBox(guiX + 290, guiY + 60, "HP EV");
        attackEV = this.createEVBox(guiX + 290, guiY + 80, "Atk EV");
        defenseEV = this.createEVBox(guiX + 290, guiY + 100, "Def EV");
        // Right column
        spAtkEV = this.createEVBox(guiX + 335, guiY + 60, "SpAtk EV");
        spDefEV = this.createEVBox(guiX + 335, guiY + 80, "SpDef EV");
        speedEV = this.createEVBox(guiX + 335, guiY + 100, "Spd EV");

        // Left column IVs
        hpIV = this.createIVBox(guiX + 290, guiY + 130, "HP IV");
        attackIV = this.createIVBox(guiX + 290, guiY + 150, "Atk IV");
        defenseIV = this.createIVBox(guiX + 290, guiY + 170, "Def IV");
        // Right column IVs
        spAtkIV = this.createIVBox(guiX + 335, guiY + 130, "SpAtk IV");
        spDefIV = this.createIVBox(guiX + 335, guiY + 150, "SpDef IV");
        speedIV = this.createIVBox(guiX + 335, guiY + 170, "Spd IV");

        level = makeNumericBox(guiX - 20, guiY + 165, "Lvl", 1, Cobblemon.config.getMaxPokemonLevel(), 50);

        scale = makeFloatBox(guiX + 32, guiY + 165, "Scale", 0, 100, 50);

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
                Component.literal("Edit"),
                (button) -> setPokemon(true)
        ).bounds(guiX + 73, guiY + 190, 80, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Reset"),
                this::resetPokemon
        ).bounds(guiX - 10, guiY + 190, 80, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Add To Party"),
                this::addToParty
        ).bounds(guiX + 155, guiY + 190, 80, 20).build());

        setPokemonBasic(false);

        RenderablePokemon renderablePokemon = showPokemon.asRenderablePokemon();
        modelWidget = new ModelWidget(guiX - 20, guiY + 57, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 3f, 325f, -10.0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        speciesField.mouseClicked(mouseX, mouseY, button);
        move1.mouseClicked(mouseX, mouseY, button);
        move2.mouseClicked(mouseX, mouseY, button);
        move3.mouseClicked(mouseX, mouseY, button);
        move4.mouseClicked(mouseX, mouseY, button);

        heldItemField.mouseClicked(mouseX, mouseY, button);
        natureField.mouseClicked(mouseX, mouseY, button);
        teraField.mouseClicked(mouseX, mouseY, button);
        abilityField.mouseClicked(mouseX, mouseY, button);
        genderField.mouseClicked(mouseX, mouseY, button);
        pokeBallField.mouseClicked(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Item pokeBall = null;

    private void addToParty(Button button) {
        setPokeball(true);
        NetworkManager.sendToServer(new PokemonAddPacket(pokemon));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (renderItem != null) {
            int guiX = (this.width - BASE_WIDTH) / 2;
            int guiY = (this.height - BASE_HEIGHT) / 2;
            guiGraphics.renderItem(renderItem.getDefaultInstance(), guiX + 50, guiY + 140);
        }

        if (pokeBall != null) {
            int guiX = (this.width - BASE_WIDTH) / 2;
            int guiY = (this.height - BASE_HEIGHT) / 2;
            guiGraphics.renderItem(pokeBall.getDefaultInstance(), guiX + 230, guiY + 9);
        }

        modelWidget.render(guiGraphics, mouseX, mouseY, partialTick);

        speciesField.renderSuggestions(guiGraphics, this.font);

        move1.renderSuggestions(guiGraphics, this.font);
        move2.renderSuggestions(guiGraphics, this.font);
        move3.renderSuggestions(guiGraphics, this.font);
        move4.renderSuggestions(guiGraphics, this.font);

        heldItemField.renderSuggestions(guiGraphics, this.font);

        natureField.renderSuggestions(guiGraphics, this.font);

        teraField.renderSuggestions(guiGraphics, this.font);

        abilityField.renderSuggestions(guiGraphics, this.font);

        genderField.renderSuggestions(guiGraphics, this.font);

        pokeBallField.renderSuggestions(guiGraphics, this.font);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    private void setPokemon(boolean set) {
        int guiX = (this.width - BASE_WIDTH) / 2;
        int guiY = (this.height - BASE_HEIGHT) / 2;

        setPokemonBasic(set);
        setEV(pokemon);
        setIV(pokemon);
        setMoves(pokemon);

        if (level.getIntValue() != -1) {
            pokemon.setLevel(level.getIntValue());
        }

        if (scale.getFloatValue() != -1) {
            pokemon.setScaleModifier(scale.getFloatValue());
        }

        if (friendShip.getIntValue() != -1) {
            ((PokemonAccessor) pokemon).setFriendship(friendShip.getIntValue());
        }

        if (!natureField.getValue().isEmpty()) {
            Nature nature = Natures.getNature(natureField.getValue());
            if (nature != null) {
                pokemon.setNature(nature);
            }
        }

        if (!genderField.getValue().isEmpty()) {
            Gender gender = Gender.valueOf(genderField.getValue());
            if (gender != null) {
                pokemon.setGender(gender);
            }
        }

        setItemRender(true);

        if (!abilityField.getValue().isEmpty()) {
            AbilityTemplate abilityTemplate = Abilities.get(abilityField.getValue());
            if (abilityTemplate != null) {
                Ability ability = abilityTemplate.create(new CompoundTag());
                pokemon.updateAbility(ability);
            }
        }

        if (dmaxLevel.getIntValue() != -1) {
            pokemon.setDmaxLevel(dmaxLevel.getIntValue());
        }

        if (!teraField.getValue().isBlank()) {
            TeraType type = TeraTypes.get(teraField.getValue());
            if (type != null) {
                pokemon.setTeraType(type);
            }
        }

        if (gmaxFactor.selected()) {
            pokemon.setGmaxFactor(true);
        }

        setPokeball(true);

        RenderablePokemon renderablePokemon = showPokemon.asRenderablePokemon();
        modelWidget = new ModelWidget(guiX - 20, guiY + 57, PORTRAIT_SIZE, PORTRAIT_SIZE, renderablePokemon, 3f, 325f, -10.0);

        if (set) {
            WandData newWandData = createWandData();
            NetworkManager.sendToServer(new PokemonEditPacket(pokemon, newWandData, uuid));
        }
    }

    private void setItemRender(boolean set) {
        if (!heldItemField.getValue().isEmpty()) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(heldItemField.getValue().toLowerCase(Locale.ROOT)));
            if (item != Items.AIR) {
                renderItem = item;
                if (set) {
                    pokemon.setHeldItem$common(item.getDefaultInstance());
                }
            }
        }
    }

    private void setPokeball(boolean set) {
        if (!pokeBallField.getValue().isEmpty()) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse("cobblemon:"+pokeBallField.getValue());
            if (resourceLocation != null) {
                PokeBall pokeBall = PokeBalls.getPokeBall(resourceLocation);
                if (pokeBall != null) {
                    if (set) {
                        pokemon.setCaughtBall(pokeBall);
                    }
                    this.pokeBall = pokeBall.item;
                } else {
                    this.pokeBall = null;
                }
            }
        }
    }

    private void resetPokemon(Button button) {
        if (speciesField != null) speciesField.setValue("");
        if (aspectField != null) aspectField.setValue("");
        if (natureField != null) natureField.setValue("");
        if (teraField != null) teraField.setValue("");
        if (abilityField != null) abilityField.setValue("");
        if (heldItemField != null) heldItemField.setValue("");
        if (pokeBallField != null) pokeBallField.setValue("");

        if (move1 != null) move1.setValue("");
        if (move2 != null) move2.setValue("");
        if (move3 != null) move3.setValue("");
        if (move4 != null) move4.setValue("");

        if (speedEV != null) speedEV.setValue("");
        if (attackEV != null) attackEV.setValue("");
        if (defenseEV != null) defenseEV.setValue("");
        if (spAtkEV != null) spAtkEV.setValue("");
        if (spDefEV != null) spDefEV.setValue("");
        if (hpEV != null) hpEV.setValue("");

        if (speedIV != null) speedIV.setValue("");
        if (attackIV != null) attackIV.setValue("");
        if (defenseIV != null) defenseIV.setValue("");
        if (spAtkIV != null) spAtkIV.setValue("");
        if (spDefIV != null) spDefIV.setValue("");
        if (hpIV != null) hpIV.setValue("");

        if (level != null) level.setValue("");
        if (scale != null) scale.setValue("");
        if (friendShip != null) friendShip.setValue("");
        if (dmaxLevel != null) dmaxLevel.setValue("");

        if (gmaxFactor != null) gmaxFactor.selected = false;
        if (statue != null) statue.selected = false;

        pokemon.setSpecies(PokemonSpecies.random());
        setPokemon(true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private WandData createWandData() {
        return new WandData(
                Optional.of(new WandData.BasicData(
                        optionalString(speciesField),
                        optionalString(teraField),
                        optionalString(aspectField),
                        optionalString(natureField),
                        optionalString(abilityField),
                        optionalString(heldItemField)
                )),
                Optional.of(new WandData.Flags(
                        optionalBool(gmaxFactor),
                        optionalBool(statue),
                        optionalString(pokeBallField)
                )),
                Optional.of(new WandData.MoveSet(
                        optionalString(move1),
                        optionalString(move2),
                        optionalString(move3),
                        optionalString(move4)
                )),
                Optional.of(new WandData.LevelData(
                        optionalInt(level).map(String::valueOf),
                        optionalFloat(scale).map(String::valueOf),
                        optionalInt(friendShip).map(String::valueOf),
                        optionalInt(dmaxLevel).map(String::valueOf)
                )),
                Optional.of(new WandData.EVs(
                        optionalInt(hpEV),
                        optionalInt(attackEV),
                        optionalInt(defenseEV),
                        optionalInt(spAtkEV),
                        optionalInt(spDefEV),
                        optionalInt(speedEV)
                )),
                Optional.of(new WandData.IVs(
                        optionalInt(hpIV),
                        optionalInt(attackIV),
                        optionalInt(defenseIV),
                        optionalInt(spAtkIV),
                        optionalInt(spDefIV),
                        optionalInt(speedIV)
                ))
        );
    }

    private Optional<String> optionalString(EditBox box) {
        if (box == null) return Optional.empty();
        String value = box.getValue();
        return value.isEmpty()
                ? Optional.empty()
                : Optional.of(value.trim());
    }

    private Optional<Integer> optionalInt(NumericEditBox box) {
        if (box == null) return Optional.empty();
        int value = box.getIntValue();
        return value == -1 ? Optional.empty() : Optional.of(value);
    }

    private Optional<Float> optionalFloat(FloatEditBox box) {
        if (box == null) return Optional.empty();
        float value = box.getFloatValue();
        return value == -1f ? Optional.empty() : Optional.of(value);
    }

    private Optional<Boolean> optionalBool(Checkbox checkbox) {
        if (checkbox == null) return Optional.empty();
        return Optional.of(checkbox.selected());
    }

    private void setPokemonBasic(boolean set) {
        if (!speciesField.getValue().isEmpty()) {
            Species pokemonSpecies = PokemonSpecies.getByName(speciesField.getValue().toLowerCase(Locale.ROOT));
            if (pokemonSpecies != null) {
                if (set) {
                    pokemon.setSpecies(pokemonSpecies);
                    showPokemon.setSpecies(pokemonSpecies);
                } else {
                    showPokemon.setSpecies(pokemonSpecies);
                }
            }
        }

        if (!aspectField.getValue().isEmpty()) {
            Set<String> aspects = Arrays.stream(aspectField.getValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            if (set) {
                pokemon.setForcedAspects(aspects);
                showPokemon.setForcedAspects(aspects);
            } else {
                showPokemon.setForcedAspects(aspects);
            }
        } else {
            pokemon.setForcedAspects(Set.of());
            showPokemon.setForcedAspects(Set.of());
        }
    }

    private void setMoves(Pokemon pokemon) {
        Set<String> movesAdded = new HashSet<>();
        if (!move1.getValue().isEmpty()) {
            MoveTemplate move = Moves.getByName(move1.getValue());
            if (move != null) {
                pokemon.getMoveSet().setMove(0, move.create());
                movesAdded.add(move.getName());
            }
        }
        if (!move2.getValue().isEmpty()) {
            MoveTemplate move = Moves.getByName(move2.getValue());
            if (move != null && !movesAdded.contains(move.getName())) {
                pokemon.getMoveSet().setMove(1, move.create());
                movesAdded.add(move.getName());
            }
        }
        if (!move3.getValue().isEmpty()) {
            MoveTemplate move = Moves.getByName(move3.getValue());
            if (move != null && !movesAdded.contains(move.getName())) {
                pokemon.getMoveSet().setMove(2, move.create());
                movesAdded.add(move.getName());
            }
        }
        if (!move4.getValue().isEmpty()) {
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

    private FloatEditBox makeFloatBox(int x, int y, String name, float min, float max, int width) {
        FloatEditBox temp = new FloatEditBox(
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
