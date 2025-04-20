package rsa.quad;

import rsa.shared.HasPoint;
import rsa.match.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Representa uma quadtree que guarda objetos que têm um ponto (x,y),
 * isto é, que implementam HasPoint.
 */
public class PointQuadtree<T extends HasPoint> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private Trie<T> root;
    private final double minX, minY, maxX, maxY;

    public PointQuadtree(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        root = new LeafTrie<>(minX, minY, maxX, maxY);
    }

    public PointQuadtree(double width, double height) {
        this(0, 0, width, height);
    }

    public PointQuadtree(double width, double height, double margin) {
        this(-margin, -margin, width + margin, height + margin);
    }

    public void insert(T point) {
        if (!inside(point))
            throw new PointOutOfBoundException(point);

        root = root.insert(point);
    }

    public void insertReplace(T point) {
        if (!inside(point))
            throw new PointOutOfBoundException(point);

        root = root.insertReplace(point);
    }

    public boolean remove(T point) {
        return root.remove(point);
    }

    public void delete(T point) {
        root.delete(point);
    }

    public T find(T point) {
        return root.find(point);
    }

    public List<T> findNear(Location center, double radius) {
        List<T> result = new ArrayList<>();
        if (root.overlaps(center, radius))
            root.collectNear(center, radius, result);
        return result;
    }

    public Set<T> findNear(double x, double y, double radius) {
        Set<T> result = new HashSet<>();
        if (root.overlaps(new Location(x, y), radius))
            root.collectNear(x, y, radius, result);
        return result;
    }

    public boolean inside(HasPoint point) {
        return point.x() >= minX && point.x() <= maxX &&
                point.y() >= minY && point.y() <= maxY;
    }

    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        root.collect(result);
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return getAll().iterator();
    }

    public int size() {
        return getAll().size();
    }
}
