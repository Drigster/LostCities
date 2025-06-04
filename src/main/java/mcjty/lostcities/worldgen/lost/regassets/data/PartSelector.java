package mcjty.lostcities.worldgen.lost.regassets.data;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A selector for parts for monorail and railstation
 */
public record PartSelector(MonorailParts monoRailParts, HighwayParts highwayParts) {

    public static final Codec<PartSelector> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MonorailParts.CODEC.optionalFieldOf("monorails").forGetter(l -> l.monoRailParts.get()),
                    HighwayParts.CODEC.optionalFieldOf("highways").forGetter(l -> l.highwayParts.get())
            ).apply(instance, (monorails, highways) -> new PartSelector(
                    monorails.orElse(MonorailParts.DEFAULT),
                    highways.orElse(HighwayParts.DEFAULT))));

    public static final PartSelector DEFAULT = new PartSelector(
            MonorailParts.DEFAULT,
            HighwayParts.DEFAULT);

    public Optional<PartSelector> get() {
        if (this == DEFAULT) {
            return Optional.empty();
        } else {
            return Optional.of(this);
        }
    }

}
