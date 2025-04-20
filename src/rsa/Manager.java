package rsa;

import rsa.match.Location;
import rsa.match.Matcher;
import rsa.match.RideMatch;
import rsa.ride.Ride;
import rsa.ride.RideRole;
import rsa.user.User;
import rsa.user.Users;

import java.io.File;
import java.util.Collection;

public class Manager {
    private static Manager instance;
    private Users users;
    private Matcher matcher;

    private Manager() throws RideSharingAppException {
        users = Users.getInstance();
        matcher = new Matcher(1000, 1000, 50, 10); // parâmetros ajustáveis
    }

    public static Manager getInstance() throws RideSharingAppException {
        if (instance == null)
            instance = new Manager();
        return instance;
    }

    public void reset() {
        users.reset();
        matcher = new Matcher(1000, 1000, 50, 10);
        instance = null;
    }

    public User register(String nick, String name) throws RideSharingAppException {
        return users.register(nick, name);
    }

    public boolean authenticate(String nick, String key) {
        return users.authenticate(nick, key);
    }

    public User getUser(String nick) {
        return users.getUser(nick);
    }

    public User getOrCreateUser(String nick, String name) throws RideSharingAppException {
        return users.getOrCreateUser(nick, name);
    }

    public void setUsersFile(File file) {
        Users.setUsersFile(file);
    }

    public Ride addRide(User user, Location from, Location to, String plate, float cost)
            throws RideSharingAppException {
        Ride ride = new Ride(user, from, to, plate, cost);
        matcher.addRide(ride);
        return ride;
    }

    public void updateRide(Ride ride, Location current) {
        matcher.updateRide(ride, current);
    }

    public RideMatch getMatch(long matchId) {
        return matcher.getMatch(matchId);
    }
}
