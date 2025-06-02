package mcjty.lostcities.worldgen.lost;

import mcjty.lostcities.api.RailChunkType;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.varia.QualityRandom;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.regassets.data.RailwayParts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.lostcities.api.RailChunkType.*;
import static mcjty.lostcities.worldgen.lost.Railway.RailDirection.*;

public class Railway {

    public static final int RAILWAY_LEVEL_OFFSET = -13;

    /*
    Railway grid:

      .   .   .   .   s   .   s   .   .   .   .   .   .   .   .

      .   .   .   .   s   s   s   .   .   .   .   .   .   .   .

      .   .   .   .   S\  0  /S   .   .   .   .   .   .   .   .
                        |   |
      .   .   .   .   S--=S=--S   .   .   .   .   .   .   .   .
                        |   |
      .   .   .   .   S/  .  \S   .   .   .   .   .   .   .   .

      .   .   .   .   s   s   s   .   .   .   .   .   .   .   .


    00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19
    SS >>          ||          << SS >>          ||          <<

     */

    public enum RailDirection {
        NORTH,
        SOUTH,
        BI,
        WEST,
        EAST
    }

    public static class RailChunkInfo {
        private final RailChunkType type;
        private final RailDirection direction;
        private final int level;
        private final String partName;
        private final ChunkCoord origin;

        public static final RailChunkInfo NOTHING = new RailChunkInfo(NONE, BI, 0);

        public RailChunkInfo(RailChunkType type, RailDirection direction, int level) {
            this(type, direction, level, (String) null);
        }

        public RailChunkInfo(RailChunkType type, RailDirection direction, int level, String partName) {
            this.type = type;
            this.direction = direction;
            this.level = level;
            this.partName = partName;
            this.origin = null;
        }

        public RailChunkInfo(RailChunkType type, RailDirection direction, int level, String partName, ChunkCoord origin) {
            this.type = type;
            this.direction = direction;
            this.level = level;
            this.partName = partName;
            this.origin = origin;
        }

        public RailChunkType getType() {
            return type;
        }

        public RailDirection getDirection() {
            return direction;
        }

        public int getLevel() {
            return level;
        }

        public String getPartName() {
            return partName;
        }

        public ChunkCoord getOrigin() {
            return origin;
        }
    }

    private static final Map<ChunkCoord, RailChunkInfo> RAIL_INFO = Collections.synchronizedMap(new HashMap<>());

    public static void cleanCache() {
        RAIL_INFO.clear();
    }

