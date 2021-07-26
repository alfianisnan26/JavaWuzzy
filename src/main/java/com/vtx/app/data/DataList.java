package com.vtx.app.data;

import com.vtx.app.SceneController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.ArrayList;

public class DataList{
    public DataFuzzy selected;
    public final StringProperty value = new SimpleStringProperty();
    public final IntegerProperty score = new SimpleIntegerProperty();
    public final ObservableList<DataFuzzy> fuzzy = FXCollections.observableArrayList();
    public final StringProperty valueProperty() {
        return value;
    }
    public final IntegerProperty scoreProperty() {
        return score;
    }
    public DataList(String value){
        this.value.set(value);
    }
    public void fuzzy(){
        fuzzy.clear();
        var fuzzyResult = FuzzySearch.extractTop(value.get(), new ArrayList<>(SceneController.categorical.keySet()), SceneController.maxResultValue, SceneController.minScoreValue);
        fuzzyResult.forEach((val)->
                fuzzy.add(new DataFuzzy(val.getString(), val.getScore()))
        );
        if(fuzzyResult.size() > 0){
            selected = fuzzy.get(0);
            score.setValue(selected.score.getValue());
        } else {
            selected = null;
            score.setValue(0);
        }
    }
}