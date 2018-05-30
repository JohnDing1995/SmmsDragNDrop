package indi.ruiyangding.smmstool;

import indi.ruiyangding.smmstool.model.ImageTableData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;


public class Controller implements Initializable {


    //@FXML
    //private ProgressBar uploadProgress;


    //private List<Runnable> uploads = new ArrayList<>();
    private HistorySmmsAPI history;
    private ClearHistorySmmsAPI cleaner;

    private List<ImageInfo> imageInfo = new ArrayList<>();
    private final ObservableList<ImageTableData> tableData = FXCollections.observableArrayList();

    private Integer currIndex = 0;


    @FXML
    private TableView<ImageTableData> imagesTable;
    @FXML
    private TableColumn<ImageTableData, String> name;
    @FXML
    private TableColumn<ImageTableData, String> url;
    @FXML
    private TableColumn<ImageTableData, String> success;

    @FXML
    private TextArea imageCode;

    @FXML
    private ImageView thisImage;

    private ExecutorService executor = Executors.newCachedThreadPool();

    public Controller() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setCellValueFactory(
                new PropertyValueFactory<ImageTableData, String>("fileName")
        );
        url.setCellValueFactory(
                new PropertyValueFactory<ImageTableData, String>("url")
        );
        success.setCellValueFactory(
                new PropertyValueFactory<ImageTableData, String>("success")
        );
        name.setPrefWidth(90);
        url.setPrefWidth(240);
        url.setEditable(true);
        success.setPrefWidth(70);
        imagesTable.setEditable(false);
        imagesTable.setItems(tableData);
        imagesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                onCellSelected(newSelection.getIndex(), newSelection.getUrl());
            }
        });
    }

    @FXML
    private void fileDragDetected(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
//            String filePath = null;
//            for (File file : db.getFiles()) {
//                filePath = file.getAbsolutePath();
//                tableData.add(new ImageTableData(file.getName(), "", "uploading...", filePath, tableData.size()));
//            }
        }
    }

    @FXML
    private void fileDraggedOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);

        } else {
            event.consume();
        }
    }

    @FXML
    private void fileDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {

            for (File file : db.getFiles()) {
                //tableData.add(new ImageTableData(file.getName(), "", "uploading...", filePath));
                Task<ImageInfo> up = new Task<ImageInfo>() {
                    @Override
                    protected ImageInfo call() throws Exception {
                        UploadSmmsAPI api = new UploadSmmsAPI(file.getAbsolutePath());
                        api.sendRequest();
                        return api.getResponseInfo();
                    }
                };
                up.setOnSucceeded(t -> {
                    ImageInfo upReturn = up.getValue();
                    upReturn.id = currIndex++;
                    imageInfo.add(upReturn);
                    tableData.add(new ImageTableData(upReturn.data.filename, upReturn.data.url, upReturn.code, upReturn.data.path, upReturn.id));
                });
                //Platform.runLater();
                executor.submit(up);

            }
            event.setDropCompleted(true);

        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }

    @FXML
    private void cleanList(){
        tableData.clear();
        imageInfo.clear();
    }

   private void onCellSelected(int index, String filePath){
       ImageInfo curr = imageInfo.get(index);
       curr.data.generateImageCode();
        thisImage.setImage(new Image(filePath, true));
        thisImage.setFitWidth(thisImage.getFitHeight());
       imageCode.clear();
       imageCode.setText("Markdown:" + curr.data.markdown + "\n HTML:" + curr.data.html + "\n URL:" + curr.data.url);
   }

}
