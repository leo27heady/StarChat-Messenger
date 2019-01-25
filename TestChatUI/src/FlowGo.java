import javafx.application.Application;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class FlowGo extends Application {
    private final ObservableList<Label> labels = FXCollections.observableArrayList(
            createLabel("oranges"),
            createLabel("apples"),
            createLabel("pears"),
            createLabel("peaches"),
            createLabel("bananas")
    );

    @Override public void start(Stage stage) {
        ScrollPane pane = new ScrollPane();
        FlowPane flow = createFlow();

        VBox layout = new VBox(10);
        layout.getChildren().setAll(
                flow,
                createActionButtons(flow)
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 10px; -fx-background-color: cornsilk;");

        stage.setScene(new Scene(layout));
        stage.show();
    }

    private HBox createActionButtons(final FlowPane flow) {
        Button sort = new Button("Sort");
        sort.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                ObservableList<Node> workingCollection = FXCollections.observableArrayList(
                        flow.getChildren()
                );

                Collections.sort(workingCollection, new NodeComparator());

                flow.getChildren().setAll(workingCollection);
            }
        });

        Button reset = new Button("Reset");
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                flow.getChildren().setAll(labels);
            }
        });

        HBox buttonBar = new HBox(10);
        buttonBar.getChildren().addAll(
                sort,
                reset
        );
        buttonBar.setAlignment(Pos.CENTER);

        return buttonBar;
    }

    private FlowPane createFlow() {
        FlowPane flow = new FlowPane();
        flow.setHgap(10);
        flow.setVgap(10);
        flow.getChildren().setAll(labels);
        flow.setAlignment(Pos.CENTER);
        flow.setStyle("-fx-padding: 10px; -fx-background-color: lightblue; -fx-font-size: 16px;");

        return flow;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-border-width: 2; -fx-border-color: #346512;");
        label.setUserData(text);

        return label;
    }

    public static void main(String[] args) { launch(args); }

    private static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            String s1 = (String) o1.getUserData();
            String s2 = (String) o2.getUserData();

            System.out.println(s1 + " " + s2);
            System.out.println(s1 + " " + s2);

            return s1.compareTo(s2);
        }
    }
}