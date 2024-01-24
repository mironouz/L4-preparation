package dao;

import model.Ticket;

public interface TicketDao extends Dao<Ticket, PrimaryKey>, TicketQuery {
}
