package application;

import game.Snake;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.Constants;
import util.Player;

public class Main extends Application {

    public enum state {MAINMENU, INGAME}
    private Stage primaryStage = null;
    private Stage gameStage = null;
    private Snake game;
    private Player player;

    // UI Components
    private Canvas canvas;
    private Button btnSend;
    private TextArea consoleInput;
    private TextArea console;
    private ListView userList;


    @Override
    public void start(Stage primaryStage) throws Exception{
        initMainApp(primaryStage);

        primaryStage.setTitle(Constants.TITLE);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        this.primaryStage = primaryStage;
        primaryStage.show();
    }


    /**
     * initializes the main menu. Used to start the game client.
     * @param startStage
     *
     */
    private void initMainApp(Stage startStage) {
        Group root = new Group();
        Scene s = new Scene(root, Constants.menuWidth, Constants.menuHeight);
        player = new Player();

        // Layout
        GridPane gridpane = new GridPane();
        gridpane.getColumnConstraints().add(new ColumnConstraints(25));
        gridpane.getColumnConstraints().add(new ColumnConstraints(120));
        gridpane.getColumnConstraints().add(new ColumnConstraints(120));
        gridpane.getRowConstraints().add(new RowConstraints(25));
        gridpane.setVgap(10);

        Label title = new Label(Constants.TITLE);
        title.setFont(new Font("Courier New", 26));
        title.autosize();
        GridPane.setRowIndex(title, 1);
        GridPane.setColumnIndex(title, 1);

        Separator hSep = new Separator();
        GridPane.setRowIndex(hSep, 2);
        GridPane.setColumnIndex(hSep, 1);

        // Buttons
        Button btnHost = new Button("Start game");
        btnHost.setOnAction(event -> {
            createGameStage();
            startStage.close();
        });
        btnHost.setMaxWidth(Double.MAX_VALUE);
        GridPane.setRowIndex(btnHost, 3);
        GridPane.setColumnIndex(btnHost, 1);

        Button btnConnect = new Button("Connect to game");
        btnConnect.setOnAction(event -> {
            // TODO: connect to other clients
        });
        btnConnect.setMaxWidth(Double.MAX_VALUE);
        GridPane.setRowIndex(btnConnect, 4);
        GridPane.setColumnIndex(btnConnect, 1);

        Label options = new Label("Options");
        options.setFont(new Font("Courier New", 22));
        options.autosize();
        GridPane.setRowIndex(options, 5);
        GridPane.setColumnIndex(options, 1);

        Separator hSep2 = new Separator();
        GridPane.setRowIndex(hSep2, 6);
        GridPane.setColumnIndex(hSep2, 1);

        Label descrUsername = new Label("Username:");
        descrUsername.setFont(Constants.consoleFont);
        descrUsername.autosize();
        GridPane.setRowIndex(descrUsername, 7);
        GridPane.setColumnIndex(descrUsername, 1);

        TextField txfUsername = new TextField(player.getUsername());
        txfUsername.setFont(Constants.consoleFont);
        txfUsername.autosize();
        txfUsername.setOnAction(event -> {
            if(txfUsername.getText().length() > 12) {
                txfUsername.setText(txfUsername.getText().substring(0,12));
            }
            player.setUsername(txfUsername.getText());
        });
        GridPane.setRowIndex(txfUsername, 8);
        GridPane.setColumnIndex(txfUsername, 1);

        Label descrColor = new Label("Snake color:");
        descrColor.setFont(Constants.consoleFont);
        descrColor.autosize();
        GridPane.setRowIndex(descrColor, 9);
        GridPane.setColumnIndex(descrColor, 1);

        ColorPicker cp1 = new ColorPicker(player.getColor());
        cp1.setOnAction(event -> {
            Color c = cp1.getValue();
            if (c.getBrightness() == 1.0) {
                c = Color.color(Math.random(), Math.random(), Math.random(), 1);
                cp1.setValue(c);
            }
            player.setColor(c);
        });
        GridPane.setRowIndex(cp1, 10);
        GridPane.setColumnIndex(cp1, 1);

        // TODO: write textfields for ip and port


        gridpane.getChildren().addAll(title, hSep, btnHost, btnConnect, options, hSep2, descrUsername, txfUsername, descrColor, cp1);

        root.getChildren().add(gridpane);
        startStage.setScene(s);
    }

