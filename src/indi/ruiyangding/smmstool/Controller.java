package indi.ruiyangding.smmstool;

import indi.ruiyangding.smmstool.model.ImageTableData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import javax.activation.MimetypesFileTypeMap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class Controller implements Initializable {

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
    private TextField urlText;
    @FXML
    private TextField htmlCode;
    @FXML
    private TextField markdownCode;

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
                onCellSelected(newSelection.getIndex(), newSelection.getUrl(), newSelection.getSuccess());
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
            long totalFileSize = 0;
            for (File file : db.getFiles()) {
                int fileIndex = currIndex;
                if (!isLegalFileSize(file)) {
                    tableData.add(new ImageTableData(file.getName(), "Single file size over 4 M", "Error", file.getPath(), -1));
                } else {
                    totalFileSize += file.length();
                    if (totalFileSize >= 8000000) {
                        tableData.add(new ImageTableData(file.getName(), "Total file size over 8 M", "Error", file.getPath(), -1));
                    } else {

                        tableData.add(new ImageTableData(file.getName(), "", "uploading", file.getPath(), fileIndex));
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
                            //upReturn.id = currIndex++;
                            imageInfo.add(upReturn);
                            String fn = file.getName();
                            tableData.set(findIdByFileName(fn), new ImageTableData(
                                    upReturn.data.filename, upReturn.data.url, upReturn.code, upReturn.data.path, findIdByFileName(fn)
                            ));
                            //tableData.add(new ImageTableData(upReturn.data.filename, upReturn.data.url, upReturn.code, upReturn.data.path, upReturn.id));
                        });
                        executor.submit(up);

                    }
                    currIndex++;

                }




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

    private void onCellSelected(int index, String filePath, String status) {
        if (status.equals("success")) {
            ImageInfo curr = imageInfo.get(index);
            curr.data.generateImageCode();
            thisImage.setImage(new Image(filePath, true));
            thisImage.setFitWidth(thisImage.getFitHeight());
            htmlCode.clear();
            markdownCode.clear();
            urlText.clear();
            markdownCode.setText(curr.data.markdown);
            urlText.setText(curr.data.url);
            htmlCode.setText(curr.data.html);
        } else {
            htmlCode.clear();
            markdownCode.clear();
            urlText.clear();
        }
   }

    private int findIdByFileName(String name) {
        List<ImageTableData> result = tableData.stream()
                .filter(item -> item.getFileName().equals(name))
                .collect(Collectors.toList());
        if (result.size() == 0)
            System.out.println("error: size 0");
        return result.size() == 1 ? result.get(0).getIndex() : -1;
   }

    private boolean isLegalFileSize(File file) {
        if (file.exists() && file.isFile() && file.length() <= 4194304) {
            return true;
        }
        return false;
    }

}
