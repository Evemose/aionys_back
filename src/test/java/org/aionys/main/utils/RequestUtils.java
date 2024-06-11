package org.aionys.main.utils;

import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;

public class RequestUtils {

    public static List<Pair<String, String>> getBearerPartsFromSetCookies(List<String> cookies) {
        return cookies.stream()
                .flatMap(header -> Arrays.stream(header.split(";")))
                .filter(e -> e.matches("Bearer[a-zA-Z]+=.*"))
                .map(e -> e.split("="))
                .map(e -> Pair.of(e[0], e[1]))
                .toList();
    }
}
