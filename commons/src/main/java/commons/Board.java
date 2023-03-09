package commons;

import java.util.ArrayList;
import java.util.Objects;

public class Board {

    private long boardId;

    private String title;

    private ArrayList<List> lists;

    private String accessKey;

    private String password;

    public Board(String title, ArrayList<List> lists, String accessKey, String password) {
        this.title = title;
        this.lists = lists;
        this.accessKey = accessKey;
        this.password = password;
    }

    public Board() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<List> getLists() {
        return lists;
    }

    public void setLists(ArrayList<List> lists) {
        this.lists = lists;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;
        Board board = (Board) o;
        return boardId == board.boardId && title.equals(board.title) && lists.equals(board.lists) && accessKey.equals(board.accessKey) && Objects.equals(password, board.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, title, lists, accessKey, password);
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardId=" + boardId +
                ", title='" + title + '\'' +
                ", lists=" + lists +
                ", access_key='" + accessKey + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
