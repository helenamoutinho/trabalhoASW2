package rsa.quad;

import rsa.shared.HasPoint;

public interface Element<T extends HasPoint> {
    void accept(Visitor<T> visitor);
}
