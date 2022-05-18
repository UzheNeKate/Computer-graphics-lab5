package com.example.lab5.view;

import com.example.lab5.Segmentation.LiangBarski;
import com.example.lab5.Segmentation.PolygonSegmentation;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class View {
    TextField tfBeginX, tfBeginY, tfEndX, tfEndY;
    TextField tfMinX, tfMinY, tfMaxX, tfMaxY;
    TextField tfPolygonMinX, tfPolygonMinY, tfPolygonMaxX, tfPolygonMaxY, tfPolygonPoints;
    GridPane pane;

    public void start(Stage stage) {
        stage.setTitle("Segmentation");
        Group root = new Group();
        pane = new GridPane();
        pane.addRow(0, lineMenuPane());
        pane.addRow(1, drawCanvas(null, null, null));
        pane.addRow(0, polygonMenuPane());
        pane.addRow(1, drawCanvas(null, null, null));

        root.getChildren().add(pane);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private Pane lineMenuPane() {
        var pane = new GridPane();
        pane.setPadding(new Insets(15, 12, 15, 12));
        var textBegin = new Text("Start Point (X, Y)");
        tfBeginX = new TextField();
        tfBeginX.setPromptText("X");
        tfBeginY = new TextField();
        tfBeginY.setPromptText("Y");
        var textEnd = new Text("End Point (X, Y)");
        tfEndX = new TextField();
        tfEndX.setPromptText("X");
        tfEndY = new TextField();
        tfEndY.setPromptText("Y");
        var textMin = new Text("Min Point (X, Y)");
        tfMinX = new TextField();
        tfMinX.setPromptText("X");
        tfMinY = new TextField();
        tfMinY.setPromptText("Y");
        var textMax = new Text("Max Point (X, Y)");
        tfMaxX = new TextField();
        tfMaxX.setPromptText("X");
        tfMaxY = new TextField();
        tfMaxY.setPromptText("Y");
        pane.addRow(0, new Text("Line"));
        pane.addRow(2, textBegin, tfBeginX, tfBeginY);
        pane.addRow(3, textEnd, tfEndX, tfEndY);
        pane.addRow(4, textMin, tfMinX, tfMinY);
        pane.addRow(5, textMax, tfMaxX, tfMaxY);
        var bt = new Button("Draw");
        bt.setOnAction((e) -> segmentLine());
        pane.addRow(6, bt);
        return pane;
    }

    private void segmentLine() {
        var beginXStr = tfBeginX.getText();
        var beginYStr = tfBeginY.getText();
        var endXStr = tfEndX.getText();
        var endYStr = tfEndY.getText();
        var maxXStr = tfMaxX.getText();
        var maxYStr = tfMaxY.getText();
        var minXStr = tfMinX.getText();
        var minYStr = tfMinY.getText();
        Point2D begin, end, max, min;
        try {
            begin = new Point2D(Integer.parseInt(beginXStr), Integer.parseInt(beginYStr));
            end = new Point2D(Integer.parseInt(endXStr), Integer.parseInt(endYStr));
            min = new Point2D(Integer.parseInt(minXStr), Integer.parseInt(minYStr));
            max = new Point2D(Integer.parseInt(maxXStr), Integer.parseInt(maxYStr));
        } catch (NumberFormatException e) {
            return;
        }
        var lb = new LiangBarski(min, max, begin, end);
        lb.execute();
        pane.add(drawCanvas(new XYChart.Series[]{lineSeries(begin, end)},
                new XYChart.Series[]{lineSeries(lb.getInPoint(), lb.getOutPoint())}, rectSeries(min, max)), 0, 1);
    }

    private Node drawCanvas(XYChart.Series<Number, Number>[] figSeries,
                            XYChart.Series<Number, Number>[] segmSeries, XYChart.Series<Number, Number>[] rectSeries) {
        final NumberAxis x = new NumberAxis(-20, 20, 1);
        final NumberAxis y = new NumberAxis(-20, 20, 1);

        x.setAutoRanging(false);
        y.setAutoRanging(false);

        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(x, y);
        lineChart.setPrefSize(600, 600);
        lineChart.setMaxSize(600, 600);
        lineChart.setMinSize(600, 600);

        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(false);
        lineChart.setAlternativeColumnFillVisible(false);
        lineChart.setAlternativeRowFillVisible(false);

        if (rectSeries == null) {
            return lineChart;
        }
        for (var rectSeriesSegm : rectSeries) {
            lineChart.getData().add(rectSeriesSegm);
            setColor(rectSeriesSegm, Color.RED);
        }

        if (figSeries == null) {
            return lineChart;
        }
        for (var series : figSeries) {
            lineChart.getData().add(series);
            setColor(series, Color.BLUE);
        }

        if (segmSeries == null) {
            return lineChart;
        }
        for (var series : segmSeries) {
            lineChart.getData().add(series);
            setColor(series, Color.YELLOW);
        }


        return lineChart;
    }

    private void setColor(XYChart.Series<Number, Number> chart, Color color) {
        Node line = chart.getNode().lookup(".chart-series-line");
        String rgb = String.format("%d, %d, %d", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
        line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");
    }

    private XYChart.Series<Number, Number> lineSeries(Point2D begin, Point2D end) {
        var lineSeries = new XYChart.Series<Number, Number>();
        var beginData = new XYChart.Data<Number, Number>(begin.getX(), begin.getY());
        var endData = new XYChart.Data<Number, Number>(end.getX(), end.getY());
        lineSeries.getData().add(beginData);
        lineSeries.getData().add(endData);

        return lineSeries;
    }

    private XYChart.Series<Number, Number>[] rectSeries(Point2D min, Point2D max) {
        var firstLSeries = new XYChart.Series<Number, Number>();
        firstLSeries.getData().add(new XYChart.Data<>(min.getX(), min.getY()));
        firstLSeries.getData().add(new XYChart.Data<>(min.getX(), max.getY()));
        firstLSeries.getData().add(new XYChart.Data<>(max.getX(), max.getY()));
        var secondLSeries = new XYChart.Series<Number, Number>();
        secondLSeries.getData().add(new XYChart.Data<>(min.getX(), min.getY()));
        secondLSeries.getData().add(new XYChart.Data<>(max.getX(), min.getY()));
        secondLSeries.getData().add(new XYChart.Data<>(max.getX(), max.getY()));

        return List.of(firstLSeries, secondLSeries).toArray(XYChart.Series[]::new);
    }

    private XYChart.Series<Number, Number>[] polygonSeries(Point2D[] points) {
        var seriesList = new ArrayList<XYChart.Series<Number, Number>>();
        for (int i = 0; i < points.length; i++) {
            var series = new XYChart.Series<Number, Number>();
            series.getData().add(new XYChart.Data<>(points[i].getX(), points[i].getY()));
            var next = (i == points.length - 1) ? 0 : i + 1;
            series.getData().add(new XYChart.Data<>(points[next].getX(), points[next].getY()));
            seriesList.add(series);
        }
        return seriesList.toArray(XYChart.Series[]::new);
    }

    private Pane polygonMenuPane() {
        var pane = new GridPane();
        pane.setPadding(new Insets(15, 12, 15, 12));
        var textMin = new Text("Min Rect Point (X, Y)");
        tfPolygonMinX = new TextField();
        tfPolygonMinX.setPromptText("X");
        tfPolygonMinY = new TextField();
        tfPolygonMinY.setPromptText("Y");
        var textMax = new Text("Max Rect Point (X, Y)");
        tfPolygonMaxX = new TextField();
        tfPolygonMaxX.setPromptText("X");
        tfPolygonMaxY = new TextField();
        tfPolygonMaxY.setPromptText("Y");
        var textPoints = new Text("Polygon points (X, Y)");
        tfPolygonPoints = new TextField();
        tfPolygonPoints.setPromptText("X Y");

        pane.addRow(0, new Text("Polygon"));
        pane.addRow(2, textMin, tfPolygonMinX, tfPolygonMinY);
        pane.addRow(3, textMax, tfPolygonMaxX, tfPolygonMaxY);
        pane.addRow(4, textPoints, tfPolygonPoints);
        var bt = new Button("Draw");
        bt.setOnAction((e) -> segmentPolygon());
        pane.addRow(5, bt);
        return pane;
    }

    private void segmentPolygon() {
        var minXStr = tfPolygonMinX.getText();
        var minYStr = tfPolygonMinY.getText();
        var maxXStr = tfPolygonMaxX.getText();
        var maxYStr = tfPolygonMaxY.getText();
        var pointsStr = tfPolygonPoints.getText().split(",");

        Point2D minRect, maxRect;
        var polygon = new ArrayList<Point2D>();
        try {
            minRect = new Point2D(Integer.parseInt(minXStr), Integer.parseInt(minYStr));
            maxRect = new Point2D(Integer.parseInt(maxXStr), Integer.parseInt(maxYStr));
            for (var pointStr : pointsStr) {
                var coords = pointStr.split(" ");
                polygon.add(new Point2D(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
            }
        } catch (NumberFormatException e) {
            return;
        }
        Point2D[] points = polygon.toArray(Point2D[]::new);
        var segmented = new PolygonSegmentation(minRect, maxRect, points);
        pane.add(drawCanvas(polygonSeries(points), polygonSeries(segmented.segment()), rectSeries(minRect, maxRect)), 1, 1);
    }
}