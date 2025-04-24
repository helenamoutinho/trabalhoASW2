package rsa.quad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test on Trie, the abstract tree common to both types of trie of a quadtree.
 * Checks the static method {@code getDistance()} and on the static 
 * getters and setters  of the capacity property.
 * 
 * @author Jos&eacute; Paulo Leal {@code zp@dcc.fc.up.pt}
 */
public class TrieTest {
	
	private final static int CAPACITY = 10;
	

	@Test
	public void testDistance() {
		double x1 = 0.007499999999999982D;
		double y1 = 0.007499999999999982D;	
		
		double x2=0.007499999999999999D;
		double y2=0.007499999999999999D;
		
		assertEquals(0,Trie.getDistance(x1, y1, x2, y2),Location.RADIUS);
		assertTrue(Trie.getDistance(x1, y1, x2, y2)<Location.RADIUS);
	}
	
	/**
	 * Test capacity of a bucket
	 */
	@Test
	public void testCapacity() {
		
		Trie.setCapacity(CAPACITY);
		assertEquals(CAPACITY, Trie.getCapacity());
	}

	@Test
	public void testOverlapsCoordinates() {
		Trie<Location> trie = new LeafTrie<>(0, 0, 10, 10);

		// Dentro do quadrado
		assertTrue(trie.overlaps(5, 5, 1));

		// Fora do quadrado
		assertFalse(trie.overlaps(20, 20, 1));

		// Tocando na borda
		assertTrue(trie.overlaps(10, 10, 1));
	}


}
