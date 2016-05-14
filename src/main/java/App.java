import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.List;
import java.util.ArrayList;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap model = new HashMap();
      model.put("userName", request.session().attribute("userName"));
      model.put("template", "templates/home.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/", (request, response) -> {
      HashMap model = new HashMap();
      String userName = request.queryParams("inputName");
      request.session().attribute("userName", userName);
      model.put("userName", userName);
      model.put("template", "templates/home.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/books/new", (request, response) -> {
      HashMap model = new HashMap();
      model.put("template", "templates/newBook.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/authors/new", (request, response) -> {
      HashMap model = new HashMap();
      String newBookName = request.queryParams("inputBook");
      model.put("newBookName", newBookName);
      model.put("template", "templates/newAuthor.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/books", (request, response) -> {
      HashMap model = new HashMap();
      String titleName = request.queryParams("inputHide");
      String authorName = request.queryParams("inputAuthor");
      Title title = new Title(titleName);
      title.save();
      Author author = new Author(authorName);
      author.save();
      title.addAuthor(author);
      model.put("titles", Title.all());
      model.put("template", "templates/AllBooks.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/books", (request, response) -> {
      HashMap model = new HashMap();
      model.put("titles", Title.all());
      model.put("template", "templates/AllBooks.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/books/:id", (request, response) -> {
      HashMap model = new HashMap();
      Title title = Title.find(Integer.parseInt(request.params(":id")));
      request.session().attribute("titleID", title.getId());
      model.put("title", title);
      model.put("authors", title.getAuthors());
      model.put("template", "templates/bookDetails.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/books/:id", (request, response) -> {
      HashMap model = new HashMap();
      Title title = Title.find(Integer.parseInt(request.params(":id")));
      String newAuthor = request.queryParams("inputAuthor");
      Author author = new Author(newAuthor);
      author.save();
      title.addAuthor(author);
      // String newEditAuthor = request.queryParams("editAuthor");
      // Author updateAuthor = Author.find(Integer.parseInt(request.params("hideAuthorID")));
      // updateAuthor.update(newEditAuthor);
      request.session().attribute("titleID", title.getId());
      model.put("title", title);
      model.put("authors", title.getAuthors());
      model.put("template", "templates/bookDetails.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // delete book by clicking on "delete book" link
    get("/books/:id/delete", (request, response) -> {
      HashMap model = new HashMap();
      Title title = Title.find(Integer.parseInt(request.params(":id")));
      title.delete();
      response.redirect("/books");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // add another author to book
    get("/books/:id/add", (request, response) -> {
      HashMap model = new HashMap();
      Title title = Title.find(Integer.parseInt(request.params(":id")));
      model.put("title", title);
      model.put("template", "templates/addAuthor.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // edit Author details. displays a link to delete author and a form to update Author's name that posts to "/books/:id"
    get("/books/authors/:id/edit", (request, response) -> {
      HashMap model = new HashMap();
      model.put("template", "templates/editAuthor.vtl");
      Title title = Title.find(request.session().attribute("titleID"));
      Author author = Author.find(Integer.parseInt(request.params(":id")));
      model.put("author", author);
      model.put("title", title);
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // delete author from book by clicking on the link. deletes author and redirects to "/books/:id"
    get("/books/authors/:id/delete", (request, response) -> {
      HashMap model = new HashMap();
      Author author = Author.find(Integer.parseInt(request.params(":id")));
      author.delete();
      Integer titleID = request.session().attribute("titleID");
      response.redirect("/books/" + titleID);
      String newInputAuthor = request.queryParams("inputAuthor");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/search", (request, response) -> {
      HashMap model = new HashMap();
      model.put("template", "templates/findBook.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/search/find", (request, response) -> {
      HashMap model = new HashMap();

      List<Title> foundTitles = null;
      List<Author> foundAuthors = null;
      List<Title> titles = Title.all();
      List<Author> authors = Author.all();
      String titleName = request.queryParams("titleName");
      String authorName = request.queryParams("authorName");

      for(Title title : titles) {
        // mainly use title to find book but if no match comes up then use author to find book
        if (title.getName().toLowerCase().equals(titleName.toLowerCase())) {
          foundTitles.add(title);
        } else {
            for(Author author : authors) {
              if (author.getName().toLowerCase().equals(authorName.toLowerCase())) {
                foundAuthors.add(author);
                for (Author foundAuthor : foundAuthors) {
                  ArrayList<Title> titleGroups = new ArrayList<Title>(foundAuthor.getTitles());
                   for(int x = 0; x < titleGroups.size(); x++) {
                     ArrayList<Title> newTitleGroups = new ArrayList<Title>();
                     newTitleGroups.add(titleGroups.get(x));
                     for(int y = 0; y < newTitleGroups.size(); y++) {
                       foundTitles = foundTitles.add(newTitleGroups.get(y));
                     }
                   }
                }
              }
            }
          }
      }

      model.put("foundTitles", foundTitles);
      model.put("template", "templates/searchList.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

  }
}
