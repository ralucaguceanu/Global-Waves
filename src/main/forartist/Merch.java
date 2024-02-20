package main.forartist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Merch {

    private String name;
    private Double price;
    private String description;

    @Override
    public String toString() {
        return String.format("%s - %.0f:\n\t%s", name, price, description);
    }

    public Merch(final String name, final Double price, final String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
