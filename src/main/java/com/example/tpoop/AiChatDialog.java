package com.example.tpoop;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Floating, draggable chat window for the farm AI assistant.
 */
class AiChatDialog {

    private static final String COLOR_ACCENT        = "#4a9e6b";
    private static final String COLOR_SIDEBAR_BG    = "#1a3a2a";
    private static final String COLOR_CARD_BG       = "#ffffff";
    private static final String COLOR_CARD_BORDER   = "#e4e8e2";
    private static final String COLOR_PAGE_BG       = "#f7f8f6";
    private static final String COLOR_TEXT_PRIMARY  = "#1a2a1e";
    private static final String COLOR_TEXT_MUTED    = "#94a89c";
    private static final String COLOR_USER_BUBBLE   = "#e8f5ee";
    private static final String COLOR_AI_BUBBLE     = "#ffffff";

    private final Stage owner;
    private final FarmAiService aiService;
    private final Stage dialogStage;
    private final VBox messagesBox;
    private TextField inputField;
    private Button sendBtn;
    private final Label statusLabel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "farm-ai-chat");
        t.setDaemon(true);
        return t;
    });

    private double dragOffsetX;
    private double dragOffsetY;

    AiChatDialog(Stage owner, FarmAiService aiService) {
        this.owner = owner;
        this.aiService = aiService;

        dialogStage = new Stage(StageStyle.UNDECORATED);
        dialogStage.initOwner(owner);
        dialogStage.setAlwaysOnTop(true);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:" + COLOR_CARD_BG + ";-fx-border-color:" + COLOR_CARD_BORDER
                + ";-fx-border-width:1;-fx-background-radius:12;-fx-border-radius:12;");
        root.setPrefWidth(380);
        root.setPrefHeight(520);
        root.setMaxWidth(380);
        root.setMaxHeight(520);

        HBox header = buildHeader();
        messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(12));
        messagesBox.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";");

        ScrollPane scroll = new ScrollPane(messagesBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:" + COLOR_PAGE_BG + ";-fx-background-color:" + COLOR_PAGE_BG
                + ";-fx-border-color:transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:11px;-fx-font-family:'Segoe UI',system;");
        statusLabel.setPadding(new Insets(4, 12, 0, 12));

        HBox inputRow = buildInputRow();
        inputRow.setPadding(new Insets(8, 12, 12, 12));
        inputRow.setStyle("-fx-background-color:" + COLOR_CARD_BG + ";-fx-border-color:" + COLOR_CARD_BORDER
                + " transparent transparent transparent;-fx-border-width:1 0 0 0;");

        root.getChildren().addAll(header, scroll, statusLabel, inputRow);

        Scene scene = new Scene(root);
        scene.setFill(null);
        dialogStage.setScene(scene);

        if (!aiService.isConfigured()) {
            appendMessage("assistant", "⚠ " + aiService.getInitError()
                    + "\n\nDéfinissez OPENAI_API_KEY puis relancez l'application.");
        } else {
            appendMessage("assistant", "Bonjour ! Je suis l'assistant GreenField. Posez-moi des questions sur vos zones, "
                    + "animaux, capteurs, alertes ou production.");
        }
    }

    private HBox buildHeader() {
        Label title = new Label(" Assistant IA");
        title.setStyle("-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:rgba(255,255,255,0.7);-fx-font-size:14px;"
                + "-fx-cursor:hand;-fx-padding:2 8;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color:rgba(255,255,255,0.15);-fx-text-fill:#ffffff;"
                + "-fx-font-size:14px;-fx-cursor:hand;-fx-padding:2 8;-fx-background-radius:4;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:rgba(255,255,255,0.7);"
                + "-fx-font-size:14px;-fx-cursor:hand;-fx-padding:2 8;"));
        closeBtn.setOnAction(e -> hide());

        HBox header = new HBox(8, title, spacer, closeBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 12, 12, 16));
        header.setStyle("-fx-background-color:" + COLOR_SIDEBAR_BG + ";-fx-background-radius:12 12 0 0;");

        header.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });
        header.setOnMouseDragged(e -> {
            dialogStage.setX(e.getScreenX() - dragOffsetX);
            dialogStage.setY(e.getScreenY() - dragOffsetY);
        });

        return header;
    }

    private HBox buildInputRow() {
        inputField = new TextField();
        inputField.setPromptText("Posez une question sur votre ferme…");
        inputField.setStyle("-fx-background-color:#ffffff;-fx-border-color:" + COLOR_CARD_BORDER
                + ";-fx-border-radius:6;-fx-background-radius:6;-fx-font-family:'Segoe UI',system;-fx-font-size:13px;"
                + "-fx-padding:8 10;");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) sendMessage();
        });

        sendBtn = new Button("Envoyer");
        sendBtn.setStyle("-fx-background-color:" + COLOR_ACCENT + ";-fx-text-fill:#ffffff;-fx-font-size:12px;"
                + "-fx-font-family:'Segoe UI',system;-fx-padding:8 14;-fx-cursor:hand;-fx-background-radius:6;");
        sendBtn.setOnAction(e -> sendMessage());
        if (!aiService.isConfigured()) {
            sendBtn.setDisable(true);
            inputField.setDisable(true);
        }

        return new HBox(8, inputField, sendBtn);
    }

    void show() {
        if (!dialogStage.isShowing()) {
            positionNearOwner();
            dialogStage.show();
        } else {
            dialogStage.toFront();
        }
    }

    void hide() {
        if (dialogStage.isShowing()) {
            dialogStage.hide();
        }
    }

    boolean isShowing() {
        return dialogStage.isShowing();
    }

    void shutdown() {
        executor.shutdownNow();
    }

    private void positionNearOwner() {
        double ownerX = owner.getX() + owner.getWidth() - 400;
        double ownerY = owner.getY() + owner.getHeight() - 560;
        dialogStage.setX(Math.max(ownerX, owner.getX() + 20));
        dialogStage.setY(Math.max(ownerY, owner.getY() + 60));
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || !aiService.isConfigured()) return;

        inputField.clear();
        inputField.setDisable(true);
        sendBtn.setDisable(true);
        statusLabel.setText("L'assistant réfléchit…");

        appendMessage("user", text);

        executor.submit(() -> {
            try {
                String reply = aiService.chat(text);
                Platform.runLater(() -> {
                    appendMessage("assistant", reply);
                    finishSending();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    appendMessage("assistant", "Erreur : " + ex.getMessage());
                    finishSending();
                });
            }
        });
    }

    private void finishSending() {
        inputField.setDisable(false);
        sendBtn.setDisable(false);
        statusLabel.setText("");
        inputField.requestFocus();
    }

    private void appendMessage(String role, String text) {
        boolean isUser = "user".equals(role);
        String align = isUser ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;";
        String bg = isUser ? COLOR_USER_BUBBLE : COLOR_AI_BUBBLE;
        String border = isUser ? COLOR_ACCENT : COLOR_CARD_BORDER;

        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(300);
        bubble.setStyle("-fx-background-color:" + bg + ";-fx-border-color:" + border
                + ";-fx-border-width:1;-fx-background-radius:10;-fx-border-radius:10;"
                + "-fx-padding:10 12;-fx-font-size:13px;-fx-font-family:'Segoe UI',system;"
                + "-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";");

        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.setStyle(align);
        messagesBox.getChildren().add(row);

        Platform.runLater(() -> {
            ScrollPane sp = (ScrollPane) messagesBox.getParent();
            sp.setVvalue(1.0);
        });
    }
}
