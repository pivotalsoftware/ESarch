package io.pivotal.catalog.commands;

public class AddProductToCatalogCommand {

    private final String id;
    private final String name;

    public AddProductToCatalogCommand(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AddProductToCatalogCommand{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
