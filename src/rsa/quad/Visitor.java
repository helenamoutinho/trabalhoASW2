package rsa.quad;

public interface Visitor<T extends rsa.shared.HasPoint> {
    void visit(LeafTrie<T> leaf);
    void visit(NodeTrie<T> node);
}

