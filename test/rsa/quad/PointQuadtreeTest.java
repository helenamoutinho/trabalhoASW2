package rsa.quad;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

import rsa.match.Location;


/**
 * Test on a PointQuadtree, the facade of quad package.
 * It supports {@code insert()}, {@code find()}
 * {@code insertReplace()} and {@code delete()} methods,
 * implemented through delegation on the root trie.
 *
 * @author Jos&eacute; Paulo Leal {@code zp@dcc.fc.up.pt}
 */
public class PointQuadtreeTest {


	private static final String DATA = "rsa/quad/portuguese-locations.txt";
	static double RADIUS = 0.0001D; // 11.13 m

	private static final int CAPACITY = 10;

	private static final int BOTTOM_RIGHT_Y = 10;
	private static final int BOTTOM_RIGHT_X = 20;
	private static final int TOP_LEFT_Y = 20;
	private static final int TOP_LEFT_X = 10;

	private static final int CENTER_X = (TOP_LEFT_X + BOTTOM_RIGHT_X)/2;
	private static final int CENTER_Y = (TOP_LEFT_Y + BOTTOM_RIGHT_Y)/2;

	private static final int TOO_LARGE_COORDINATE = 30;
	private static final int TOO_SMALL_COORDINATE =  0;

	private static final int SMALL_RADIUS = 1;

	static Map<String,Location> locations;

	@BeforeAll
	static public void setUp() {
		Trie.setCapacity(CAPACITY);
		locations = load();
	}

	PointQuadtree<Location> quad;

	Location porto;

	@BeforeEach
	public void prepare() {
		quad = new PointQuadtree<>(TOP_LEFT_X,TOP_LEFT_Y,BOTTOM_RIGHT_X,BOTTOM_RIGHT_Y);

		porto = locations.get("Porto");
	}

