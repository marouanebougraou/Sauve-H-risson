    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ubgarden.game.engine;

    import fr.ubx.poo.ubgarden.game.Direction;
    import fr.ubx.poo.ubgarden.game.Game;
    import fr.ubx.poo.ubgarden.game.Map;
    import fr.ubx.poo.ubgarden.game.Position;
    import fr.ubx.poo.ubgarden.game.go.GameObject;
    import fr.ubx.poo.ubgarden.game.go.decor.Decor;
    import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
    import fr.ubx.poo.ubgarden.game.go.decor.Door;
    import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
    import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
    import fr.ubx.poo.ubgarden.game.go.personage.Insect;
    import fr.ubx.poo.ubgarden.game.view.ImageResource;
    import fr.ubx.poo.ubgarden.game.view.Sprite;
    import fr.ubx.poo.ubgarden.game.view.SpriteFactory;
    import fr.ubx.poo.ubgarden.game.view.SpriteGardener;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;


    import java.util.*;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private final Gardener gardener;
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();

        private final Scene scene;

        private StatusBar statusBar;

        private final Pane rootPane = new Pane();
        private final Group root = new Group();
        private final Pane layer = new Pane();
        private Input input;

        public GameEngine(Game game, Scene scene) {
            this.game = game;
            this.scene = scene;
            this.gardener = game.getGardener();
            game.setGameEngine(this);
            initialize();
            buildAndSetGameLoop();
        }

        public Pane getRoot() {
            return rootPane;
        }

        private void initialize() {
            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
            input = new Input(scene);

            root.getChildren().clear();
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            rootPane.getChildren().clear();
            rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
            rootPane.getChildren().add(root);

            // Create sprites
            int currentLevel = game.world().currentLevel();

            for (var decor : game.world().getGrid().values()) {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                }
            }

            sprites.add(new SpriteGardener(layer, gardener));
            resizeScene(sceneWidth, sceneHeight);
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();

                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }

        private void checkCollision() {
            Position gardenerPos = gardener.getPosition();
            for (Insect insect : game.world().getInsects()) {
                if (!insect.isDeleted() && insect.getPosition().equals(gardenerPos)) {
                    gardener.hurt(insect instanceof Hornet ? 30 : 20);
                    insect.hurt();
                }
            }

            if (gardener.getInsecticideNumber() > 0) {
                for (Insect insect : game.world().getInsects()) {
                    if (!insect.isDeleted()) {
                        Position insectPos = insect.getPosition();
                        if (isAdjacent(gardenerPos, insectPos)) {
                            gardener.useInsecticide();
                            insect.hurt();
                            break;
                        }
                    }
                }
            }
        }

        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        public void showMessage(String msg, Color color) {
            boolean isGameOver = (color == Color.RED || color == Color.GREEN);

            Text message = new Text(msg);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setFont(new Font(isGameOver ? 60 : 30));
            message.setFill(color);

            StackPane messagePane = new StackPane(message);

            if (isGameOver) {
                messagePane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
                rootPane.getChildren().clear();
                rootPane.getChildren().add(messagePane);

                if (gameLoop != null) {
                    gameLoop.stop();
                }

                new AnimationTimer() {
                    public void handle(long now) {
                        processInput();
                    }
                }.start();
            } else {
                messagePane.setPrefSize(rootPane.getWidth(), 100);
                messagePane.setLayoutY(50);

                rootPane.getChildren().add(messagePane);
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(() -> rootPane.getChildren().remove(messagePane));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));
            List<Insect> insects = new ArrayList<>(game.world().getInsects());
            for (Insect insect : insects) {
                if (!insect.isDeleted()) {
                    insect.update(now);
                    boolean hasSprite = false;
                    for (Sprite sprite : sprites) {
                        if (sprite.getGameObject() == insect) {
                            hasSprite = true;
                            break;
                        }
                    }
                    if (!hasSprite) {
                        Sprite sprite = SpriteFactory.create(layer, insect);
                        sprites.add(sprite);
                    }
                } else {
                    game.world().removeInsect(insect);
                }
            }
            gardener.update(now);
            if (gardener.getEnergy() < 0) {
                gameLoop.stop();
                showMessage("Perdu!", Color.RED);
            }
            Hedgehog hedgehog = game.getHedgehog();
            if (hedgehog != null && hedgehog.isPickedUp()) {
                gameLoop.stop();
                showMessage("Gagné !", Color.GREEN);
            }
        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(Sprite::render);
        }

        public void start() {
            gameLoop.start();
        }

        private void resizeScene(int width, int height) {
            rootPane.setPrefSize(width, height + StatusBar.height);
            layer.setPrefSize(width, height);
            Platform.runLater(() -> scene.getWindow().sizeToScene());
        }
        public void stop() {
            gameLoop.stop();
        }


        private void checkLevel() {
            if (game.isSwitchLevelRequested()) {
                int newLevel = game.getSwitchLevel();
                int currentLevel = game.world().currentLevel();
                Position gardenerPos = gardener.getPosition();
                game.world().setCurrentLevel(newLevel);
                Position newPosition = null;
                Map newLevelMap = game.world().getGrid();
                for (Decor decor : newLevelMap.values()) {
                    if (decor instanceof Door) {
                        Door door = (Door) decor;
                        if (door.getTargetLevel() == currentLevel) {
                            newPosition = door.getPosition().neighbor(Direction.DOWN);
                            break;
                        }
                    }
                }
                if (newPosition == null) {
                    newPosition = new Position(newLevel, 1, 1);
                }
                gardener.setPosition(newPosition);
                sprites.clear();
                layer.getChildren().clear();
                int height = game.world().getGrid().height();
                int width = game.world().getGrid().width();
                int sceneWidth = width * ImageResource.size;
                int sceneHeight = height * ImageResource.size;
                root.getChildren().clear();
                root.getChildren().add(layer);
                statusBar = new StatusBar(root, sceneWidth, sceneHeight);
                resizeScene(sceneWidth, sceneHeight);
                for (var decor : game.world().getGrid().values()) {
                    sprites.add(SpriteFactory.create(layer, decor));
                    decor.setModified(true);
                    var bonus = decor.getBonus();
                    if (bonus != null) {
                        sprites.add(SpriteFactory.create(layer, bonus));
                        bonus.setModified(true);
                    }
                }

                sprites.add(new SpriteGardener(layer, gardener));
                game.clearSwitchLevel();
            }
        }
        public void createSpriteForGameObject(GameObject gameObject) {
            Sprite sprite = SpriteFactory.create(layer, gameObject);
            sprites.add(sprite);
        }

        private boolean isAdjacent(Position pos1, Position pos2) {
            if (pos1.level() != pos2.level()) return false;
            int dx = Math.abs(pos1.x() - pos2.x());
            int dy = Math.abs(pos1.y() - pos2.y());
            return dx <= 1 && dy <= 1 && (dx + dy > 0);
        }
    }

