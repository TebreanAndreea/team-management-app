package commons;

//import java.lang.reflect.Array;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
//import java.util.Set;

public class Card {


    private long cardId;
    private String description;
    private String name;
    private Date dueDate;
    @OneToMany
    private ArrayList<String> tags;
    private ArrayList<SubTasks> miniTasks;
    private boolean complete;

    /**
     * Constructor for the card class.
     * @param description - description of the card
     * @param name - Name of the card
     * @param dueDate - The due date of the card
     * @param tags - List with tags which will make searching for the card easier
     * @param miniTasks - List of smaller simple subtasks of the global goal
     */
    public Card(String description, String name, Date dueDate, ArrayList<String> tags, ArrayList<SubTasks> miniTasks) {
        this.description = description;
        this.name = name;
        this.dueDate = dueDate;
        this.tags = tags;
        this.miniTasks = miniTasks;
    }

    public Card() {

    }

    /**
     * Getter for the id.
     * @return the id of the card
     */
    public long getCardId() {
        return cardId;
    }

    /**
     * Getter for the description.
     * @return the description of the card
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description.
     * @param description - the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the name.
     * @return the name of the card
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name.
     * @param name - the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the due date.
     * @return the due date of the card
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Setter for the due date.
     * @param dueDate - the new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Getter for the tags.
     * @return the tags of the card
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Alters the tags list with a new one.
     * @param tags the new tags list
     */
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * Getter for the mini tasks.
     * @return the mini tasks of the card
     */
    public ArrayList<SubTasks> getMiniTasks() {
        return miniTasks;
    }

    /**
     * Alters the mini tasks list with a new one.
     * @param miniTasks - the new array list
     */
    public void setMiniTasks(ArrayList<SubTasks> miniTasks) {
        this.miniTasks = miniTasks;
    }

    /**
     * Getter for the completion.
     * @return whether the task is complete
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Alters the status of the task.
     * @param complete - the new status
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /**
     * Equals method for the Card class.
     * @param o - the object with which we check for equality
     * @return - a boolean based on the outcome
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardId == card.cardId && complete == card.complete && Objects.equals(description, card.description) && Objects.equals(name, card.name) && Objects.equals(dueDate, card.dueDate) && Objects.equals(tags, card.tags) && Objects.equals(miniTasks, card.miniTasks);
    }

    /**
     * A hashcode value of the card.
     * @return - the new generated hash code of the card
     */
    @Override
    public int hashCode() {
        return Objects.hash(cardId, description, name, dueDate, tags, miniTasks, complete);
    }
}
