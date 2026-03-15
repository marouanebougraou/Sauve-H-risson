package fr.ubx.poo.ubgarden.game;

public record Position (int level, int x, int y) {
    public Position neighbor(Direction direction) {
        return direction.nextPosition(this);
    }
}
