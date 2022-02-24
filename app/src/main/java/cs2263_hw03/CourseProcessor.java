package cs2263_hw03;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages the course objects
 * @author Tyson Cox
 */
public class CourseProcessor extends Application {
    private List<Course> courses;
    private ObservableList<Course> selectedCourses;
    private TableView<Course> courseTable;

    private TextField fldNum;
    private TextField fldName;
    private TextField fldCred;
    private ChoiceBox<String> departmentsDrop;

    public CourseProcessor() {
        courses = new ArrayList<>();
        selectedCourses = FXCollections.observableArrayList();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Course Editor");

        HBox centersplit = new HBox();
        Scene scene = new Scene(centersplit);

        GridPane editorControls = new GridPane();
        editorControls.setHgap(2);
        editorControls.setVgap(10);
        editorControls.setPadding(new Insets(10, 10, 0, 10));

        departmentsDrop = new ChoiceBox<>();
        departmentsDrop.getItems().addAll(Course.departments);
        Button btnShowDept = new Button("Display (dept.)");
        Button btnShowAll = new Button("Display (all)");
        editorControls.addRow(0, departmentsDrop, btnShowDept, btnShowAll);

        btnShowDept.setOnAction(value -> displayCourses(departmentsDrop.getValue()));
        btnShowAll.setOnAction(value -> displayCourses(null));

        Label lblNum = new Label("Course number");
        Label lblName = new Label("Course name");
        Label lblCred = new Label("Course credits");
        fldNum = new TextField();
        fldName = new TextField();
        fldCred = new TextField();
        fldNum.setOnKeyPressed(value -> { if (value.getCode() == KeyCode.ENTER) enterCourse(); });
        fldName.setOnKeyPressed(value -> { if (value.getCode() == KeyCode.ENTER) enterCourse(); });
        fldCred.setOnKeyPressed(value -> { if (value.getCode() == KeyCode.ENTER) enterCourse(); });

        editorControls.addColumn(0, lblNum, fldNum, lblName, fldName, lblCred, fldCred);

        Button btnQuit = new Button("Quit");
        Button btnLoad = new Button("Load");
        Button btnSave = new Button("Save");

        editorControls.addRow(7, btnQuit, btnLoad, btnSave);

        btnSave.setOnAction(value -> serialize());
        btnLoad.setOnAction(value -> readFromFile());


        Dialog<ButtonType> dialExit = new Dialog<>();
        dialExit.setTitle("Quit?");
        dialExit.setContentText("Exit the application?");
        dialExit.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Yes", ButtonBar.ButtonData.YES),
                new ButtonType("No", ButtonBar.ButtonData.NO)
        );

        btnQuit.setOnAction(value -> {
            Optional<ButtonType> resp = dialExit.showAndWait();
            if (resp.isPresent() && resp.get().getButtonData() == ButtonBar.ButtonData.YES) {
                Platform.exit();
            }
        });


        ScrollPane scroll = new ScrollPane();
        centersplit.getChildren().addAll(editorControls, scroll);

        courseTable = new TableView<Course>();
        courseTable.setMinWidth(100);
        courseTable.setItems(selectedCourses);
        TableColumn<Course, String> colDept = new TableColumn<>("Department");
        TableColumn<Course, Integer> colNum = new TableColumn<>("Number");
        TableColumn<Course, String> colName = new TableColumn<>("Name");
        TableColumn<Course, Integer> colCred = new TableColumn<>("Credits");

        colDept.setCellValueFactory(new PropertyValueFactory("department"));
        colNum.setCellValueFactory(new PropertyValueFactory("number"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        colCred.setCellValueFactory(new PropertyValueFactory("credits"));

        courseTable.getColumns().setAll(colDept, colNum, colName, colCred);

        scroll.setContent(courseTable);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Pop up an error message to the user
     * @param message The text to display
     */
    private void popupError(String message) {
        Dialog<ButtonType> dialErr = new Dialog<>();
        dialErr.setTitle("Error");
        dialErr.setContentText(message);
        dialErr.getDialogPane().getButtonTypes().addAll(new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE));
        dialErr.showAndWait();

    }

    /**
     * Enter a course to the list from the data supplied
     */
    private void enterCourse() {
        String chosendept = departmentsDrop.getValue();
        String name = fldName.getText();
        String num = fldNum.getText();
        String cred = fldCred.getText();

        int numint = 0;
        int credint = 0;

        String errmsg = null;

        try {
            numint = Integer.parseInt(num);
        }
        catch (Exception e) {
            errmsg = "Course number must be numeric";
        }

        try {
            credint = Integer.parseInt(cred);
        }
        catch (Exception e) {
            errmsg = "Course credits must be numeric";
        }


        if (chosendept == null || chosendept.isEmpty()) errmsg = "No department selected";
        if (name == null || name.isEmpty()) errmsg = "No course name entered";
        if (num == null || num.isEmpty()) errmsg = "No course number entered";
        if (cred == null || cred.isEmpty()) errmsg = "No course credit value entered";

        if (errmsg != null) {
            popupError(errmsg);
            return;
        }

        courses.add(new Course(name, chosendept, numint, credint));
    }

    /**
     * Display the selected courses to the table view
     * @param dept the department to display, or null for all courses
     */
    private void displayCourses(String dept) {
        selectedCourses.clear();

        if (dept == null) {
            selectedCourses.addAll(courses);
        }
        else {
            for(Course c : courses) {
                if (c.getDepartment().equals(dept)) {
                    selectedCourses.add(c);
                }
            }
        }
    }

    /**
     * Serialize the courses to a file
     * @return true on success, false on error
     */
    private boolean serialize() {
        String json = new Gson().toJson(courses);
        try {
            FileWriter f = new FileWriter("courses.json");
            f.write(json);
            f.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Read in a list of courses from a file
     * @return true on success, false on error
     */
    private boolean readFromFile() {
        try {
            FileReader reader = new FileReader("courses.json");
            List<Course> tempcourses = new Gson().fromJson(reader, new TypeToken<List<Course>>() {}.getType());
            reader.close();

            courses = tempcourses;

            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
