package map.pathfinding;

import map.AIInt2;

import java.util.Objects;

public class PQEntry implements Comparable{

    public AIInt2 coords = new AIInt2();

    public int cost;

    public AIInt2 prevCoords = new AIInt2();

    public PQEntry(AIInt2 coords, int cost, AIInt2 prevCoords) {
        this.coords = coords;
        this.cost = cost;
        this.prevCoords = prevCoords;
    }

    public PQEntry(int x, int z, int cost, int prevX, int prevZ) {
        this.coords.x = x;
        this.coords.z = z;
        this.cost = cost;
        this.prevCoords.x = prevX;
        this.prevCoords.z = prevZ;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PQEntry pqEntry = (PQEntry) o;
        return Objects.equals(coords, pqEntry.coords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords);
    }


    @Override
    public String toString() {
        return "PQEntry{" +
                "coords=" + coords +
                ", cost=" + cost +
                ", prevCoords=" + prevCoords +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return this.cost - ((PQEntry)o).cost;
    }
}
