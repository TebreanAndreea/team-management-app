package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tagId;
    private String title;
    private String color;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Card> cards = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    /**
     * Constructor for the Tags class.
     * @param title - the title to the class
     */
    public Tag(String title) {
        this.title = title;
        this.cards = new ArrayList<>();
    }

    /**
     * Default constructor.
     */
    @SuppressWarnings("unused")
    public Tag() {

    }

    /**
     * Getter for the color code of a tag.
     *
     * @return the color of a tag
     */
    public String getColor() {
        return color;
    }

    /**
     * Setting the color code for a tag.
     *
     * @param color (code) the color of the card.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Constructor for the tag which sets the title and board.
     * @param title the title of the tag
     * @param board the board of the tag
     */
    public Tag(String title, Board board){
        this.title = title;
        this.board = board;
    }

    /**
     * Setter for the tag id.
     *
     * @param id the new id
     */
    public void setTagId(long id) {
        this.tagId = id;
    }

    /**
     * Setter for the board.
     * @param board the board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Getter for the id.
     * @return the tag id
     */
    public long getTagId() {
        return tagId;
    }

    /**
     * Getter for the title of the tag.
     * @return the title of the tag
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of the tag.
     * @param title - title of the tag item
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the cards.
     * @return the list of cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Setter for list of cards attributed to this tag.
     * @param cards - Card items that have this tag assigned
     */
    public void setCards(List<Card> cards){this.cards = cards;}

    /**
     * Method that removes the tag to be deleted from all cards.
     */
    @PreRemove
    public void removeTagFromCards(){
        for(Card card : cards){
            card.getTags().remove(this);
        }
    }

    /**
     * Equals method for comparing two tags.
     * @param o - the tag compared to this object
     * @return - true if the tags are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tags = (Tag) o;
        return tagId == tags.tagId && title.equals(tags.title);
        //cards.equals(tags.cards);
    }

    /**
     * Hashcode method for the tag.
     * @return - this tag's hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagId, title, cards);
    }

}
