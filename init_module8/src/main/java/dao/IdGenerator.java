package dao;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    /*
        With frequent restart and heavy object creation overlap is possible. Especially when used in unit tests.
     */
    private static final AtomicLong src = new AtomicLong(new Date().getTime());

    public long next() {
        return src.incrementAndGet();
    }
}
