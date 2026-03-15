// Classe NestWasp qui hérite maintenant de NestInsect
package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;

public class NestWasp extends NestInsect {
    private static final long SPAWN_DELAY = 5000; // 5 secondes

    public NestWasp(Game game, Position position) {
        super(game, position, SPAWN_DELAY);
    }

    @Override
    protected void spawnInsect() {
        Wasp wasp = new Wasp(game, getPosition());
        game.world().addInsect(wasp);
        generateInsecticide(1);
    }
}