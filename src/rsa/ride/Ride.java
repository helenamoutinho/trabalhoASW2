package rsa.ride;

import rsa.RideSharingAppException;
import rsa.match.Location;
import rsa.match.RideMatch;
import rsa.user.User;
import rsa.match.PreferredMatch;
import rsa.shared.HasPoint;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Representa uma boleia (como condutor ou passageiro).
 * Implementa HasPoint para suportar geolocalização via QuadTrees.
 */
public class Ride implements HasPoint, RideMatchSorter {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    private long id;
    private User user;
    private Location from;
    private Location to;
    private Location current;
    private float cost;
    private String plate; // se for null, é passageiro
    private RideMatch match;

    public Ride(User user, Location from, Location to, String plate, float cost) throws RideSharingAppException {
        if (user == null || from == null || to == null) {
            throw new RideSharingAppException("Invalid parameters for the ride.");
        }
        this.id = idGenerator.getAndIncrement();
        this.user = user;
        this.from = from;
        this.to = to;
        this.current = from;
        this.plate = plate;
        this.cost = cost;
    }

    public long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isDriver() { return plate != null; }
    public boolean isPassenger() { return !isDriver(); }

    public Location getFrom() { return from; }
    public void setFrom(Location from) { this.from = from; }

    public Location getTo() { return to; }
    public void setTo(Location to) { this.to = to; }

    public Location getCurrent() { return current; }
    public void setCurrent(Location current) { this.current = current; }

    // Métodos exigidos por HasPoint
    @Override
    public double x() { return current.x(); }

    @Override
    public double y() { return current.y(); }

    public float getCost() { return cost; }
    public void setCost(float cost) { this.cost = cost; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public RideMatch getMatch() { return match; }
    public void setMatch(RideMatch match) { this.match = match; }

    public boolean isMatched() { return match != null; }

    public RideRole getRideRole() {
        return isDriver() ? RideRole.DRIVER : RideRole.PASSENGER;
    }

    /**
     * Devolve um comparador de RideMatch com base na preferência do utilizador.
     * Pode ordenar por melhor avaliação, preço mais barato ou maior proximidade.
     */
    @Override
    public Comparator<RideMatch> getComparator() {
        PreferredMatch preference = user.getPreferredMatch();

        return switch (preference) {
            case BETTER -> Comparator.comparingDouble(
                m -> -m.getRide(RideRole.DRIVER).getUser().getAverage(RideRole.DRIVER)
            );

            case CHEAPER -> Comparator.comparingDouble(
                m -> m.getRide(RideRole.DRIVER).getCost()
            );

            case CLOSER -> Comparator.comparingDouble(
                m -> distance(m.getRide(RideRole.DRIVER).getCurrent(), m.getRide(RideRole.DRIVER).getFrom())
            );
        };
    }

    private double distance(Location a, Location b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}

