package net.mamot.bot.services.debts;

import net.mamot.bot.services.impl.PGSQLRepo;

public class PGSQLDataRepo extends PGSQLRepo {
    public PGSQLDataRepo() {
        super("data", "data");
    }
}