    /**
     * The station grid repeats every 9 chunks. There is never a station at every 18/18 multiple chunk
     */
    private static RailChunkInfo getRailChunkTypeInternal(ChunkCoord key, IDimensionInfo provider) {
        int chunkX = key.chunkX();
        int chunkZ = key.chunkZ();
        QualityRandom randomRailChunkType = new QualityRandom(provider.getSeed() + chunkZ * 2600003897L + chunkX * 43600002517L);

        LostCityProfile profile = BuildingInfo.getProfile(key, provider);

        if (RAIL_INFO.containsKey(key)) {
            RailChunkInfo info = RAIL_INFO.get(key);
            if (info.type == STATION_UNDERGROUND) {
                return info;
            } else if (info.type == STATION_SURFACE) {
                return info;
            }
        }

        int mx = Math.floorMod(chunkX + 1, 20);       // The +1 to avoid having them on highways
        int mz = Math.floorMod(chunkZ + 1, 20);
        if (mx == 0 && mz == 10 || mx == 10 && mz == 0 || mx == 10 && mz == 10) {
            if (!BuildingInfo.isCityRaw(key, provider, profile)) {
                // There is no city here. So no station. But we still need a railway. A station at this
                // point will get a three line rail through it
                if (profile.RAILWAYS_CAN_END) {
                    // Check if there are stations at either side
                    boolean cityEast = BuildingInfo.isCityRaw(key.offset(10, 0), provider, profile)
                            || BuildingInfo.isCityRaw(key.offset(10, -10), provider, profile)
                            || BuildingInfo.isCityRaw(key.offset(10, 10), provider, profile);
                    boolean cityWest = BuildingInfo.isCityRaw(key.offset(10, 0), provider, profile)
                            || BuildingInfo.isCityRaw(key.offset(-10, -10), provider, profile)
                            || BuildingInfo.isCityRaw(key.offset(-10, 10), provider, profile);
                    if (!cityEast && !cityWest) {
                        return RailChunkInfo.NOTHING;
                    }
                    if (!cityEast) {
                        return new RailChunkInfo(RAILS_END_HERE, WEST, -3);
                    }
                    if (!cityWest) {
                        return new RailChunkInfo(RAILS_END_HERE, EAST, -3);
                    }
                }
                // @todo: DRIGSTER change NORTH to north or south
                return new RailChunkInfo(RAIL, EAST, RAILWAY_LEVEL_OFFSET);
            }
            // @todo: DRIGSTER change open roof station is currently not accounted for
            return getStationType(key, provider, profile, randomRailChunkType);
//            return getStationType(key, provider, profile, r, 3,
//                    randomRailChunkType.nextFloat() < .5f ? railwayParts.railwayStationOpen() : railwayParts.railwayStationOpenRoof());
        }
        if (mx == 0 && mz == 0) {
            return RailChunkInfo.NOTHING;
        }

        if (mz == 0 || mz == 10) {
            // Handle the rail sections left or right of every station
            if ((mx >= 16 && mz != 0) || (mx >= 6 && mx <= 9)) {
                ChunkCoord east = key.east();
                RailChunkInfo adjacent = getRailChunkType(east, provider, profile);
                RailDirection direction = adjacent.getDirection();
                direction = WEST;
                return testAdjacentRailChunk(randomRailChunkType.nextFloat(), adjacent, direction, key.west(), provider, profile);
            }
            if ((mx >= 1 && mx <= 4 && mz != 0) || (mx >= 11 && mx <= 14)) {
                ChunkCoord west = key.west();
                RailChunkInfo adjacent = getRailChunkType(west, provider, profile);
                RailDirection direction = adjacent.getDirection();
                direction = EAST;
                return testAdjacentRailChunk(randomRailChunkType.nextFloat(), adjacent, direction, key.east(), provider, profile);
            }
            if (mz == 0 && mx == 5) {
                if (profile.RAILWAYS_CAN_END) {
                    boolean cityWest = BuildingInfo.isCityRaw(key.offset(-5, -10), provider, profile)
                            || BuildingInfo.isCityRaw(key.offset(-5, 10), provider, profile);
                    boolean cityEast = BuildingInfo.isCityRaw(key.offset(5, 0), provider, profile);
                    if (!cityEast && !cityWest) {
                        return RailChunkInfo.NOTHING;
                    }
                }
                return new RailChunkInfo(T_JUNCTION, EAST, RAILWAY_LEVEL_OFFSET);
            }
            if (mz == 0 && mx == 15) {
                if (profile.RAILWAYS_CAN_END) {
                    boolean cityEast = BuildingInfo.isCityRaw(key.offset(5, -10), provider, profile)
                            || BuildingInfo.isCityRaw(key.offset(5, 10), provider, profile);
                    boolean cityWest = BuildingInfo.isCityRaw(key.offset(-5, 0), provider, profile);
                    if (!cityEast && !cityWest) {
                        return RailChunkInfo.NOTHING;
                    }
                }
                return new RailChunkInfo(T_JUNCTION, WEST, RAILWAY_LEVEL_OFFSET);
            }
            if (mz == 10 && mx == 5) {
                if (profile.RAILWAYS_CAN_END) {
                    boolean cityEast = BuildingInfo.isCityRaw(key.offset(5, 0), provider, profile);
                    boolean cityWest = BuildingInfo.isCityRaw(key.offset(-5, 0), provider, profile);
                    if (!cityEast && !cityWest) {
                        // Check the double bends
                        RailChunkInfo typeNorth = getRailChunkType(key.offset(0, -10), provider, profile);
                        if (typeNorth.getType() == NONE) {
                            RailChunkInfo typeSouth = getRailChunkType(key.offset(0, 10), provider, profile);
                            if (typeSouth.getType() == NONE) {
                                return RailChunkInfo.NOTHING;
                            }
                        }
                    }
                }
                return new RailChunkInfo(X_JUNCTION, EAST, RAILWAY_LEVEL_OFFSET);
            }
            if (mz == 10 && mx == 15) {
                if (profile.RAILWAYS_CAN_END) {
                    boolean cityEast = BuildingInfo.isCityRaw(key.offset(5, 0), provider, profile);
                    boolean cityWest = BuildingInfo.isCityRaw(key.offset(-5, 0), provider, profile);
                    if (!cityEast && !cityWest) {
                        // Check the double bends
                        RailChunkInfo typeNorth = getRailChunkType(key.offset(0, -10), provider, profile);
                        if (typeNorth.getType() == NONE) {
                            RailChunkInfo typeSouth = getRailChunkType(key.offset(0, 10), provider, profile);
                            if (typeSouth.getType() == NONE) {
                                return RailChunkInfo.NOTHING;
                            }
                        }
                    }
                }
                return new RailChunkInfo(X_JUNCTION, WEST, RAILWAY_LEVEL_OFFSET);
            }
            return RailChunkInfo.NOTHING;
        }
        if (mx == 5) {
            if (profile.RAILWAYS_CAN_END) {
                RailChunkInfo typeNorth = getRailChunkType(key.offset(0, -(mz % 10)), provider, profile);
                RailChunkInfo typeSouth = getRailChunkType(key.offset(0, -(mz % 10) + 10), provider, profile);
                if (typeNorth.getType() == NONE || typeSouth.getType() == NONE) {
                    return RailChunkInfo.NOTHING;
                }
            }
            return new RailChunkInfo(RAIL, NORTH, RAILWAY_LEVEL_OFFSET);
        }
        if (mx == 15) {
            if (profile.RAILWAYS_CAN_END) {
                RailChunkInfo typeNorth = getRailChunkType(key.offset(0, -(mz % 10)), provider, profile);
                RailChunkInfo typeSouth = getRailChunkType(key.offset(0, -(mz % 10) + 10), provider, profile);
                if (typeNorth.getType() == NONE || typeSouth.getType() == NONE) {
                    return RailChunkInfo.NOTHING;
                }
            }
            return new RailChunkInfo(RAIL, SOUTH, RAILWAY_LEVEL_OFFSET);
        }

        return RailChunkInfo.NOTHING;
    }

