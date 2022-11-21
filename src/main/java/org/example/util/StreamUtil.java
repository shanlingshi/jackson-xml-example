package org.example.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author lingshr
 * @since 2021-11-15
 */
public class StreamUtil {

    public static <T> List<T> filter(List<T> source, Predicate<? super T> predicate) {

        return Optional.ofNullable(source).orElse(new ArrayList<>()).stream()
                .filter(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> filter(Iterator<T> source, Predicate<? super T> predicate) {
        if (source == null) {
            return new ArrayList<>();
        }

        ArrayList<T> result = new ArrayList<>();
        while (source != null && source.hasNext()) {
            T current = source.next();
            if (predicate.test(current)) {
                result.add(current);
            }
        }
        return result;
    }

    public static <T> T first(Collection<T> source) {

        return first(source, null);
    }

    public static <T> T first(Collection<T> source, T defaultValue) {

        return Optional.ofNullable(source).orElse(new ArrayList<>()).stream().findFirst().orElse(defaultValue);
    }

    public static <T> T find(Collection<T> source, Predicate<? super T> predicate) {

        return find(source, predicate, null);
    }

    public static <T> T find(T[] source, Predicate<? super T> predicate) {

        return find(source, predicate, null);
    }

    public static <T> T find(T[] source, Predicate<? super T> predicate, T defaultValue) {

        return Arrays.stream(source).filter(predicate).findFirst().orElse(defaultValue);
    }

    public static <T> T find(Collection<T> source, Predicate<? super T> predicate, T defaultValue) {

        return Optional.ofNullable(source).orElse(new ArrayList<>()).stream()
                .filter(predicate).findFirst().orElse(defaultValue);
    }

}
