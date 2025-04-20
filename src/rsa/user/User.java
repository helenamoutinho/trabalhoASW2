package rsa.user;

import java.io.Serializable;
import java.util.*;

import rsa.ride.RideRole;
import rsa.match.PreferredMatch;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nick;
    private String name;
    private String key;
    private Map<String, Car> cars;
    private PreferredMatch preferredMatch;
    private int totalStarsDriver;
    private int countDriver;
    private int totalStarsPassenger;
    private int countPassenger;

    // 🔓 Construtor agora é público
    public User(String nick, String name) {
        this.nick = nick;
        this.name = name;
        this.cars = new HashMap<>();
        this.preferredMatch = PreferredMatch.BETTER;
        totalStarsDriver = 0;
        countDriver = 0;
        totalStarsPassenger = 0;
        countPassenger = 0;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getNick() { return nick; }

    public void setNick(String nick) { this.nick = nick; }

    // 🔓 Método público
    public String generateKey() {
        String toKey = nick + name;
        UUID uuid = UUID.nameUUIDFromBytes(toKey.getBytes());
        this.key = uuid.toString();
        return key;
    }

    public String getKey() {
        if (key == null) return generateKey();
        return key;
    }

    // 🔓 Método público
    public boolean authenticate(String testKey) {
        return getKey().equals(testKey);
    }

    public void addCar(Car car) {
        cars.put(car.getPlate(), car);
    }

    public Car getCar(String plate) {
        return cars.get(plate);
    }

    public void deleteCar(String plate) {
        cars.remove(plate);
    }

    // ✅ Novo: obter todos os carros
    public Collection<Car> getCars() {
        return cars.values();
    }

    public void addStars(UserStars moreStars, RideRole role) {
        if (role == RideRole.DRIVER) {
            totalStarsDriver += moreStars.getStars();
            countDriver++;
        } else {
            totalStarsPassenger += moreStars.getStars();
            countPassenger++;
        }
    }

    public float getAverage(RideRole role) {
        if (role == RideRole.DRIVER)
            return countDriver == 0 ? 0 : (float) totalStarsDriver / countDriver;
        else
            return countPassenger == 0 ? 0 : (float) totalStarsPassenger / countPassenger;
    }

    public PreferredMatch getPreferredMatch() {
        return (preferredMatch == null) ? PreferredMatch.BETTER : preferredMatch;
    }

    // ✅ Novo: definir preferência de emparelhamento
    public void setPreferredMatch(PreferredMatch match) {
        this.preferredMatch = match;
    }
}
