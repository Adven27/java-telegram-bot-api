package net.mamot.bot.services.debts;

import net.mamot.bot.services.impl.Injector;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.math.BigDecimal.ZERO;

public class Transaction {
    private DebtsRepo debtsRepo = (DebtsRepo) Injector.provide(DebtsRepo.class);

    private final int me;
    private String who;
    private BigDecimal sum = ZERO;
    private boolean income;

    public Transaction(int me) {
        this.me = me;
    }

    //TODO refactor
    public static Transaction fromId(String id) {
        try {
            String[] strings = id.split(":");
            Transaction trn = new Transaction(parseInt(strings[0]));
            trn.to(strings[1]);
            trn.income(parseBoolean(strings[2]));
            trn.sum(new BigDecimal(strings[3]));
            return trn;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return isNullOrEmpty(who) ? "" : who + (income ? " дал мне " : " взял у меня ") + sum;
    }

    public String id() {
        return me + ":" + who + ":" + income + ":" + sum;
    }

    public int me() {
        return me;
    }

    public void to(String to) {
        this.who = to;
    }

    public BigDecimal sum() {
        return sum;
    }

    public void sum(BigDecimal sum) {
        this.sum = sum;
    }

    boolean isValid() {
        return !(who == null || sum == null);
    }

    public void commit() {
        if (!isValid()) {
            throw new InvalidDebtTransactionException();
        }
        Optional<Debt> debtOptional = debtsRepo.select(me).stream().filter(d -> d.who().equals(who)).findFirst();
        if (debtOptional.isPresent()) {
            Debt debt = debtOptional.get();
            if (income) {
                debt.minus(sum);
            } else {
                debt.plus(sum);
            }
            if (debt.total().equals(ZERO)) {
                debtsRepo.delete(me, debt);
            } else {
                debtsRepo.update(me, debt);
            }
        } else {
            Debt debt = new Debt(who);
            if (income) {
                debt.minus(sum);
            } else {
                debt.plus(sum);
            }
            debtsRepo.insert(me, debt);
        }
    }

    public void income(boolean income) {
        this.income = income;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return me == that.me &&
                income == that.income &&
                Objects.equals(who, that.who) &&
                Objects.equals(sum, that.sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(me, who, sum, income);
    }

    private static class InvalidDebtTransactionException extends RuntimeException { }
}
