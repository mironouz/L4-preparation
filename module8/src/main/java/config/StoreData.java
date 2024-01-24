package config;

import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dao.PrimaryKey;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StoreData {

    private static final Logger LOG  = LoggerFactory.getLogger(StoreData.class);

    private final Reader input;

    private final SimpleDateFormat df;

    public StoreData(Reader input) {
        this(input, "yyyy-mm-dd");
    }

    public StoreData(Reader input, String df) {
        this.input = input;
        this.df = new SimpleDateFormat(df);
    }

    public static StoreData fromPath(String path) {
        return new StoreData(fileReader(path));
    }

    public static StoreData fromString(String value) {
        return new StoreData(new StringReader(value));
    }

    public Map<String, Object> load() {
        Map<String, Object> result = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(input)) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {

                String type = values[0];
                switch(type) {
                    case "user" -> {
                        User user = buildUserFrom(values);
                        result.put(PrimaryKey.userKey(user.getId()).id(), user);
                    }
                    case "event" -> {
                        Event event = buildEventFrom(values);
                        result.put(PrimaryKey.eventKey(event.getId()).id(), event);
                    }
                    case "ticket" -> {
                        Ticket ticket = buildTicketFrom(result, values);
                        result.put(PrimaryKey.ticketKey(ticket.getId()).id(), ticket);
                    }
                    default -> {
                        throw new IllegalArgumentException("Unknown type value %s".formatted(type));
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Loaded successfully {} entities.", result.size());
        return result;
    }

    private static User buildUserFrom(String[] values) {
        // type = values[0], id = values[1], name = values[2], email = values[3]

        Preconditions.checkNotNull(values, "values cannot be null");
        Preconditions.checkArgument(values.length >= 4, "Not enough values (%s) to build user from", values.length );

        long userId = Long.parseLong(values[1]);

        return UserFactory.create(userId, values[2], values[3]);
    }

    private static Ticket buildTicketFrom(Map<String, Object> result, String[] values) {
        // type = values[0], id = values[1], category = values[2], userId = values[3], eventId = values[4], place = values [5]

        Preconditions.checkNotNull(values, "values cannot be null");
        Preconditions.checkArgument(values.length >= 6, "Not enough values (%s) to build ticket from", values.length );

        long ticketId = Long.parseLong(values[1]);
        Ticket.Category category = Ticket.Category.valueOf(values[2]);
        User user = (User) result.get(PrimaryKey.userKey(Long.parseLong(values[3])).id());
        Event event = (Event) result.get(PrimaryKey.eventKey(Long.parseLong(values[4])).id());
        int place = Integer.parseInt(values[5]);

        if (user == null || event == null) {
            throw new IllegalArgumentException("Unsatisfied dependency for ticket %d (%s, %s)".formatted(ticketId, user, event));
        }
        return TicketFactory.create(ticketId, event, user, category, place);

    }

    private Event buildEventFrom(String[] values) {
        // type = values[0], id = values[1], title = values[2], date = values[3]

        Preconditions.checkNotNull(values, "values cannot be null");
        Preconditions.checkArgument(values.length >= 4, "Not enough values (%s) to build event from", values.length);

        long eventId = Long.parseLong(values[1]);
        Date eventDate = parseDate(values[3]);

        return EventFactory.create(eventId, values[2], eventDate);
    }

    private Date parseDate(String value) {
        try {
            return df.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileReader fileReader(String path) {
        try {
            return new FileReader(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
