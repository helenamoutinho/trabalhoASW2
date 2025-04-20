package rsa.ride;

import java.io.Serializable;

public enum RideRole implements Serializable {
    DRIVER,
    PASSENGER;

    public RideRole other() {
        return (this == DRIVER) ? PASSENGER : DRIVER;
    }
}