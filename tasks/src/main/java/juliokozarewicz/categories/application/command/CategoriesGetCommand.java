package juliokozarewicz.categories.application.command;

public interface CategoriesGetCommand {

    Integer pageNumber();
    Integer pageSize();
    String categoryName();

}