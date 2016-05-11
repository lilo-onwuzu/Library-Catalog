import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Title {
  private String name;
  // this id for title will not be assigned in the construct but will be returned as this.id when we save() or insert the object instance into the DB. Until then it will have a value of "null"
  private int id;

  public Title(String name) {
    // the property/attribute/instance variable (this.name) of the Title object instance or "this" will be assigned to the argument String name once constructed
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getId() {
   return id;
  }

  // getcopies() method for copies/titles one to many relationship
  public List<Copy> getCopies() {
    String sql = "SELECT * FROM copies where title_id=:titleId";
    try (Connection con = DB.sql2o.open()) {
      return con.createQuery(sql)
        .addParameter("titleId", this.id)
        .executeAndFetch(Copy.class);
    }
  }

  // all() will collect the static list of all the rows or objects in the DB. The DB class is defined in DB.java file and references the library DB
  public static List<Title> all(){
    String sql = "SELECT * FROM titles";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Title.class);
    }
  }

  @Override
  // .equals() methods to compare both the names and ids of two Title objects and return a boolean if they are the same
  public boolean equals(Object otherTitle) {
    if (!(otherTitle instanceof Title)) {
      return false;
    } else {
      Title newTitle = (Title) otherTitle;
      // should return true if the string names and integer ids are equal
      return this.getName().equals(newTitle.getName()) &&
             this.getId() == newTitle.getId();
    }
  }

  // save() will insert rows of title objects (id, name) to the titles table
  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO titles (name) VALUES (:name);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the title_id. The title object instance id is now set/assigned
      this.id = (int) con.createQuery(sql, true)
        // the value to be saved is the name of the title object that is being saved (this.name)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  // find(id) uses an integer id in the argument to select a row or a title object instance from the titles table and return the object. Because the title's id was set using the primary key field of the titles table, they should still match if we are correctly cleaning up or DBs after use
  public static Title find(int id) {
    String sql = "SELECT * FROM titles WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Title title = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Title.class);
      return title;
    }
  }

  // update() finds the row or object instance with id=this.id (title's id) and resets its name to String update
  public void update(String update) {
    String sql = "UPDATE titles SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  // addAuthor() inserts title/author relationship matrix using author_id and title_id integers into the authors_titles join table DB
  public void addAuthor(Author myAuthor) {
    // author_id and title_id are the two fields in the authors_titles join table
    // :author_id and :title_id are the placeholders for the incoming values
    String sql = "INSERT INTO authors_titles (author_id, title_id) VALUES (:author_id, :title_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        // the "Author myAuthor" argument in addAuthor() would have been already added to the list of authors when it was created/instantiated from the Author class and given a id by the DB primary key. This id will be returned from myAuthor.getId()
        // addParameter("placeholder", value)
        .addParameter("author_id", myAuthor.getId())
        // Title title or "this" is the subject that we are adding an author to
        .addParameter("title_id", this.getId())
        .executeUpdate();
    }
  }

  // addAuthors() makes a join table of relationships between author and books using author id and title id. To get all the authors that have a relationship with one book, we would need to sift through all the rows in the join table to see where the title_id is called, export all the author id's attached to those rows into a list<integer> of author id's and then sift through the authors table DB to collect the names authors with those ids and adds them to an arraylist
  public List<Author> getAuthors() {
    String joinQuery = "SELECT author_id FROM authors_titles WHERE title_id=:title_id";
    try (Connection con = DB.sql2o.open()) {
      List<Integer> authorIds = con.createQuery(joinQuery)
        .addParameter("title_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Author> authorList = new ArrayList<Author>();

      for (Integer authorId : authorIds) {
        String taskQuery = "SELECT * FROM authors WHERE id=:author_id";
          Author title_author = con.createQuery(taskQuery)
            .addParameter("author_id", authorId)
            .executeAndFetchFirst(Author.class);
            authorList.add(title_author);
      }
      return authorList;
    }
  }

  // Title myTitle.delete() for instance finds the row where id equals this.id OR myTitle.id and deletes it
  public void delete() {
    // delete the title from titles table
    String deleteQuery = "DELETE FROM titles WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    // then delete the title's row in the join table as well
    String joinDeleteQuery = "DELETE FROM authors_titles WHERE title_id=:title_id";
      con.createQuery(joinDeleteQuery)
        .addParameter("title_id", this.getId())
        .executeUpdate();
    }
  }

}
