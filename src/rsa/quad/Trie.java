package rsa.quad;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import rsa.match.Location;
import rsa.shared.HasPoint;

public abstract class Trie<T extends HasPoint> implements Serializable {
    private static final long serialVersionUID = 1L;

    protected double minX, minY, maxX, maxY;
    private static int capacity = 4;

    public Trie(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capacity) {
        Trie.capacity = capacity;
    }

    public boolean overlaps(Location center, double radius) {
        double closestX = Math.max(minX, Math.min(center.x(), maxX));
        double closestY = Math.max(minY, Math.min(center.y(), maxY));
        double dx = closestX - center.x();
        double dy = closestY - center.y();
        return dx * dx + dy * dy <= radius * radius;
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }





    public abstract Trie<T> insert(T point);
    public abstract Trie<T> insertReplace(T point);
    public abstract boolean remove(T point);
    public abstract void delete(T point);
    public abstract T find(T point);
    public abstract void collect(List<T> points);
    public abstract void collectNear(Location center, double radius, List<T> points);
    public abstract void collectNear(double x, double y, double radius, Set<T> points);
    public abstract void collectAll(Set<T> points);
}
