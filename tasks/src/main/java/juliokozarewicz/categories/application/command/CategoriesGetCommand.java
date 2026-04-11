package juliokozarewicz.categories.application.command;

public interface CategoriesGetCommand {

    Integer pageSize();
    Integer pageNumber();
    String categoryName();

}