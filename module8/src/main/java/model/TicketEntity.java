package model;

import com.google.common.base.Preconditions;

class TicketEntity implements Ticket {

    private long id;
    private final Event event;
    private final User user;
    private Category category;
    private int place;

    public TicketEntity(long id, Event event, User user, Category category, int place) {
        this.id = id;

        this.event = Preconditions.checkNotNull(event, "Event cannot be null");
        this.user = Preconditions.checkNotNull(user, "User cannot be null");

        this.category = category;
        this.place = place;
    }

    public TicketEntity(Event event, User user, Category category, int place) {
        this(Long.MIN_VALUE, event, user, category, place);
    }

    public TicketEntity(TicketEntity ticket) {
        this(ticket.id, ticket.event, ticket.user, ticket.category, ticket.place);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getEventId() {
        return event.getId();
    }

    @Override
    public void setEventId(long eventId) {
        throw new UnsupportedOperationException("EventId cannot be set");
        // this.eventId = eventId;
    }

    @Override
    public long getUserId() {
        return user.getId();
    }

    @Override
    public void setUserId(long userId) {
        throw new UnsupportedOperationException("UserId cannot be set");
        // this.userId = userId;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public int getPlace() {
        return place;
    }

    @Override
    public void setPlace(int place) {
        this.place = place;
    }
}