    private static RailChunkInfo getStationType(ChunkCoord coord, IDimensionInfo provider, LostCityProfile profile, QualityRandom r) {
        CityStyle cityStyle = City.getCityStyle(coord, provider, profile);
        String stationUnderground = cityStyle.getRandomRailwayStationUnderground(r);

        int cityLevel = BuildingInfo.getCityLevel(coord, provider);
        if (cityLevel > 2) {
            // We are too high here. We need an underground station
            return createRailInfo(STATION_UNDERGROUND, BI, RAILWAY_LEVEL_OFFSET, stationUnderground, coord, provider);
        }
        // If there is a highway exactly at this spot we cannot have a station. @todo? How to solve this
        int highwayX = Highway.getXHighwayLevel(coord, provider, profile);
        int highwayZ = Highway.getZHighwayLevel(coord, provider, profile);
        if ((highwayX != -1 && cityLevel >= highwayX) || (highwayZ != -1 && cityLevel >= highwayZ)) {
            // @todo Problem! We cannot have a station here! At least we cannot get stairs to the top here
            // Because this is very rare we just generate an underground station because that looks reasonable
            return createRailInfo(STATION_UNDERGROUND, BI, RAILWAY_LEVEL_OFFSET, stationUnderground, coord, provider);
        } else {
            // Check if there is a highway directly adjacent (east/west) to the station. In that case we go to underground station mode
            highwayZ = Highway.getZHighwayLevel(coord.west(), provider, profile);
            if (highwayZ != -1 && cityLevel >= highwayZ) {
                return createRailInfo(STATION_UNDERGROUND, BI, RAILWAY_LEVEL_OFFSET, stationUnderground, coord, provider);
            }
            highwayZ = Highway.getZHighwayLevel(coord.east(), provider, profile);
            if (highwayZ != -1 && cityLevel >= highwayZ) {
                return createRailInfo(STATION_UNDERGROUND, BI, RAILWAY_LEVEL_OFFSET, stationUnderground, coord, provider);
            }
        }

        return r.nextDouble() < .5f 
        ? createRailInfo(STATION_SURFACE, BI, RAILWAY_LEVEL_OFFSET, cityStyle.getRandomRailwayStation(r), coord, provider)
        : createRailInfo(STATION_UNDERGROUND, BI, RAILWAY_LEVEL_OFFSET, stationUnderground, coord, provider);
    }

