package com.pengrad.telegrambot.tester;

import com.pengrad.telegrambot.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestMatcher {

    public List<Mismatch> match(BaseRequest[] expectedRequests, List<BaseRequest> actualRequests) {
        List<Mismatch> result = new ArrayList<>();
        int max = Math.max(actualRequests.size(), expectedRequests.length);
        for (int i = 0; i < max; i++) {
            BaseRequest actual = i < actualRequests.size() ? actualRequests.get(i) : null;
            BaseRequest expected = i < expectedRequests.length ? expectedRequests[i] : null;
            if (actual == null) {
                result.add(new Mismatch("Missing request", expected.toString(), null));
            } else if (expected == null) {
                result.add(new Mismatch("Unwanted request", null, actual.toString()));
            } else if (!actual.equals(expected)) {
                result.add(new Mismatch("Unmatched requests ", expected.toString(), actual.toString()));
            }
        }
        return result;
    }

    public class Mismatch {
        private final String key;
        private final String exp;
        private final String act;

        public Mismatch(String key, String exp, String act) {
            this.key = key;
            this.exp = exp;
            this.act = act;
        }

        @Override
        public String toString() {
            String res = "\n\n" + key + ": \n\t Expected: " + exp + "\n\t   Actual: " + act;
            if (exp == null) {
                res = "\n\n" + key + ": \n\t " + act;
            } else if (act == null) {
                res = "\n\n" + key + ": \n\t " + exp;
            }
            return res;
        }
    }
}