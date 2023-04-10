package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class ColorScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long schemeId;
    private String name;
    private String backgroundColor;
    private String fontColor;
    private boolean def;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    public ColorScheme(String name, String backgroundColor, String fontColor, Board board) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
        this.def = false;
        this.board = board;
    }

    public ColorScheme() {

    }

    public long getSchemeId() {
        return schemeId;
    }
    public void setSchemeId(long schemeId) {
        this.schemeId = schemeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColorScheme)) return false;
        ColorScheme colorScheme = (ColorScheme) o;
        return getSchemeId() == colorScheme.getSchemeId() && Objects.equals(getName(), colorScheme.getName()) && Objects.equals(getBackgroundColor(), colorScheme.getBackgroundColor()) && Objects.equals(getFontColor(), colorScheme.getFontColor()) && getBoard().equals(colorScheme.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSchemeId(), getName(), getBackgroundColor(), getFontColor(), getBoard());
    }
}
