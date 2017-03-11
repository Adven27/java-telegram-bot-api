package net.mamot.bot.services.debts;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class Transaction {
    public static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1);
    private int me;
    private String who;
    private BigDecimal sum = ZERO;
    private boolean income;

    public Transaction(int me) {
        this.me = me;
    }

    @Override
    public String toString() {
        return who + " сумма " + sum.multiply(income ? ONE : MINUS_ONE);
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
        //todo commit
    }

    public void income(boolean income) {
        this.income = income;
    }

    private class InvalidDebtTransactionException extends RuntimeException { }
}
