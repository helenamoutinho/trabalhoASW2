package rsa.quad;

import rsa.shared.HasPoint;
import rsa.match.Location;

import java.util.List;
import java.util.Set;

public class NodeTrie<T extends HasPoint> extends Trie<T> {

    private final double midX, midY;
    private Trie<T> nw, ne, sw, se;

    public NodeTrie(double minX, double minY, double maxX, double maxY) {
        super(minX, minY, maxX, maxY);
        this.midX = (minX + maxX) / 2;
        this.midY = (minY + maxY) / 2;

        nw = new LeafTrie<>(minX, midY, midX, maxY);
        ne = new LeafTrie<>(midX, midY, maxX, maxY);
        sw = new LeafTrie<>(minX, minY, midX, midY);
        se = new LeafTrie<>(midX, minY, maxX, midY);
    }

    @Override
    public Trie<T> insert(T point) {
        Trie<T> quadrant = getQuadrant(point);
        Trie<T> newQuadrant = quadrant.insert(point);
        setQuadrant(point, newQuadrant);
        return this;
    }

    @Override
    public Trie<T> insertReplace(T point) {
        Trie<T> quadrant = getQuadrant(point);
        Trie<T> newQuadrant = quadrant.insertReplace(point);
        setQuadrant(point, newQuadrant);
        return this;
    }

    @Override
    public boolean remove(T point) {
        return getQuadrant(point).remove(point);
    }

    @Override
    public void delete(T point) {
        getQuadrant(point).delete(point);
    }

    @Override
    public T find(T point) {
        return getQuadrant(point).find(point);
    }

    @Override
    public void collect(List<T> out) {
        nw.collect(out);
        ne.collect(out);
        sw.collect(out);
        se.collect(out);
    }

    @Override
    public void collectNear(Location center, double radius, List<T> out) {
        if (nw.overlaps(center, radius)) nw.collectNear(center, radius, out);
        if (ne.overlaps(center, radius)) ne.collectNear(center, radius, out);
        if (sw.overlaps(center, radius)) sw.collectNear(center, radius, out);
        if (se.overlaps(center, radius)) se.collectNear(center, radius, out);
    }

    @Override
    public void collectNear(double x, double y, double radius, Set<T> result) {
        Location center = new Location(x, y);
        if (nw.overlaps(center, radius)) nw.collectNear(x, y, radius, result);
        if (ne.overlaps(center, radius)) ne.collectNear(x, y, radius, result);
        if (sw.overlaps(center, radius)) sw.collectNear(x, y, radius, result);
        if (se.overlaps(center, radius)) se.collectNear(x, y, radius, result);
    }

    @Override
    public void collectAll(Set<T> points) {
        nw.collectAll(points);
        ne.collectAll(points);
        sw.collectAll(points);
        se.collectAll(points);
    }

    private Trie<T> getQuadrant(HasPoint point) {
        double x = point.x();
        double y = point.y();

        if (x < midX) {
            if (y < midY) return sw;
            else return nw;
        } else {
            if (y < midY) return se;
            else return ne;
        }
    }

    private void setQuadrant(HasPoint point, Trie<T> quadrant) {
        double x = point.x();
        double y = point.y();

        if (x < midX) {
            if (y < midY) sw = quadrant;
            else nw = quadrant;
        } else {
            if (y < midY) se = quadrant;
            else ne = quadrant;
        }
    }

    @Override
    public void accept(Visitor<T> visitor) {
        visitor.visit(this);
    }
}
