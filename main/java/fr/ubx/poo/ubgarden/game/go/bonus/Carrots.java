package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Carrots extends Bonus {
    private boolean pickedUp = false;

    public Carrots(Position position, Decor decor) {
        super(position, decor);
    }

    @Override
    public void pickUpBy(Gardener gardener) {
        gardener.pickUp(this);
    }

    public void setPickedUp() {
        this.pickedUp = true;
        remove();
    }

    public boolean isPickedUp() {
        return pickedUp;
    }
}