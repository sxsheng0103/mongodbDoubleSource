package com.pangu.crawler.transfer.utils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;

public class MainFrame extends Application {

    private Stage primary;

    private Stage stage;

    ImageView iv;

    HBox hBox;

    VideoRecode videoRecord;

    double screenX_start;
    double screenY_start;
    double screenX_end;
    double screenY_end;
    double sceneX =100;
    double sceneY =100;

    String path="D://screenRecoding";//存储位置


    @Override
    public void start(Stage primaryStage) throws Exception {
        primary = primaryStage;

        AnchorPane root = new AnchorPane();

        Button bu = new Button("选择录屏区域");
        bu.setPrefWidth(180);
        Button begin_recode_btn = new Button("开始录屏");
        begin_recode_btn.setPrefWidth(180);
        begin_recode_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                beginRecodeVideo();
            }
        });


        Button stop_recode_btn = new Button("结束录屏");
        stop_recode_btn.setPrefWidth(180);
        stop_recode_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                stopRecodeVideo();
            }
        });

        Button open_dir_btn = new Button("打开存放位置");
        open_dir_btn.setPrefWidth(180);
        Button choose_dir_btn = new Button("选择存储位置");
        choose_dir_btn.setPrefWidth(180);
        final Label choose_dir_label = new Label(path);
        choose_dir_label.setPrefWidth(500);
        choose_dir_label.setPrefHeight(50);
        choose_dir_label.setBorder(new Border(new BorderStroke( Paint.valueOf("#292929"),BorderStrokeStyle.SOLID,new CornerRadii(5.0),new BorderWidths(2))));

        choose_dir_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File file = directoryChooser.showDialog(stage);
                try{
                    path = file.getPath();//选择的文件夹路径
                }catch (Exception e){
                    path="D://screenRecoding";
                }

                choose_dir_label.setText("  "+path);
                System.out.println(path);
            }
        });

        root.getChildren().add(bu);
        AnchorPane.setTopAnchor(bu,100.0);
        AnchorPane.setLeftAnchor(bu,50.0);

        root.getChildren().add(choose_dir_btn);
        AnchorPane.setTopAnchor(choose_dir_btn,200.0);
        AnchorPane.setLeftAnchor(choose_dir_btn,50.0);

        root.getChildren().add(choose_dir_label);
        AnchorPane.setTopAnchor(choose_dir_label,200.0);
        AnchorPane.setLeftAnchor(choose_dir_label,250.0);

        root.getChildren().add(begin_recode_btn);
        AnchorPane.setTopAnchor(begin_recode_btn,300.0);
        AnchorPane.setLeftAnchor(begin_recode_btn,50.0);


        root.getChildren().add(stop_recode_btn);
        AnchorPane.setTopAnchor(stop_recode_btn,300.0);
        AnchorPane.setLeftAnchor(stop_recode_btn,250.0);

        Scene scene = new Scene(root);
        primaryStage.setTitle("java录屏工具");
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(1000);
        primaryStage.show();

        bu.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                show();
            }
        });

        //快捷键
        KeyCombination key = KeyCombination.valueOf("ctrl+alt+p");
        Mnemonic mc = new Mnemonic(bu, key);
        scene.addMnemonic(mc);
    }

    public void show(){
        primary.setIconified(true);

        stage = new Stage();



        AnchorPane root = new AnchorPane();
        root.setBackground(Background.EMPTY);

        Scene scene = new Scene(root);
        scene.setFill(Paint.valueOf("#ffffee11"));

        stage.setFullScreenExitHint("");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
        drag(root);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.ESCAPE){
                    stage.close();
                    primary.setIconified(false);
                }
            }
        });
    }

    /**
     * 绘制带边框的矩形
     */
    public void drag(final AnchorPane an){
        an.setOnMousePressed(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                an.getChildren().clear();
                hBox = new HBox();
                hBox.setBackground(null);
                hBox.setBorder(new Border(new BorderStroke( Paint.valueOf("#CD3700"),BorderStrokeStyle.SOLID,null,new BorderWidths(2))));
                screenX_start = event.getScreenX();
                screenY_start = event.getScreenY();
                an.getChildren().add(hBox);

                AnchorPane.setTopAnchor(hBox,screenY_start);
                AnchorPane.setLeftAnchor(hBox,screenX_start);

                System.out.println(event.getScreenX());
            }
        });

        an.setOnDragDetected(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                an.startFullDrag();
            }
        });

        an.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {

            public void handle(MouseDragEvent event) {
                Label label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setPrefWidth(320);
                label.setPrefHeight(40);
                an.getChildren().add(label);
                AnchorPane.setTopAnchor(label,screenY_start-40);
                AnchorPane.setLeftAnchor(label,screenX_start);

                sceneX = event.getScreenX();
                sceneY = event.getScreenY();
                //System.out.println(sceneX);

                double width = sceneX-screenX_start;
                double height = sceneY-screenY_start;
                hBox.setPrefWidth(width);
                hBox.setPrefHeight(height);

                label.setTextFill(Paint.valueOf("#CD3700"));
                label.setStyle("-fx-background-color: dimgrey");
                label.setText("宽度："+width+"  高度："+height);
            }
        });

        an.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {

            public void handle(MouseDragEvent event) {
                screenX_end = event.getScreenX();
                screenY_end = event.getScreenY();
                Button complete_btn = new Button("确认");
                hBox.getChildren().add(complete_btn);
                hBox.setAlignment(Pos.BOTTOM_RIGHT);
                complete_btn.setOnAction(new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent event) {
                        //返回到主界面
                        stage.close();
                        primary.setIconified(false);
                    }
                });

            }
        });

    }

    public void beginRecodeVideo(){

        if(videoRecord!=null){videoRecord.stop();}
        int width = (int) (sceneX-screenX_start);
        int height = (int)(sceneY-screenY_start);
//        Rectangle rectangle = new Rectangle((int)screenX_start,(int)screenY_start,width, height); // 截屏的大小
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rectangle = new Rectangle((int)screenX_start,(int)screenY_start,screen.width-3, screen.height-3); //
        //最小化主窗口
        primary.setIconified(true);
        //调用录屏API
        videoRecord = new VideoRecode(path, true,rectangle);
        videoRecord.start();

    }

    public void stopRecodeVideo(){
        videoRecord.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}