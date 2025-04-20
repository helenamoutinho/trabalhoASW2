package rsa;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rsa.match.Location;
import rsa.ride.Ride;
import rsa.user.User;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerTest extends rsa.TestData {
	static Manager manager;

	Location from = new Location(X1, Y1);
	Location to = new Location(X2, Y2);
	Location other = new Location(X3, Y3);

	@BeforeAll
	public static void setUpClass() throws RideSharingAppException {
		manager = Manager.getInstance();
		manager.reset();
	}

	@BeforeEach
	public void setUp() throws Exception {
		manager.reset();
	}

	@Test
	public void testRegister() throws RideSharingAppException {
		User user = manager.register(NICK, NAME);
		assertEquals(NICK, user.getNick());
		assertEquals(NAME, user.getName());

		// Test duplicate nick
		assertThrows(RideSharingAppException.class, () -> {
			manager.register(NICK, NAME);
		});
	}

	@Test
	public void testAuthenticate() throws RideSharingAppException {
		User user = manager.register(NICK, NAME);
		String key = user.getKey();

		assertTrue(manager.authenticate(NICK, key));
		assertFalse(manager.authenticate(NICK, "wrong-key"));
		assertFalse(manager.authenticate("invalid", key));
	}

	@Test
	public void testPreferredMatch() throws RideSharingAppException {
		User user = manager.register("testuser", "Test User");
		assertNotNull(user.getPreferredMatch());
	}

	@Test
	public void testRidesDontMatchBothDrivers() throws RideSharingAppException {
		User u1 = manager.register("driver1", "Driver 1");
		User u2 = manager.register("driver2", "Driver 2");
		Ride r1 = manager.addRide(u1, from, to, PLATE, COST);
		Ride r2 = manager.addRide(u2, from, to, "XY-12-ZZ", COST);

		assertFalse(r1.isMatched());
		assertFalse(r2.isMatched());
	}

	@Test
	public void testRidesDontMatchBothPassengers() throws RideSharingAppException {
		User u1 = manager.register("pass1", "Passenger 1");
		User u2 = manager.register("pass2", "Passenger 2");
		Ride r1 = manager.addRide(u1, from, to, null, 0);
		Ride r2 = manager.addRide(u2, from, to, null, 0);

		assertFalse(r1.isMatched());
		assertFalse(r2.isMatched());
	}

	@Test
	public void testRidesDontMatchDifferentDestination() throws RideSharingAppException {
		User driver = manager.register("driver", "Driver");
		User passenger = manager.register("passenger", "Passenger");
		Ride r1 = manager.addRide(driver, from, to, PLATE, COST);
		Ride r2 = manager.addRide(passenger, from, other, null, 0);

		assertFalse(r1.isMatched());
		assertFalse(r2.isMatched());
	}

	@Test
	public void testRidesDontMatchWhenInDifferentPositions() throws RideSharingAppException {
		User driver = manager.register("driver", "Driver");
		User passenger = manager.register("passenger", "Passenger");
		Ride r1 = manager.addRide(driver, from, to, PLATE, COST);
		Ride r2 = manager.addRide(passenger, from, to, null, 0);
		manager.updateRide(r2, other);

		assertFalse(r1.isMatched());
		assertFalse(r2.isMatched());
	}

	@Test
	public void testSimpleMatch() throws RideSharingAppException {
		User driver = manager.register("driver", "Driver");
		User passenger = manager.register("passenger", "Passenger");
		Ride r1 = manager.addRide(driver, from, to, PLATE, COST);
		Ride r2 = manager.addRide(passenger, from, to, null, 0);

		assertTrue(r1.isMatched());
		assertTrue(r2.isMatched());
		assertSame(r1.getMatch(), r2.getMatch());
	}

	// Os testes DoubleDriverMatch* são mais complexos e requerem setPreferredMatch e stars
	// Podemos implementá-los se quiseres, diz só
}
