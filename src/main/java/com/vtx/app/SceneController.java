package com.vtx.app;

import com.vtx.app.data.DataFuzzy;
import com.vtx.app.data.DataList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;


public class SceneController{
    public ComboBox<String> cb_score;
    public ComboBox<String> cb_export;
    public ComboBox<String> cb_base;
    public Text text_min_score;
    public Button btn_fuzzyAll;
    public Slider slider_min_score;
    public static int minScoreValue = 50;
    public static int maxResultValue = 0;
    public Text text_max_res;
    public Slider slider_max_res;
    public Text text_percentage;
    public Button btn_abort;
    public TableColumn<DataList, Integer> tc_highScore;
    public Button btn_null;
    public ComboBox<String> cb_category;
    public CheckBox check_category;
    public Button btn_base;
    public Button btn_db;
    public Label label_base;
    public Label label_db;
    public TableView<DataList> tv_base;
    public TableColumn<DataList,String> tc_base;
    public TableView<DataFuzzy> tv_match;
    public TableColumn<DataFuzzy, String> tc_matches;
    public TableColumn<DataFuzzy, Integer> tc_score;
    public Button btn_export;
    public Label label_export;
    public Button btn_from_base;
    public Button btn_to_export;
    public Button btn_import;
    public Text text_total_base;
    public Text text_total_database;
    public Button btn_fuzzy;
    public ProgressBar progressBar;
    public CheckBox check_score;

    String base;
    String data;
    String export;
    boolean fuzzyAborted = false;
    public static HashMap<String, String> categorical;

    public void abortFuzzy() {
        fuzzyAborted = true;
        btn_abort.setDisable(true);
    }

    public void fromBase() throws IOException {
        MC.iswait();
        label_export.setText(label_base.getText());
        cb_export.setDisable(false);
        btn_to_export.setDisable(false);
        check_score.setDisable(false);
        check_category.setDisable(false);
        ObservableList<String> as_cb_base = FXCollections.observableArrayList(Excelness.readExcelColumnName(base));
        export = base;
        cb_export.setItems(as_cb_base);
        cb_score.setItems(as_cb_base);
        cb_category.setItems(as_cb_base);
        MC.unwait();
    }

    public void giveNull() {
        var item = tv_base.getSelectionModel().getSelectedItem();
        item.selected = null;
        item.score.setValue(0);
        tv_match.getSelectionModel().clearSelection();
    }

