package dao;

import model.Event;

import java.util.Date;

public interface EventQuery {

    Iterable<Event> findByTitle(String title);

    Iterable<Event> findByDate(Date date);
}
