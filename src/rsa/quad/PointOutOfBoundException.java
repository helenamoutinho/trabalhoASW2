package rsa.quad;

import rsa.shared.HasPoint;

public class PointOutOfBoundException extends RuntimeException {
    public PointOutOfBoundException(HasPoint point) {
        super("Point " + point + " is outside the bounds of the Quadtree.");
    }
}
