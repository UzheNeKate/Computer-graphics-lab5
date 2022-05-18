package com.example.lab5;

import com.example.lab5.Segmentation.LiangBarski;
import com.example.lab5.Segmentation.PolygonSegmentation;
import com.example.lab5.view.View;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

public class SegmentationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        var ps = new PolygonSegmentation(new Point2D(3,-1), new Point2D(10, 3),
                new Point2D[] {
                        new Point2D(6, -1),
                        new Point2D(3, 4),
                        new Point2D(5, 6),
                        new Point2D(9, 6),
                        new Point2D(10, 2),
                        new Point2D(6, -1)
                });
        Arrays.stream(ps.segment()).forEach(System.out::println);
        new View().start(stage);
    }


    public static void main(String[] args) {
        launch();
    }
}