import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.List;

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
      model.put("title", title);
      model.put("authors", title.getAuthors());
      model.put("template", "templates/bookDetails.vtl");
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

    // delete book request by clicking on link
    get("/books/:id/delete", (request, response) -> {
      HashMap model = new HashMap();
      Title title = Title.find(Integer.parseInt(request.params(":id")));
      title.delete();
      response.redirect("/books");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // delete author for a book
    get("/books/:id/authors", (request, response) -> {
      HashMap model = new HashMap();
      Title title = Title.find(Integer.parseInt(request.params(":id")));
      model.put("title", title);
      model.put("authors", title.getAuthors());
      model.put("template", "templates/AuthorList.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

  }
}
