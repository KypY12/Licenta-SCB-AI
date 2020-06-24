package map;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.*;
import com.springrts.ai.oo.clb.Map;
import logger.AILogger;
import map.pathfinding.PathCreator;

import java.util.*;

public class GeneralMap {

    private AILogger logger;
    private OOAICallback callback;

    private Map engineMap;

    // Buildings Maps
    private List<List<Float>> buildingsMap1024 = new ArrayList<>();
    private List<List<Float>> buildingsMap512 = new ArrayList<>();
    private List<List<Float>> buildingsMap256 = new ArrayList<>();
    private List<List<Float>> buildingsMap128 = new ArrayList<>();
    private List<List<Float>> buildingsMap64 = new ArrayList<>();


    // Slope Maps
    private List<List<Float>> slopeMap512; // max 2 size units
    private List<List<Float>> slopeMap256; // max 4 size units
    private List<List<Float>> slopeMap128; // max 8 size units
    private List<List<Float>> slopeMap64; // max 16 size units


    // Height Maps
    private List<List<Float>> heightMap1024; // max 1 size units
    private List<List<Float>> heightMap512; // max 2 size units
    private List<List<Float>> heightMap256; // max 4 size units
    private List<List<Float>> heightMap128; // max 8 size units
    private List<List<Float>> heightMap64; // max 16 size units


    // Resource Maps (doar metal)
    private List<List<Float>> resourceMap512; // max 2 size units
    private List<List<Float>> resourceMap256; // max 4 size units
    private List<List<Float>> resourceMap128; // max 8 size units
    private List<List<Float>> resourceMap64; // max 16 size units


    // LOS Map
    private int losIndex;
    private List<List<Integer>> losMap; // max 2 size units


    // Exploration Map
    private List<List<Integer>> explorationMap;
    private java.util.Map<Integer, List<AIInt2>> notExploredMap;


    private List<AIFloat3> availableMetalSpots;


    private PathCreator pathCreator;


    public GeneralMap(AILogger logger) {
        this.logger = logger;
        this.callback = logger.getCallback();
        this.engineMap = callback.getMap();

        constructHeightMaps();
        constructSlopeMaps();
        constructResourceMaps();
        constructBuildingsMaps();

        losIndex = (int) Math.pow(2, callback.getMod().getLosMipLevel());

        constructLosMap();
        constructExplorationMap();

        this.pathCreator = new PathCreator(logger);

        Resource metal = callback.getResourceByName("Metal");
        this.availableMetalSpots = this.engineMap.getResourceMapSpotsPositions(metal);

    }


    // ============================================ MAPS CONSTRUCTORS ==================================================

