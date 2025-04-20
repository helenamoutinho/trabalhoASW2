package rsa.ride;

import java.util.Comparator;
import rsa.match.RideMatch;

public interface RideMatchSorter {
    Comparator<RideMatch> getComparator();
}
