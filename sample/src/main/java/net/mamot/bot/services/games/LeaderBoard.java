package net.mamot.bot.services.games;

import net.mamot.bot.games.game2048.Game2048;

import java.util.List;

public interface LeaderBoard {
    int BOARD_SIZE = 5;
    boolean update(String username, Game2048 game);
    List<LeaderBoardRepo.Record> getAll();
}
