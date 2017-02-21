package net.mamot.bot.games;

import net.mamot.bot.services.Emoji;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static net.mamot.bot.games.TicTacToe.Result.*;

public class TicTacToe {
    private Result result = Result.NONE;

    Integer[] board = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public final static int HUPLAYER = -1;
    public final static int AIPLAYER = -2;
    int iter = 0;
    int round = 0;

    public List<Integer> availableMoves() {
       return asList(avail(board));
    }

    public List<Integer> board() {
        return asList(board);
    }

    private Integer[] avail(Integer[] reboard){
        return stream(reboard).filter(i -> i != HUPLAYER && i != AIPLAYER).collect(toList()).toArray(new Integer[]{});
    }

    public void move(int move) {
        move(move, HUPLAYER);
    }

    public void reset() {
        round = 0;
        board = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        result = NONE;
    }

    private void move(int move, int player) {
        if (board[move] != HUPLAYER && board[move] != AIPLAYER) {
            round++;
            board[move] = player;
            System.out.println("---HUMAN---\n" + stream(board).map(i -> print(i)).reduce((s, s2) -> reduce(s, s2)).get());
            if (winning(board, player)) {
                result = HU;
                return;
            } else if (round > 8) {
                result = TIE;
                return;
            } else {
                round++;
                int index = minimax(board, AIPLAYER).index;
                board[index] = AIPLAYER;
                System.out.println("----AI----\n" + stream(board).map(i -> print(i)).reduce((s, s2) -> reduce(s, s2)).get());
                if (winning(board, AIPLAYER)) {
                    result = AI;
                    return;
                } else if (round == 0) {
                    result = TIE;
                    return;
                }
            }
        }
    }

    public Move minimax(Integer[] reboard, int player) {
        iter++;
        Integer[] array = avail(reboard);
        if (winning(reboard, HUPLAYER)) {
            return new Move(-10);
        } else if (winning(reboard, AIPLAYER)) {
            return new Move(10);
        } else if (array.length == 0) {
            return new Move(0);
        }

        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            Move move = new Move();
            move.index = reboard[array[i]];
            reboard[array[i]] = player;

            if (player == AIPLAYER) {
                Move g = minimax(reboard, HUPLAYER);
                move.score = g.score;
            } else {
                Move g = minimax(reboard, AIPLAYER);
                move.score = g.score;
            }
            reboard[array[i]] = move.index;
            moves.add(move);
        }

        int bestMove = 0;
        if (player == AIPLAYER) {
            int bestScore = -10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score > bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        } else {
            int bestScore = 10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score < bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        }
        return moves.get(bestMove);
    }

    class Move {
        int index;
        int score;

        public Move(int score) {
            this.score = score;
        }

        public Move() {}
    }

    public boolean keepPlaying() {
        return result == NONE;
    }

    public Result result() {
        return result;
    }

    private boolean winning(Integer[] board, int player) {
        if (
                (board[0] == player && board[1] == player && board[2] == player) ||
                        (board[3] == player && board[4] == player && board[5] == player) ||
                        (board[6] == player && board[7] == player && board[8] == player) ||
                        (board[0] == player && board[3] == player && board[6] == player) ||
                        (board[1] == player && board[4] == player && board[7] == player) ||
                        (board[2] == player && board[5] == player && board[8] == player) ||
                        (board[0] == player && board[4] == player && board[8] == player) ||
                        (board[2] == player && board[4] == player && board[6] == player)
                ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = "";
        switch (result()) {
            case AI: result += "Проиграл, кожаный ублюдок!"; break;
            case HU: result += "Не может быть!!! Кожаный ублюдок выйграл... :("; break;
            case TIE: result += "Ничья, кожаный ублюдок!"; break;
            case NONE: result += "Твой ход, кожаный ублюдок!"; break;
        }
        return result + "\n\n" + board().stream().map(this::print).reduce(this::reduce).get();
    }

    private String print(int i){
        if(i == HUPLAYER ) {
            return Emoji.EMPTY_CELL_WHITE.toString();
        } else if ( i == AIPLAYER) {
            return Emoji.CROSS_MARK.toString();
        }
        return Emoji.EMPTY_CELL.toString();
    }

    private String reduce(String s1, String s2){
        if (s1.replace("\n","").length() % 3 == 0) {
            return  s1 + "\n"+ s2;
        }
        return s1 + s2;
    }

    public enum Result {
        AI, HU, TIE, NONE;
    }
}
