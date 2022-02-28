/**
 * MIT License
 *
 * Copyright (c) 2022 Tyson Cox
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
 * Manages the course objects and contains the ui
 * @author Tyson Cox
 */
public class CourseProcessor extends Application {
    private List<Course> courses;
    private final ObservableList<Course> selectedCourses;

    private TextField fldNum;
    private TextField fldName;
    private TextField fldCred;
    private ComboBox<String> departmentsDrop;

    public CourseProcessor() {
        courses = new ArrayList<>();
        selectedCourses = FXCollections.observableArrayList();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Course Editor");

        HBox centersplit = new HBox();
        Scene scene = new Scene(centersplit);

        GridPane editorControls = new GridPane();
        editorControls.setHgap(2);
        editorControls.setVgap(10);
        editorControls.setPadding(new Insets(10, 10, 0, 10));

        departmentsDrop = new ComboBox<>();
        departmentsDrop.getItems().addAll(Course.departmentNames);
        Button btnShowDept = new Button("Display (dept.)");
        Button btnShowAll = new Button("Display (all)");
        editorControls.addRow(0, departmentsDrop, btnShowDept, btnShowAll);

        btnShowDept.setOnAction(value -> displayCourses(Course.getDeptFromName(departmentsDrop.getValue())));
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

        Button btnEnter = new Button("Enter");
        btnEnter.setOnAction(value -> enterCourse());

        editorControls.addColumn(0, lblNum, fldNum, lblName, fldName, lblCred, fldCred, btnEnter);

        Button btnQuit = new Button("Quit");
        Button btnLoad = new Button("Load");
        Button btnSave = new Button("Save");

        editorControls.addRow(8, btnSave, btnLoad, btnQuit);

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

        TableView<Course> courseTable = new TableView<>();
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
        dialErr.getDialogPane().getButtonTypes().addAll(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
        dialErr.showAndWait();
    }

    /**
     * Enter a course to the list from the data supplied
     */
    private void enterCourse() {
        String chosendept = Course.getDeptFromName(departmentsDrop.getValue());

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


        if (chosendept == null || chosendept.isEmpty()) errmsg = "No valid department selected";
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
     */
    private void serialize() {
        String json = new Gson().toJson(courses);
        try {
            FileWriter f = new FileWriter("courses.json");
            f.write(json);
            f.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            popupError(e.getMessage());
        }
    }

    /**
     * Read in a list of courses from a file
     */
    private void readFromFile() {
        try {
            FileReader reader = new FileReader("courses.json");
            List<Course> tempcourses = new Gson().fromJson(reader, new TypeToken<List<Course>>() {}.getType());
            reader.close();

            courses = tempcourses;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            popupError(e.getMessage());
        }
    }
}
