package net.mamot.bot.services.debts;

import net.mamot.bot.services.Repo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DebtsRepo {
    private final Map<Integer, Set<Debt>> user2Debts = new HashMap<>();
    private final Repo repo = new PGSQLDataRepo();


    public void insert(int user, Debt debt) {
        Set<Debt> debts = select(user);
        debts.add(debt);
//        repo.insert(String.valueOf(user), new Gson().toJson(debt));
        user2Debts.put(user, debts);
    }

    public Set<Debt> select(int user) {
        return user2Debts.get(user) == null ? new HashSet<>() : user2Debts.get(user);
    }

    public void update(int user, Debt debt) {
        Set<Debt> debts = select(user);
        debts.remove(debt);
        debts.add(debt);
        user2Debts.put(user, debts);
    }

    public void delete(int user, Debt debt) {
        Set<Debt> debts = select(user);
        debts.remove(debt);
        user2Debts.put(user, debts);
    }
}