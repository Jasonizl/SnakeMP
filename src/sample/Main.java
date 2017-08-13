package sample;

import game.Snake;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public enum state {MAINMENU, INGAME}
    private Stage primaryStage = null;
    private Stage gameStage = null;
    private Snake game;

    // UI Components
    private Canvas canvas;
    private Button btnSend;
    private TextArea consoleInput;


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

        // Layout
        BorderPane border = new BorderPane();
        VBox vbox = new VBox(25);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setPadding(new Insets(100, 0, 0,0));

        // Buttons
        Button btnHost = new Button("Start game");
        btnHost.setOnAction(event -> {
            createGameStage();
            startStage.close();
        });

        Button btnConnect = new Button("Connect to game");
        btnConnect.setOnAction(event -> {
            // TODO: connect to other clients
        });

        vbox.getChildren().addAll(btnHost, btnConnect);
        vbox.setPrefSize(Constants.menuWidth, Constants.menuHeight);


        border.setCenter(vbox);


        root.getChildren().add(border);
        startStage.setScene(s);
    }

    /**
     * Creates the gameclient, including all components.
     */
    public void createGameStage() {
        Stage gameStage = new Stage();
        Group root = new Group();
        Scene s = new Scene(root, Constants.gameWindowWidth, Constants.gameWindowHeight);
        BorderPane layout = new BorderPane();
        game = new Snake(0);

        // put things onto layout (center and right)
        layout.setRight(createChatComponents());
        layout.setCenter(createGameComponents());

        root.getChildren().add(layout);

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
                        case P: if(!game.isHalted) game.isHalted = true; break;
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
        chatFlowPane.setPadding(new Insets(10, 0, 0, 0));

        // actual components
        TextArea console = new TextArea();
        console.setPrefSize(320, 645);
        console.setWrapText(true);
        console.setEditable(false);
        console.setFocusTraversable(false);
        console.setFont(Constants.consoleFont);

        consoleInput = new TextArea();
        consoleInput.setPrefSize(220, 50);
        consoleInput.setWrapText(true);
        consoleInput.setFocusTraversable(false);
        consoleInput.setFont(Constants.consoleFont);
        consoleInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    btnSend.fire();
                }
            }
        });

        btnSend = new Button("send");
        btnSend.setFont(Constants.consoleFont);
        btnSend.setFocusTraversable(false);
        btnSend.setPrefSize(95, 50);
        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                consoleInput.clear();
                game.isHalted = false;
            }
        });

        chatFlowPane.setHgap(5);
        chatFlowPane.setVgap(5);
        chatFlowPane.getChildren().addAll(console, consoleInput, btnSend);

        return chatFlowPane;
    }

    /**
     *
     * @return FlowPane holding all game related components. (Canvas)
     */
    private FlowPane createGameComponents() {
        FlowPane gameFlowPane = new FlowPane();
        gameFlowPane.setPadding(new Insets(10, 10, 0, 10));
        this.canvas = new Canvas(Constants.canvasWidth, Constants.canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        game.gc = gc;
        //gc.setFill(Color.BLACK);
        //gc.fillRect(0,0, 700, 700);
        //drawGrid(gc);

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
                    if(!canvas.isFocused() && !game.isHalted)
                       canvas.requestFocus();

                    if(!game.gameover)
                        game.update();
                    game.drawGame();
                }
                else if((lastTime == 0 || now - lastTime > menuUpdateRate)) {
                    lastTime = now;
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