    private static RailChunkInfo createRailInfo(RailChunkType type, RailDirection direction, int level, String partName, ChunkCoord coord, IDimensionInfo provider) {
        MultiBuilding multiBuilding = AssetRegistries.MULTI_BUILDINGS.get(provider.getWorld(), partName);

        if (partName == null || partName.isEmpty()) {
            return new RailChunkInfo(NONE, direction, level);
        }
        if (multiBuilding == null) {
            return new RailChunkInfo(type, direction, level, partName);
        }

        RailChunkInfo returnStationInfo = null;
        int dx = multiBuilding.getDimX();
        int dz = multiBuilding.getDimZ();

        int offsetX = Math.ceilDiv(dx, 2) - 1;
        int offsetZ = Math.ceilDiv(dz, 2) - 1;

        int originX = coord.chunkX() - offsetX;
        int originZ = coord.chunkZ() - offsetZ;

        for (int i = -offsetX; i < dx - offsetX; i++) {
            for (int j = -offsetZ; j < dz - offsetZ; j++) {
                ChunkCoord subCoord = coord.offset(i, j);
                RailChunkInfo stationInfo = new RailChunkInfo(type, direction, level, partName, new ChunkCoord(coord.dimension(), originX, originZ));
                RAIL_INFO.put(subCoord, stationInfo);
                if (returnStationInfo == null) {
                    returnStationInfo = stationInfo;
                }
            }
        }
        if (returnStationInfo == null && RAIL_INFO.containsKey(coord)) {
            returnStationInfo = RAIL_INFO.get(coord);
        }
        return returnStationInfo;
    }

    public static RailChunkInfo getRailChunkType(ChunkCoord coord, IDimensionInfo provider, LostCityProfile profile) {
        if (RAIL_INFO.containsKey(coord)) {
            return RAIL_INFO.get(coord);
        }
        RailChunkInfo info = getRailChunkTypeInternal(coord, provider);
        if ((provider.getProfile().isSpace() || provider.getProfile().isSpheres()) && CitySphere.onCitySphereBorder(coord, provider)) {
            info = RailChunkInfo.NOTHING;
        } else if (info.getType().isStation()) {
            if (!profile.RAILWAY_STATIONS_ENABLED) {
                info = RailChunkInfo.NOTHING;
            }
        } else {
            if (!profile.RAILWAYS_ENABLED) {
                info = RailChunkInfo.NOTHING;
            }
        }
        RAIL_INFO.put(coord, info);
        return info;
    }

    public static void removeRailChunkType(ChunkCoord coord) {
        RAIL_INFO.put(coord, RailChunkInfo.NOTHING);
    }