    public void selectExportFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Export Destination File");
        File f = fileChooser.showOpenDialog(App.mainStage);
        if(f!=null){
            MC.iswait();
            String export = f.getPath();
            label_export.setText(f.getName());
            var getName = f.getName().split("\\.");
            boolean state = !getName[getName.length-1].equals("xlsx");
            cb_export.setDisable(state);
            btn_to_export.setDisable(state);
            check_score.setDisable(state);
            check_category.setDisable(state);
            ObservableList<String> as_cb_base = FXCollections.observableArrayList(Excelness.readExcelColumnName(export));
            this.export = export;
            cb_export.setItems(as_cb_base);
            cb_score.setItems(as_cb_base);
            cb_category.setItems(as_cb_base);
            MC.unwait();
        }
    }

    public void checkWithScore() {
        cb_score.setDisable(!check_score.isSelected());
    }

    public void checkWithCategory(){
        cb_category.setDisable(!check_category.isSelected());
    }

    public void exportValue() {
        MC.iswait();
        Thread ath = new Thread(()->{
            try {
                Excelness.writeExcel(export, cb_export.getValue(), as_base.stream().map(e->{
                    if(e.selected == null) return "N/A";
                    else return e.selected.value.get();
                }).collect(Collectors.toList()));
                if(check_score.isSelected()) Excelness.writeExcel(export, cb_score.getValue(), as_base.stream().map(e->{
                    if(e.selected == null) return "N/A";
                    else return String.valueOf(e.selected.score.getValue());
                }).collect(Collectors.toList()));
                if(check_category.isSelected()) Excelness.writeExcel(export, cb_category.getValue(), as_base.stream().map(e->{
                    if(e.selected == null) return "N/A";
                    else return categorical.get(e.selected.value.get());
                }).collect(Collectors.toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(()->{
                MC.unwait();
                text_percentage.setText("Fuzzy Data Exported");
            });
        });
        ath.setDaemon(false);
        ath.start();
    }

    private static class MC{
        public static void iswait(){
            App.mainStage.getScene().setCursor(Cursor.WAIT);
        }
        public static void unwait(){
            App.mainStage.getScene().setCursor(Cursor.DEFAULT);
        }
    }

    public void loadBase() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Base File");
        File f = fileChooser.showOpenDialog(App.mainStage);
        if(f!=null){
            MC.iswait();
            base = f.getPath();
            label_base.setText(f.getName());
            var getName = f.getName().split("\\.");
            boolean state = getName[getName.length-1].equals("xlsx");
            cb_base.setDisable(!state);
            btn_import.setDisable(!state);
            ObservableList<String> as_cb_base = FXCollections.observableArrayList(Excelness.readExcelColumnName(base));
            cb_base.setItems(as_cb_base);
            MC.unwait();
        }

    }
    public void loadDatabase() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Database File");
        File f = fileChooser.showOpenDialog(App.mainStage);
        if(f!=null){
            MC.iswait();
            label_db.setText(f.getName());
            data = f.getPath();
            var getName = f.getName().split("\\.");
            boolean state = !getName[getName.length - 1].equals("xlsx");
            btn_fuzzy.setDisable(state);
            btn_fuzzyAll.setDisable(state);
            btn_from_base.setDisable(state);
            btn_export.setDisable(state);
            btn_null.setDisable(state);
            cb_export.setDisable(state);
            label_export.setDisable(state);
            try {
                categorical = Excelness.getDatabase(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int totalDB =categorical.size();
            int totalCategory = new HashSet<>(categorical.values()).size();
            text_total_database.setText(": " + totalDB + " in " + totalCategory);
            if(totalDB > 0){
                slider_max_res.setMin(1);
                slider_max_res.setMax(totalDB);
                maxResultValue = Math.min(totalDB, 10);
                slider_max_res.setValue(maxResultValue);
                text_max_res.setText(": " + maxResultValue);
            }
            MC.unwait();
        }

    }
    ObservableList<DataList> as_base = FXCollections.observableArrayList();
    public void importBase() {
        new Thread(() -> {
            MC.iswait();
            try {
                var value = Excelness.readExcel(base, cb_base.getValue());
                text_total_base.setText(": " + value.size());
                as_base.clear();
                as_base.addAll(value.stream().map(DataList::new).collect(Collectors.toList()));
                tv_base.setItems(as_base);
            } catch (IOException e) {
                text_total_base.setText(": ERROR");
            }
            MC.unwait();
        }).start();
    }
    @FXML
    void initialize(){
        tc_base.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        tc_highScore.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        tc_matches.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        tc_score.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        slider_min_score.valueProperty().addListener((ov, old_val, new_val) -> {
            text_min_score.setText(String.format(": %d", new_val.intValue()));
            minScoreValue = new_val.intValue();
        });
        slider_max_res.valueProperty().addListener((ov, old_val, new_val) -> {
            text_max_res.setText(String.format(": %d", new_val.intValue()));
            maxResultValue = new_val.intValue();
        });
        //tc_matches.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
    }

    @FXML
    void fuzzier(){
        int pos = tv_base.getSelectionModel().getSelectedIndex();
        if(pos < 0 || pos > as_base.size()) return;
        MC.iswait();
        as_base.get(pos).fuzzy();
        var obj = tv_base.getSelectionModel().getSelectedItem();
        if(obj.selected!=null)tv_match.getSelectionModel().select(obj.selected);
        MC.unwait();
    }

    @FXML
    void fuzzierAll() {
        progressBar.setProgress(0);
        btn_abort.setDisable(false);
        btn_fuzzy.setDisable(true);
        btn_fuzzyAll.setDisable(true);
        tv_base.setDisable(true);
        tv_match.setDisable(true);
        slider_max_res.setDisable(true);
        slider_min_score.setDisable(true);
        btn_import.setDisable(true);
        btn_to_export.setDisable(true);
        Thread th = new Thread(new Runnable() {
            int i = 0;
            int time = 0;
            final int threadCount = 4;
            final long sysStartTime = System.currentTimeMillis();

            @Override
            public void run() {
                final int segment = (int) Math.ceil(as_base.size() / (double) threadCount);
                Thread[] threads = new Thread[threadCount];
                for(int k = 0; k < threadCount ; k ++){
                    int finalK = k;
                    threads[k] = new Thread(()->{
                        for(int a = 0; a <=segment; a++) {
                            if(fuzzyAborted) break;
                            final int index = a + (segment* finalK);
                            if(index >= as_base.size()) break;
                            as_base.get(index).fuzzy();
                            i++;
                            Platform.runLater(() -> {
                                progressBar.setProgress(i / (double) as_base.size());
                                time = Math.toIntExact(((System.currentTimeMillis() - sysStartTime) / 1000));
                                text_percentage.setText(String.format("%d/%d - %ds", i + 1, as_base.size(), time));
                            });
                        }
                    });
                    threads[k].setDaemon(false);
                    threads[k].start();
                }
                for (Thread thread : threads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(()->{
                    btn_abort.setDisable(true);
                    var obj = tv_base.getSelectionModel().getSelectedItem();
                    if(obj!=null && obj.selected!=null)tv_match.getSelectionModel().select(obj.selected);
                    progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    btn_fuzzy.setDisable(false);
                    btn_fuzzyAll.setDisable(false);
                    tv_base.setDisable(false);
                    tv_match.setDisable(false);
                    slider_max_res.setDisable(false);
                    slider_min_score.setDisable(false);
                    btn_import.setDisable(false);
                    btn_to_export.setDisable(false);
                    if(fuzzyAborted) {
                        fuzzyAborted = false;
                        text_percentage.setText("Fuzzy All is Aborted (" + time + "s)");
                    }else text_percentage.setText("Fuzzy All is Finished (" + time + "s)");

                });
            }
        });
        th.setDaemon(false);
        th.start();
    }

    public void clickItemKey(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) fuzzier();
        else if(keyEvent.getCode() == KeyCode.DELETE) giveNull();
        else{
            var obj = tv_base.getSelectionModel().getSelectedItem();
            tv_match.setItems(obj.fuzzy);
            if (obj.selected != null) tv_match.getSelectionModel().select(obj.selected);
        }
    }

    @FXML
    public void clickItem(MouseEvent event)
    {
        if (event.getClickCount() == 1) {
            var obj = tv_base.getSelectionModel().getSelectedItem();
            if(obj == null) return;
            tv_match.setItems(obj.fuzzy);
            if (obj.selected != null){
                tv_match.getSelectionModel().select(obj.selected);
            }
        }else if(event.getClickCount() == 2){
            fuzzier();
        }
    }

    public void clickedOnMathces(){
        var item = tv_base.getSelectionModel().getSelectedItem();
        if(item != null){
            item.selected = tv_match.getSelectionModel().getSelectedItem();
            item.score.setValue(item.selected.score.getValue());
        }
    }
}
