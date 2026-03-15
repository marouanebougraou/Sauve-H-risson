package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }



    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }
    public Game load(File file) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            Configuration configuration = getConfiguration(properties);
            int levels = integerProperty(properties, "levels", 1);
            boolean compression = booleanProperty(properties, "compression", false);
            World world = new World(levels);
            Position gardenerPosition = null;
            Game game = null;

            for (int i = 1; i <= levels; i++) {
                String levelKey = "level" + i;
                String mapString = properties.getProperty(levelKey);
                if (mapString == null)
                    throw new RuntimeException("Level " + i + " is missing");

                if (compression) {
                    mapString = decompressMap(mapString);
                }
                MapLevel mapLevel = stringToLevel(mapString);

                if (i == 1) {
                    gardenerPosition = mapLevel.getGardenerPosition();
                    if (gardenerPosition == null)
                        throw new RuntimeException("Gardener not found in level 1");

                    game = new Game(world, configuration, gardenerPosition);
                }

                Map level = new Level(game, i, mapLevel);
                world.put(i, level);
            }

            return game;
        } catch (IOException e) {
            throw new RuntimeException("Error loading game from file: " + e.getMessage(), e);
        }
    }

    private String decompressMap(String compressedMap) {
        StringBuilder decompressed = new StringBuilder();
        int i = 0;
        while (i < compressedMap.length()) {
            char c = compressedMap.charAt(i);
            i++;

            if (Character.isDigit(c)) {
                int repeat = Character.getNumericValue(c);
                char toRepeat = compressedMap.charAt(i);
                i++;

                for (int j = 0; j < repeat; j++) {
                    decompressed.append(toRepeat);
                }
            } else {
                decompressed.append(c);
            }
        }
        return decompressed.toString();
    }

    private MapLevel stringToLevel(String mapString) {
        String[] rows = mapString.split("x");
        int width = rows[0].length();
        int height = rows.length;

        MapLevel mapLevel = new MapLevel(width, height);

        for (int j = 0; j < height; j++) {
            String row = rows[j];
            for (int i = 0; i < row.length() && i < width; i++) {
                char c = row.charAt(i);
                MapEntity entity = MapEntity.fromCode(c);
                mapLevel.set(i, j, entity);
            }
        }

        return mapLevel;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
