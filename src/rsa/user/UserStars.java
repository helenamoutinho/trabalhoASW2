package rsa.user;

public enum UserStars {
    FIVE_STARS,
    FOUR_STARS,
    THREE_STARS,
    TWO_STARS,
    ONE_STARS;

    int getStars() {
        return switch (this) {
            case FIVE_STARS -> 5;
            case FOUR_STARS -> 4;
            case THREE_STARS -> 3;
            case TWO_STARS -> 2;
            case ONE_STARS -> 1;
        };
    }
}
