package net.mamot.bot.services.debts;

import net.mamot.bot.services.impl.PGSQLRepo;

public class PGSQLDataRepo extends PGSQLRepo {
    public PGSQLDataRepo(String databaseUrl) {
        super(databaseUrl, "data", "data");
    }
}
