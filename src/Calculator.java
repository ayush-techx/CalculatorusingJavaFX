import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Calculator extends Application {
    private final TextField display = new TextField();
    private final List<Button> allButtons = new ArrayList<>();
    private VBox layout;
    private ToggleButton themeToggle;
    private boolean isDarkMode = true;

    @Override
    public void start(Stage stage) {
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setEffect(new DropShadow());
        display.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(display, Priority.NEVER);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        VBox.setVgrow(grid, Priority.ALWAYS);

        String[][] buttons = {
                {"(", ")", "←", "C"},
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
                {"x²", "√", "1/x", "%"}
        };

        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String label = buttons[row][col];
                Button btn = createStyledButton(label);
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(btn, Priority.ALWAYS);
                GridPane.setVgrow(btn, Priority.ALWAYS);
                grid.add(btn, col, row);
            }
        }

        themeToggle = new ToggleButton("☀️ Light");
        themeToggle.setOnAction(e -> toggleTheme());
        themeToggle.setFocusTraversable(false);
        HBox topBar = new HBox(themeToggle);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        layout = new VBox(10, topBar, display, grid);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        applyDarkMode(); // Start in dark mode

        Scene scene = new Scene(layout, 360, 500);
        scene.setOnKeyPressed(this::handleKeyPress);

        stage.setTitle("JavaFX Calculator");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(60, 60);
        button.setFocusTraversable(false);
        allButtons.add(button); // Store for theme switching
        button.setOnAction(e -> handleInput(text));
        return button;
    }

    private void applyDarkMode() {
        layout.setStyle("-fx-background-color: #121212;");
        display.setStyle("""
            -fx-font-size: 26px;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 10;
            -fx-background-color: #1e1e1e;
            -fx-text-fill: white;
            -fx-border-color: #555;
        """);

        for (Button button : allButtons) {
            String text = button.getText();
            String style = """
                -fx-font-size: 18px;
                -fx-background-radius: 10;
                -fx-text-fill: white;
                -fx-background-color: #333;
                -fx-cursor: hand;
            """;

            if (text.matches("[0-9.]")) style = style.replace("#333", "#1E88E5");
            else if (text.equals("=")) style = style.replace("#333", "#FF5722");
            else if (text.equals("C")) style = style.replace("#333", "#E53935");

            button.setStyle(style);
        }

        themeToggle.setText("☀️ Light");
        isDarkMode = true;
    }

    private void applyLightMode() {
        layout.setStyle("-fx-background-color: #f4f4f4;");
        display.setStyle("""
            -fx-font-size: 26px;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 10;
            -fx-background-color: white;
            -fx-text-fill: black;
            -fx-border-color: #aaa;
        """);

        for (Button button : allButtons) {
            String text = button.getText();
            String style = """
                -fx-font-size: 18px;
                -fx-background-radius: 10;
                -fx-text-fill: black;
                -fx-background-color: #ddd;
                -fx-cursor: hand;
            """;

            if (text.matches("[0-9.]")) style = style.replace("#ddd", "#90CAF9");
            else if (text.equals("=")) style = style.replace("#ddd", "#FF8A65");
            else if (text.equals("C")) style = style.replace("#ddd", "#EF9A9A");

            button.setStyle(style);
        }

        themeToggle.setText("🌙 Dark");
        isDarkMode = false;
    }

    private void toggleTheme() {
        if (isDarkMode) applyLightMode();
        else applyDarkMode();
    }

    private void handleInput(String input) {
        switch (input) {
            case "=" -> {
                try {
                    double result = eval(display.getText());
                    display.setText(Double.isFinite(result) ? String.valueOf(result) : "Error");
                } catch (Exception e) {
                    display.setText("Error");
                }
            }
            case "C" -> display.clear();
            case "←" -> {
                String current = display.getText();
                if (!current.isEmpty()) display.setText(current.substring(0, current.length() - 1));
            }
            case "x²" -> {
                try {
                    double val = Double.parseDouble(display.getText());
                    display.setText(String.valueOf(val * val));
                } catch (Exception e) {
                    display.setText("Error");
                }
            }
            case "√" -> {
                try {
                    double val = Double.parseDouble(display.getText());
                    display.setText(String.valueOf(Math.sqrt(val)));
                } catch (Exception e) {
                    display.setText("Error");
                }
            }
            case "1/x" -> {
                try {
                    double val = Double.parseDouble(display.getText());
                    display.setText(val == 0 ? "Error" : String.valueOf(1 / val));
                } catch (Exception e) {
                    display.setText("Error");
                }
            }
            case "%" -> {
                try {
                    double val = Double.parseDouble(display.getText());
                    display.setText(String.valueOf(val / 100));
                } catch (Exception e) {
                    display.setText("Error");
                }
            }
            default -> display.appendText(input);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode key = event.getCode();
        String text = event.getText();

        if (key == KeyCode.ENTER) handleInput("=");
        else if (key == KeyCode.BACK_SPACE) handleInput("←");
        else if (text.matches("[0-9+\\-*/().=]")) handleInput(text);
    }

    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() { ch = (++pos < expression.length()) ? expression.charAt(pos) : -1; }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        launch();
        
    }
}
