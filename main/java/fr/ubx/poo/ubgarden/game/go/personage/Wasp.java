package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;

public class Wasp extends Insect {
    public Wasp(Game game, Position position) {
        super(game, position, 1, 20, 1000 / game.configuration().waspMoveFrequency());
    }
}
