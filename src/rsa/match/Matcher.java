package rsa.match;

import rsa.quad.PointQuadtree;
import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.ride.RideMatchSorter;
import rsa.RideSharingAppException;


import java.io.Serializable;
import java.util.*;

public class Matcher implements Serializable {
    private static final long serialVersionUID = 1L;

    private PointQuadtree<Ride> rides;
    private Map<Long, RideMatch> matches = new HashMap<>();
    private double radius;

    public Matcher(double width, double height, double margin, double radius) {
        this.rides = new PointQuadtree<>(width, height, margin);
        this.radius = radius;
    }

    public void addRide(Ride ride) {
        rides.insert(ride);
        tryToMatch(ride);
    }

    public RideMatch getMatch(long matchId) {
        return matches.get(matchId);
    }

    public void updateRide(Ride ride, Location current) {
        ride.setCurrent(current);
        tryToMatch(ride);
    }

    private void tryToMatch(Ride ride) {
        if (ride.isMatched()) return;

        RideRole role = ride.getRideRole();
        RideRole opposite = role == RideRole.DRIVER ? RideRole.PASSENGER : RideRole.DRIVER;

        List<Ride> nearby = rides.findNear(ride.getCurrent(), radius);

        List<Ride> candidates = new ArrayList<>();
        for (Ride r : nearby) {
            if (!r.isMatched() && r.getRideRole() == opposite &&
                    closeEnough(ride.getTo(), r.getTo())) {
                candidates.add(r);
            }
        }

        if (candidates.isEmpty()) return;

        Comparator<RideMatch> comparator = ((RideMatchSorter) ride).getComparator();
        List<RideMatch> matchOptions = new ArrayList<>();

        for (Ride other : candidates) {
            try {
                matchOptions.add(new RideMatch(ride, other));
            } catch (RideSharingAppException e) {
                throw new RuntimeException("Erro ao tentar emparelhar boleias", e);
            }
        }

        matchOptions.sort(comparator);

        RideMatch best = matchOptions.get(0);
        best.getRide(RideRole.DRIVER).setMatch(best);
        best.getRide(RideRole.PASSENGER).setMatch(best);
        matches.put(best.getId(), best);
    }


    private boolean closeEnough(Location a, Location b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double d2 = dx * dx + dy * dy;
        return d2 <= radius * radius;
    }
}
