package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageMaker {
    public static Pageable toPage(Integer from, Integer size) {
        int startPage = from / size;
        return PageRequest.of(startPage, size);
    }
}
