package jtechlog.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/bookmarks")
public class BookmarkResource {

    private static BookmarkDao bookmarkDao = BookmarkDao.getBookmarkDao();

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Bookmark> listBookmarks() {
        return bookmarkDao.listBookmarks();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Bookmark findBookmark(@PathParam("id") long id) {
        Bookmark bookmark = bookmarkDao.findBookmark(id);
        if (bookmark == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return bookmark;
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createBookmark(Bookmark bookmark) {
        Bookmark createdBookmark = bookmarkDao.createBookmark(bookmark);
        return Response.status(Response.Status.CREATED).entity(createdBookmark).build();
    }
}