package dao;

public enum Namespace {
    TICKET("ticket"), EVENT("event"), USER("user");

    private final String namespace;

    Namespace(String ns) {
        this.namespace = ns;
    }

    public String ns() {
        return namespace;
    }

    public String prefixed() {
        return namespace + ":";
    }

    public String supplementedWith(long id) {
        return prefixed() + id;
    }
}
