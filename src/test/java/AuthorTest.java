import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class AuthorTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Author_InstantiatesWithString_true() {
    Author testAuthor = new Author("author");
    assertEquals("author",testAuthor.getName());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Author.all().size());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAreTheSame_true() {
    Author firstAuthor = new Author("author1");
    Author secondAuthor = new Author("author1");
    assertTrue(firstAuthor.equals(secondAuthor));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Author testAuthor = new Author("author");
    testAuthor.save();
    assertTrue(Author.all().get(0).equals(testAuthor));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Author testAuthor = new Author("author");
    testAuthor.save();
    Author savedAuthor = Author.all().get(0);
    assertEquals(testAuthor.getId(), savedAuthor.getId());
  }

  @Test
  public void find_findAuthorInDatabase_true() {
    Author testAuthor = new Author("author");
    testAuthor.save();
    Author savedAuthor = Author.all().get(0);
    assertEquals(savedAuthor, Author.find(testAuthor.getId()));
  }

  @Test
  public void update_updateTaskDescriptionInDatabase_true() {
    Author testAuthor = new Author("author");
    testAuthor.save();
    testAuthor.update("other author");
    assertEquals("other author", Author.find(testAuthor.getId()).getName());
  }

  @Test
  public void delete_deletesTaskInDatabase_true() {
    Author testAuthor = new Author("author");
    testAuthor.save();
    int id = testAuthor.getId();
    testAuthor.delete();
    assertEquals(Author.all().size(), 0);
  }

  @Test
  public void addTitle_addsTitleToAuthor_true() {
    Author myAuthor = new Author("James Blake");
    myAuthor.save();
    Title myTitle = new Title("Boy Meets World");
    myTitle.save();
    myAuthor.addTitle(myTitle);
    List savedTitles = myAuthor.getTitles();
    assertEquals(1, savedTitles.size());
  }

  @Test
  public void getTitles_getsTitlesForAnAuthor_true() {
    Author myAuthor = new Author("James Blake");
    myAuthor.save();
    Title myTitle = new Title("Boy meets World");
    myTitle.save();
    myAuthor.addTitle(myTitle);
    List savedTitles = myAuthor.getTitles();
    assertEquals(1, savedTitles.size());
  }
}
