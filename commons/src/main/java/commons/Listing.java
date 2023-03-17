package commons;

import java.util.*;

import javax.persistence.*;


@Entity
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long listId;
    private String title;
    @OneToMany(
        mappedBy = "list",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Card> cards;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    /**
     * Constructs empty tables at database initialisation.
     */
    @SuppressWarnings("unused")
    public Listing() {
        // for object mapper
    }

    /**
     * Creates a new list.
     * @param title the title of the list.
     * @param board the board containing the list.
     */
    public Listing(String title, Board board) {
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
     * Getter for the Set of cards.
     * @return the Set of cards.
     */
    public List<Card> getCards() {
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
        if (this == o) return true;
        if (!(o instanceof Listing)) return false;
        Listing listing = (Listing) o;
        return listId == listing.listId && title.equals(listing.title) && cards.equals(listing.cards) && board.equals(listing.board);
    }

    /**
     * Creates a hash code for the list.
     */
    @Override
    public int hashCode() {
        return Objects.hash(listId, title, cards, board);
    }
    

}