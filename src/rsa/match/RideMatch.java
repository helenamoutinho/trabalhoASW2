package rsa.match;

import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.user.User;
import rsa.user.Car;
import rsa.RideSharingAppException;

import java.io.Serializable;

public class RideMatch implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Ride driverRide;
    private final Ride passengerRide;
    private final long id;

    private static long counter = 0; // contador global para IDs únicos

    public RideMatch(Ride left, Ride right) throws RideSharingAppException {
        if (left == null || right == null) {
            throw new RideSharingAppException("Ride is null.");
        }

        if (left.isDriver() == right.isDriver()) {
            throw new RideSharingAppException("Both rides are drivers or both are passengers.");
        }

        this.driverRide = left.isDriver() ? left : right;
        this.passengerRide = left.isPassenger() ? left : right;
        this.id = counter++; // garante IDs únicos
    }

    public long getId() {
        return id;
    }

    public Ride getRide(RideRole role) {
        return (role == RideRole.DRIVER) ? driverRide : passengerRide;
    }

    public String getName(RideRole role) {
        return getRide(role).getUser().getName();
    }

    public float getStars(RideRole role) {
        return getRide(role).getUser().getAverage(role);
    }

    public Location getWhere(RideRole role) {
        return getRide(role).getCurrent();
    }

    public Car getCar() {
        return driverRide.getUser().getCar(driverRide.getPlate());
    }

    public float getCost() {
        return driverRide.getCost();
    }

    public boolean matchable() {
        // Ambos os rides devem existir, estar por emparelhar e ir para o mesmo destino
        return driverRide != null &&
                passengerRide != null &&
                driverRide.getMatch() == null &&
                passengerRide.getMatch() == null &&
                driverRide.getTo().equals(passengerRide.getTo());
    }

}