    private static RailChunkInfo testAdjacentRailChunk(float r, RailChunkInfo adjacent, RailDirection direction, ChunkCoord coord, IDimensionInfo provider, LostCityProfile profile) {
        switch (adjacent.getType()) {
            case NONE:
                return RailChunkInfo.NOTHING;
            case STATION_SURFACE:
                // chunkX actually points to the next chunk. If there is a highway there we want to avoid that and go down this level already
                int highwayX = Highway.getXHighwayLevel(coord, provider, profile);
                int highwayZ = Highway.getZHighwayLevel(coord, provider, profile);
                if ((highwayX != -1 && adjacent.getLevel() == highwayX) || (highwayZ != -1 && adjacent.getLevel() == highwayZ)) {
                    // We have a highway there so go down here by setting r to 1
                    r = 1;
                }

                if ((adjacent.getLevel() & 1) == 0) {
                    return new RailChunkInfo(GOING_DOWN_ONE_FROM_SURFACE, direction, adjacent.getLevel() - 1);
                } else {
                    return new RailChunkInfo(GOING_DOWN_TWO_FROM_SURFACE, direction, adjacent.getLevel() - 2);
                }
            case STATION_UNDERGROUND:
                return new RailChunkInfo(RAIL, direction, adjacent.getLevel());
            case GOING_DOWN_FURTHER:
            case GOING_DOWN_ONE_FROM_SURFACE:
            case GOING_DOWN_TWO_FROM_SURFACE:
                if (adjacent.getLevel() == RAILWAY_LEVEL_OFFSET) {
                    return new RailChunkInfo(RAIL, direction, adjacent.getLevel());
                } else {
                    return new RailChunkInfo(GOING_DOWN_FURTHER, direction, adjacent.getLevel() - 2);
                }
            case X_JUNCTION:
                break;
            case T_JUNCTION:
                break;
            case RAILS_END_HERE:
                if (direction == adjacent.getDirection()) {
                    return new RailChunkInfo(RAIL, direction, adjacent.getLevel());
                } else {
                    return RailChunkInfo.NOTHING;
                }
            case RAIL:
                return adjacent;
        }
        throw new RuntimeException("This is really impossible!");
    }

//    public static void main(String[] args) {
//        int chunkX = -16;
//        int chunkZ = -1;
//        int mx = Math.floorMod(chunkX + 1, 20);       // The +1 to avoid having them on highways
//        int mz = Math.floorMod(chunkZ + 1, 20);
//        System.out.println("mx = " + mx);
//        System.out.println("mz = " + mz);
//
//        for (int i = -40 ; i < 40 ; i++) {
//            System.out.println("Math.floorMod(" + i + ", 20) = " + Math.floorMod(i, 20));
//        }

//
//
//
//        for (int z = 0 ; z < 50 ; z++) {
//            String s = "";
//            for (int x = 0 ; x < 50 ; x++) {
//                RailChunkInfo info = getRailChunkType(x, z, null);
//                switch (info.getType()) {
//                    case NONE:
//                        s += "  ";
//                        break;
//                    case STATION_SURFACE:
//                        s += "Ss";
//                        break;
//                    case STATION_UNDERGROUND:
//                        s += "Su";
//                        break;
//                    case STATION_EXTENSION_SURFACE:
//                        s += "s+";
//                        break;
//                    case STATION_EXTENSION_UNDERGROUND:
//                        s += "u+";
//                        break;
//                    case GOING_DOWN_TWO_FROM_SURFACE:
//                        if (info.getDirection() == WEST) {
//                            s += "<2";
//                        } else {
//                            s += "2>";
//                        }
//                        break;
//                    case GOING_DOWN_ONE_FROM_SURFACE:
//                        if (info.getDirection() == WEST) {
//                            s += "<1";
//                        } else {
//                            s += "1>";
//                        }
//                        break;
//                    case GOING_DOWN_FURTHER:
//                        if (info.getDirection() == WEST) {
//                            s += "<<";
//                        } else {
//                            s += ">>";
//                        }
//                        break;
//                    case HORIZONTAL:
//                        if (info.getRails() > 1) {
//                            s += "==";
//                        } else {
//                            s += "--";
//                        }
//                        break;
//                    case THREE_SPLIT:
//                        s += "=-";
//                        break;
//                    case VERTICAL:
//                        s += "||";
//                        break;
//                    case DOUBLE_BEND:
//                        s += "<>";
//                        break;
//                }
//            }
//            System.out.println("" + s);
//        }
//    }
}