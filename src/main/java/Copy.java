import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Copy {
  private int id;
  private int copy;
  private int title_id;

  public Copy(int copy, int title_id) {
    this.copy = copy;
    this.title_id = title_id;
  }

  public int getCopy() {
    return copy;
  }

  public int getId() {
   return id;
  }

  // every book copy should be able to return it's title
  // Copy and Title have a one to many relationships
  // In many to many relationships, you will need add*() get*() methods in BOTH classes as well as a join table
  // In one to many relationships, you only need a get*() method in the "one" or subject class and a add*() method in the "many" or object class
  // In title and copies O2M relationship, title is the subject class and copies is the object class. Therefore we will need a getCopies() method in the Title class and an addTitle() method in the Copies class
  // In this particular case, we do not need an addTitles() method because we are requiring the Title object to be already made and id to be attached when the copy object is made
  // you need classes when you want to add a changing attribute/property and copy that template/form across multiple elements without having to recreate it
  // you could also use a class when you want to perform a function to a group of elements at once
  public int getTitleId() {
    return title_id;
  }

  public static List<Copy> all(){
    String sql = "SELECT * FROM copies";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Copy.class);
    }
  }

  @Override
  public boolean equals(Object otherCopy) {
    if (!(otherCopy instanceof Copy)) {
      return false;
    } else {
      Copy newCopy = (Copy) otherCopy;
      return this.getCopy() == newCopy.getCopy() &&
             this.getId() == newCopy.getId() &&
             this.getTitleId() == newCopy.getTitleId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO copies (copy, title_id) VALUES (:copy, :title_id);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the copy_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("copy", this.copy)
        .addParameter("title_id", title_id)
        .executeUpdate()
        .getKey();
    }
  }

  public static Copy find(int id) {
    String sql = "SELECT * FROM copies WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Copy copy = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Copy.class);
      return copy;
    }
  }

  public void updateID(int newId) {
    String sql = "UPDATE copies SET copy=:copy WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("copy", newId)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void updateTitleID(int titleID) {
    String sql = "UPDATE copies SET title_id=:title_id WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("title_id", titleID)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void addPatron(Patron myPatron) {
    String sql = "INSERT INTO copies_patrons (copy_id, patron_id) VALUES (:copy_id, :patron_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("copy_id", this.getId())
        .addParameter("patron_id", myPatron.getId())
        .executeUpdate();
    }
  }

  public List<Patron> getPatrons() {
    String joinQuery = "SELECT patron_id FROM copies_patrons WHERE copy_id=:copy_id";
    try(Connection con = DB.sql2o.open()) {
      List<Integer> patronIds = con.createQuery(joinQuery)
        .addParameter("copy_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Patron> patronList = new ArrayList<Patron>();

      for (Integer patronId : patronIds) {
        String taskQuery = "SELECT * FROM patrons WHERE id=:patron_id";
        Patron copy_patron = con.createQuery(taskQuery)
          .addParameter("patron_id", patronId)
          .executeAndFetchFirst(Patron.class);
        patronList.add(copy_patron);
      }
      return patronList;
    }
  }

  public void delete() {
    String deleteQuery = "DELETE FROM copies WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    String joinDeleteQuery = "DELETE FROM copies_patrons WHERE copy_id=:copy_id";
      con.createQuery(joinDeleteQuery)
        .addParameter("copy_id", this.getId())
        .executeUpdate();
    }
  }

}
