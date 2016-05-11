import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;
import java.util.ArrayList;

public class TitleTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Title_InstantiatesWithString_true() {
    Title testTitle = new Title("title");
    assertEquals("title",testTitle.getName());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Title.all().size());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAreTheSame_true() {
    Title firstTitle = new Title("title1");
    Title secondTitle = new Title("title1");
    assertTrue(firstTitle.equals(secondTitle));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Title testTitle = new Title("title");
    testTitle.save();
    assertTrue(Title.all().get(0).equals(testTitle));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Title testTitle = new Title("title");
    testTitle.save();
    Title savedTitle = Title.all().get(0);
    assertEquals(testTitle.getId(), savedTitle.getId());
  }

  @Test
  public void find_findTitleInDatabase_true() {
    Title testTitle = new Title("title");
    testTitle.save();
    Title savedTitle = Title.all().get(0);
    assertEquals(savedTitle, Title.find(testTitle.getId()));
  }

  @Test
  public void update_updateTitleDescriptionInDatabase_true() {
    Title testTitle = new Title("title");
    testTitle.save();
    testTitle.update("other title");
    assertEquals("other title", Title.find(testTitle.getId()).getName());
  }

  @Test
  public void addAuthor_addsAuthorToTitle_true() {
    Title myTitle = new Title("Boy Meets World");
    // adds Book to list of books
    myTitle.save();
    Author myAuthor = new Author("James Blake");
    // adds Author to list of authors
    myAuthor.save();
    // create relationship between author and book
    myTitle.addAuthor(myAuthor);
    Author savedAuthor = myTitle.getAuthors().get(0);
    assertTrue(myAuthor.equals(savedAuthor));
  }

  @Test
  public void getAuthor_getsAuthorForATitle_true() {
    Title myBook = new Title("Boy meets World");
    myBook.save();
    Author myAuthor = new Author("James Blake");
    myAuthor.save();
    myBook.addAuthor(myAuthor);
    List savedAuthors = myBook.getAuthors();
    assertEquals(1, savedAuthors.size());
  }

  // AUTHOR AND TITLE DELETION
  @Test
  public void delete_deletesAllTitlesAndAuthorsAssociations() {
    Author myAuthor = new Author("JK Rowling");
    myAuthor.save();
    Title myTitle = new Title("Harry Potter");
    myTitle.save();
    myTitle.addAuthor(myAuthor);
    myTitle.delete();
    assertEquals(0, myAuthor.getTitles().size());
  }

}
