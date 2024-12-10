package org.example.gameshop.model;

public enum GameType {
    ONLINE(1), OFFLINE(2);

    private final int code;

    GameType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static GameType fromCode(Integer code) {
        for (GameType gameType : GameType.values()) {
            if (gameType.getCode() == code) {
                return gameType;
            }
        }
        throw new IllegalArgumentException("Unknown code for game type: " + code);
    }

}
