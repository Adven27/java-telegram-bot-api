package net.mamot.bot.services.debts;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

public class Debt {
    private final String who;
    private BigDecimal total;

    public Debt(String who) {
        this.who = who;
        this.total = ZERO;
    }

    public String who() {
        return who;
    }

    public BigDecimal total() {
        return total;
    }

    public BigDecimal plus(BigDecimal sum) {
        return total = total.add(sum);
    }

    public BigDecimal minus(BigDecimal sum) {
        return total = total.subtract(sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return Objects.equals(who, debt.who);
    }

    @Override
    public int hashCode() {
        return Objects.hash(who);
    }

    @Override
    public String toString() {
        return who + (total.compareTo(ZERO) > 0 ? " должен мне " : " одолжил мне ") + total.abs() ;
    }
}