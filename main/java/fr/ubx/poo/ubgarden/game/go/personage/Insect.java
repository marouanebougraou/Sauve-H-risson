package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Flowers;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;

public abstract class Insect extends GameObject implements Movable {
    protected Direction direction;
    protected final Timer moveTimer;
    protected int lifePoints; // Points de vie de l'insecte
    protected int damageValue; // Dommage infligé au jardinier

    public Insect(Game game, Position position, int lifePoints, int damageValue, long moveDelay) {
        super(game, position);
        this.direction = Direction.random();
        this.lifePoints = lifePoints;
        this.damageValue = damageValue;
        this.moveTimer = new Timer(moveDelay);
        this.moveTimer.start();
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if (!game.world().getGrid().inside(nextPos)) {
            return false;
        }

        Decor obstacle = game.world().getGrid().get(nextPos);
        if (obstacle != null) {
            // Les insectes peuvent se déplacer sur l'herbe, la terre et les fleurs
            // Mais pas sur les arbres ou les portes
            return obstacle instanceof Flowers ||
                    obstacle instanceof Grass ||
                    obstacle instanceof Land;
        }

        return true;
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);

        // Vérifier s'il y a une bombe insecticide sur cette position
        Decor decor = game.world().getGrid().get(nextPos);
        if (decor != null && decor.getBonus() instanceof Insecticide) {
            Insecticide insecticide = (Insecticide) decor.getBonus();
            hurt();
            insecticide.remove();
        }

        // Vérifier si l'insecte a touché le jardinier
        Gardener gardener = game.getGardener();
        if (gardener.getPosition().equals(nextPos)) {
            // Le jardinier est piqué
            gardener.hurt(damageValue);
            hurt(); // L'insecte est blessé après avoir piqué
        }

        return nextPos;
    }

    @Override
    public void update(long now) {
        moveTimer.update(now);

        if (!moveTimer.isRunning()) {
            // Choisir une nouvelle direction aléatoire
            Direction newDirection;
            int attempts = 0;
            do {
                newDirection = Direction.random();
                attempts++;
            } while (!canMove(newDirection) && attempts < 4);

            if (canMove(newDirection)) {
                direction = newDirection;
                move(direction);
            }

            moveTimer.restart();
        }
    }

    public void hurt() {
        this.lifePoints--;
        if (this.lifePoints <= 0) {
            remove();
        }
    }

    public Direction getDirection() {
        return direction;
    }
}