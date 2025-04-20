package rsa.quad;

import rsa.shared.HasPoint;
import rsa.match.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LeafTrie<T extends HasPoint> extends Trie<T> {
    private final List<T> points = new ArrayList<>();

    public LeafTrie(double minX, double minY, double maxX, double maxY) {
        super(minX, minY, maxX, maxY);
    }

    @Override
    public Trie<T> insert(T point) {
        if (points.size() < Trie.getCapacity()) {
            points.add(point);
            return this;
        }

        NodeTrie<T> node = new NodeTrie<>(minX, minY, maxX, maxY);
        for (T p : points) {
            node.insert(p);
        }
        node.insert(point);
        return node;
    }

    @Override
    public Trie<T> insertReplace(T point) {
        delete(point);
        return insert(point);
    }

    @Override
    public boolean remove(T point) {
        return points.removeIf(p -> p.x() == point.x() && p.y() == point.y());
    }

    @Override
    public void delete(T point) {
        remove(point);
    }

    @Override
    public T find(T point) {
        for (T p : points) {
            if (p.x() == point.x() && p.y() == point.y()) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void collect(List<T> result) {
        result.addAll(points);
    }

    @Override
    public void collectNear(Location center, double radius, List<T> result) {
        double x = center.x();
        double y = center.y();
        double r2 = radius * radius;

        for (T p : points) {
            double dx = p.x() - x;
            double dy = p.y() - y;
            if (dx * dx + dy * dy <= r2) {
                result.add(p);
            }
        }
    }

    @Override
    public void collectNear(double x, double y, double radius, Set<T> result) {
        double r2 = radius * radius;

        for (T p : points) {
            double dx = p.x() - x;
            double dy = p.y() - y;
            if (dx * dx + dy * dy <= r2) {
                result.add(p);
            }
        }
    }

    @Override
    public void collectAll(Set<T> result) {
        result.addAll(points);
    }

    @Override
    public String toString() {
        return "LeafTrie with " + points.size() + " points";
    }
}
