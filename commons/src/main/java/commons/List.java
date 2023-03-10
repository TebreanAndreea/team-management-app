package commons;

import java.util.ArrayList;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class List {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long listId;

    private String title;
    @OneToMany
    private ArrayList<Card> cards;

    @ManyToOne
    private Board board;

    /**
     * Constructs empty tables at database initialisation.
     */
    @SuppressWarnings("unused")
    private List() {
        // for object mapper
    }

    /**
     * Creates a new list.
     * @param title the title of the list.
     * @param board the board containing the list.
     */
    public List(String title, Board board) {
        this.title = title;
        this.board = board;
        this.cards = new ArrayList<>();
    }


    /**
     * Getter for the id.
     * @return the id of the list object.
     */
    public long getListId() {
        return this.listId;
    }

    /**
     * Getter for the title.
     * @return title of the list.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for the title.
     * @param title the title to be set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the ArrayList of cards.
     * @return the ArrayList of cards.
     */
    public ArrayList<Card> getCards() {
        return this.cards;
    }

    /**
     * Getter for the board.
     * @return the board containing the list.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Compares the list to another object.
     * @param o the object being compared with.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List)) {
            return false;
        }
        List list = (List) o;
        return listId == list.listId && Objects.equals(title, list.title) && Objects.equals(cards, list.cards) && Objects.equals(board, list.board);
    }

    /**
     * Creates a hash code for the list.
     */
    @Override
    public int hashCode() {
        return Objects.hash(listId, title, cards, board);
    }
    

}