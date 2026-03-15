// SpriteInsect.java - Nouvelle classe
package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.go.personage.Insect;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteInsect extends Sprite {
    private final ImageResource downImage;
    private final ImageResource leftImage;
    private final ImageResource upImage;
    private final ImageResource rightImage;

    public SpriteInsect(Pane layer, Insect insect, ImageResource downImage,
                        ImageResource leftImage, ImageResource upImage, ImageResource rightImage) {
        super(layer, null, insect);
        this.downImage = downImage;
        this.leftImage = leftImage;
        this.upImage = upImage;
        this.rightImage = rightImage;
        updateImage();
    }

    @Override
    public void updateImage() {
        Insect insect = (Insect) getGameObject();
        Direction direction = insect.getDirection();
        Image image = getInsectImage(direction);
        setImage(image);
    }

    private Image getInsectImage(Direction direction) {
        ImageResource resource;
        switch (direction) {
            case UP:
                resource = upImage;
                break;
            case RIGHT:
                resource = rightImage;
                break;
            case DOWN:
                resource = downImage;
                break;
            case LEFT:
                resource = leftImage;
                break;
            default:
                resource = downImage;
                break;
        }
        return ImageResourceFactory.getInstance().get(resource);
    }
}