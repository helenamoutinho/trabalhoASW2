package rsa.match;

import rsa.shared.HasPoint;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable, HasPoint {
    private static final long serialVersionUID = 1L;

    private double latitude, longitude;
    private String name;

    // Construtor padrão: x = longitude, y = latitude
    public Location(double x, double y) {
        this.longitude = x;
        this.latitude = y;
    }

    // Novo construtor com nome
    public Location(String name, double x, double y) {
        this.name = name;
        this.longitude = x;
        this.latitude = y;
    }

    @Override
    public double x() {
        return longitude;
    }

    @Override
    public double y() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Location)) return false;
        Location other = (Location) obj;
        return Double.compare(latitude, other.latitude) == 0 &&
                Double.compare(longitude, other.longitude) == 0 &&
                Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, name);
    }

    @Override
    public String toString() {
        if (name != null) {
            return String.format("Location[%s](%.5f, %.5f)", name, latitude, longitude);
        } else {
            return String.format("Location(%.5f, %.5f)", latitude, longitude);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
