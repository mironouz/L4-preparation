package model;

import java.util.Date;

public class EventFactory {

    public static Event create(long id, String title, Date date) {
        return new EventEntity(id, title, date);
    }

    public static Event createNonPersisted(String title, Date date) {
        return new EventEntity(title, date);
    }
}
