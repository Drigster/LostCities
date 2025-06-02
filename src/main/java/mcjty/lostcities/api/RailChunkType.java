package mcjty.lostcities.api;

/**
 * The type of a railway at this spot
 */
public enum RailChunkType {
    NONE(false, false),
    STATION_SURFACE(true, true),
    STATION_UNDERGROUND(true, false),
    GOING_DOWN_TWO_FROM_SURFACE(false, true),
    GOING_DOWN_ONE_FROM_SURFACE(false, true),
    GOING_DOWN_FURTHER(false, false),
    RAIL(false, false),
    X_JUNCTION(false, false),
    T_JUNCTION(false, false),
    RAILS_END_HERE(false, false);

    private final boolean isStation;
    private final boolean isSurface;

    RailChunkType(boolean isStation, boolean isSurface) {
        this.isStation = isStation;
        this.isSurface = isSurface;
    }

    public boolean isStation() {
        return isStation;
    }

    public boolean isSurface() {
        return isSurface;
    }
}
