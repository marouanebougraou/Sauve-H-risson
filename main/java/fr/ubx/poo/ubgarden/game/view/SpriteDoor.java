package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.go.decor.Door;
import javafx.scene.layout.Pane;

public class SpriteDoor extends Sprite {
    private final Door door;
    private final ImageResourceFactory factory;

    public SpriteDoor(Pane layer, Door door) {
        super(layer, null, door);
        this.door = door;
        this.factory = ImageResourceFactory.getInstance();
        updateImage();
    }

    @Override
    public void updateImage() {
        if (door.isOpen()) {
            setImage(factory.get(ImageResource.DOOR_OPENED));
        } else {
            setImage(factory.get(ImageResource.DOOR_CLOSED));
        }
    }
}