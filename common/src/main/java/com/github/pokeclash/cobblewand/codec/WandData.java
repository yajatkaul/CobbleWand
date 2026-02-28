package com.github.pokeclash.cobblewand.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record WandData(
        Optional<BasicData> basic,
        Optional<Flags> flags,
        Optional<MoveSet> moves,
        Optional<LevelData> levelData,
        Optional<EVs> evs,
        Optional<IVs> ivs
) {
    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BasicData.CODEC.optionalFieldOf("basic").forGetter(WandData::basic),
            Flags.CODEC.optionalFieldOf("flags").forGetter(WandData::flags),
            MoveSet.CODEC.optionalFieldOf("moves").forGetter(WandData::moves),
            LevelData.CODEC.optionalFieldOf("levelData").forGetter(WandData::levelData),
            EVs.CODEC.optionalFieldOf("evs").forGetter(WandData::evs),
            IVs.CODEC.optionalFieldOf("ivs").forGetter(WandData::ivs)
    ).apply(instance, WandData::new));

    public static final StreamCodec<ByteBuf, WandData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(BasicData.STREAM_CODEC), WandData::basic,
                    ByteBufCodecs.optional(Flags.STREAM_CODEC), WandData::flags,
                    ByteBufCodecs.optional(MoveSet.STREAM_CODEC), WandData::moves,
                    ByteBufCodecs.optional(LevelData.STREAM_CODEC), WandData::levelData,
                    ByteBufCodecs.optional(EVs.STREAM_CODEC), WandData::evs,
                    ByteBufCodecs.optional(IVs.STREAM_CODEC), WandData::ivs,
                    WandData::new
            );

    public record MoveSet(
            Optional<String> move1,
            Optional<String> move2,
            Optional<String> move3,
            Optional<String> move4
    ) {
        public static final Codec<MoveSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("move1").forGetter(MoveSet::move1),
                Codec.STRING.optionalFieldOf("move2").forGetter(MoveSet::move2),
                Codec.STRING.optionalFieldOf("move3").forGetter(MoveSet::move3),
                Codec.STRING.optionalFieldOf("move4").forGetter(MoveSet::move4)
        ).apply(instance, MoveSet::new));

        public static final StreamCodec<ByteBuf, MoveSet> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), MoveSet::move1,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), MoveSet::move2,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), MoveSet::move3,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), MoveSet::move4,
                        MoveSet::new
                );
    }

    public record BasicData(
            Optional<String> species,
            Optional<String> tera,
            Optional<String> aspects,
            Optional<String> nature,
            Optional<String> ability,
            Optional<String> held_item
    ) {
        public static final Codec<BasicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("species").forGetter(BasicData::species),
                Codec.STRING.optionalFieldOf("tera").forGetter(BasicData::tera),
                Codec.STRING.optionalFieldOf("aspects").forGetter(BasicData::aspects),
                Codec.STRING.optionalFieldOf("nature").forGetter(BasicData::nature),
                Codec.STRING.optionalFieldOf("ability").forGetter(BasicData::ability),
                Codec.STRING.optionalFieldOf("held_item").forGetter(BasicData::held_item)
        ).apply(instance, BasicData::new));

        public static final StreamCodec<ByteBuf, BasicData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BasicData::species,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BasicData::tera,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BasicData::aspects,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BasicData::nature,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BasicData::ability,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), BasicData::held_item,
                        BasicData::new
                );
    }

    public record Flags(
            Optional<Boolean> gmaxFactor,
            Optional<Boolean> statue,
            Optional<String> pokeball
    ) {
        public static final Codec<Flags> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("gmaxFactor").forGetter(Flags::gmaxFactor),
                Codec.BOOL.optionalFieldOf("statue").forGetter(Flags::statue),
                Codec.STRING.optionalFieldOf("pokeball").forGetter(Flags::pokeball)
        ).apply(instance, Flags::new));

        public static final StreamCodec<ByteBuf, Flags> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.optional(ByteBufCodecs.BOOL), Flags::gmaxFactor,
                        ByteBufCodecs.optional(ByteBufCodecs.BOOL), Flags::statue,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Flags::pokeball,
                        Flags::new
                );
    }

    public record LevelData(
            Optional<String> lvl,
            Optional<String> scale,
            Optional<String> frnd,
            Optional<String> dlvl
    ) {
        public static final Codec<LevelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("lvl").forGetter(LevelData::lvl),
                Codec.STRING.optionalFieldOf("scale").forGetter(LevelData::scale),
                Codec.STRING.optionalFieldOf("frnd").forGetter(LevelData::frnd),
                Codec.STRING.optionalFieldOf("dlvl").forGetter(LevelData::dlvl)
        ).apply(instance, LevelData::new));

        public static final StreamCodec<ByteBuf, LevelData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), LevelData::lvl,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), LevelData::scale,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), LevelData::frnd,
                        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), LevelData::dlvl,
                        LevelData::new
                );
    }

    public record IVs(
            Optional<Integer> hp,
            Optional<Integer> attack,
            Optional<Integer> defense,
            Optional<Integer> specialAttack,
            Optional<Integer> specialDefense,
            Optional<Integer> speed
    ) {
        public static final Codec<IVs> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 31).optionalFieldOf("hp").forGetter(IVs::hp),
                Codec.intRange(0, 31).optionalFieldOf("attack").forGetter(IVs::attack),
                Codec.intRange(0, 31).optionalFieldOf("defense").forGetter(IVs::defense),
                Codec.intRange(0, 31).optionalFieldOf("special_attack").forGetter(IVs::specialAttack),
                Codec.intRange(0, 31).optionalFieldOf("special_defense").forGetter(IVs::specialDefense),
                Codec.intRange(0, 31).optionalFieldOf("speed").forGetter(IVs::speed)
        ).apply(instance, IVs::new));

        public static final StreamCodec<ByteBuf, IVs> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), IVs::hp,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), IVs::attack,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), IVs::defense,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), IVs::specialAttack,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), IVs::specialDefense,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), IVs::speed,
                        IVs::new
                );
    }

    public record EVs(
            Optional<Integer> hp,
            Optional<Integer> attack,
            Optional<Integer> defense,
            Optional<Integer> specialAttack,
            Optional<Integer> specialDefense,
            Optional<Integer> speed
    ) {
        public static final Codec<EVs> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 252).optionalFieldOf("hp").forGetter(EVs::hp),
                Codec.intRange(0, 252).optionalFieldOf("attack").forGetter(EVs::attack),
                Codec.intRange(0, 252).optionalFieldOf("defense").forGetter(EVs::defense),
                Codec.intRange(0, 252).optionalFieldOf("special_attack").forGetter(EVs::specialAttack),
                Codec.intRange(0, 252).optionalFieldOf("special_defense").forGetter(EVs::specialDefense),
                Codec.intRange(0, 252).optionalFieldOf("speed").forGetter(EVs::speed)
        ).apply(instance, EVs::new));

        public static final StreamCodec<ByteBuf, EVs> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), EVs::hp,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), EVs::attack,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), EVs::defense,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), EVs::specialAttack,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), EVs::specialDefense,
                        ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), EVs::speed,
                        EVs::new
                );
    }
}
