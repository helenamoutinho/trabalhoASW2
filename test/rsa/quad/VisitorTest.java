package rsa.quad;

import org.junit.jupiter.api.Test;
import rsa.match.Location;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {

    static class CountingVisitor implements Visitor<Location> {
        int leafCount = 0;
        int nodeCount = 0;

        @Override
        public void visit(LeafTrie<Location> leaf) {
            leafCount++;
        }

        @Override
        public void visit(NodeTrie<Location> node) {
            nodeCount++;
        }
    }

    @Test
    public void testAcceptVisitor() {
        Trie<Location> trie = new LeafTrie<>(0, 0, 100, 100);

        // Forçar transformação em NodeTrie
        for (int i = 0; i < 10; i++) {
            trie = trie.insert(new Location("loc" + i, i, i));
        }

        CountingVisitor visitor = new CountingVisitor();
        trie.accept(visitor);

        assertEquals(1, visitor.nodeCount); // Esperamos pelo menos 1 nó
        assertTrue(visitor.leafCount > 0);  // Esperamos algumas folhas
    }
}
