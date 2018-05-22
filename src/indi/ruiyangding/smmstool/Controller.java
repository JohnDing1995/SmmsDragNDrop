package indi.ruiyangding.smmstool;

import indi.ruiyangding.smmstool.model.ImageTableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import sun.jvm.hotspot.runtime.Threads;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;


public class Controller implements Initializable {


    @FXML
    private ProgressBar uploadProgress;


    //private List<Runnable> uploads = new ArrayList<>();
    private HistorySmmsAPI history;
    private ClearHistorySmmsAPI cleaner;

    private List<ImageInfo> imageInfo = new ArrayList<>();
    private final ObservableList<ImageTableData> tableData = FXCollections.observableArrayList();

    @FXML
    private TableView<ImageTableData> imagesTable;
    @FXML
    private TableColumn<ImageTableData, String> name;
    @FXML
    private TableColumn<ImageTableData, String> url;
    @FXML
    private TableColumn<ImageTableData, String> success;

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
        imagesTable.setEditable(false);
        imagesTable.setItems(tableData);
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
            event.setDropCompleted(true);
            String filePath = null;
            List<Future<ImageInfo>> imageReturns = new ArrayList<>();
            for (File file : db.getFiles()) {
                filePath = file.getAbsolutePath();
                //upload files, multi-thread
                UploadSmmsAPI up = new UploadSmmsAPI(filePath);
                ExecutorService executor = Executors.newCachedThreadPool();
                imageReturns.add(executor.submit(up));
                System.out.println("Starting upload " + up.getPath());
            }

            int index = 0;
            for(Future<ImageInfo> eachImage : imageReturns){
                index ++;
                try {
                    ImageInfo curr = eachImage.get();
                    imageInfo.add(curr);
                    uploadProgress.setProgress(index);
                    tableData.add(new ImageTableData(curr.data.filename, curr.data.url, curr.code));


                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }


}
