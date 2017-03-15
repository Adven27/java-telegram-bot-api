package net.mamot.bot.services.debts;

import com.google.common.collect.Maps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.EMPTY_SET;

public class Favorites {
    private final Map<Integer, FavInfo> all = Maps.newHashMap();

    public Set<String> getAllCounterparties(int user){
        return all.get(user) == null ? EMPTY_SET : all.get(user).counterparties();
    }

    public Set<Transaction> getAllTransactions(int user){
        return all.get(user) == null ? EMPTY_SET : all.get(user).transactions();
    }

    public void addCounterparty(int user, String counterparty) {
        FavInfo favInfo = all.getOrDefault(user, new FavInfo(new HashSet<>(), new HashSet<>()));
        favInfo.addCounterparty(counterparty);
        all.put(user, favInfo);
    }

    public void addTransaction(int user, Transaction trn) {
        FavInfo favInfo = all.getOrDefault(user, new FavInfo(new HashSet<>(), new HashSet<>()));
        favInfo.addTransactions(trn);
        all.put(user, favInfo);
    }

    public void removeTransactions(int user, Transaction trn) {
        FavInfo favInfo = all.get(user);
        favInfo.removeTransactions(trn);
        all.put(user, favInfo);
    }

    static class FavInfo {
        private final Set<Transaction> transactions;

        private final Set<String> counterparties;

        FavInfo(Set<Transaction> transactions, Set<String> counterparties) {
            this.transactions = transactions;
            this.counterparties = counterparties;
        }

        public Set<Transaction> transactions() {
            return transactions;
        }

        public void addTransactions(Transaction t) {
            transactions.add(t);
        }

        public void removeTransactions(Transaction t) {
            transactions.remove(t);
        }

        public void addCounterparty(String cp) {
            counterparties.add(cp);
        }

        public Set<String> counterparties() {
            return counterparties;
        }
    }
}