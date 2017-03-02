package net.mamot.bot.services.poll;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PollsInMemRepo implements PollsRepo {
    private static List<Poll> polls = new ArrayList<>();

    @Override
    public void add(Poll poll) {
        polls.add(poll);
    }

    @Override
    public void remove(Poll poll) {
        polls.remove(poll);
    }

    @Override
    public Optional<Poll> get(long id) {
        return polls.stream().filter(poll -> poll.id() == id).findFirst();
    }

    @Override
    public void update(Poll poll) {
        polls.replaceAll(p -> p.id() == poll.id()? poll : p  );
    }
}
