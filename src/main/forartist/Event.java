package main.forartist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Event {

    private String name;
    private String date;
    private String description;

    public Event(final String name, final String date, final String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s - %s:\n\t%s", name, date, description);
    }
}
