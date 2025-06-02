package mcjty.lostcities.worldgen.lost.regassets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

/**
 * For a city style this object represents the possible objects for all types
 */
public class Selectors {
    private final List<ObjectSelector> buildingSelector;
    private final List<ObjectSelector> bridgeSelector;
    private final List<ObjectSelector> parkSelector;
    private final List<ObjectSelector> fountainSelector;
    private final List<ObjectSelector> stairSelector;
    private final List<ObjectSelector> frontSelector;
    private final List<ObjectSelector> multiBuildingSelector;
    //Railway
    private final List<ObjectSelector> railwayRailSelector;
    private final List<ObjectSelector> railwayRailEndSelector;
    private final List<ObjectSelector> railwayRailWaterSelector;
    private final List<ObjectSelector> railwayStationSelector;
    private final List<ObjectSelector> railwayStationUndergroundSelector;
    private final List<ObjectSelector> railwayStationStaircaseSelector;
    private final List<ObjectSelector> railwayStationStaircaseSurfaceSelector;
    private final List<ObjectSelector> railwayXJunctionSelector;
    private final List<ObjectSelector> railwayTJunctionSelector;

    public static final Codec<Selectors> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("buildings").forGetter(l -> Optional.ofNullable(l.buildingSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("bridges").forGetter(l -> Optional.ofNullable(l.bridgeSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("parks").forGetter(l -> Optional.ofNullable(l.parkSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("fountains").forGetter(l -> Optional.ofNullable(l.fountainSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("stairs").forGetter(l -> Optional.ofNullable(l.stairSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("fronts").forGetter(l -> Optional.ofNullable(l.frontSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("multibuildings").forGetter(l -> Optional.ofNullable(l.multiBuildingSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_rail").forGetter(l -> Optional.ofNullable(l.railwayRailSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_rail_end").forGetter(l -> Optional.ofNullable(l.railwayRailEndSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_rail_water").forGetter(l -> Optional.ofNullable(l.railwayRailWaterSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_station").forGetter(l -> Optional.ofNullable(l.railwayStationSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_station_underground").forGetter(l -> Optional.ofNullable(l.railwayStationUndergroundSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_station_staircase").forGetter(l -> Optional.ofNullable(l.railwayStationStaircaseSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_station_staircase_surface").forGetter(l -> Optional.ofNullable(l.railwayStationStaircaseSurfaceSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_x_junction").forGetter(l -> Optional.ofNullable(l.railwayXJunctionSelector)),
                    Codec.list(ObjectSelector.CODEC).optionalFieldOf("railway_t_junction").forGetter(l -> Optional.ofNullable(l.railwayTJunctionSelector))
            ).apply(instance, Selectors::new));

    public Optional<List<ObjectSelector>> getBuildingSelector() {
        return Optional.ofNullable(buildingSelector);
    }

    public Optional<List<ObjectSelector>> getBridgeSelector() { return Optional.ofNullable(bridgeSelector); }

    public Optional<List<ObjectSelector>> getParkSelector() {
        return Optional.ofNullable(parkSelector);
    }

    public Optional<List<ObjectSelector>> getFountainSelector() {
        return Optional.ofNullable(fountainSelector);
    }

    public Optional<List<ObjectSelector>> getStairSelector() {
        return Optional.ofNullable(stairSelector);
    }

    public Optional<List<ObjectSelector>> getFrontSelector() {
        return Optional.ofNullable(frontSelector);
    }

    public Optional<List<ObjectSelector>> getMultiBuildingSelector() {
        return Optional.ofNullable(multiBuildingSelector);
    }

    //Railway
    public Optional<List<ObjectSelector>> getRailwayRailSelector() {
        return Optional.ofNullable(railwayRailSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayRailEndSelector() {
        return Optional.ofNullable(railwayRailEndSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayRailWaterSelector() {
        return Optional.ofNullable(railwayRailWaterSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayStationSelector() {
        return Optional.ofNullable(railwayStationSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayStationUndergroundSelector() {
        return Optional.ofNullable(railwayStationUndergroundSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayStationStaircaseSelector() {
        return Optional.ofNullable(railwayStationStaircaseSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayStationStaircaseSurfaceSelector() {
        return Optional.ofNullable(railwayStationStaircaseSurfaceSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayXJunctionSelector() {
        return Optional.ofNullable(railwayXJunctionSelector);
    }

    public Optional<List<ObjectSelector>> getRailwayTJunctionSelector() {
        return Optional.ofNullable(railwayTJunctionSelector);
    }

    public Selectors(Optional<List<ObjectSelector>> buildingSelector,
            Optional<List<ObjectSelector>> bridgeSelector,
            Optional<List<ObjectSelector>> parkSelector,
            Optional<List<ObjectSelector>> fountainSelector,
            Optional<List<ObjectSelector>> stairSelector,
            Optional<List<ObjectSelector>> frontSelector,
            Optional<List<ObjectSelector>> multiBuildingSelector,
            Optional<List<ObjectSelector>> railwayRailSelector,
            Optional<List<ObjectSelector>> railwayRailEndSelector,
            Optional<List<ObjectSelector>> railwayRailWaterSelector,
            Optional<List<ObjectSelector>> railwayStationSelector,
            Optional<List<ObjectSelector>> railwayStationUndergroundSelector,
            Optional<List<ObjectSelector>> railwayStationStaircaseSelector,
            Optional<List<ObjectSelector>> railwayStationStaircaseSurfaceSelector,
            Optional<List<ObjectSelector>> railwayXJunctionSelector,
            Optional<List<ObjectSelector>> railwayTJunctionSelector) {
        this.buildingSelector = buildingSelector.orElse(null);
        this.bridgeSelector = bridgeSelector.orElse(null);
        this.parkSelector = parkSelector.orElse(null);
        this.fountainSelector = fountainSelector.orElse(null);
        this.stairSelector = stairSelector.orElse(null);
        this.frontSelector = frontSelector.orElse(null);
        this.multiBuildingSelector = multiBuildingSelector.orElse(null);
        // Railway
        this.railwayRailSelector = railwayRailSelector.orElse(null);
        this.railwayRailEndSelector = railwayRailEndSelector.orElse(null);
        this.railwayRailWaterSelector = railwayRailWaterSelector.orElse(null);
        this.railwayStationSelector = railwayStationSelector.orElse(null);
        this.railwayStationUndergroundSelector = railwayStationUndergroundSelector.orElse(null);
        this.railwayStationStaircaseSelector = railwayStationStaircaseSelector.orElse(null);
        this.railwayStationStaircaseSurfaceSelector = railwayStationStaircaseSurfaceSelector.orElse(null);
        this.railwayXJunctionSelector = railwayXJunctionSelector.orElse(null);
        this.railwayTJunctionSelector = railwayTJunctionSelector.orElse(null);
    }
}
