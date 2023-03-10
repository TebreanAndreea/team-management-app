package commons;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;

@Entity
public class Tags {
    @Id
    private long tagId;
    private String title;
    @ManyToMany
    private ArrayList<Card> cards;

    /**
     * Constructor for the Tags class.
     * @param title - the title to the class
     * @param cards - the cards which have the tag in them
     */
    public Tags(String title, ArrayList<Card> cards) {
        this.title = title;
        this.cards = cards;
    }

    /**
     * Default constructor.
     */
    public Tags() {

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
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the cards.
     * @return the list of cards
     */
    public ArrayList<Card> getCards() {
        return cards;
    }
}
