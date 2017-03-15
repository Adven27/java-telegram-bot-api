package net.mamot.bot.services.games.impl;

import net.mamot.bot.services.impl.PGSQLRepo;

public class PGSQLGameRepo extends PGSQLRepo {
    public PGSQLGameRepo() {
        super("games", "game");
    }
}
