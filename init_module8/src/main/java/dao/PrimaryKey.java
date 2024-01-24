package dao;

import com.google.common.base.Preconditions;

public class PrimaryKey {

    private final Namespace ns;
    private final long id;

    PrimaryKey(Namespace ns, long id) {
        this.ns = Preconditions.checkNotNull(ns, "Namespace cannot be null");
        Preconditions.checkArgument(id > 0, "Id must be positive number");
        this.id = id;
    }

    public static PrimaryKey userKey(long id) {
        return new PrimaryKey(Namespace.USER, id);
    }

    public static PrimaryKey eventKey(long id) {
        return new PrimaryKey(Namespace.EVENT, id);
    }

    public static PrimaryKey ticketKey(long id) {
        return new PrimaryKey(Namespace.TICKET, id);
    }

    public String id() {
        return ns.supplementedWith(id);
    }



}
