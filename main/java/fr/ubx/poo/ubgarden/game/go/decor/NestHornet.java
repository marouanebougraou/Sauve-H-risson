    // Classe NestHornet qui hérite aussi de NestInsect
    package fr.ubx.poo.ubgarden.game.go.decor;

    import fr.ubx.poo.ubgarden.game.Game;
    import fr.ubx.poo.ubgarden.game.Position;
    import fr.ubx.poo.ubgarden.game.go.personage.Hornet;

    public class NestHornet extends NestInsect {
        private static final long SPAWN_DELAY = 10000; // 10 secondes

        public NestHornet(Game game, Position position) {
            super(game, position, SPAWN_DELAY);
        }

        @Override
        protected void spawnInsect() {
            Hornet hornet = new Hornet(game, getPosition());
            game.world().addInsect(hornet);
            generateInsecticide(2);
        }
    }