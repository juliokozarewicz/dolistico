package juliokozarewicz.categories.application.command;

public interface CategoriesGetCommand {

    Integer sizePagination();
    Integer pageNumber();
    String categoryName();

}