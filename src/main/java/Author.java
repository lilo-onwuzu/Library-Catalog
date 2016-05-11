import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Author {
  private String name;
  private int id;

  public Author(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getId() {
   return id;
  }

  public static List<Author> all(){
    String sql = "SELECT id, name FROM authors";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Author.class);
    }
  }

  @Override
  public boolean equals(Object otherAuthor) {
    if (!(otherAuthor instanceof Author)) {
      return false;
    } else {
      Author newAuthor = (Author) otherAuthor;
      return this.getName().equals(newAuthor.getName()) &&
             this.getId() == newAuthor.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO authors (name) VALUES (:name);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the author_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Author find(int id) {
    String sql = "SELECT * FROM authors WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Author author = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Author.class);
      return author;
    }
  }

  public void update(String update) {
    String sql = "UPDATE authors SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void delete() {
    String sql = "DELETE FROM authors WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  // addTitle() inserts author/title relationship matrix using author_id and title_id integers into the authors_titles join table DB
  public void addTitle(Title myTitle) {
    // author_id and title_id are the two fields in the authors_titles join table
    // :author_id and :title_id are the placeholders for the incoming values
    String sql = "INSERT INTO authors_titles (author_id, title_id) VALUES (:author_id, :title_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        // the "Title myTitle" argument in addTitle() would have been already added to the list of titles when it was created/instantiated from the Title class and given a id by the DB primary key. This id will be returned from myTitle.getId()
        // addParameter("placeholder", value)
        .addParameter("title_id", myTitle.getId())
        // Author author or "this" is the subject that we are adding an title to
        .addParameter("author_id", this.getId())
        .executeUpdate();
    }
  }

  // addTitles() makes a join table of relationships between authors and titles/books using author id and title id. To get all the titles that have a relationship with one author, we would need to sift through all the rows in the join table to see where the author_id is called, export all the title id's attached to those rows into a list<integer> of title id's and then sift through the titles table DB to collect the names titles with those ids and adds them to an arraylist
  public List<Title> getTitles() {
    String joinQuery = "SELECT title_id FROM authors_titles WHERE author_id=:author_id";
    try(Connection con = DB.sql2o.open()) {
      List<Integer> titleIds = con.createQuery(joinQuery)
        .addParameter("author_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Title> titleList = new ArrayList<Title>();

      for (Integer titleId : titleIds) {
        String taskQuery = "SELECT * FROM titles WHERE id=:title_id";
        Title author_title = con.createQuery(taskQuery)
          .addParameter("title_id", titleId)
          .executeAndFetchFirst(Title.class);
        titleList.add(author_title);
      }
      return titleList;
    }
  }

}
