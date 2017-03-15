package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import net.mamot.bot.games.game2048.Game2048;
import net.mamot.bot.services.Emoji;
import net.mamot.bot.services.Repo;
import net.mamot.bot.services.games.LeaderBoard;
import net.mamot.bot.services.games.LeaderBoardRepo;

import java.util.*;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.model.Chat.Type.Private;
import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static java.util.stream.Collectors.toSet;
import static net.mamot.bot.services.Emoji.RIGHT_ARROW;

public class Game2048Command extends CallbackCommand {
    private static final String DOWN = "\uD83D\uDD3D";
    private static final String UP = "\uD83D\uDD3C";
    private static final String LEFT = Emoji.LEFT_ARROW.toString();
    private static final String RIGHT = RIGHT_ARROW.toString();
    private static final String RESTART = "\uD83D\uDD04";
    private static final String LEADER_BOARD = "\uD83D\uDCF6";
    private static final String LOSE_MSG = " \uD83D\uDC80";
    private static final String WON_MSG = " \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89";
    private static final String BORDER = "\uD83D\uDDB1";
    private static Map<String, Game2048> userGames = new HashMap<>();
    private final Repo repo;
    private final LeaderBoard leaderBoard;
    private boolean viewLeaderBoard = false;

    public Game2048Command(Repo repo, LeaderBoard leaderBoard) {
        super("/game2048","Game 2048");
        this.repo = repo;
        this.leaderBoard = leaderBoard;

        repo.selectAll().forEach((name, game) -> userGames.put(name, new Game2048(game)));

        System.out.println("userGames = " + userGames);
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        String userName = getUserName(user);
        if (userGames.get(userName) != null) {
            bot.execute(message(chat, screen(chat)).replyMarkup(getInlineKeyboard()));
            return;
        }
        Game2048 g = new Game2048();
        userGames.put(userName, g);
        repo.insert(userName, g.toJSON());
        bot.execute(message(chat, screen(chat)).replyMarkup(getInlineKeyboard()));
    }

    private String getUserName(User user) {
        return user.lastName();
    }

    private InlineKeyboardMarkup getInlineKeyboard() {
        return keyboard().row(UP, "up", DOWN, "down", LEFT, "left", RIGHT, "right")
                            .row(LEADER_BOARD,"top", RESTART, "restart")
                            .build();
    }

    private String drawTile(Game2048.Tile tile) {
        switch (tile.value){
            case 2: return Emoji.ZERO.toString();
            case 4: return Emoji.ONE.toString();
            case 8: return Emoji.TWO.toString();
            case 16: return Emoji.THREE.toString();
            case 32: return Emoji.FOUR.toString();
            case 64: return Emoji.FIVE.toString();
            case 128: return Emoji.SIX.toString();
            case 256: return Emoji.SEVEN.toString();
            case 512: return Emoji.EIGHT.toString();
            case 1024: return Emoji.NINE.toString();
            case 2048: return Emoji.TEN.toString();
            default: return Emoji.EMPTY_CELL.toString();
        }
    }

    private String screen(Chat chat) {
        String msg = "";
        Set<Map.Entry<String, Game2048>> games = chat.type() == Private
                ? userGames.entrySet().stream().filter(e -> e.getKey().equals(chat.lastName())).collect(toSet())
                : userGames.entrySet();
        LinkedList<Game2048> gs = new LinkedList<>();
        for (Map.Entry<String, Game2048> game : games) {
            Game2048 g = game.getValue();
            gs.add(g);
            msg += game.getKey() + " " + g.getScore();

            repo.update(game.getKey(), g.toJSON());

            if (g.isLose()) {
                msg += LOSE_MSG;

            } else if (g.isWin()) {
                msg += WON_MSG;
            }
            msg += "\n";
        }

        if (viewLeaderBoard) {
            msg = "\tTop\n";
            List<LeaderBoardRepo.Record> recordStream = leaderBoard.getAll();
            int pos = 1;
            for(LeaderBoardRepo.Record record : recordStream) {
                msg += pos + ". " + record.user() + " " + record.score() + "\n";
                pos++;
            }
        }

        final int maxInRow = 4;
        int cur = 0;

        List<List<Game2048>> rows = new ArrayList<>();
        List<Game2048> row = new ArrayList<>();

        int size = gs.size();
        for (int i = 0; i < size; i++) {
            if(cur < maxInRow) {
                cur++;
                row.add(gs.remove());
            } else {
                rows.add(row);
                row = new ArrayList<>();
                row.add(gs.remove());
                cur = 1;
            }
        }
        rows.add(row);

        for (List<Game2048> r : rows) {
            for (int y = 0; y < 4; y++) {
                for (Game2048 game : r) {
                    for (int x = 0; x < 4; x++) {
                        msg += drawTile(game.getTiles()[x + y * 4]);
                    }
                    msg += BORDER;
                }
                msg += "\n";
            }
            msg += "\n";
        }

        return msg;
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        Message message = cb.message();
        if (userGames.isEmpty()) {
            repo.selectAll().forEach((name, game) -> userGames.put(name, new Game2048(game)));
        }
        String data = cb.data();
        User from = cb.from();

        doAction(data, from);

        bot.execute(editMessage(message, screen(message.chat())).replyMarkup(getInlineKeyboard()));
        return true;
    }

    private void doAction(String action, User from) {
        String userName = getUserName(from);
        Game2048 g = userGames.get(userName);
        if (g == null) {
            userGames.put(userName, new Game2048());
        } else {
            switch (action) {
                case "left":    g.left();  break;
                case "right":   g.right(); break;
                case "up":      g.up();    break;
                case "down":    g.down();  break;
                case "restart": leaderBoard.update(userName, g); g.resetGame(); break;
                case "top": this.viewLeaderBoard = !viewLeaderBoard; break;
                default:
            }

        }
    }
}