package net.mamot.bot.services.poll;

import java.util.Optional;

public interface PollsRepo {
    void add(Poll poll);
    void remove(Poll poll);
    Optional<Poll> get(long id);
    void update(Poll poll);
}