    /**
     * Creates the gameclient, including all components.
     */
    public void createGameStage() {
        Stage gameStage = new Stage();
        Group root = new Group();
        Scene s = new Scene(root, Constants.gameWindowWidth, Constants.gameWindowHeight);
        game = new Snake(0);
        GridPane mainGridpane = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints(700);
        ColumnConstraints c2 = new ColumnConstraints(450);
        RowConstraints r1 = new RowConstraints(720);

        mainGridpane.getColumnConstraints().addAll(c1, c2);
        mainGridpane.getRowConstraints().addAll(r1);
        mainGridpane.setHgap(10);

        // put things onto layout (center and right)
        mainGridpane.add(createChatComponents(),1, 0);
        mainGridpane.add(createGameComponents(), 0, 0);

        root.getChildren().add(mainGridpane);

        s.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!game.gameover)
                    switch (event.getCode()) {
                        case UP:  if(!game.up && !game.down) game.setDirection(0); break;
                        case LEFT: if(!game.left && !game.right) game.setDirection(1); break;
                        case RIGHT: if(!game.right && !game.left) game.setDirection(2); break;
                        case DOWN: if(!game.down && !game.up) game.setDirection(3); break;
                        case ENTER: if(!game.isStarted) game.isStarted = true; break;
                    }
            }
        });

        gameStage.setScene(s);
        gameStage.setTitle(Constants.TITLE);
        gameStage.setResizable(false);
        gameStage.sizeToScene();
        this.gameStage = gameStage;
        gameStage.show();

        // "loop"
        initGameloop();
    }

    /**
     *
     * @return FlowPane which holds the chat, chat_input and the send button.
     */
    private FlowPane createChatComponents() {
        FlowPane chatFlowPane = new FlowPane();
        chatFlowPane.setPadding(new Insets(10, 0, 0, 10));

        // actual components
        userList = new ListView();
        userList.setPrefSize(320, 100);
        userList.setEditable(false);
        userList.setFixedCellSize(25);
        userList.setFocusTraversable(false);

        console = new TextArea();
        console.setPrefSize(320, 545);
        console.setWrapText(true);
        console.setEditable(false);
        console.setFocusTraversable(false);
        console.setFont(Constants.consoleFont);

        consoleInput = new TextArea();
        consoleInput.setPrefSize(220, 45);
        consoleInput.setWrapText(true);
        consoleInput.setFocusTraversable(false);
        consoleInput.setFont(Constants.consoleFont);
        consoleInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    btnSend.fire();
                    event.consume();
                }
            }
        });

        btnSend = new Button("send");
        btnSend.setFont(Constants.consoleFont);
        btnSend.setFocusTraversable(false);
        btnSend.setPrefSize(95, 45);
        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                console.appendText(player.getUsername() + ":" + consoleInput.getText() + "\n");
                consoleInput.clear();
                canvas.requestFocus();
            }
        });

        chatFlowPane.setHgap(5);
        chatFlowPane.setVgap(5);
        chatFlowPane.getChildren().addAll(userList, console, consoleInput, btnSend);

        return chatFlowPane;
    }

    /**
     *
     * @return FlowPane holding all game related components. (Canvas)
     */
    private FlowPane createGameComponents() {
        FlowPane gameFlowPane = new FlowPane();
        gameFlowPane.setPadding(new Insets(10, 0, 0, 10));
        this.canvas = new Canvas(Constants.canvasWidth, Constants.canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        game.gc = gc;

        gameFlowPane.getChildren().add(canvas);
        canvas.requestFocus();
        return gameFlowPane;
    }

    /**
     * initializes the logic of how the looping is handled for the game
     */
    private void initGameloop() {
        AnimationTimer timer = new AnimationTimer() {

            long lastTime = 0;
            double gameUpdateRate = Math.pow(10, 9)/20;// ~0.35 seconds
            double menuUpdateRate = Math.pow(10, 9); // 1 second

            @Override
            public void handle(long now) {
                if(game.isStarted && (lastTime == 0 || now - lastTime > gameUpdateRate)) {
                    lastTime = now;

                    //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    //Date date = new Date();
                    //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
                    if(!canvas.isFocused() && !consoleInput.isFocused())
                       canvas.requestFocus();

                    if(!game.gameover)
                        game.update();
                    game.drawGame();
                }
                else if((lastTime == 0 || now - lastTime > menuUpdateRate)) {
                    lastTime = now;

                    if(!canvas.isFocused() && !consoleInput.isFocused())
                        canvas.requestFocus();

                    game.drawMenu();
                }

            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
