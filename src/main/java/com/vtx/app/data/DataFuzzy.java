package com.vtx.app.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataFuzzy{
    public final StringProperty value = new SimpleStringProperty();
    public final IntegerProperty score = new SimpleIntegerProperty();
    public final StringProperty valueProperty() {
        return value;
    }
    public final IntegerProperty scoreProperty() {
        return score;
    }
    DataFuzzy(String value, Integer score){
        this.value.set(value);
        this.score.set(score);
    }
}
