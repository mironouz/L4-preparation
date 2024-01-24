package dao;

import model.Event;

public interface EventDao extends Dao<Event, PrimaryKey>, EventQuery {
}
