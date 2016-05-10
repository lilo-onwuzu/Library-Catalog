import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;

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
  public void update_updateTaskDescriptionInDatabase_true() {
    Title testTitle = new Title("title");
    testTitle.save();
    testTitle.update("other title");
    assertEquals("other title", Title.find(testTitle.getId()).getName());
  }

  @Test
  public void delete_deletesTaskInDatabase_true() {
    Title testTitle = new Title("title");
    testTitle.save();
    int id = testTitle.getId();
    testTitle.delete();
    assertEquals(Title.all().size(), 0);
  }
}
