package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;


import javafx.scene.paint.Color;

import java.awt.*;

public class Door extends Decor {
    private final int targetLevel;
    private boolean open;

    public Door(Position position, int targetLevel, boolean open) {
        super(position);
        this.targetLevel = targetLevel;
        this.open = open;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        setModified(true);
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        if (open) {
            if (gardener.getGame().world().getGrid(targetLevel) == null) {
                if (gardener.getGame().getEngine() != null) {
                    gardener.getGame().getEngine().showMessage("Niveau suivant non disponible!", Color.ORANGE);
                }
                return true;
            }


            gardener.getGame().requestSwitchLevel(targetLevel);
            return true;
        }
        return false;
    }
}