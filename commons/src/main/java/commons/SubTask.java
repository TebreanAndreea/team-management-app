package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long stId;
    private String title;
    private boolean done;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    /**
     * Constructor for a SubTask object.
     * @param title - title of the subtask
     * @param card - card this subtask is assigned to
     */
    public SubTask(String title, Card card) {
        this.title = title;
        this.card = card;
        this.done = false;
    }

    /**
     * Default constructor for a SubTask object.
     */
    @SuppressWarnings("unused")
    public SubTask(){

    }

    /**
     * Getter for the id.
     * @return the id of the subTask
     */
    public long getStId() {
        return stId;
    }

    /**
     * Getter for the title of the subtask.
     * @return the title of the subtask
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title.
     * @param title - the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the done boolean.
     * @return the done boolean.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Setter for the done boolean.
     * @param done - the new value of the boolean
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Getter for the card parent.
     * @return - this subtask's card parent
     */
    public Card getCard() {
        return card;
    }

    /**
     * Setter for the parent card.
     * @param card - the new parent card of the subtask
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * Equals method for comparing two subtasks.
     * @param o - the subtask compared to this object
     * @return - true if the subtasks are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return stId == subTask.stId && done == subTask.done && title.equals(subTask.title) && card.getCardId()==subTask.card.getCardId();
    }

    /**
     * Hashcode method for the subtask.
     * @return - this subtask's hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(stId, title, done, card);
    }
}
