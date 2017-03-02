package net.mamot.bot.services.poll;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public class Poll {
    private static final String QUESTION_MARK = "?";
    public static final String OPTIONS_SPLITTER = "|";
    final private String question;
    final private Map<String, List<String>> options;
    private long id;

    public Poll(String poll) {
        if (poll == null || !poll.contains(QUESTION_MARK)) {
            throw new InvalidQuestionFormatEx();
        }
        String[] parts = poll.split("\\" + QUESTION_MARK);
        if (parts.length != 2) {
            throw new InvalidQuestionFormatEx();
        }
        question = parts[0].trim() + QUESTION_MARK;
        String[] opts = parts[1].split("\\" + OPTIONS_SPLITTER);
        if (opts.length == 0) {
            throw new InvalidQuestionFormatEx();
        }
        options = stream(opts).filter(s -> !s.isEmpty()).collect(toMap(String::trim, s -> new ArrayList<String>()));
    }

    public Poll(String question, String... opts) {
        this.question = question;
        options = stream(opts).filter(s -> !s.isEmpty()).collect(toMap(String::trim, s -> new ArrayList<String>()));
    }

    public static Poll poll(String question, String... opts) {
        return new Poll(question, opts);
    }

    public static Poll poll(long id, String question, String... opts) {
        Poll p = poll(question, opts);
        p.id(id);
        return p;
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

    public Poll revote(String option, String voter) {
        Optional<String> prevOpt = optionOf(voter);
        prevOpt.ifPresent(opt -> unvote(opt, voter));
        vote(option, voter);
        return this;
    }

    public long id() {
        return id;
    }

    public void id(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return id == poll.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public class InvalidQuestionFormatEx extends  RuntimeException{}
}