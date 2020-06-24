package map.pathfinding;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import logger.AILogger;
import map.AIInt2;

import java.util.*;

public class PathCreator {

    private AILogger logger;
    private OOAICallback callback;

    private List<List<Float>> currentSlopeMap;
    private List<List<Float>> currentHeightMap;
    private List<List<Float>> currentBuildingsMap;

    private int divSize = 1;

    private Queue<PQEntry> pQueue = new PriorityQueue<>();
    private Map<AIInt2, AIInt2> visited = new HashMap<>();


    public PathCreator(AILogger logger) {
        this.logger = logger;
        this.callback = logger.getCallback();

    }

    public void setCurrentMaps(List<List<Float>> slopeMap, List<List<Float>> heightMap, List<List<Float>> buildingsMap, int size) {
        this.currentSlopeMap = slopeMap;
        this.currentHeightMap = heightMap;
        this.currentBuildingsMap = buildingsMap;


        if (size == 512) {
            divSize = 16;
        } else if (size == 256) {
            divSize = 32;
        } else if (size == 128) {
            divSize = 64;
        } else if (size == 64) {
            divSize = 128;
        }

    }


    private int calculateMatrixDistance(AIInt2 first, AIInt2 second) {
        // Distanta este de fapt suma diferentelor dintre coordonate
        return 2 * Math.abs(first.x - second.x) + 2 * Math.abs(first.z - second.z);
    }


    private List<PQEntry> getNeighboursAsPQE(AIInt2 currentCoords, float maxSlope, int currentCost, AIInt2 finishCoords) {
        try {
            int x = currentCoords.x;
            int z = currentCoords.z;

            if (x >= currentSlopeMap.get(0).size() || x < 0 || z > currentSlopeMap.size() || z < 0) {
                throw new Exception("Invalid coordinates at getNeighbours function!");
            }

            List<PQEntry> neighbours = new LinkedList<>();
            boolean left = true, right = true, up = true, down = true;

            if (x == 0) {
                left = false;
            } else if (x == currentSlopeMap.get(0).size() - 1) {
                right = false;
            }

            if (z == 0) {
                up = false;
            } else if (z == currentSlopeMap.size() - 1) {
                down = false;
            }

            float currentPosValue = currentSlopeMap.get(z).get(x);
            float waterLevel = 0; // Nivelul marii este 0

            if (left
                    && currentHeightMap.get(z).get(x - 1) > waterLevel
                    && currentSlopeMap.get(z).get(x - 1) < maxSlope
                    && currentBuildingsMap.get(z).get(x - 1) == 0f
            ) {

                int aStarDistance = calculateMatrixDistance(new AIInt2(x - 1, z), finishCoords);
                neighbours.add(new PQEntry(x - 1, z, currentCost + 1 + aStarDistance, x, z));
            }

            if (right
                    && currentHeightMap.get(z).get(x + 1) > waterLevel
                    && currentSlopeMap.get(z).get(x + 1) < maxSlope
                    && currentBuildingsMap.get(z).get(x + 1) == 0f
            ) {
                int aStarDistance = calculateMatrixDistance(new AIInt2(x + 1, z), finishCoords);
                neighbours.add(new PQEntry(x + 1, z, currentCost + 1 + aStarDistance, x, z));
            }

            if (up
                    && currentHeightMap.get(z - 1).get(x) > waterLevel
                    && currentSlopeMap.get(z - 1).get(x) < maxSlope
                    && currentBuildingsMap.get(z - 1).get(x) == 0f
            ) {
                int aStarDistance = calculateMatrixDistance(new AIInt2(x, z - 1), finishCoords);
                neighbours.add(new PQEntry(x, z - 1, currentCost + 1 + aStarDistance, x, z));
            }

            if (down
                    && currentHeightMap.get(z + 1).get(x) > waterLevel
                    && currentSlopeMap.get(z + 1).get(x) < maxSlope
                    && currentBuildingsMap.get(z + 1).get(x) == 0f
            ) {
                int aStarDistance = calculateMatrixDistance(new AIInt2(x, z + 1), finishCoords);
                neighbours.add(new PQEntry(x, z + 1, currentCost + 1 + aStarDistance, x, z));
            }

            return neighbours;

        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }


    private List<AIInt2> backConstructPath(AIInt2 startCoords, AIInt2 finishCoords) {
        try {
            LinkedList<AIInt2> path = new LinkedList<>();

            AIInt2 current = finishCoords;
            while (!current.equals(startCoords)) {
                path.addFirst(current);
                current = visited.get(current);
            }
            path.addFirst(current);

            return path;
        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }


    private List<AIFloat3> convertMatrixToWorldCoords(List<AIInt2> matrixCoords) {
        try {
            LinkedList<AIFloat3> worldCoords = new LinkedList<>();

            Iterator<AIInt2> it = matrixCoords.iterator();
            while (it.hasNext()) {
                AIInt2 current = it.next();
                worldCoords.add(new AIFloat3(current.x * divSize + divSize / 2, 0, current.z * divSize + divSize / 2));
            }
            return worldCoords;
        } catch (Exception e) {
            logger.log(e);
        }
        return null;
    }


    public List<AIFloat3> createPath(AIFloat3 start, AIFloat3 finish, float maxSlope) {

        try {
            pQueue.clear();
            visited.clear();

            AIInt2 startCoords = new AIInt2((int) start.x / divSize, (int) start.z / divSize);
            AIInt2 finishCoords = new AIInt2((int) finish.x / divSize, (int) finish.z / divSize);


            PQEntry current = new PQEntry(startCoords, 0, startCoords);
            pQueue.add(current);


            while (pQueue.size() > 0) {
                current = pQueue.poll();
                if (current.coords.equals(finishCoords)) {
                    visited.put(current.coords, current.prevCoords);
                    break;
                }

                List<PQEntry> currentNeighbours = getNeighboursAsPQE(current.coords, maxSlope, current.cost, finishCoords);

                for (PQEntry pqe : currentNeighbours) {
                    if (visited.containsKey(pqe.coords)) {
                        continue;
                    }

                    boolean found = false;
                    boolean isLower = false;

                    Iterator<PQEntry> it = pQueue.iterator();
                    while (it.hasNext()) {
                        PQEntry currentPqe = it.next();
                        if (currentPqe.equals(pqe)) {
                            found = true;
                            if (pqe.cost < currentPqe.cost) {
                                // Daca in pQueue costul este mai mare, inlocuim cu costul vecinului gasit in iteratia curenta (pqe)
                                it.remove();
                                isLower = true;
                            }
                            break;
                        }
                    }

                    // Daca exista deja in pQueue si nu este mai mic, nu il adauga, altfel il adauga:
                    if (!found || isLower) {
                        pQueue.add(pqe);
                    }

                }
                visited.put(current.coords, current.prevCoords);
            }

            if (current.coords.equals(finishCoords)) {
                // Am gasit drum
                List<AIInt2> matrixPath = backConstructPath(startCoords, finishCoords);
                List<AIFloat3> worldPath = convertMatrixToWorldCoords(matrixPath);
                return worldPath;

            } else if (pQueue.size() == 0) {
                // Nu am gasit drum
                return null;
            }

        } catch (Exception e) {
            logger.log(e);
        }

        return null;
    }


}