package mcjty.lostcities.worldgen.gen;

import mcjty.lostcities.api.RailChunkType;
import mcjty.lostcities.worldgen.ChunkDriver;
import mcjty.lostcities.worldgen.ChunkHeightmap;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.Railway;
import mcjty.lostcities.worldgen.lost.Transform;
import mcjty.lostcities.worldgen.lost.cityassets.AssetRegistries;
import mcjty.lostcities.worldgen.lost.cityassets.BuildingPart;
import mcjty.lostcities.worldgen.lost.regassets.data.RailwayParts;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Railways {
    public static void generateRailwayDungeons(LostCityTerrainFeature feature, BuildingInfo info) {
        if (info.railDungeon == null) {
            return;
        }
        if (info.getZmin().getRailInfo().getType() == RailChunkType.RAIL ||
                info.getZmax().getRailInfo().getType() == RailChunkType.RAIL) {
            int height = info.groundLevel + Railway.RAILWAY_LEVEL_OFFSET * LostCityTerrainFeature.FLOORHEIGHT;
            feature.generatePart(info, info.railDungeon, Transform.ROTATE_NONE, 0, height, 0, LostCityTerrainFeature.HardAirSetting.AIR);
        }
    }

    public static void generateRailways(LostCityTerrainFeature feature, BuildingInfo info, Railway.RailChunkInfo railInfo, ChunkHeightmap heightmap) {
        IDimensionInfo provider = feature.provider;
        ChunkDriver driver = feature.driver;
        BlockState liquid = feature.liquid;
        CityStyle cityStyle = info.getCityStyle();
        int height = info.groundLevel + railInfo.getLevel() * LostCityTerrainFeature.FLOORHEIGHT;
        RailChunkType type = railInfo.getType();
        BuildingPart part;
        Transform transform = Transform.ROTATE_NONE;

        int chunkX = info.coord.chunkX();
        int chunkZ = info.coord.chunkZ();

        // @todo: DRIGSTER Need to find where to get this from
        int areasize = 10;

        int ax = (chunkX + 2000000) / areasize;
        int az = (chunkZ + 2000000) / areasize;
        QualityRandom railwayRandom = new QualityRandom(
                provider.getSeed() + ax * 5564338337L + az * 25564337621L);
        switch (railInfo.getDirection()) {
            case NORTH:
            case BI: {
                break;
            }
            case SOUTH: {
                transform = Transform.ROTATE_180;
                break;
            }
            case WEST: {
                transform = Transform.ROTATE_270;
                break;
            }
            case EAST: {
                transform = Transform.ROTATE_90;
                break;
            }
        }
        boolean needsStaircase = false;
        boolean clearUpper = false;
        String partName;
        switch (type) {
            case NONE:
                return;
            case STATION_SURFACE:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayStation(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);

                clearUpper = true;
                break;
            case STATION_UNDERGROUND:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayStationUnderground(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                if (part.getMetaBoolean("staircase")) {
                    needsStaircase = true;
                }
                break;
            case RAILS_END_HERE:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayRailEnd(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                break;
            case RAIL:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayRail(railwayRandom);
                }

                RailChunkType type1 = info.getXmin().getRailInfo().getType();
                RailChunkType type2 = info.getXmax().getRailInfo().getType();
                if (railInfo.getDirection() == Railway.RailDirection.EAST
                        || railInfo.getDirection() == Railway.RailDirection.WEST) {
                    if (!type1.isStation() && !type2.isStation()) {
                        if (driver.getBlock(3, height + 2, 3) == liquid
                                && driver.getBlock(12, height + 2, 3) == liquid
                                && driver.getBlock(3, height + 2, 12) == liquid
                                && driver.getBlock(12, height + 2, 12) == liquid
                                && driver.getBlock(3, height + 4, 7) == liquid
                                && driver.getBlock(12, height + 4, 8) == liquid) {
                            partName = cityStyle.getRandomRailwayRailWater(railwayRandom);
                        }
                    }
                } else {
                    if (driver.getBlock(3, height + 2, 3) == liquid
                            && driver.getBlock(12, height + 2, 3) == liquid
                            && driver.getBlock(3, height + 2, 12) == liquid
                            && driver.getBlock(12, height + 2, 12) == liquid
                            && driver.getBlock(3, height + 4, 7) == liquid
                            && driver.getBlock(12, height + 4, 8) == liquid) {
                        partName = cityStyle.getRandomRailwayRailWater(railwayRandom);
                    }
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                break;
            case X_JUNCTION:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayXJunction(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                break;
            case T_JUNCTION:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayTJunction(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                break;
            case GOING_DOWN_TWO_FROM_SURFACE:
            case GOING_DOWN_FURTHER:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayRail(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                // part = AssetRegistries.PARTS.getOrThrow(provider.getWorld(),
                //         feature.getRandomPart(railwayParts.railsDown2()));
                break;
            case GOING_DOWN_ONE_FROM_SURFACE:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayRail(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                // part = AssetRegistries.PARTS.getOrThrow(provider.getWorld(),
                //         feature.getRandomPart(railwayParts.railsDown1()));
                break;
            default:
                if (railInfo.getPartName() != null) {
                    partName = railInfo.getPartName();
                } else {
                    partName = cityStyle.getRandomRailwayRail(railwayRandom);
                }

                part = getPart(partName, railInfo, provider, railwayRandom, cityStyle, info.coord);
                break;
        }
        int h = feature.generatePart(info, part, transform, 0, height, 0,
                LostCityTerrainFeature.HardAirSetting.AIR);
        if (clearUpper) {
            int maxh = heightmap.getHeight() + 4;
            if (h < maxh) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        feature.clearRange(info, x, z, h, maxh, false);
                    }
                }
            }
        }

        // Character railMainBlock = info.getCityStyle().getRailMainBlock();
        // BlockState rail = info.getCompiledPalette().get(railMainBlock);
        // if (rail == null) {
        //     throw new RuntimeException(
        //             "Cannot find rail block '" + railMainBlock + "' for type '" + type + "'!");
        // }
        // if (railInfo.getDirection() == Railway.RailDirection.EAST
        //         || railInfo.getDirection() == Railway.RailDirection.WEST) {
        //     // If there is a rail dungeon north or south we must make a connection here
        //     if (info.getZmin().railDungeon != null) {
        //         for (int z = 0; z < 4; z++) {
        //             driver.current(6, height + 1, z).add(rail).add(air).add(air);
        //             driver.current(7, height + 1, z).add(rail).add(air).add(air);
        //         }
        //         for (int z = 0; z < 3; z++) {
        //             driver.current(5, height + 2, z).add(rail).add(rail).add(rail);
        //             driver.current(6, height + 4, z).block(rail);
        //             driver.current(7, height + 4, z).block(rail);
        //             driver.current(8, height + 2, z).add(rail).add(rail).add(rail);
        //         }
        //     }
        //     if (info.getZmax().railDungeon != null) {
        //         for (int z = 0; z < 5; z++) {
        //             driver.current(6, height + 1, 15 - z).add(rail).add(air).add(air);
        //             driver.current(7, height + 1, 15 - z).add(rail).add(air).add(air);
        //         }
        //         for (int z = 0; z < 4; z++) {
        //             driver.current(5, height + 2, 15 - z).add(rail).add(rail).add(rail);
        //             driver.current(6, height + 4, 15 - z).block(rail);
        //             driver.current(7, height + 4, 15 - z).block(rail);
        //             driver.current(8, height + 2, 15 - z).add(rail).add(rail).add(rail);
        //         }
        //     }
        // }
        if (needsStaircase) {
            int heightOffset = Math.ceilDiv(part.getSliceCount(), 6);
            part = AssetRegistries.PARTS.getOrThrow(provider.getWorld(), cityStyle.getRandomRailwayStationStaircase(railwayRandom));
            for (int i = railInfo.getLevel() + heightOffset; i < info.cityLevel; i++) {
                height = info.groundLevel + i * LostCityTerrainFeature.FLOORHEIGHT;
                feature.generatePart(info, part, transform, 0, height, 0,
                        LostCityTerrainFeature.HardAirSetting.AIR);
            }
            height = info.groundLevel + info.cityLevel * LostCityTerrainFeature.FLOORHEIGHT;
            part = AssetRegistries.PARTS.getOrThrow(provider.getWorld(),
                    cityStyle.getRandomRailwayStationStaircase(railwayRandom));
            feature.generatePart(info, part, transform, 0, height, 0,
                    LostCityTerrainFeature.HardAirSetting.AIR);
        }
    }

    static BuildingPart getPart(String partName, Railway.RailChunkInfo railInfo, IDimensionInfo provider, QualityRandom random, CityStyle cityStyle, ChunkCoord coord) {
        MultiBuilding multiBuilding = AssetRegistries.MULTI_BUILDINGS.getOrWarn(provider.getWorld(), partName);

        if (multiBuilding != null) {
            ChunkCoord origin = railInfo.getOrigin();

            int relx = coord.chunkX() - origin.chunkX();
            int relz = coord.chunkZ() - origin.chunkZ();

            String buildingName = multiBuilding.getBuilding(relx, relz);
            Building building
                    = AssetRegistries.BUILDINGS.getOrThrow(provider.getWorld(), buildingName);

            return AssetRegistries.PARTS.getOrThrow(provider.getWorld(), building.getRandomPart(random));
        } else {
            return AssetRegistries.PARTS.getOrThrow(provider.getWorld(), partName);
        }
    }
}
