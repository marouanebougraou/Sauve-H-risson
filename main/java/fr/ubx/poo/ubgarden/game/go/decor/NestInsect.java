// Nouvelle classe abstraite NestInsect
package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;

import java.util.Random;

public abstract class NestInsect extends Decor {
    protected final Game game;
    protected final Timer spawnTimer;
    protected static final Random random = new Random();

    public NestInsect(Game game, Position position, long spawnDelay) {
        super(position);
        this.game = game;
        this.spawnTimer = new Timer(spawnDelay);
        this.spawnTimer.start();
    }

    protected abstract void spawnInsect();

    @Override
    public void update(long now) {
        super.update(now);

        spawnTimer.update(now);

        if (!spawnTimer.isRunning()) {
            if (game.getGardener().getPosition().level() == getPosition().level()) {
                spawnInsect();
            }
            spawnTimer.restart();
        }
    }

    protected void generateInsecticide(int count) {
        for (int i = 0; i < count; i++) {
            boolean placed = false;
            for (int attempts = 0; attempts < 20 && !placed; attempts++) {
                int x = 1 + random.nextInt(game.world().getGrid().width() - 2);
                int y = 1 + random.nextInt(game.world().getGrid().height() - 2);
                Position position = new Position(getPosition().level(), x, y);
                if (game.world().getGrid().inside(position)) {
                    Decor decor = game.world().getGrid().get(position);
                    if ((decor instanceof Grass || decor instanceof Land) && decor.getBonus() == null) {
                        Insecticide insecticide = new Insecticide(position, decor);
                        decor.setBonus(insecticide);
                        insecticide.setModified(true);
                        game.createSpriteForBonus(insecticide);
                        placed = true;
                    }
                }
            }

            if (!placed) {
                System.out.println("Impossible de placer un insecticide après plusieurs tentatives");
            }
        }
    }
}