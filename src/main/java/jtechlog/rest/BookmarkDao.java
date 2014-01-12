package jtechlog.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarkDao {

    private static BookmarkDao bookmarkDao;

    private List<Bookmark> bookmarks = new ArrayList<>();

    private BookmarkDao() {
    }

    public static BookmarkDao getBookmarkDao() {
        if (bookmarkDao == null) {
            bookmarkDao = new BookmarkDao();
        }
        return bookmarkDao;
    }

    public List<Bookmark> listBookmarks() {
        return Collections.unmodifiableList(bookmarks);
    }

    public Bookmark findBookmark(long id) {
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getId() == id) {
                return bookmark;
            }
        }
        return null;
    }

    public synchronized Bookmark createBookmark(Bookmark bookmark) {
        bookmark.setId((long) bookmarks.size() + 1);
        bookmarks.add(bookmark);
        return bookmark;
    }

    public void clear() {
        bookmarks.clear();
    }
}
