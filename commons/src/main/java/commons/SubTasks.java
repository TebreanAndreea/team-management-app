package commons;

public class SubTasks {
    private long stId;
    private String title;
    private boolean done;

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
     * @param title the new title
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
     * @param done the new value of the boolean
     */
    public void setDone(boolean done) {
        this.done = done;
    }


}
