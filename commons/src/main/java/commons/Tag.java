package commons;

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
    @ManyToMany
    @JoinTable(
        name = "tagged_cards",
        joinColumns = @JoinColumn(name = "card_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Card> cards;

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
     * Equals method for comparing two tags.
     * @param o - the tag compared to this object
     * @return - true if the tags are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tags = (Tag) o;
        return tagId == tags.tagId && title.equals(tags.title) && cards.equals(tags.cards);
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
