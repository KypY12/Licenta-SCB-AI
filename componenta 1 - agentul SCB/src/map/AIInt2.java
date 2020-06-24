package map;

import java.util.Objects;

public class AIInt2 {
    public int x;
    public int z;

    public AIInt2(){
        this.x = 0;
        this.z = 0;
    }

    public AIInt2(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return "AIInt2{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIInt2 aiInt2 = (AIInt2) o;
        return Float.compare(aiInt2.x, x) == 0 &&
                Float.compare(aiInt2.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
