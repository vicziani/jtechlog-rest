package jtechlog.rest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BookmarkResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(BookmarkResource.class)
            .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        super.configureClient(config);
        config.register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @Before
    public void init() {
        BookmarkDao.getBookmarkDao().clear();
    }

    @Test
    public void testListBookmarksWithXml() {
        // Given
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("http://jtechlog.blogspot.hu", "JTechLog"));
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("https://github.com/vicziani", "GitHub"));

        // When
        String xml = target("bookmarks").request().accept(MediaType.APPLICATION_XML_TYPE).get(String.class);

        // Then
        System.out.println(xml);
        assertEquals("JTechLog", evalXpathAsString(xml, "/bookmarks/bookmark[position() = 1]/title"));
    }

    private String evalXpathAsString(String xml, String xpathExpression) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            return (String) expr.evaluate(doc, XPathConstants.STRING);
        }
        catch (Exception e) {
            throw new AssertionError("Error by evaluating xpath", e);
        }
    }

    @Test
    public void testListBookmarksWithJson() throws Exception {
        // Given
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("http://jtechlog.blogspot.hu", "JTechLog"));
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("https://github.com/vicziani", "GitHub"));


        // When
        String json = target("bookmarks").request().accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);

        // Then
        System.out.println(json);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        assertEquals(2, node.size());
        assertEquals("JTechLog", node.get(0).get("title").getTextValue());
    }



    @Test
    public void testListBookmark() {
        // Given
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("http://jtechlog.blogspot.hu", "JTechLog"));
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("https://github.com/vicziani", "GitHub"));

        // When
        List<Bookmark> bookmarks = target("bookmarks").request().accept(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Bookmark>>(){});

        // Then
        assertEquals(2, bookmarks.size());
        assertEquals("JTechLog", bookmarks.get(0).getTitle());
    }

    @Test
    public void testFindBookmark() {
        // Given
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("http://jtechlog.blogspot.hu", "JTechLog"));
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("https://github.com/vicziani", "GitHub"));

        // When
        Bookmark bookmark = target("bookmarks").path("1").request().accept(MediaType.APPLICATION_JSON_TYPE).get(Bookmark.class);

        // Then
        assertEquals("JTechLog", bookmark.getTitle());
    }

    @Test
    public void testCreateBookmark() {
        // Given
        BookmarkDao.getBookmarkDao().createBookmark(createBookmark("http://jtechlog.blogspot.hu", "JTechLog"));

        // When
        Bookmark bookmark = createBookmark("https://github.com/vicziani", "GitHub");
        target("bookmarks").request().post(Entity.entity(bookmark, MediaType.APPLICATION_JSON_TYPE));

        // Then
        List<Bookmark> bookmarks = target("bookmarks").request().accept(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Bookmark>>(){});
        assertEquals("GitHub", bookmarks.get(1).getTitle());
    }

    private Bookmark createBookmark(String url, String title) {
            Bookmark bookmark = new Bookmark();
            bookmark.setUrl(url);
            bookmark.setTitle(title);
            return bookmark;
    }
}