    private void constructHeightMaps() {
        List<Float> classicHeightMap = engineMap.getHeightMap();
        int mapWidth = this.engineMap.getWidth();
        int mapHeight = classicHeightMap.size() / mapWidth;


        heightMap1024 = new ArrayList<>();
        for (int zIndex = 0; zIndex < mapHeight; zIndex++) {
            heightMap1024.add(new ArrayList<>());
            int currentSegment = zIndex * mapWidth;
            for (int xIndex = 0; xIndex < mapWidth; xIndex++) {
                heightMap1024.get(zIndex).add(classicHeightMap.get(currentSegment + xIndex));
            }
        }

        heightMap512 = new ArrayList<>();
        for (int zIndex = 0; zIndex < heightMap1024.size(); zIndex += 2) {
            heightMap512.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < heightMap1024.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(heightMap1024.get(zIndex).get(xIndex), heightMap1024.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(heightMap1024.get(zIndex + 1).get(xIndex), heightMap1024.get(zIndex + 1).get(xIndex + 1));
                heightMap512.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        heightMap256 = new ArrayList<>();
        for (int zIndex = 0; zIndex < heightMap512.size(); zIndex += 2) {
            heightMap256.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < heightMap512.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(heightMap512.get(zIndex).get(xIndex), heightMap512.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(heightMap512.get(zIndex + 1).get(xIndex), heightMap512.get(zIndex + 1).get(xIndex + 1));
                heightMap256.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        heightMap128 = new ArrayList<>();
        for (int zIndex = 0; zIndex < heightMap256.size(); zIndex += 2) {
            heightMap128.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < heightMap256.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(heightMap256.get(zIndex).get(xIndex), heightMap256.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(heightMap256.get(zIndex + 1).get(xIndex), heightMap256.get(zIndex + 1).get(xIndex + 1));
                heightMap128.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        heightMap64 = new ArrayList<>();
        for (int zIndex = 0; zIndex < heightMap128.size(); zIndex += 2) {
            heightMap64.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < heightMap128.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(heightMap128.get(zIndex).get(xIndex), heightMap128.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(heightMap128.get(zIndex + 1).get(xIndex), heightMap128.get(zIndex + 1).get(xIndex + 1));
                heightMap64.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        logger.log("SLOPE MAPS 1024");
        logger.log(this.heightMap1024.size());
        logger.log(this.heightMap1024.get(0).size());
        logger.log(this.heightMap1024.get(heightMap1024.size() - 1).size());
        logger.log("SLOPE MAPS 512");
        logger.log(this.heightMap512.size());
        logger.log(this.heightMap512.get(0).size());
        logger.log(this.heightMap512.get(heightMap512.size() - 1).size());
        logger.log("SLOPE MAPS 256");
        logger.log(this.heightMap256.size());
        logger.log(this.heightMap256.get(0).size());
        logger.log(this.heightMap256.get(heightMap256.size() - 1).size());
        logger.log("SLOPE MAPS 128");
        logger.log(this.heightMap128.size());
        logger.log(this.heightMap128.get(0).size());
        logger.log(this.heightMap128.get(heightMap128.size() - 1).size());
        logger.log("SLOPE MAPS 64");
        logger.log(this.heightMap64.size());
        logger.log(this.heightMap64.get(0).size());
        logger.log(this.heightMap64.get(heightMap64.size() - 1).size());

    }

    private void constructSlopeMaps() {
        List<Float> classicSlopeMap = this.engineMap.getSlopeMap();
        int mapWidth = this.engineMap.getWidth() / 2; // SlopeMap e HeighMap/2
        int mapHeight = classicSlopeMap.size() / mapWidth;

        slopeMap512 = new ArrayList<>();
        for (int zIndex = 0; zIndex < mapHeight; zIndex++) {
            slopeMap512.add(new ArrayList<>());
            int currentSegment = zIndex * mapWidth;
            for (int xIndex = 0; xIndex < mapWidth; xIndex++) {
                slopeMap512.get(zIndex).add(classicSlopeMap.get(currentSegment + xIndex));
            }
        }

        slopeMap256 = new ArrayList<>();
        for (int zIndex = 0; zIndex < slopeMap512.size(); zIndex += 2) {
            slopeMap256.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < slopeMap512.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(slopeMap512.get(zIndex).get(xIndex), slopeMap512.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(slopeMap512.get(zIndex + 1).get(xIndex), slopeMap512.get(zIndex + 1).get(xIndex + 1));
                slopeMap256.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        slopeMap128 = new ArrayList<>();
        for (int zIndex = 0; zIndex < slopeMap256.size(); zIndex += 2) {
            slopeMap128.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < slopeMap256.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(slopeMap256.get(zIndex).get(xIndex), slopeMap256.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(slopeMap256.get(zIndex + 1).get(xIndex), slopeMap256.get(zIndex + 1).get(xIndex + 1));
                slopeMap128.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        slopeMap64 = new ArrayList<>();
        for (int zIndex = 0; zIndex < slopeMap128.size(); zIndex += 2) {
            slopeMap64.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < slopeMap128.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(slopeMap128.get(zIndex).get(xIndex), slopeMap128.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(slopeMap128.get(zIndex + 1).get(xIndex), slopeMap128.get(zIndex + 1).get(xIndex + 1));
                slopeMap64.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        logger.log("SLOPE MAPS 512");
        logger.log(this.slopeMap512.size());
        logger.log(this.slopeMap512.get(0).size());
        logger.log(this.slopeMap512.get(slopeMap512.size() - 1).size());
        logger.log("SLOPE MAPS 256");
        logger.log(this.slopeMap256.size());
        logger.log(this.slopeMap256.get(0).size());
        logger.log(this.slopeMap256.get(slopeMap256.size() - 1).size());
        logger.log("SLOPE MAPS 128");
        logger.log(this.slopeMap128.size());
        logger.log(this.slopeMap128.get(0).size());
        logger.log(this.slopeMap128.get(slopeMap128.size() - 1).size());
        logger.log("SLOPE MAPS 64");
        logger.log(this.slopeMap64.size());
        logger.log(this.slopeMap64.get(0).size());
        logger.log(this.slopeMap64.get(slopeMap64.size() - 1).size());

    }

    private void constructResourceMaps() {
        List<Short> classicResourceMap = this.engineMap.getResourceMapRaw(callback.getResourceByName("Metal"));

        List<Float> classicFloatResourceMap = new ArrayList<>();
        for (Short elem : classicResourceMap) {
            classicFloatResourceMap.add((float) elem);
        }

        int mapWidth = this.engineMap.getWidth() / 2; // ResourceMap e HeighMap/2
        int mapHeight = classicFloatResourceMap.size() / mapWidth;

        resourceMap512 = new ArrayList<>();
        for (int zIndex = 0; zIndex < mapHeight; zIndex++) {
            resourceMap512.add(new ArrayList<>());
            int currentSegment = zIndex * mapWidth;
            for (int xIndex = 0; xIndex < mapWidth; xIndex++) {
                resourceMap512.get(zIndex).add(classicFloatResourceMap.get(currentSegment + xIndex));
            }
        }

        resourceMap256 = new ArrayList<>();
        for (int zIndex = 0; zIndex < resourceMap512.size(); zIndex += 2) {
            resourceMap256.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < resourceMap512.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(resourceMap512.get(zIndex).get(xIndex), resourceMap512.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(resourceMap512.get(zIndex + 1).get(xIndex), resourceMap512.get(zIndex + 1).get(xIndex + 1));
                resourceMap256.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        resourceMap128 = new ArrayList<>();
        for (int zIndex = 0; zIndex < resourceMap256.size(); zIndex += 2) {
            resourceMap128.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < resourceMap256.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(resourceMap256.get(zIndex).get(xIndex), resourceMap256.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(resourceMap256.get(zIndex + 1).get(xIndex), resourceMap256.get(zIndex + 1).get(xIndex + 1));
                resourceMap128.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        resourceMap64 = new ArrayList<>();
        for (int zIndex = 0; zIndex < resourceMap128.size(); zIndex += 2) {
            resourceMap64.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < resourceMap128.get(0).size(); xIndex += 2) {
                float maxSlopeLine1 = Math.max(resourceMap128.get(zIndex).get(xIndex), resourceMap128.get(zIndex).get(xIndex + 1));
                float maxSlopeLine2 = Math.max(resourceMap128.get(zIndex + 1).get(xIndex), resourceMap128.get(zIndex + 1).get(xIndex + 1));
                resourceMap64.get(zIndex / 2).add(Math.max(maxSlopeLine1, maxSlopeLine2));
            }
        }

        logger.log("RESOURCE MAPS 512");
        logger.log(this.resourceMap512.size());
        logger.log(this.resourceMap512.get(0).size());
        logger.log(this.resourceMap512.get(resourceMap512.size() - 1).size());
        logger.log("RESOURCE MAPS 256");
        logger.log(this.resourceMap256.size());
        logger.log(this.resourceMap256.get(0).size());
        logger.log(this.resourceMap256.get(resourceMap256.size() - 1).size());
        logger.log("RESOURCE MAPS 128");
        logger.log(this.resourceMap128.size());
        logger.log(this.resourceMap128.get(0).size());
        logger.log(this.resourceMap128.get(resourceMap128.size() - 1).size());
        logger.log("RESOURCE MAPS 64");
        logger.log(this.resourceMap64.size());
        logger.log(this.resourceMap64.get(0).size());
        logger.log(this.resourceMap64.get(resourceMap64.size() - 1).size());

    }

    private void constructBuildingsMaps() {
        try {
            // Harta poate sa nu fie patrat !!!
            float mapXSize = this.engineMap.getWidth();
            float mapZSize = this.engineMap.getHeight();

            for (int mapIndex = 1024; mapIndex >= 64; mapIndex /= 2) {
                List<List<Float>> correspodingMap = getCorrespondingBMap(mapIndex);
                for (int i = 0; i < mapZSize; ++i) {
                    correspodingMap.add(new ArrayList<Float>());
                    for (int j = 0; j < mapXSize; ++j) {
                        correspodingMap.get(i).add(0f);
                    }
                }
                mapXSize /= 2;
                mapZSize /= 2;
            }
        } catch (Exception e) {
            logger.log(e);
        }

    }

    private void constructLosMap() {
        List<Integer> classicLosMap = engineMap.getLosMap();
        int divSize = losIndex;

        int mapWidth = this.engineMap.getWidth() / divSize;
        int mapHeight = classicLosMap.size() / mapWidth;

        losMap = new ArrayList<>();
        for (int zIndex = 0; zIndex < mapHeight; zIndex++) {
            losMap.add(new ArrayList<>());
            int currentSegment = zIndex * mapWidth;
            for (int xIndex = 0; xIndex < mapWidth; xIndex++) {
                losMap.get(zIndex).add(classicLosMap.get(currentSegment + xIndex));
            }
        }

    }

    private void constructExplorationMap() {
        int divSize = losIndex * 8;

        List<List<Float>> correspondingHMap = getCorrespondingHMap(getMapIndexFromDivSize(divSize));

        notExploredMap = new HashMap<>();
        notExploredMap.put(0, new LinkedList<>());
        notExploredMap.put(2, new LinkedList<>());

        int mapWidth = this.engineMap.getWidth() / losIndex;
        int mapHeight = this.engineMap.getHeight() / losIndex;

        explorationMap = new ArrayList<>();
        for (int zIndex = 0; zIndex < mapHeight; zIndex++) {
            explorationMap.add(new ArrayList<>());
            for (int xIndex = 0; xIndex < mapWidth; xIndex++) {
                if (correspondingHMap.get(zIndex).get(xIndex) > 0) {
                    explorationMap.get(zIndex).add(0);
                    notExploredMap.get(0).add(new AIInt2(xIndex, zIndex));
                } else {
                    explorationMap.get(zIndex).add(2);
                    notExploredMap.get(2).add(new AIInt2(xIndex, zIndex));
                }
            }
        }
        logger.log("Exploration map size (width, height) : ");
        logger.log(explorationMap.get(0).size());
        logger.log(explorationMap.size());
        logger.log(notExploredMap.get(0).size());
        logger.log(notExploredMap.get(2).size());
    }

    public void updateLosMap() {
        constructLosMap();
    }

    public void clearExplorationMap() {
        List<List<Float>> correspondingHMap = getCorrespondingHMap(getMapIndexFromDivSize(losIndex));

        notExploredMap.get(0).clear();
        notExploredMap.get(2).clear();

        for (int zIndex = 0; zIndex < explorationMap.size(); zIndex++) {
            for (int xIndex = 0; xIndex < explorationMap.get(0).size(); xIndex++) {
                if (correspondingHMap.get(zIndex).get(xIndex) > 0) {
                    explorationMap.get(zIndex).set(xIndex, 0);
                    notExploredMap.get(0).add(new AIInt2(xIndex, zIndex));
                } else {
                    explorationMap.get(zIndex).set(xIndex, 2);
                    notExploredMap.get(2).add(new AIInt2(xIndex, zIndex));
                }
            }
        }
    }


    // ============================================== INTERNAL HELPERS =================================================

    public int getMaxSize(List<Unit> units) {
        int maxSize = 0;
        for (Unit unit : units) {
            {
                int current = Math.max(unit.getDef().getXSize(), unit.getDef().getZSize());
                if (current > maxSize) {
                    maxSize = current;
                }
            }

        }
        return maxSize;
    }


    // ============================================== MOVING FUNCTIONS =================================================

    public List<AIFloat3> constructPath(AIFloat3 start, AIFloat3 finish, float maxSlope, int size) {

        if (2 <= size && size < 4) {
            this.pathCreator.setCurrentMaps(this.slopeMap512, this.heightMap512, this.buildingsMap512, 512);
        } else if (4 <= size && size < 8) {
            this.pathCreator.setCurrentMaps(this.slopeMap256, this.heightMap256, this.buildingsMap256, 256);
        } else if (8 <= size && size < 16) {
            this.pathCreator.setCurrentMaps(this.slopeMap128, this.heightMap128, this.buildingsMap128, 128);
        } else if (16 <= size && size < 32) {
            this.pathCreator.setCurrentMaps(this.slopeMap64, this.heightMap64, this.buildingsMap64, 64);
        }

        return this.pathCreator.createPath(start, finish, maxSlope);
    }


    // ========================================== CONVERSION FUNCTIONS =================================================

    private int convertUnitSize(int mapIndex, int unitSize) {
        int result = 0;
        if (mapIndex == 1024) {
            result = unitSize;
        } else if (mapIndex == 512) {
            result = unitSize / 2;
        } else if (mapIndex == 256) {
            result = unitSize / 4;
        } else if (mapIndex == 128) {
            result = unitSize / 8;
        } else if (mapIndex == 64) {
            result = unitSize / 16;
        }

        if (result <= 0) {
            return 1;
        } else {
            return result;
        }
    }

    public int getMapIndexFromUnitSize(Unit unit) {
        int mapIndex = 1024;
        int maxSize = Math.max(unit.getDef().getXSize(), unit.getDef().getZSize());
        while (mapIndex >= 64 && convertUnitSize(mapIndex, maxSize) != 1) {
            mapIndex /= 2;
        }
        return mapIndex;
    }

    private int getDivSize(int mapIndex) {
        int divSize = 1;
        if (mapIndex == 1024) {
            divSize = 8;
        } else if (mapIndex == 512) {
            divSize = 16;
        } else if (mapIndex == 256) {
            divSize = 32;
        } else if (mapIndex == 128) {
            divSize = 64;
        } else if (mapIndex == 64) {
            divSize = 128;
        }
        return divSize;
    }

    private int getMapIndexFromDivSize(int divSize) {
        if (divSize == 8) {
            return 1024;
        } else if (divSize == 16) {
            return 512;
        } else if (divSize == 32) {
            return 256;
        } else if (divSize == 64) {
            return 128;
        } else if (divSize == 128) {
            return 64;
        }
        return 0;
    }


    // ============================================ GET CORRESPONDINGS =================================================

    private List<List<Float>> getCorrespondingHMap(int mapIndex) {
        try {
            if (mapIndex == 1024) {
                return heightMap1024;
            } else if (mapIndex == 512) {
                return heightMap512;
            } else if (mapIndex == 256) {
                return heightMap256;
            } else if (mapIndex == 128) {
                return heightMap128;
            } else if (mapIndex == 64) {
                return heightMap64;
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }

    private List<List<Float>> getCorrespondingSMap(int mapIndex) {
        try {
            if (mapIndex == 512) {
                return slopeMap512;
            } else if (mapIndex == 256) {
                return slopeMap256;
            } else if (mapIndex == 128) {
                return slopeMap128;
            } else if (mapIndex == 64) {
                return slopeMap64;
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }

    private List<List<Float>> getCorrespondingRMap(int mapIndex) {
        try {
            if (mapIndex == 512) {
                return resourceMap512;
            } else if (mapIndex == 256) {
                return resourceMap256;
            } else if (mapIndex == 128) {
                return resourceMap128;
            } else if (mapIndex == 64) {
                return resourceMap64;
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }

    private List<List<Float>> getCorrespondingBMap(int mapIndex) {
        try {
            if (mapIndex == 1024) {
                return buildingsMap1024;
            } else if (mapIndex == 512) {
                return buildingsMap512;
            } else if (mapIndex == 256) {
                return buildingsMap256;
            } else if (mapIndex == 128) {
                return buildingsMap128;
            } else if (mapIndex == 64) {
                return buildingsMap64;
            }

        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }


    // ================================================== ADD / REMOVE =================================================

    private void addElementsToBuildingsMap(List<List<Float>> buildingsMap, int x1, int z1, int x2, int z2, float element) {
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                buildingsMap.get(z).set(x, element);
            }
        }
    }

    private void addToNextBuildingsMap(List<List<Float>> buildingsMap, List<List<Float>> prevBuildingsMap,
                                       int x1, int z1, int x2, int z2, float opposingElement, float element) {
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                int xPrev = x * 2;
                int zPrev = z * 2;
                boolean set = false;

                for (int xP = xPrev; xP < xPrev + 2; ++xP) {
                    for (int zP = zPrev; zP < zPrev + 2; ++zP) {
                        if (prevBuildingsMap.get(zP).get(xP) == opposingElement) {
                            set = true;
                            break;
                        }

                    }
                    if (set) break;
                }

                if (!set) {
                    buildingsMap.get(z).set(x, element);
                }
            }
        }
    }

    public void addBuildingToBMap(Unit building) {
        try {
            float xPos = building.getPos().x;
            float zPos = building.getPos().z;
            float xSize = building.getDef().getXSize();
            float zSize = building.getDef().getZSize();

            // 1 = sus stanga ; 2 = jos dreapta
            int x1 = (int) (xPos / 8);
            int z1 = (int) (zPos / 8);
            int x2 = (int) ((xPos + xSize) / 8);
            int z2 = (int) ((zPos + zSize) / 8);

            for (int mapIndex = 1024; mapIndex >= 64; mapIndex /= 2) {
                addElementsToBuildingsMap(getCorrespondingBMap(mapIndex), x1, z1, x2, z2, 1f);
                x1 /= 2;
                z1 /= 2;
                x2 /= 2;
                z2 /= 2;
            }


        } catch (Exception e) {
            logger.log(e);
        }
    }

    public void removeBuilding(Unit building) {
        try {

            float xPos = building.getPos().x;
            float zPos = building.getPos().z;
            float xSize = building.getDef().getXSize();
            float zSize = building.getDef().getZSize();

            int x1 = (int) (xPos / 8);
            int z1 = (int) (zPos / 8);
            int x2 = (int) ((xPos + xSize) / 8);
            int z2 = (int) ((zPos + zSize) / 8);

            int mapIndex = 1024;
            int prevMapIndex = 1024;
            addElementsToBuildingsMap(getCorrespondingBMap(mapIndex), x1, z1, x2, z2, 0f);

            for (mapIndex = 512; mapIndex >= 64; mapIndex /= 2) {
                x1 /= 2;
                z1 /= 2;
                x2 /= 2;
                z2 /= 2;

                addToNextBuildingsMap(getCorrespondingBMap(mapIndex), getCorrespondingBMap(prevMapIndex), x1, z1, x2, z2, 1f, 0f);
                prevMapIndex = mapIndex;
            }

        } catch (Exception e) {
            logger.log(e);
        }

    }


    // ========================================== INFO GETTERS TO OUTSIDE ==============================================

    private boolean checkAreaForResource(int xLeft, int zUp, int xSize, int zSize, int mapIndex, boolean useMatrixCoords) {
        if (useMatrixCoords) {
            List<List<Float>> resourceMap = getCorrespondingRMap(mapIndex);
            int xMax = xLeft + xSize;
            int zMax = zUp + zSize;
            for (int xIndex = xLeft; xIndex < xMax; ++xIndex) {
                for (int zIndex = zUp; zIndex < zMax; ++zIndex) {
                    if (resourceMap.get(zIndex).get(xIndex) != 0f) {
                        return true;
                    }
                }
            }
            return false;

        } else {
            List<List<Float>> resourceMap = getCorrespondingRMap(mapIndex);
            int xMax = xLeft + convertUnitSize(mapIndex, xSize);
            int zMax = zUp + convertUnitSize(mapIndex, zSize);
            for (int xIndex = xLeft; xIndex < xMax; ++xIndex) {
                for (int zIndex = zUp; zIndex < zMax; ++zIndex) {
                    if (resourceMap.get(zIndex).get(xIndex) != 0f) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // Cauta o pozitie pentru constructia lui toBuild in apropierea lui startSearchPos
    public AIFloat3 getClosestBuildPosForFrom(UnitDef toBuild, AIFloat3 startSearchPos, int mapIndex) {
        int divSize = getDivSize(mapIndex);
        int xStart = (int) (startSearchPos.x / divSize);
        int zStart = (int) (startSearchPos.z / divSize);
        float xCoord = 0, zCoord = 0;
        int leftStart = 0, rightEnd = 0, upStart = 0, downEnd = 0;

        List<List<Float>> correspondingBMap = getCorrespondingBMap(mapIndex);
        int xMax = correspondingBMap.get(0).size();
        int zMax = correspondingBMap.size();

        logger.log(toBuild);
        int unitXSize = convertUnitSize(mapIndex, toBuild.getXSize());
        int unitZSize = convertUnitSize(mapIndex, toBuild.getZSize());


        int incrementIndex = 1;
        boolean found = false;

        while (!found) {
            leftStart = Math.max(xStart - incrementIndex, 0);
            rightEnd = Math.min(xStart + incrementIndex, xMax);
            upStart = Math.max(zStart - incrementIndex, 0);
            downEnd = Math.min(zStart + incrementIndex, zMax);

            // partea de sus
            if (zStart - incrementIndex >= 0) {
                zCoord = upStart * divSize;
                for (int index = leftStart; index <= rightEnd; ++index) {
                    xCoord = index * divSize;
                    AIFloat3 position = new AIFloat3(xCoord, 0f, zCoord);
                    if (this.engineMap.isPossibleToBuildAt(toBuild, position, 0) &&
                            correspondingBMap.get(upStart).get(index) == 0 &&
                            !checkAreaForResource(index, upStart, unitXSize, unitZSize, mapIndex, true)) {
                        found = true;
                        return position;
                    }
                }
            }

            // partea de jos
            if (zStart + incrementIndex < zMax) {
                zCoord = downEnd * divSize;
                for (int index = leftStart; index <= rightEnd; ++index) {
                    xCoord = index * divSize;
                    AIFloat3 position = new AIFloat3(xCoord, 0f, zCoord);
                    if (this.engineMap.isPossibleToBuildAt(toBuild, position, 0) &&
                            correspondingBMap.get(downEnd).get(index) == 0 &&
                            !checkAreaForResource(index, downEnd, unitXSize, unitZSize, mapIndex, true)) {
                        found = true;
                        return position;
                    }
                }
            }

            // partea din stanga
            if (xStart - incrementIndex >= 0) {
                xCoord = leftStart * divSize;
                for (int index = upStart + 1; index < downEnd; ++index) {
                    zCoord = index * divSize;
                    AIFloat3 position = new AIFloat3(xCoord, 0f, zCoord);
                    if (this.engineMap.isPossibleToBuildAt(toBuild, position, 0) &&
                            correspondingBMap.get(index).get(leftStart) == 0 &&
                            !checkAreaForResource(leftStart, index, unitXSize, unitZSize, mapIndex, true)) {
                        found = true;
                        return position;
                    }
                }
            }

            // partea din dreapta
            if (xStart + incrementIndex < xMax) {
                xCoord = rightEnd * divSize;
                for (int index = upStart + 1; index < downEnd; ++index) {
                    zCoord = index * divSize;
                    AIFloat3 position = new AIFloat3(xCoord, 0f, zCoord);
                    if (this.engineMap.isPossibleToBuildAt(toBuild, position, 0) &&
                            correspondingBMap.get(index).get(rightEnd) == 0 &&
                            !checkAreaForResource(rightEnd, index, unitXSize, unitZSize, mapIndex, true)) {
                        found = true;
                        return position;
                    }
                }
            }

            ++incrementIndex;
        }

        return null;
    }

    public List<AIFloat3> getBuildGroupPositions(int xCoord, int zCoord, int xCount, int zCount, int xSize, int zSize, int divSize, boolean useMatrixCoords) {
        if (useMatrixCoords) {
            int matrixDivSize = divSize;
            int xMax = xCoord + xCount * xSize;
            int zMax = zCoord + zCount * zSize;
            List<AIFloat3> positions = new ArrayList<>(xCount * zCount);
            for (int xIndex = xCoord; xIndex < xMax; xIndex += xSize) {
                for (int zIndex = zCoord; zIndex < zMax; zIndex += zSize) {
                    positions.add(new AIFloat3(xIndex * matrixDivSize, 0f, zIndex * matrixDivSize));
                }
            }
            return positions;
        } else {
            List<AIFloat3> positions = new ArrayList<>(xCount * zCount);
            float xStart = xCoord * divSize;
            float zStart = zCoord * divSize;
            for (int xIndex = 0; xIndex < xCount; xIndex++) {
                for (int zIndex = 0; zIndex < zCount; zIndex++) {
                    positions.add(new AIFloat3(xStart + xIndex * xSize * 8, 0, zStart + zIndex * zSize * 8));
                }
            }
            return positions;
        }
    }

    public boolean checkIfPossibleToBuildGroup(UnitDef toBuild, List<AIFloat3> positions, int facing) {

        for (AIFloat3 pos : positions) {
            if (!engineMap.isPossibleToBuildAt(toBuild, pos, facing)) {
                return false;
            }
        }
        return true;
    }

    // Cauta o pozitie pentru constructia lui toBuild in apropierea lui startSearchPos
    public List<AIFloat3> getClosestBuildPosForFrom(UnitDef toBuild, int countX, int countZ, AIFloat3 startSearchPos, int mapIndex, boolean useMatrixCoords) {
        int divSize = getDivSize(mapIndex);
        int xStart = (int) (startSearchPos.x / divSize);
        int zStart = (int) (startSearchPos.z / divSize);
        int xCoord = 0, zCoord = 0;
        int leftStart = 0, rightEnd = 0, upStart = 0, downEnd = 0;

        List<List<Float>> correspondingBMap = getCorrespondingBMap(mapIndex);
        int xMax = correspondingBMap.get(0).size();
        int zMax = correspondingBMap.size();

        int unitXSize = convertUnitSize(mapIndex, toBuild.getXSize());
        int unitZSize = convertUnitSize(mapIndex, toBuild.getZSize());
        if (!useMatrixCoords) {
            unitXSize = toBuild.getXSize();
            unitZSize = toBuild.getZSize();
        }

        int incrementIndex = 1;
        boolean found = false;

        boolean upInvalid = false;
        boolean downInvalid = false;
        boolean leftInvalid = false;
        boolean rightInvalid = false;

        while (!upInvalid || !downInvalid || !leftInvalid || !rightInvalid) {
            upInvalid = true;
            downInvalid = true;
            leftInvalid = true;
            rightInvalid = true;

            leftStart = Math.max(xStart - incrementIndex, 0);
            rightEnd = Math.min(xStart + incrementIndex, xMax);
            upStart = Math.max(zStart - incrementIndex, 0);
            downEnd = Math.min(zStart + incrementIndex, zMax);

            // partea de sus
            if (zStart - incrementIndex >= 0) {
                upInvalid = false;
                zCoord = upStart;
                for (int index = leftStart; index <= rightEnd; ++index) {
                    xCoord = index;
                    List<AIFloat3> positions = getBuildGroupPositions(xCoord, zCoord, countX, countZ, unitXSize, unitZSize, divSize, useMatrixCoords);
                    if (checkIfPossibleToBuildGroup(toBuild, positions, 0) &&
                            correspondingBMap.get(upStart).get(index) == 0 &&
                            !checkAreaForResource(index, upStart, unitXSize * countX, unitZSize * countZ, mapIndex, useMatrixCoords)) {
                        found = true;
                        return positions;
                    }
                }
            }

            // partea de jos
            if (zStart + incrementIndex < zMax) {
                downInvalid = false;
                zCoord = downEnd;
                for (int index = leftStart; index <= rightEnd; ++index) {
                    xCoord = index;
                    List<AIFloat3> positions = getBuildGroupPositions(xCoord, zCoord, countX, countZ, unitXSize, unitZSize, divSize, useMatrixCoords);
                    if (checkIfPossibleToBuildGroup(toBuild, positions, 0) &&
                            correspondingBMap.get(downEnd).get(index) == 0 &&
                            !checkAreaForResource(index, downEnd, unitXSize * countX, unitZSize * countZ, mapIndex, useMatrixCoords)) {
                        found = true;
                        return positions;
                    }
                }
            }

            // partea din stanga
            if (xStart - incrementIndex >= 0) {
                leftInvalid = false;
                xCoord = leftStart;
                for (int index = upStart + 1; index < downEnd; ++index) {
                    zCoord = index;
                    List<AIFloat3> positions = getBuildGroupPositions(xCoord, zCoord, countX, countZ, unitXSize, unitZSize, divSize, useMatrixCoords);
                    if (checkIfPossibleToBuildGroup(toBuild, positions, 0) &&
                            correspondingBMap.get(index).get(leftStart) == 0 &&
                            !checkAreaForResource(leftStart, index, unitXSize * countX, unitZSize * countZ, mapIndex, useMatrixCoords)) {
                        found = true;
                        return positions;
                    }
                }
            }

            // partea din dreapta
            if (xStart + incrementIndex < xMax) {
                rightInvalid = false;
                xCoord = rightEnd;
                for (int index = upStart + 1; index < downEnd; ++index) {
                    zCoord = index;
                    List<AIFloat3> positions = getBuildGroupPositions(xCoord, zCoord, countX, countZ, unitXSize, unitZSize, divSize, useMatrixCoords);
                    if (checkIfPossibleToBuildGroup(toBuild, positions, 0) &&
                            correspondingBMap.get(index).get(rightEnd) == 0 &&
                            !checkAreaForResource(rightEnd, index, unitXSize * countX, unitZSize * countZ, mapIndex, useMatrixCoords)) {
                        found = true;
                        return positions;
                    }
                }
            }

            ++incrementIndex;
        }

        return null;
    }

    public List<Integer> getXZDistances(AIFloat3 position, Float maxSlope) {
        int divSize = losIndex * 8;
        int mapIndex = getMapIndexFromDivSize(divSize);

        List<List<Float>> correspondingHMap = getCorrespondingHMap(mapIndex);
        List<List<Float>> correspondingSMap = getCorrespondingSMap(mapIndex);

        int xRightDist = 0;
        int xLeftDist = 0;
        int zUpDist = 0;
        int zDownDist = 0;

        int xCoord = (int) (position.x / divSize);
        int zCoord = (int) (position.z / divSize);

        int currentValue;

        // Sus
        currentValue = zCoord - 1;
        while (currentValue >= 0 &&
                correspondingHMap.get(currentValue).get(xCoord) > 0 &&
                correspondingSMap.get(currentValue).get(xCoord) < maxSlope) {
            ++zUpDist;
            --currentValue;
        }

        // Jos
        currentValue = zCoord + 1;
        while (currentValue < losMap.size() &&
                correspondingHMap.get(currentValue).get(xCoord) > 0 &&
                correspondingSMap.get(currentValue).get(xCoord) < maxSlope) {
            ++zDownDist;
            ++currentValue;
        }

        // Stanga
        currentValue = xCoord - 1;
        while (currentValue >= 0 &&
                correspondingHMap.get(zCoord).get(currentValue) > 0 &&
                correspondingSMap.get(zCoord).get(currentValue) < maxSlope) {
            ++xLeftDist;
            --currentValue;
        }

        // Dreapta
        currentValue = xCoord + 1;
        while (currentValue < losMap.get(0).size() &&
                correspondingHMap.get(zCoord).get(currentValue) > 0 &&
                correspondingSMap.get(zCoord).get(currentValue) < maxSlope) {
            ++xRightDist;
            ++currentValue;
        }

        return Arrays.asList(zUpDist, zDownDist, xLeftDist, xRightDist);
    }

    public AIFloat3 getNextLinePossiblePosition(AIFloat3 currentPosition, String direction, int movementUnit, float maxSlope, int mapIndex) {
        int divSize = getDivSize(mapIndex);
        AIInt2 currentMatrixPos = new AIInt2((int) (currentPosition.x / divSize), (int) (currentPosition.z / divSize));

        List<List<Float>> correspondingBMap = getCorrespondingBMap(mapIndex);
        List<List<Float>> correspondingHMap = getCorrespondingHMap(mapIndex);
        List<List<Float>> correspondingSMap = getCorrespondingSMap(mapIndex);

        int currentStep = 0;
        if (direction.equals("left")) {

            while (currentStep < movementUnit &&
                    currentMatrixPos.x - currentStep >= 0 &&
                    correspondingBMap.get(currentMatrixPos.z).get(currentMatrixPos.x - currentStep) == 0 &&
                    correspondingHMap.get(currentMatrixPos.z).get(currentMatrixPos.x - currentStep) > 0 &&
                    correspondingSMap.get(currentMatrixPos.z).get(currentMatrixPos.x - currentStep) < maxSlope) {
                currentStep++;
            }
            currentStep--;
            return new AIFloat3((currentMatrixPos.x - currentStep) * divSize, 0, currentMatrixPos.z * divSize);
        } else if (direction.equals("right")) {
            while (currentStep < movementUnit &&
                    currentMatrixPos.x + currentStep < correspondingBMap.get(0).size() &&
                    correspondingBMap.get(currentMatrixPos.z).get(currentMatrixPos.x + currentStep) == 0 &&
                    correspondingHMap.get(currentMatrixPos.z).get(currentMatrixPos.x + currentStep) > 0 &&
                    correspondingSMap.get(currentMatrixPos.z).get(currentMatrixPos.x + currentStep) < maxSlope) {

                currentStep++;
            }
            currentStep--;
            return new AIFloat3((currentMatrixPos.x + currentStep) * divSize, 0, currentMatrixPos.z * divSize);

        } else if (direction.equals("up")) {
            while (currentStep < movementUnit &&
                    currentMatrixPos.z - currentStep >= 0 &&
                    correspondingBMap.get(currentMatrixPos.z - currentStep).get(currentMatrixPos.x) == 0 &&
                    correspondingHMap.get(currentMatrixPos.z - currentStep).get(currentMatrixPos.x) > 0 &&
                    correspondingSMap.get(currentMatrixPos.z - currentStep).get(currentMatrixPos.x) < maxSlope) {

                currentStep++;
            }
            currentStep--;
            return new AIFloat3(currentMatrixPos.x * divSize, 0, (currentMatrixPos.z - currentStep) * divSize);

        } else if (direction.equals("down")) {
            while (currentStep < movementUnit &&
                    currentMatrixPos.z + currentStep < correspondingBMap.size() &&
                    correspondingBMap.get(currentMatrixPos.z + currentStep).get(currentMatrixPos.x) == 0 &&
                    correspondingHMap.get(currentMatrixPos.z + currentStep).get(currentMatrixPos.x) > 0 &&
                    correspondingSMap.get(currentMatrixPos.z + currentStep).get(currentMatrixPos.x) < maxSlope) {

                currentStep++;
            }
            currentStep--;
            return new AIFloat3(currentMatrixPos.x * divSize, 0, (currentMatrixPos.z + currentStep) * divSize);
        }

        return currentPosition;
    }

    public AIFloat3 randomNotExploredPosition() {

        int divSize = losIndex * 8;

        if (notExploredMap.get(0).size() == 0) {
            logger.log("CLEAR EXPLORATIONS ");
            clearExplorationMap();
        }

        AIInt2 randomPosition = notExploredMap.get(0).get(new Random().nextInt(notExploredMap.get(0).size()));
        AIFloat3 currentFloatPos = new AIFloat3(randomPosition.x * divSize, 0f, randomPosition.z * divSize);
        notExploredMap.get(0).remove(randomPosition);
        explorationMap.get(randomPosition.z).set(randomPosition.x, 1);

        return currentFloatPos;
    }

    public static float calculateDistance(AIFloat3 a, AIFloat3 b) {
        float xDistance = a.x - b.x;
        float zDistance = a.z - b.z;
        float totalDistanceSquared = xDistance * xDistance + zDistance * zDistance;
        return (float) Math.sqrt(totalDistanceSquared);
    }

    public AIFloat3 closestMetalSpotForAir(AIFloat3 startSearchPos, UnitDef toBuild) {

        AIFloat3 closestSpot = null;
        float minDistance = 0;
        for (AIFloat3 metalspot : availableMetalSpots) {

            if (this.engineMap.isPossibleToBuildAt(toBuild, metalspot, 0)) {
                float currentDistance = calculateDistance(metalspot, startSearchPos);
                if (closestSpot == null) {
                    closestSpot = metalspot;
                    minDistance = currentDistance;
                } else if (currentDistance < minDistance) {
                    closestSpot = metalspot;
                    minDistance = currentDistance;
                }
            }
        }

        return closestSpot;
    }

    public AIFloat3 closestMetalSpotForLand(AIFloat3 startSearchPos, Unit builder, UnitDef toBuild) {

        AIFloat3 closestSpot = null;
        float minDistance = 0;
        for (AIFloat3 metalspot : availableMetalSpots) {

            if (this.engineMap.isPossibleToBuildAt(toBuild, metalspot, 0) &&
            constructPath(startSearchPos, metalspot, builder.getDef().getMoveData().getMaxSlope(), builder.getDef().getXSize()) != null) {
                float currentDistance = calculateDistance(metalspot, startSearchPos);
                if (closestSpot == null) {
                    closestSpot = metalspot;
                    minDistance = currentDistance;
                } else if (currentDistance < minDistance) {
                    closestSpot = metalspot;
                    minDistance = currentDistance;
                }
            }
        }

        return closestSpot;
    }

}