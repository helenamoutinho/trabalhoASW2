package rsa.match;

import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.ride.RideMatchSorter;
import rsa.user.User;
import rsa.user.UserStars;
import rsa.quad.PointQuadtree;

import rsa.RideSharingAppException;

import java.io.Serializable;
import java.util.*;

public class Matcher implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Location topLeft;
    private static Location bottomRight;
    private static double radius;

    private final List<Ride> rides = new ArrayList<>();
    private final Map<Long, RideMatch> matches = new TreeMap<>();
    private final PointQuadtree<Ride> rideTree = new PointQuadtree<>(0, 0, 1000, 1000); // ajustável

    public static void setTopLeft(Location loc) {
        topLeft = loc;
    }

    public static void setBottomRight(Location loc) {
        bottomRight = loc;
    }

    public static void setRadius(double r) {
        radius = r;
    }

    public static Location getTopLeft() {
        return topLeft;
    }

    public static Location getBottomRight() {
        return bottomRight;
    }

    public static double getRadius() {
        return radius;
    }

    public long addRide(User user, Location from, Location to, String plate, float cost) throws RideSharingAppException {
        Ride ride = new Ride(user, from, to, plate, cost);
        rides.add(ride);
        rideTree.insert(ride);
        tryToMatch(ride);
        return ride.getId();
    }

    public SortedSet<RideMatch> updateRide(long rideId, Location current) {
        Ride ride = findRideById(rideId);
        if (ride == null) return new TreeSet<>();
        ride.setCurrent(current);
        return tryToMatch(ride);
    }

    public void acceptMatch(long rideId, long matchId) {
        Ride ride = findRideById(rideId);
        if (ride == null) return;
        RideMatch match = matches.get(matchId);
        if (match == null) return;
        ride.setMatch(match);
    }

    public void concludeRide(long rideId, UserStars stars) {
        Ride ride = findRideById(rideId);
        if (ride == null || ride.getMatch() == null) return;

        RideMatch match = ride.getMatch();
        RideRole role = ride.getRideRole();

        ride.getUser().addStars(stars, role);
        ride.setMatch(null);
        matches.remove(match.getId());
    }


    private Ride findRideById(long id) {
        for (Ride ride : rides) {
            if (ride.getId() == id) return ride;
        }
        return null;
    }

    private SortedSet<RideMatch> tryToMatch(Ride ride) {
        RideRole role = ride.getRideRole();
        RideRole opposite = (role == RideRole.DRIVER) ? RideRole.PASSENGER : RideRole.DRIVER;

        List<Ride> nearby = new ArrayList<>();
        rideTree.collectNear(ride.getCurrent(), radius, nearby);

        SortedSet<RideMatch> results = new TreeSet<>(((RideMatchSorter) ride).getComparator());

        for (Ride other : nearby) {
            if (other.getRideRole() == opposite &&
                    other.getMatch() == null &&
                    ride.getTo().x() == other.getTo().x() &&
                    ride.getTo().y() == other.getTo().y()) {
                try {
                    RideMatch match = new RideMatch(ride, other);
                    results.add(match);
                    matches.put(match.getId(), match);
                } catch (RideSharingAppException ignored) {}
            }
        }

        return results;
    }
}

