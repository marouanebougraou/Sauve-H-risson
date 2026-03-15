package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;

public class Hornet extends Insect {
    public Hornet(Game game, Position position) {
        super(game, position, 2, 30, 1000 / game.configuration().hornetMoveFrequency());
    }
}