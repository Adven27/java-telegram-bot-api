package net.mamot.bot.poll;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public class Poll {
    private static final String QUESTION_MARK = "?";
    final private String question;
    final private Map<String, List<String>> options;

    public Poll(String poll) {
        if (poll == null || !poll.contains(QUESTION_MARK)) {
            throw new InvalidQuestionFormatEx();
        }
        String[] parts = poll.split("\\" + QUESTION_MARK);
        if (parts.length != 2) {
            throw new InvalidQuestionFormatEx();
        }
        question = parts[0] + QUESTION_MARK;
        String[] opts = parts[1].split("/");
        if (opts.length == 0) {
            throw new InvalidQuestionFormatEx();
        }
        options = stream(opts).filter(s -> !s.isEmpty()).collect(toMap(String::trim, s -> new ArrayList<String>()));
    }

    public String question() {
        return question;
    }

    public Set<String> options() {
        return options.keySet();
    }

    public void vote(String option, String voter) {
        List<String> currentVotes = votesFor(option);
        currentVotes.add(voter);
        options.put(option, currentVotes);

    }

    public Map<String, List<String>> votes() {
        return options;
    }

    public List<String> votesFor(String option) {
        return options.get(option);
    }

    public void unvote(String option, String voter) {
        List<String> currentVotes = votesFor(option);
        currentVotes.remove(voter);
        options.put(option, currentVotes);
    }

    public Optional<String> optionOf(String voter) {
        Optional<Map.Entry<String, List<String>>> entry = options.entrySet().stream().filter(e -> e.getValue().contains(voter)).findFirst();
        if (entry.isPresent()) {
            return Optional.of(entry.get().getKey());
        }
        return Optional.empty();
    }

    public class InvalidQuestionFormatEx extends  RuntimeException{}
}