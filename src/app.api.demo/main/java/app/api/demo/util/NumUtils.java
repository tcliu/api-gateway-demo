package app.api.demo.util;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class NumUtils {

    public static List<Integer> getNums(String range) {
        if (range == null) {
            return emptyList();
        } else if (range.indexOf(",") != -1) {
            return Stream.of(range.split(","))
                .flatMap(subRange -> getNums(subRange).stream())
                .collect(toList());
        } else {
            String[] toks = range.split(":");
            if (toks.length == 1) {
                return singletonList(Integer.valueOf(toks[0]));
            } else {
                return IntStream.rangeClosed(Integer.valueOf(toks[0]), Integer.valueOf(toks[1]))
                    .boxed().collect(toList());
            }
        }
    }

}