	/**
	 * Points outside the boundaries should raise an exception
	 */
	@Test
	public void testBoundariesOut() {

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insert(new Location("too left and too low",
								TOO_SMALL_COORDINATE, TOO_SMALL_COORDINATE)));

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insert(new Location("too high",
								TOO_LARGE_COORDINATE, CENTER_Y)));

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insert(new Location("too right",
								CENTER_X, TOO_LARGE_COORDINATE)));

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insert(new Location("too high and too right",
								TOO_LARGE_COORDINATE, TOO_LARGE_COORDINATE)));
	}

	/**
	 * Points on the boundaries should not raise exceptions
	 */
	@Test
	public void testBoundariesIn() {
		quad.insert(new Location("center", CENTER_X, CENTER_Y));
		quad.insert(new Location("top left", TOP_LEFT_X, TOP_LEFT_Y));
		quad.insert(new Location("top right", BOTTOM_RIGHT_X, TOP_LEFT_Y));
		quad.insert(new Location("bottom right", TOP_LEFT_X, BOTTOM_RIGHT_Y));
		quad.insert(new Location("bottom left", BOTTOM_RIGHT_X, BOTTOM_RIGHT_Y));
	}


	/**
	 * Check if point is absent before insertion
	 */
	@Test
	public void testFindAbsent() {
		quad = makeQuadTreeFor(porto);

		assertNull(quad.find(porto));
	}

	/**
	 * Check if point is present after insertion
	 */
	@Test
	public void testFindPresent() {
		quad = makeQuadTreeFor(porto);

		quad.insert(porto);

		assertEquals(porto,quad.find(porto));
	}


	/**
	 * Check if point is absent after deletion
	 */
	@Test
	public void testDelete() {
		quad = makeQuadTreeFor(porto);

		quad.insert(porto);

		quad.delete(porto);
		assertNull(quad.find(porto));
	}


	/**
	 * Check if a point replaces another in the  same location
	 */
	@Test
	public void testInsertReplace() {

		quad = makeQuadTreeFor(porto);

		quad.insert(porto);

		assertAll(
				() -> {
					Set<Location> near =
							quad.findNear(porto.x(), porto.y(), SMALL_RADIUS);

					assertAll(
							() -> assertEquals(1,near.size()),
							() -> assertEquals("Porto",near.iterator()
									.next().getName()));
				},
				() -> {
					String otherName = "Oporto";
					Location other = new Location(otherName,
							porto.getLatitude(), porto.getLongitude());

					quad.insertReplace(other);

					Set<Location> near =
							quad.findNear(porto.x(), porto.y(), 1);

					assertAll(
							() -> assertEquals(1,near.size()),
							() -> assertEquals(otherName,near.iterator()
									.next().getName()));
				});
	}


	/**
	 * Points outside the boundaries should raise an exception when replacing
	 */
	@Test
	public void testBoundariesOutInReplace() {

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insertReplace(new Location("too left and too low",
								TOO_SMALL_COORDINATE, TOO_SMALL_COORDINATE)));

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insertReplace(new Location("too high",
								TOO_LARGE_COORDINATE, CENTER_Y)));

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insertReplace(new Location("too right",
								CENTER_X, TOO_LARGE_COORDINATE)));

		assertThrows(PointOutOfBoundException.class,
				() ->
						quad.insertReplace(new Location("too high and too right",
								TOO_LARGE_COORDINATE, TOO_LARGE_COORDINATE)));
	}



	/**
	 * Find near points on a QuadTree having a single leaf
	 */
	@Test
	public void testFindNearLeaf() {
		checkAroundCenter(1,5);
	}


	/**
	 * Find near points of a QuadTree containing a single trie with 4 leaves
	 */
	@Test
	public void testFindNearNodes() {
		checkAroundCenter(2,13);
	}

	/**
	 * Find near points on a QuadTree with multiples tries
	 */
	@Test
	public void testFindNearNodes2() {
		checkAroundCenter(3,29);
	}

	/**
	 * Place points in a panel around center and check if expected number is retrieved
	 */
	private void checkAroundCenter(int radius,int expected) {
		for(int x=CENTER_X-radius; x <= CENTER_X+radius; x++)
			for(int y=CENTER_Y-radius; y <= CENTER_Y+radius; y++)
				quad.insert(new Location("",x,y));


		assertEquals(expected,quad.findNear(CENTER_X, CENTER_Y, radius).size());
	}

	/**
	 * Check all points in Portuguese locations 
	 */
	@Test
	public void testFindAllPortugueseLocations() {
		int count=0;

		for(Location location: loadLocations().getAll()) {
			assertNotNull(location);

			count++;
		}

		assertEquals(313,count);
	}

	/**
	 * Test iterable pattern in quad tree
	 */
	@Test
	public void testIterable( ) {
		quad = loadLocations();
		int count=0;

		for(Location location: quad) {
			assertNotNull(location);
			count++;
		}

		assertEquals(313,count);
	}


	/**
	 * Load Portuguese locations file into a quad tree
	 * Retrieve locations near Porto and check them against expected results
	 */
	@Test
	public void testPortugueseLocationsAroundPorto() {
		quad = loadLocations();
		HashSet<Location> near = new HashSet<>();

		near.add(porto);

		assertAll(
				() -> {
					for(String name: new String[] {
							"Vila Nova de Gaia",
							"Gondomar",
							"Maia",
							"Matosinhos"} )
						near.add(locations.get(name));

					assertEquals(near,quad.findNear(
							porto.x(), porto.y(), 0.1));
				},
				() -> {
					for(String name: new String[] {
							"Espinho",
							"Valongo" } )
						near.add(locations.get(name));

					assertEquals(near,quad.findNear(
							porto.x(), porto.y(), 0.2));
				},
				() -> {
					for(String name: new String[] {
							"Santo Tirso",
							"Vila Nova de Famalicão",
							"Vila do Conde",
							"Póvoa de Varzim" } )
						near.add(locations.get(name));

					assertEquals(near,quad.findNear(
							porto.x(), porto.y(), 0.3));
				});
	}

	/**
	 * Some locations overlap (have the same coordinates) and are automatically
	 * removed using find. Set this constant to true to have these locations listed
	 * on the standard output.
	 */
	static final boolean PRINT_OVERLAPPED_LOCATIONS = false;
	private static final int SLACK = 10;

	/**
	 * Test a series of circles centered in base and 
	 * with radius stepping until a limit  
	 * @param base	location 
	 * @param step	increment of radius
	 * @param limit	maximum radius
	 */
	private void testLocationsAround(Location base,double step, double limit) {
		quad = loadLocations();
		HashSet<Location> near = new HashSet<>();

		near.add(base);

		for(double radius = step; radius <= limit; radius += step) {
			addNear(base,near,radius);
			assertEquals(near,quad.findNear(base.x(), base.y(),radius),
					"expected at a distance "+radius+" of "+base);
		}
	}

	/**
	 * Add locations near given base, in those location recorded in  
	 * @param base		center for points
	 * @param near		set of nearby locations 
	 * @param radius    for selecting points
	 */
	void addNear(Location base, HashSet<Location> near,double radius) {

		for(Location location: locations.values()) {
			Location inQuad = quad.find(location);

			if(inQuad == null)
				throw new RuntimeException("Unused location in quad:"+location);
			if(location.getName().equals(inQuad.getName())) {
				// some locations have same coordinates as others and where omitted from the quad
				double distance = Trie.getDistance(
						base.x(), 		base.y(),
						location.x(), 	location.y());
				if(distance <= radius)
					near.add(location);
			}
		}
	}

	/**
	 * Test series of concentric circles around main Portuguese cities,
	 * and those near the corners of the "corners"
	 */
	@Test
	public void testPortugueseLocations() {

		for(String name: new String[] { "Porto", "Lisboa", "Coimbra", "Faro",
				"Valença", "Bragança", "Vila Real de Santo António", "Sagres"}) {
			testLocationsAround(locations.get(name),0.01,5.0);
		}
	}

	/**
	 * Double-checking for radius 0.006 and getting locations in that radius
	 */
	@Test
	public void testAroundPorto006() {
		quad = loadLocations();
		HashSet<Location> near = new HashSet<>();
		Location base = locations.get("Porto");
		double radius = 0.060000000000000005;

		addNear(base,near,radius);

		assertEquals(near,quad.findNear(base.x(), base.y(),radius));
	}

	/**
	 * Make a QuadTree large enough to contain given points
	 */
	private PointQuadtree<Location> makeQuadTreeFor(Location... points) {
		Location first = points[0];

		double westernLongitude = first.getLongitude();
		double easternLongitude = first.getLongitude();
		double northernLatitude = first.getLatitude();
		double southernLatitude = first.getLatitude();

		for(Location point: points) {
			if(point.getLongitude() < westernLongitude)
				westernLongitude = point.getLongitude();
			if(point.getLongitude() > easternLongitude)
				easternLongitude = point.getLongitude();

			if(point.getLatitude() < southernLatitude)
				southernLatitude = point.getLatitude();
			if(point.getLatitude() > northernLatitude)
				northernLatitude = point.getLatitude();
		}

		return new PointQuadtree<>(
				westernLongitude-SLACK, northernLatitude+SLACK,
				easternLongitude+SLACK, southernLatitude-SLACK);
	}

	/**
	 * Load location in a map to a QuadTree
	 * @return quadtree
	 */
	private PointQuadtree<Location> loadLocations() {

		Location first = locations.get(locations.keySet().iterator().next());
		double westernLongitude = first.getLongitude();
		double easternLongitude = first.getLongitude();
		double northernLatitude = first.getLatitude();
		double southernLatitude = first.getLatitude();

		for(String name: locations.keySet()) {
			Location location = locations.get(name);
			if(location.getLongitude() < westernLongitude)
				westernLongitude = location.getLongitude();
			if(location.getLongitude() > easternLongitude)
				easternLongitude = location.getLongitude();

			if(location.getLatitude() < southernLatitude)
				southernLatitude = location.getLatitude();
			if(location.getLatitude() > northernLatitude)
				northernLatitude = location.getLatitude();
		}

		PointQuadtree<Location> quadTree = new PointQuadtree<>(
				westernLongitude, northernLatitude,
				easternLongitude, southernLatitude);

		for(String name: locations.keySet()) {
			Location location = locations.get(name);
			Location other = quadTree.find(location);

			if(other == null)
				quadTree.insert(location);
			else if(PRINT_OVERLAPPED_LOCATIONS)
				System.out.println(String.format("%20s : %20s",location.getName(),other.getName()));

		}

		return quadTree;
	}


	static final Pattern linePattern =
			Pattern.compile("([^\t]+)\t(\\d+)([NS])(\\d+)\\s+(\\d+)([EW])(\\d+)\\s+.*");

	static private Map<String,Location> load() {

		Map<String,Location> locations = new HashMap<>();

		try (InputStream stream = ClassLoader.getSystemResourceAsStream(DATA);
			 Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8)) {

			scanner.nextLine(); // skip header line
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				Matcher matcher = linePattern.matcher(line);

				if(!matcher.matches())
					continue;

				String location = matcher.group(1);
				int latitudeDegrees = Integer.parseInt(matcher.group(2));
				String latitudeHemisphere = matcher.group(3);
				int latitudeMinutes = Integer.parseInt(matcher.group(4));
				int longitudeDegrees = Integer.parseInt(matcher.group(5));
				String longitudeHemisphere = matcher.group(6);
				int longitudeMinutes = Integer.parseInt(matcher.group(7));

				locations.put(location,new Location(location, toDecimal(latitudeDegrees,
						latitudeMinutes, latitudeHemisphere),
						toDecimal(longitudeDegrees, longitudeMinutes,
								longitudeHemisphere)));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return locations;
	}


	private static double toDecimal(int degrees, int minutes, String side) {
		double decimal = degrees + minutes/60F;
		return switch (side) {
			case "S", "W" -> -decimal;
			default -> decimal;
		};
	}
}
