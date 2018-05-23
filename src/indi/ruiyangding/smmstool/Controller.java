package indi.ruiyangding.smmstool;

import indi.ruiyangding.smmstool.model.ImageTableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
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
    private List<Future<ImageInfo>> imageReturns = new ArrayList<>();

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
    }

    @FXML
    private void fileDragDetected(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            String filePath = null;

            for (File file : db.getFiles()) {
                filePath = file.getAbsolutePath();
                tableData.add(new ImageTableData(file.getName(), "", "uploading...", filePath));
            }
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
            event.setDropCompleted(true);
            event.consume();
            String filePath = null;

            for (File file : db.getFiles()) {
                filePath = file.getAbsolutePath();
                //tableData.add(new ImageTableData(file.getName(), "", "uploading...", filePath));
                UploadSmmsAPI up = new UploadSmmsAPI(filePath);
                imageReturns.add(executor.submit(up));
                imagesTable.refresh();
            }
            int index = -1;
            for(Future<ImageInfo> eachImage : imageReturns){
                index ++;
                try {
                    while(!eachImage.isDone());
                    ImageInfo curr = eachImage.get();
                    curr.id = index;
                    curr.data.generateImageCode();
                    imageInfo.add(curr);
                    //uploadProgress.setProgress(index);
                    tableData.get(index).setSuccess(curr.code);
                    tableData.get(index).setUrl(curr.data.url);
                    imagesTable.refresh();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("uploaded");
                    //event.consume();
                }
            }

        } else {
            event.setDropCompleted(false);
            event.consume();
        }

    }

    @FXML
    private void cleanList(){
        tableData.clear();
        imageReturns.clear();
        imageInfo.clear();
    }


}
