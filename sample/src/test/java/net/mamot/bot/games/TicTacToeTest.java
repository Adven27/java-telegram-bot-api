package net.mamot.bot.games;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import static net.mamot.bot.games.TicTacToe.Result.HU;
import static net.mamot.bot.games.TicTacToe.Result.NONE;
import static org.junit.Assert.assertTrue;

public class TicTacToeTest {

    TicTacToe sut = new TicTacToe();

    @Test
    public void humanCantWin() throws Exception {
        while(sut.keepPlaying()) {
            sut.move(humanMove());
        }
        System.out.println("Result " + sut.result());
        assertTrue(sut.result() != HU && sut.result() != NONE);
    }

    private int humanMove() {
        List<Integer> ms = sut.availableMoves();
        return ms.get(new Random().nextInt(ms.size()));
    }
}
