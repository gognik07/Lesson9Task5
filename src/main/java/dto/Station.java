package dto;

import java.util.List;

public class Station {
    private String color;
    private String numberLine;
    private String name;
    private String[] transfers;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumberLine() {
        return numberLine;
    }

    public void setNumberLine(String numberLine) {
        this.numberLine = numberLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getTransfers() {
        return transfers;
    }

    public void setTransfers(String[] transfers) {
        this.transfers = transfers;
    }
}
