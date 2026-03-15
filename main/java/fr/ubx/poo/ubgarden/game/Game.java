package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.engine.GameEngine;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;


public class Game {
    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private Hedgehog hedgehog;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    private GameEngine engine;

    public Game(World world, Configuration configuration, Position gardenerPosition) {
        this.configuration = configuration;
        this.world = world;
        this.gardener = new Gardener(this, gardenerPosition);
    }
    public void setGameEngine(GameEngine engine) {
        this.engine = engine;
    }


    public Configuration configuration() {
        return configuration;
    }

    public Hedgehog getHedgehog() {
        return this.hedgehog;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public void setHedgehog(Hedgehog hedgehog) {
        this.hedgehog = hedgehog;
    }

    public World world() {
        return world;
    }


    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }
    public void createSpriteForBonus(GameObject bonus) {
        if (engine != null) {
            engine.createSpriteForGameObject(bonus);
        }
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

    public GameEngine getEngine() {return this.engine;}

}