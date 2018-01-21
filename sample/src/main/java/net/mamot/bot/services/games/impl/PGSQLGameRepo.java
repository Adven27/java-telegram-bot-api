package net.mamot.bot.services.games.impl;

import net.mamot.bot.services.impl.PGSQLRepo;

public class PGSQLGameRepo extends PGSQLRepo {
    public PGSQLGameRepo(String databaseUrl) {
        super(databaseUrl, "games", "game");
    }
}
