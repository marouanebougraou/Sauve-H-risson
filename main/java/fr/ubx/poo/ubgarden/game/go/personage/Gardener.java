/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;
    private Direction direction;
    Timer moveTimer;
    protected int diseaseLevel = 1;
    protected int insecticideNumber = 0;
    private final Timer sicknessTimer;
    private final List<Timer> poisoningTimers = new ArrayList<>();
    private boolean moveRequested = false;
    private boolean hasMoved = false;





    public Gardener(Game game, Position position) {

        super(game, position);
        this.energy = game.configuration().gardenerEnergy();
        this.direction = Direction.DOWN;
        long energyRecoverDuration = game.configuration().energyRecoverDuration();
        this.moveTimer = new Timer(energyRecoverDuration);
        this.sicknessTimer=new Timer(game.configuration().diseaseDuration());
        this.sicknessTimer.start();
        this.moveTimer.start();
        this.hasMoved = false;

    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
        int boost = game.configuration().energyBoost();
        int max = game.configuration().gardenerEnergy();

        if (this.energy < max) {
            this.energy += boost;
            if (this.energy > max) {
                this.energy = max;
            }
        }

        this.diseaseLevel = 1;
        poisoningTimers.clear();
        energyBoost.remove();

        System.out.println("Apple picked up");
    }

    public void pickUp(PoisonedApple poisonedApple) {
        this.diseaseLevel += 1;

        Timer poisoningTimer = new Timer(game.configuration().diseaseDuration());
        poisoningTimers.add(poisoningTimer);
        poisoningTimer.start();

        poisonedApple.remove();
        System.out.println("Poisoned apple picked up");
    }
    public void pickUp(Hedgehog hedgehog) {
        hedgehog.setPickedUp(true);
        System.out.println("Hedgehog picked up");
    }
    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction dir) {
        Position target = dir.nextPosition(getPosition());
        if (!game.world().getGrid().inside(target)) {
            return false;
        }
        Decor obstacle = game.world().getGrid().get(target);
        if (obstacle != null && !obstacle.walkableBy(this)) {
            return false;
        }
        return true;
    }
    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);
        checkInsectCollision(nextPos);
        hasMoved = true;
        if (next != null)
            next.pickUpBy(this);
        return nextPos;
    }

    public void update(long now) {
        if (moveRequested) {
            boolean canProceed = canMove(direction);
            moveRequested = false;

            if (canProceed) {
                move(direction);
                double cost = game.world().getGrid().get(getPosition()).energyConsumptionWalk();
                this.energy -= cost * diseaseLevel;
                moveTimer.restart();
            }
        }

        moveTimer.update(now);
        if (!moveTimer.isRunning() && !hasMoved) {
            recoverEnergy();
            moveTimer.restart();
        }
        if (moveTimer.isRunning()) {
            hasMoved = false;
        }

        sicknessTimer.update(now);
        if (!sicknessTimer.isRunning()) {
            diseaseLevel = 1;
            sicknessTimer.start();
        }
        updatePoisoningTimers(now);
    }


    public Direction getDirection() {
        return direction;
    }

    public int getDiseaseLevel() {
        return this.diseaseLevel;
    }

    public int getInsecticideNumber() {
        return this.insecticideNumber;
    }

    private void recoverEnergy() {
        if(energy<game.configuration().gardenerEnergy())
            energy++;
    }
    private void updatePoisoningTimers(long now) {
        diseaseLevel = 1;

        List<Timer> activeTimers = new ArrayList<>();
        for (Timer timer : poisoningTimers) {
            timer.update(now);
            if (timer.isRunning()) {
                diseaseLevel++;
                activeTimers.add(timer);
            }
        }

        poisoningTimers.clear();
        poisoningTimers.addAll(activeTimers);
    }

    public void pickUp(Carrots carrots) {
        System.out.println("Carrot picked up");
        carrots.setPickedUp();
        if (game.world().getGrid() instanceof Level) {
            Level level = (Level) game.world().getGrid();
            if (level.allCarrotsCollected()) {
                level.openAllDoors();
            }
        }
    }
    public void hurt(int damage) {
        this.energy -= damage;
    }

    public void pickUp(Insecticide insecticide) {
        this.insecticideNumber++;
        insecticide.remove();
        System.out.println("Insecticide picked up");
    }

    public void useInsecticide() {
        if (this.insecticideNumber > 0) {
            this.insecticideNumber--;
            System.out.println("Insecticide utilisé, restant: " + insecticideNumber);
        }
    }
    private void checkInsectCollision(Position position) {
        for (Insect insect : game.world().getInsects()) {
            if (!insect.isDeleted() && insect.getPosition().equals(position)) {
                hurt(insect instanceof Hornet ? 30 : 20);
                insect.hurt();
            }
        }
    }


}
