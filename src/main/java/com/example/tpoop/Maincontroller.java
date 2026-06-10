package com.example.tpoop;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class MainController {

    // ── Design Tokens ────────────────────────────────────────────────
    private static final String COLOR_SIDEBAR_BG    = "#1a3a2a";
    private static final String COLOR_SIDEBAR_HOVER = "#2d5c3e";
    private static final String COLOR_SIDEBAR_ACTIVE = "#3a7a52";
    private static final String COLOR_ACCENT        = "#4a9e6b";
    private static final String COLOR_ACCENT_LIGHT  = "#e8f5ee";
    private static final String COLOR_ACCENT_BORDER = "#b8ddc8";

    private static final String COLOR_PAGE_BG       = "#f7f8f6";
    private static final String COLOR_CARD_BG        = "#ffffff";
    private static final String COLOR_CARD_BORDER    = "#e4e8e2";

    private static final String COLOR_TEXT_PRIMARY   = "#1a2a1e";
    private static final String COLOR_TEXT_SECONDARY = "#5a7060";
    private static final String COLOR_TEXT_MUTED     = "#94a89c";

    private static final String COLOR_SUCCESS_BG     = "#eaf6ef";
    private static final String COLOR_SUCCESS_TEXT   = "#1f6b3e";
    private static final String COLOR_SUCCESS_BORDER = "#a8d5b8";

    private static final String COLOR_WARNING_BG     = "#fef9ec";
    private static final String COLOR_WARNING_TEXT   = "#7a5a0a";
    private static final String COLOR_WARNING_BORDER = "#f0d58a";

    private static final String COLOR_DANGER_BG      = "#fef0f0";
    private static final String COLOR_DANGER_TEXT    = "#8b1a1a";
    private static final String COLOR_DANGER_BORDER  = "#f0b8b8";

    private static final String COLOR_INFO_BG        = "#eef4fb";
    private static final String COLOR_INFO_TEXT      = "#1a4a7a";
    private static final String COLOR_INFO_BORDER    = "#b8d0ee";

    private static final String RADIUS_SM   = "4";
    private static final String RADIUS_MD   = "8";
    private static final String RADIUS_LG   = "12";

    private static final String STYLE_BTN_PRIMARY =
            "-fx-background-color:" + COLOR_ACCENT + ";" +
                    "-fx-text-fill:#ffffff;" +
                    "-fx-font-size:13px;" +
                    "-fx-font-family:'Segoe UI',system;" +
                    "-fx-padding:7 18;" +
                    "-fx-cursor:hand;" +
                    "-fx-background-radius:" + RADIUS_SM + ";" +
                    "-fx-font-weight:normal;";

    private static final String STYLE_BTN_SECONDARY =
            "-fx-background-color:#ffffff;" +
                    "-fx-text-fill:" + COLOR_ACCENT + ";" +
                    "-fx-border-color:" + COLOR_ACCENT_BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-font-size:13px;" +
                    "-fx-font-family:'Segoe UI',system;" +
                    "-fx-padding:6 16;" +
                    "-fx-cursor:hand;" +
                    "-fx-background-radius:" + RADIUS_SM + ";" +
                    "-fx-border-radius:" + RADIUS_SM + ";";

    private static final String STYLE_BTN_GHOST =
            "-fx-background-color:transparent;" +
                    "-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";" +
                    "-fx-border-color:" + COLOR_CARD_BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-font-size:12px;" +
                    "-fx-font-family:'Segoe UI',system;" +
                    "-fx-padding:5 12;" +
                    "-fx-cursor:hand;" +
                    "-fx-background-radius:" + RADIUS_SM + ";" +
                    "-fx-border-radius:" + RADIUS_SM + ";";

    private static final String STYLE_CARD =
            "-fx-background-color:" + COLOR_CARD_BG + ";" +
                    "-fx-border-color:" + COLOR_CARD_BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-padding:16;" +
                    "-fx-background-radius:" + RADIUS_MD + ";" +
                    "-fx-border-radius:" + RADIUS_MD + ";";

    private static final String STYLE_CARD_FLUSH =
            "-fx-background-color:" + COLOR_CARD_BG + ";" +
                    "-fx-border-color:" + COLOR_CARD_BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-padding:0;" +
                    "-fx-background-radius:" + RADIUS_MD + ";" +
                    "-fx-border-radius:" + RADIUS_MD + ";";

    private static final String STYLE_TABLE =
            "-fx-background-color:" + COLOR_CARD_BG + ";" +
                    "-fx-border-color:transparent;" +
                    "-fx-background-radius:0;" +
                    "-fx-table-cell-border-color:" + COLOR_CARD_BORDER + ";" +
                    "-fx-font-family:'Segoe UI',system;" +
                    "-fx-font-size:13px;";

    private static final String STYLE_INPUT =
            "-fx-background-color:#ffffff;" +
                    "-fx-border-color:" + COLOR_CARD_BORDER + ";" +
                    "-fx-border-width:1;" +
                    "-fx-border-radius:" + RADIUS_SM + ";" +
                    "-fx-background-radius:" + RADIUS_SM + ";" +
                    "-fx-font-family:'Segoe UI',system;" +
                    "-fx-font-size:13px;" +
                    "-fx-padding:6 10;" +
                    "-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";";

    private static final String STYLE_INPUT_ERROR =
            "-fx-background-color:#fff8f8;" +
                    "-fx-border-color:" + COLOR_DANGER_BORDER + ";" +
                    "-fx-border-width:1.5;" +
                    "-fx-border-radius:" + RADIUS_SM + ";" +
                    "-fx-background-radius:" + RADIUS_SM + ";" +
                    "-fx-font-family:'Segoe UI',system;" +
                    "-fx-font-size:13px;" +
                    "-fx-padding:6 10;" +
                    "-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";";

    // ── State ─────────────────────────────────────────────────────────
    private final Stage stage;
    private final Ferme ferme;
    private final Gestionnaire g;
    private BorderPane root;
    private String activePage = "ferme";

    // Track expanded zones (by code)
    private final Set<String> expandedZones = new HashSet<>();

    public MainController(Stage stage) {
        this.stage = stage;
        this.ferme = new Ferme("Ferme Principale");
        this.g = new Gestionnaire(ferme);
        Seeds.initialiser(ferme);
    }

    public void show() {
        root = new BorderPane();
        root.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";");
        root.setLeft(buildSidebar());
        showPage("ferme");
        Scene scene = new Scene(root, 1280, 800);
        stage.setTitle("GreenField — Gestion de Ferme");
        stage.setScene(scene);
        stage.show();
    }

    // ─────────────────────────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sb = new VBox(0);
        sb.setStyle(
                "-fx-background-color:" + COLOR_SIDEBAR_BG + ";" +
                        "-fx-min-width:200px;" +
                        "-fx-pref-width:200px;" +
                        "-fx-max-width:200px;"
        );

        VBox brand = new VBox(2);
        brand.setPadding(new Insets(24, 20, 20, 20));
        brand.setStyle("-fx-border-color:transparent transparent rgba(255,255,255,0.08) transparent;-fx-border-width:0 0 1 0;");
        Label lBrand = new Label("GreenField");
        lBrand.setStyle("-fx-text-fill:#ffffff;-fx-font-size:17px;-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;");
        Label lSub = new Label("Gestion de ferme");
        lSub.setStyle("-fx-text-fill:rgba(255,255,255,0.45);-fx-font-size:11px;-fx-font-family:'Segoe UI',system;");
        brand.getChildren().addAll(lBrand, lSub);
        sb.getChildren().add(brand);

        Region topSpacer = new Region();
        topSpacer.setPrefHeight(8);
        sb.getChildren().add(topSpacer);

        String[][] navItems = {
                {"Tableau de bord", "ferme"},
                {"Zones",           "zones"},
                {"Cultures",        "cultures"},
                {"Animaux",         "animaux"},
                {"Capteurs",        "capteurs"},
                {"Alertes",         "alertes"},
                {"Production",      "production"},
        };
        String[] icons = { "\u25A6", "\u25A3", "\u2663", "\u2726", "\u25C9", "\u25B2", "\u25BA" };

        for (int i = 0; i < navItems.length; i++) {
            sb.getChildren().add(buildNavButton(navItems[i][0], icons[i], navItems[i][1]));
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sb.getChildren().add(spacer);

        VBox bottom = new VBox(0);
        bottom.setStyle("-fx-border-color:rgba(255,255,255,0.08) transparent transparent transparent;-fx-border-width:1 0 0 0;");
        bottom.setPadding(new Insets(12, 0, 12, 0));
        Label lVersion = new Label("v1.0.0");
        lVersion.setPadding(new Insets(0, 0, 0, 20));
        lVersion.setStyle("-fx-text-fill:rgba(255,255,255,0.25);-fx-font-size:11px;-fx-font-family:'Segoe UI',system;");
        bottom.getChildren().add(lVersion);
        sb.getChildren().add(bottom);

        return sb;
    }

    private Button buildNavButton(String label, String icon, String page) {
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        Label lIcon = new Label(icon);
        lIcon.setStyle("-fx-text-fill:rgba(255,255,255,0.55);-fx-font-size:12px;");
        Label lLabel = new Label(label);
        lLabel.setStyle("-fx-text-fill:rgba(255,255,255,0.80);-fx-font-size:13px;-fx-font-family:'Segoe UI',system;");
        content.getChildren().addAll(lIcon, lLabel);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 16, 0, 20));

        String baseStyle = "-fx-background-color:transparent;-fx-cursor:hand;-fx-border-color:transparent;-fx-background-radius:0;";
        String hoverStyle = "-fx-background-color:" + COLOR_SIDEBAR_HOVER + ";-fx-cursor:hand;-fx-border-color:transparent;-fx-background-radius:0;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        btn.setOnAction(e -> { activePage = page; showPage(page); });
        return btn;
    }

    private void showPage(String page) {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";-fx-border-color:transparent;-fx-background:" + COLOR_PAGE_BG + ";");
        switch (page) {
            case "ferme"      -> sp.setContent(buildFermePage());
            case "zones"      -> sp.setContent(buildZonesPage());
            case "cultures"   -> sp.setContent(buildCulturesPage());
            case "animaux"    -> sp.setContent(buildAnimauxPage());
            case "capteurs"   -> sp.setContent(buildCapteursPage());
            case "alertes"    -> sp.setContent(buildAlertesPage());
            case "production" -> sp.setContent(buildProductionPage());
            default           -> sp.setContent(buildFermePage());
        }
        root.setCenter(sp);
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE FERME
    // ─────────────────────────────────────────────────────────────────
    private VBox buildFermePage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader(ferme.getNom(), "Vue d'ensemble de votre exploitation agricole"));

        long alertesActives = ferme.getAlertes().stream().filter(Alerte::isActive).count();
        int totalCapteurs   = ferme.getTousLesCapteurs().size();
        int totalZones      = ferme.getZones().size();
        int totalAnimaux    = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneElevage)
                .mapToInt(z -> ((ZoneElevage) z).getAnimaux().size())
                .sum();

        HBox statsRow = new HBox(12);
        statsRow.getChildren().addAll(
                buildStatCard("Zones actives",   String.valueOf(totalZones),    "Total des zones gérées",   COLOR_ACCENT_LIGHT, COLOR_ACCENT),
                buildStatCard("Capteurs",        String.valueOf(totalCapteurs), "Capteurs déployés",         "#eef4fb", COLOR_INFO_TEXT),
                buildStatCard("Animaux",         String.valueOf(totalAnimaux),  "Têtes de bétail",           "#fef9ec", COLOR_WARNING_TEXT),
                buildStatCard("Alertes actives", String.valueOf(alertesActives), "Requièrent attention",
                        alertesActives > 0 ? COLOR_DANGER_BG : COLOR_SUCCESS_BG,
                        alertesActives > 0 ? COLOR_DANGER_TEXT : COLOR_SUCCESS_TEXT)
        );
        page.getChildren().add(statsRow);

        page.getChildren().add(buildSectionHeader("Zones de la ferme", null));

        VBox zonesCard = new VBox(0);
        zonesCard.setStyle(STYLE_CARD_FLUSH);

        TableView<Zone> tz = buildStyledTable();
        tz.setMaxHeight(240);
        tz.getColumns().addAll(
                styledCol("Code",     80,  z -> z.getCode()),
                styledCol("Nom",      180, z -> z.getName()),
                styledCol("Type",     110, z -> z instanceof ZoneCulture ? "Culture" : z instanceof ZoneElevage ? "Elevage" : "Aquaculture"),
                styledCol("Statut",   100, z -> z.getStatus().name()),
                styledCol("Capteurs", 80,  z -> String.valueOf(z.getCapteurs().size())),
                styledCol("Détail",   200, z -> {
                    if (z instanceof ZoneCulture zc) return zc.getCultures() != null ? zc.getCultures().getNom() : "—";
                    if (z instanceof ZoneElevage ze) return ze.getAnimaux().size() + " animaux";
                    if (z instanceof ZoneAqua za)    return za.getEspece() + " (" + za.getNbAnimaux() + ")";
                    return "";
                })
        );
        styledStatusRows(tz, z -> z.getStatus() == Status.ACTIF ? "ok" : "off");
        tz.getItems().addAll(ferme.getZones());
        zonesCard.getChildren().add(tz);
        page.getChildren().add(zonesCard);

        List<Alerte> actives = ferme.getAlertes().stream()
                .filter(Alerte::isActive)
                .sorted((a, b) -> b.getGravite().compareTo(a.getGravite()))
                .collect(Collectors.toList());

        page.getChildren().add(buildSectionHeader("Alertes actives", actives.size() + " alerte(s)"));

        VBox alertsCard = new VBox(0);
        alertsCard.setStyle(STYLE_CARD_FLUSH);

        if (actives.isEmpty()) {
            Label lOk = new Label("Aucune alerte active — tout est nominal.");
            lOk.setPadding(new Insets(20));
            lOk.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;-fx-font-family:'Segoe UI',system;");
            alertsCard.getChildren().add(lOk);
        } else {
            boolean first = true;
            for (Alerte a : actives) {
                alertsCard.getChildren().add(buildAlertRow(a, !first));
                first = false;
            }
        }
        page.getChildren().add(alertsCard);
        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE ZONES  (with expandable cards — point 3)
    // ─────────────────────────────────────────────────────────────────
    private VBox buildZonesPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Zones", "Gérez les zones de culture, d'élevage et d'aquaculture"));

        // ── Add zone form ────────────────────────────────────────────
        VBox addCard = new VBox(12);
        addCard.setStyle(STYLE_CARD);
        Label lAddTitle = new Label("Ajouter une zone");
        lAddTitle.setStyle(sectionTitleStyle());

        TextField tfNom = styledInput("Nom de la zone", 200);
        Label errNom = errorLabel("");

        ComboBox<TypeZone> cbType = new ComboBox<>(FXCollections.observableArrayList(TypeZone.values()));
        cbType.getSelectionModel().selectFirst();
        styleCombo(cbType);
        cbType.setPrefWidth(160);

        // ── Culture fields (shown only when TypeZone == CULTURE) ──
        VBox cultureFields = new VBox(10);
        cultureFields.setVisible(false);
        cultureFields.setManaged(false);

        HBox cultureRow1 = new HBox(10);
        cultureRow1.setAlignment(Pos.BOTTOM_LEFT);
        TextField tfCultNom   = styledInput("Nom de la culture", 160);
        Label errCultNom      = errorLabel("");
        DatePicker dpCultPlant = new DatePicker();
        dpCultPlant.setPromptText("Date plantation");
        dpCultPlant.setPrefWidth(150);
        Label errCultPlant    = errorLabel("");
        DatePicker dpCultRecolte = new DatePicker();
        dpCultRecolte.setPromptText("Date récolte");
        dpCultRecolte.setPrefWidth(150);
        Label errCultRecolte  = errorLabel("");
        ComboBox<StadeCroissance> cbStade = new ComboBox<>(FXCollections.observableArrayList(StadeCroissance.values()));
        cbStade.getSelectionModel().selectFirst();
        styleCombo(cbStade);
        cultureRow1.getChildren().addAll(
                fieldGroup("Nom culture", new VBox(2, tfCultNom, errCultNom)),
                fieldGroup("Plantation",  new VBox(2, dpCultPlant, errCultPlant)),
                fieldGroup("Récolte",     new VBox(2, dpCultRecolte, errCultRecolte)),
                fieldGroup("Stade",       cbStade)
        );

        HBox cultureRow2 = new HBox(10);
        cultureRow2.setAlignment(Pos.BOTTOM_LEFT);
        TextField tfPhMin2  = styledInput("5.5", 70);
        TextField tfPhMax2  = styledInput("7.0", 70);
        TextField tfHMin2   = styledInput("30",  70);
        TextField tfHMax2   = styledInput("80",  70);
        TextField tfAzMin2  = styledInput("50",  70);
        TextField tfAzMax2  = styledInput("150", 70);
        Label errPh2  = errorLabel("");
        Label errHum2 = errorLabel("");
        Label errAz2  = errorLabel("");
        cultureRow2.getChildren().addAll(
                fieldGroup("pH min / max",      new VBox(2, new HBox(4, tfPhMin2, new Label("/"), tfPhMax2), errPh2)),
                fieldGroup("Humidité min / max", new VBox(2, new HBox(4, tfHMin2,  new Label("/"), tfHMax2),  errHum2)),
                fieldGroup("Azote min / max",    new VBox(2, new HBox(4, tfAzMin2, new Label("/"), tfAzMax2), errAz2))
        );

        cultureFields.getChildren().addAll(cultureRow1, cultureRow2);

        cbType.setOnAction(e -> {
            boolean isCulture = cbType.getValue() == TypeZone.CULTURE;
            cultureFields.setVisible(isCulture);
            cultureFields.setManaged(isCulture);
        });

        HBox addRow = new HBox(10);
        addRow.setAlignment(Pos.CENTER_LEFT);

        VBox nomGroup = new VBox(2);
        Label lNomLbl = new Label("Nom");
        lNomLbl.setStyle("-fx-font-size:11px;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-font-family:'Segoe UI',system;");
        nomGroup.getChildren().addAll(lNomLbl, tfNom, errNom);

        Button btnAjout = btn("Ajouter la zone", STYLE_BTN_PRIMARY);
        btnAjout.setAlignment(Pos.CENTER);

        addRow.getChildren().addAll(nomGroup, fieldGroup("Type", cbType), btnAjout);

        btnAjout.setOnAction(e -> {
            if (tfNom.getText().isBlank()) {
                markError(tfNom, errNom, "Le nom de la zone est obligatoire.");
                return;
            }
            if (!isValidName(tfNom.getText())) {
                markError(tfNom, errNom, "Le nom doit être du texte, pas un nombre.");
                return;
            }
            clearError(tfNom, errNom);

            if (cbType.getValue() == TypeZone.AQUA) {
                TextInputDialog d = new TextInputDialog("Tilapia");
                styleDialog(d);
                d.setHeaderText("Espèce aquacole");
                d.showAndWait().ifPresent(esp -> {
                    if (esp.isBlank()) { info("L'espèce ne peut pas être vide."); return; }
                    ferme.ajouterZone(new ZoneAqua(tfNom.getText().trim(), Status.ACTIF, esp.trim(), ferme));
                    showPage("zones");
                });
            } else if (cbType.getValue() == TypeZone.ELEVAGE) {
                ChoiceDialog<TypeAnimal> d = new ChoiceDialog<>(TypeAnimal.RUMINANT, TypeAnimal.values());
                styleDialog(d);
                d.setHeaderText("Type d'animal");
                d.showAndWait().ifPresent(ta -> {
                    ferme.ajouterZone(new ZoneElevage(tfNom.getText().trim(), Status.ACTIF, ta, ferme));
                    showPage("zones");
                });
            } else {
                // Validate culture fields
                boolean valid = true;
                if (tfCultNom.getText().isBlank()) { markError(tfCultNom, errCultNom, "Nom de culture obligatoire."); valid = false; } else clearError(tfCultNom, errCultNom);
                if (dpCultPlant.getValue() == null) { errCultPlant.setText("Date de plantation obligatoire."); valid = false; } else errCultPlant.setText("");
                if (dpCultRecolte.getValue() == null) { errCultRecolte.setText("Date de récolte obligatoire."); valid = false; } else errCultRecolte.setText("");
                if (!isDouble(tfPhMin2.getText()) || !isDouble(tfPhMax2.getText())) { errPh2.setText("Valeurs pH invalides (nombres attendus)."); valid = false; } else errPh2.setText("");
                if (!isDouble(tfHMin2.getText()) || !isDouble(tfHMax2.getText()))   { errHum2.setText("Valeurs humidité invalides (nombres attendus)."); valid = false; } else errHum2.setText("");
                if (!isDouble(tfAzMin2.getText()) || !isDouble(tfAzMax2.getText())) { errAz2.setText("Valeurs azote invalides (nombres attendus)."); valid = false; } else errAz2.setText("");
                if (!valid) return;

                ExigPedologiques ep = new ExigPedologiques(
                        Double.parseDouble(tfPhMin2.getText().trim()), Double.parseDouble(tfPhMax2.getText().trim()),
                        Double.parseDouble(tfHMin2.getText().trim()),  Double.parseDouble(tfHMax2.getText().trim()),
                        Double.parseDouble(tfAzMin2.getText().trim()), Double.parseDouble(tfAzMax2.getText().trim()));
                Culture cult = new Culture(tfCultNom.getText().trim(),
                        dpCultPlant.getValue().toString(), dpCultRecolte.getValue().toString(),
                        cbStade.getValue(), ep);
                ferme.ajouterZone(new ZoneCulture(tfNom.getText().trim(), Status.ACTIF, cult, ferme));
                showPage("zones");
            }
        });

        addCard.getChildren().addAll(lAddTitle, addRow, cultureFields);
        page.getChildren().add(addCard);

        // ── Tabbed zone list ─────────────────────────────────────────
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";-fx-tab-min-height:38px;");
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        List<Zone> cultures    = ferme.getZones().stream().filter(z -> z instanceof ZoneCulture).collect(Collectors.toList());
        List<Zone> elevages    = ferme.getZones().stream().filter(z -> z instanceof ZoneElevage).collect(Collectors.toList());
        List<Zone> aquaculture = ferme.getZones().stream().filter(z -> z instanceof ZoneAqua).collect(Collectors.toList());

        tabs.getTabs().addAll(
                buildZoneTab("Cultures (" + cultures.size() + ")",       cultures),
                buildZoneTab("Elevage (" + elevages.size() + ")",         elevages),
                buildZoneTab("Aquaculture (" + aquaculture.size() + ")",  aquaculture)
        );
        page.getChildren().add(tabs);
        return page;
    }

    private Tab buildZoneTab(String title, List<Zone> zones) {
        Tab tab = new Tab(title);
        VBox content = new VBox(10);
        content.setPadding(new Insets(16, 0, 16, 0));
        content.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";");
        for (Zone z : zones) content.getChildren().add(buildZoneCard(z));
        if (zones.isEmpty()) {
            Label lEmpty = new Label("Aucune zone dans cette catégorie.");
            lEmpty.setPadding(new Insets(24));
            lEmpty.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
            content.getChildren().add(lEmpty);
        }
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";-fx-border-color:transparent;-fx-background:" + COLOR_PAGE_BG + ";");
        tab.setContent(sp);
        return tab;
    }

    // ── Zone card with expand/collapse (point 3) ──────────────────────
    private VBox buildZoneCard(Zone z) {
        VBox card = new VBox(0);
        card.setStyle(STYLE_CARD);

        boolean expanded = expandedZones.contains(z.getCode());

        // ── Header row ──────────────────────────────────────────────
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, expanded ? 12 : 0, 0));
        header.setStyle("-fx-cursor:hand;");

        Label lExpand = new Label(expanded ? "▼" : "▶");
        lExpand.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:11px;");

        Label lNom = new Label(z.getName());
        lNom.setStyle("-fx-font-weight:bold;-fx-font-size:15px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");

        Label lCode = new Label(z.getCode());
        lCode.setStyle("-fx-background-color:#f0f2f0;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-padding:2 8;-fx-background-radius:4;-fx-font-size:12px;");

        String statusBg   = z.getStatus() == Status.ACTIF ? COLOR_SUCCESS_BG  : "#f5f5f5";
        String statusText = z.getStatus() == Status.ACTIF ? COLOR_SUCCESS_TEXT : COLOR_TEXT_MUTED;
        Label lStatus = new Label(z.getStatus().name());
        lStatus.setStyle("-fx-background-color:" + statusBg + ";-fx-text-fill:" + statusText + ";-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;");

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Action buttons
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (z.getStatus() == Status.ACTIF) {
            Button bSusp = btn("Désactiver", STYLE_BTN_GHOST);
            bSusp.setOnAction(e -> { e.consume(); g.desactiverZone(z); showPage("zones"); });
            actions.getChildren().add(bSusp);
        } else {
            Button bAct = btn("Réactiver", STYLE_BTN_SECONDARY);
            bAct.setOnAction(e -> { e.consume(); g.reactiverZone(z); showPage("zones"); });
            actions.getChildren().add(bAct);
        }

        Button bRen = btn("Renommer", STYLE_BTN_GHOST);
        bRen.setOnAction(e -> {
            e.consume();
            TextInputDialog d = new TextInputDialog(z.getName());
            styleDialog(d);
            d.setHeaderText("Nouveau nom pour la zone");
            d.showAndWait().ifPresent(n -> {
                if (n.isBlank()) { info("Le nom ne peut pas être vide."); return; }
                g.modifierNomZone(z, n.trim());
                showPage("zones");
            });
        });

        Button bDel = btn("Supprimer", STYLE_BTN_GHOST);
        bDel.setStyle(STYLE_BTN_GHOST + "-fx-text-fill:" + COLOR_DANGER_TEXT + ";-fx-border-color:" + COLOR_DANGER_BORDER + ";");
        bDel.setOnAction(e -> {
            e.consume();
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                    "Confirmer la suppression de \"" + z.getName() + "\" ?\nSes capteurs et alertes seront aussi supprimés.", ButtonType.YES, ButtonType.NO);
            styleDialog(conf);
            conf.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    // Point 10: suppress capteurs and alertes of the zone
                    supprimerZoneAvecDependances(z);
                    showPage("zones");
                }
            });
        });

        actions.getChildren().addAll(bRen, bDel);
        header.getChildren().addAll(lExpand, lNom, lCode, lStatus, hSpacer, actions);

        // Toggle expand on header click
        header.setOnMouseClicked(e -> {
            if (expandedZones.contains(z.getCode())) expandedZones.remove(z.getCode());
            else expandedZones.add(z.getCode());
            showPage("zones");
        });

        card.getChildren().add(header);

        // ── Expanded details ────────────────────────────────────────
        if (expanded) {
            card.getChildren().add(buildDivider());
            VBox details = new VBox(12);
            details.setPadding(new Insets(12, 0, 4, 0));

            if (z instanceof ZoneCulture zc) {
                details.getChildren().add(buildZoneCultureDetails(zc));
            } else if (z instanceof ZoneElevage ze) {
                details.getChildren().add(buildZoneElevageDetails(ze));
            } else if (z instanceof ZoneAqua za) {
                details.getChildren().add(buildZoneAquaDetails(za));
            }

            card.getChildren().add(details);
        } else {
            // Collapsed: show quick chips
            card.getChildren().add(buildDivider());
            HBox summary = new HBox(10);
            summary.setPadding(new Insets(10, 0, 0, 0));
            summary.setAlignment(Pos.CENTER_LEFT);

            if (z instanceof ZoneCulture zc) {
                summary.getChildren().add(buildDetailChip("Culture", zc.getCultures() != null ? zc.getCultures().getNom() : "—"));
            } else if (z instanceof ZoneElevage ze) {
                summary.getChildren().addAll(
                        buildDetailChip("Animaux", String.valueOf(ze.getAnimaux().size())),
                        buildDetailChip("Type", ze.getTypeAnimal().name())
                );
            } else if (z instanceof ZoneAqua za) {
                summary.getChildren().addAll(
                        buildDetailChip("Espèce", za.getEspece()),
                        buildDetailChip("Individus", String.valueOf(za.getNbAnimaux()))
                );
            }
            summary.getChildren().add(buildDetailChip("Capteurs", String.valueOf(z.getCapteurs().size())));
            Label hint = new Label("Cliquer pour voir les détails");
            hint.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:11px;-fx-font-style:italic;");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            summary.getChildren().addAll(sp, hint);
            card.getChildren().add(summary);
        }

        return card;
    }

    // ── Expanded zone detail panels (point 3) ────────────────────────

    private VBox buildZoneCultureDetails(ZoneCulture zc) {
        VBox v = new VBox(12);

        // Culture info chips
        if (zc.getCultures() != null) {
            Culture c = zc.getCultures();
            HBox chips = new HBox(10);
            chips.getChildren().addAll(
                    buildDetailChip("Culture",    c.getNom()),
                    buildDetailChip("Plantation", c.getDatePlantation()),
                    buildDetailChip("Récolte",    c.getDateRecolte()),
                    buildDetailChip("Stade",      c.getStadeCroiss().name())
            );
            v.getChildren().add(chips);

            // Exigences pédologiques display
            if (c.getExigPed() != null) {
                ExigPedologiques ep = c.getExigPed();
                HBox exigChips = new HBox(10);
                exigChips.getChildren().addAll(
                        buildDetailChip("pH min/max", ep.getPhMin() + " / " + ep.getPhMax()),
                        buildDetailChip("Humidité min/max", ep.getHumMin() + " / " + ep.getHumMax()),
                        buildDetailChip("Azote min/max", ep.getAzoteMin() + " / " + ep.getAzoteMax())
                );
                v.getChildren().add(exigChips);
            }

            // Stade update
            HBox stadeRow = new HBox(8);
            Button bStade = btn("Mettre à jour stade", STYLE_BTN_SECONDARY);
            bStade.setOnAction(ev -> {
                ChoiceDialog<StadeCroissance> d = new ChoiceDialog<>(c.getStadeCroiss(), StadeCroissance.values());
                styleDialog(d);
                d.setHeaderText("Sélectionner le stade de croissance");
                d.showAndWait().ifPresent(s -> { g.mettreAJourStadeCroissance(zc, s); showPage("zones"); });
            });
            stadeRow.getChildren().add(bStade);
            v.getChildren().add(stadeRow);
        } else {
            Label lNone = new Label("Aucune culture affectée.");
            lNone.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
            v.getChildren().add(lNone);
        }

        // Capteurs list
        v.getChildren().add(buildCapteursSubList(zc));

        // Productions
        if (!zc.getProductions().isEmpty()) v.getChildren().add(buildProductionSubList(zc));

        // Action buttons
        HBox acts = new HBox(8);
      //  Button bAff = btn("Affecter culture", STYLE_BTN_PRIMARY);
       // bAff.setOnAction(e -> { affecterCultureDialog(zc, false); showPage("zones"); });
        Button bMod = btn("Modifier culture", STYLE_BTN_SECONDARY);
        bMod.setOnAction(e -> { affecterCultureDialog(zc, true); showPage("zones"); });
        // Point 2: editable exig pédologiques
        Button bExig = btn("Exigences sol", STYLE_BTN_GHOST);
        bExig.setOnAction(e -> showExigPedologiquesEditable(zc));
        acts.getChildren().addAll( bMod, bExig);
        v.getChildren().add(acts);

        return v;
    }

    private VBox buildZoneElevageDetails(ZoneElevage ze) {
        VBox v = new VBox(12);

        // Summary chips
        HBox chips = new HBox(10);
        chips.getChildren().addAll(
                buildDetailChip("Animaux",  String.valueOf(ze.getAnimaux().size())),
                buildDetailChip("Malades",  String.valueOf(ze.getNbAnimauxMalades())),
                buildDetailChip("Type",     ze.getTypeAnimal().name())
        );
        v.getChildren().add(chips);

        // Programme alimentaire
        if (ze.getProgAlim() != null) {
            ProgAlimentaire pa = ze.getProgAlim();
            HBox paChips = new HBox(10);
            paChips.getChildren().addAll(
                    buildDetailChip("Aliment",       pa.getTypeAliment()),
                    buildDetailChip("Qté/repas(kg)", String.format("%.2f", pa.getQuantiteParRepas())),
                    buildDetailChip("Repas/jour",    String.valueOf(pa.getRepasParJour())),
                    buildDetailChip("Total/jour(kg)", String.format("%.2f", pa.getQuantiteJournaliere()))
            );
            v.getChildren().add(paChips);
        }

        // Animal list (mini table)
        if (!ze.getAnimaux().isEmpty()) {
            TableView<Animal> ta = buildStyledTable();
            ta.setMaxHeight(160);
            ta.getColumns().addAll(
                    styledCol("ID",       55,  a -> String.valueOf(a.getID())),
                    styledCol("Espèce",   130, a -> a.getEspece().getName()),
                    styledCol("Age",      50,  a -> a.age + " ans"),
                    styledCol("Poids",    70,  a -> String.format("%.1f kg", a.getPoids())),
                    styledCol("Santé",    110, a -> a.getEtat().name())
            );
            ta.setRowFactory(tv -> new TableRow<>() {
                @Override protected void updateItem(Animal a, boolean empty) {
                    super.updateItem(a, empty);
                    if (a == null || empty) { setStyle(""); return; }
                    setStyle(switch (a.getEtat()) {
                        case MALADE         -> "-fx-background-color:" + COLOR_DANGER_BG + ";";
                        case EN_QUARANTAINE -> "-fx-background-color:" + COLOR_WARNING_BG + ";";
                        default             -> "";
                    });
                }
            });
            ta.getItems().addAll(ze.getAnimaux());
            v.getChildren().add(ta);
        }

        // Capteurs
        v.getChildren().add(buildCapteursSubList(ze));

        // Productions
        if (!ze.getProductions().isEmpty()) v.getChildren().add(buildProductionSubList(ze));

        // Actions
        HBox acts = new HBox(8);
        // Point 6: add animal directly from zone card
        Button bAddAnim = btn("Ajouter un animal", STYLE_BTN_PRIMARY);
        bAddAnim.setOnAction(e -> { ajouterAnimalDialog(ze); showPage("zones"); });
        Button bProg = btn("Programme alimentaire", STYLE_BTN_SECONDARY);
        bProg.setOnAction(e -> showProgAlimDialog(ze));
        acts.getChildren().addAll(bAddAnim, bProg);
        v.getChildren().add(acts);

        return v;
    }

    private VBox buildZoneAquaDetails(ZoneAqua za) {
        VBox v = new VBox(12);

        HBox chips = new HBox(10);
        chips.getChildren().addAll(
                buildDetailChip("Espèce",    za.getEspece()),
                buildDetailChip("Individus", String.valueOf(za.getNbAnimaux()))
        );
        v.getChildren().add(chips);

        if (za.getProgAlim() != null) {
            ProgAlimentaire pa = za.getProgAlim();
            HBox paChips = new HBox(10);
            paChips.getChildren().addAll(
                    buildDetailChip("Aliment",       pa.getTypeAliment()),
                    buildDetailChip("Qté/repas(kg)", String.format("%.2f", pa.getQuantiteParRepas())),
                    buildDetailChip("Repas/jour",    String.valueOf(pa.getRepasParJour())),
                    buildDetailChip("Total/jour(kg)", String.format("%.2f", pa.getQuantiteJournaliere()))
            );
            v.getChildren().add(paChips);
        }

        v.getChildren().add(buildCapteursSubList(za));
        if (!za.getProductions().isEmpty()) v.getChildren().add(buildProductionSubList(za));

        HBox acts = new HBox(8);
        Button bProg = btn("Programme alimentaire", STYLE_BTN_SECONDARY);
        bProg.setOnAction(e -> showProgAlimDialog(za));
        Button bNb = btn("Modifier effectif", STYLE_BTN_GHOST);
        bNb.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog(String.valueOf(za.getNbAnimaux()));
            styleDialog(d);
            d.setHeaderText("Nombre d'individus");
            d.showAndWait().ifPresent(s -> {
                if (!isInt(s)) { info("Valeur invalide : entrez un nombre entier."); return; }
                za.setNbAnimaux(Integer.parseInt(s.trim()));
                showPage("zones");
            });
        });
        acts.getChildren().addAll(bProg, bNb);
        v.getChildren().add(acts);

        return v;
    }

    private VBox buildCapteursSubList(Zone z) {
        VBox v = new VBox(6);
        Label lTitle = new Label("Capteurs (" + z.getCapteurs().size() + ")");
        lTitle.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";");
        v.getChildren().add(lTitle);
        if (z.getCapteurs().isEmpty()) {
            Label l = new Label("Aucun capteur installé.");
            l.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:12px;");
            v.getChildren().add(l);
        } else {
            HBox row = new HBox(8);
            for (Capteurs c : z.getCapteurs()) {
                VBox chip = new VBox(1);
                chip.setStyle("-fx-background-color:" + COLOR_INFO_BG + ";-fx-border-color:" + COLOR_INFO_BORDER + ";-fx-border-width:1;-fx-padding:5 10;-fx-background-radius:6;-fx-border-radius:6;");
                Label lCode = new Label(c.getCode());
                lCode.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_INFO_TEXT + ";");
                Label lType = new Label(c.getType().name());
                lType.setStyle("-fx-font-size:10px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";");
                chip.getChildren().addAll(lCode, lType);
                row.getChildren().add(chip);
            }
            v.getChildren().add(row);
        }
        return v;
    }

    private VBox buildProductionSubList(Zone z) {
        VBox v = new VBox(6);
        Label lTitle = new Label("Production — Total : " + String.format("%.2f", z.getTotal()));
        lTitle.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_ACCENT + ";");
        v.getChildren().add(lTitle);
        HBox row = new HBox(8);
        for (Prod p : z.getProductions()) {
            VBox chip = new VBox(1);
            chip.setStyle("-fx-background-color:" + COLOR_ACCENT_LIGHT + ";-fx-border-color:" + COLOR_ACCENT_BORDER + ";-fx-border-width:1;-fx-padding:5 10;-fx-background-radius:6;-fx-border-radius:6;");
            Label lVal = new Label(String.format("%.2f %s", p.getVal(), p.getProd().getUnite()));
            lVal.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_ACCENT + ";");
            Label lType = new Label(p.getProd().name());
            lType.setStyle("-fx-font-size:10px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";");
            chip.getChildren().addAll(lVal, lType);
            row.getChildren().add(chip);
        }
        v.getChildren().add(row);
        return v;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE CULTURES  (points 5, 11)
    // ─────────────────────────────────────────────────────────────────
    private VBox buildCulturesPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Cultures", "Suivi des cultures par zone"));

        List<ZoneCulture> zcs = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneCulture).map(z -> (ZoneCulture) z).collect(Collectors.toList());

        for (ZoneCulture zc : zcs) {
            VBox card = new VBox(12);
            card.setStyle(STYLE_CARD);

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lZone = new Label(zc.getName());
            lZone.setStyle("-fx-font-weight:bold;-fx-font-size:15px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
            Label lCode = new Label(zc.getCode());
            lCode.setStyle("-fx-background-color:#f0f2f0;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-padding:2 8;-fx-background-radius:4;-fx-font-size:12px;");
            String sBg  = zc.getStatus() == Status.ACTIF ? COLOR_SUCCESS_BG  : "#f5f5f5";
            String sTxt = zc.getStatus() == Status.ACTIF ? COLOR_SUCCESS_TEXT : COLOR_TEXT_MUTED;
            Label lStat = new Label(zc.getStatus().name());
            lStat.setStyle("-fx-background-color:" + sBg + ";-fx-text-fill:" + sTxt + ";-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;");
            header.getChildren().addAll(lZone, lCode, lStat);
            card.getChildren().add(header);
            card.getChildren().add(buildDivider());

            if (zc.getCultures() == null) {
                Label lNone = new Label("Aucune culture affectée à cette zone.");
                lNone.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
                card.getChildren().add(lNone);
            } else {
                Culture c = zc.getCultures();
                HBox chips = new HBox(10);
                chips.getChildren().addAll(
                        buildDetailChip("Culture",    c.getNom()),
                        buildDetailChip("Plantation", c.getDatePlantation()),
                        buildDetailChip("Récolte",    c.getDateRecolte()),
                        buildDetailChip("Stade",      c.getStadeCroiss().name())
                );
                if (c.getExigPed() != null) {
                    ExigPedologiques ep = c.getExigPed();
                    chips.getChildren().addAll(
                            buildDetailChip("pH", ep.getPhMin() + "–" + ep.getPhMax()),
                            buildDetailChip("Hum.", ep.getHumMin() + "–" + ep.getHumMax()),
                            buildDetailChip("Azote", ep.getAzoteMin() + "–" + ep.getAzoteMax())
                    );
                }
                card.getChildren().add(chips);

                HBox acts = new HBox(8);
                Button bStade = btn("Mettre à jour stade", STYLE_BTN_SECONDARY);
                bStade.setOnAction(e -> {
                    ChoiceDialog<StadeCroissance> d = new ChoiceDialog<>(c.getStadeCroiss(), StadeCroissance.values());
                    styleDialog(d);
                    d.setHeaderText("Sélectionner le stade de croissance");
                    d.showAndWait().ifPresent(s -> { g.mettreAJourStadeCroissance(zc, s); showPage("cultures"); });
                });
                // Point 2: editable exig from cultures page too
                Button bExig = btn("Exigences pédologiques", STYLE_BTN_GHOST);
                bExig.setOnAction(e -> showExigPedologiquesEditable(zc));
                acts.getChildren().addAll(bStade, bExig);
                card.getChildren().add(acts);
            }

            // Point 5: separate Affecter and Modifier buttons
            HBox bottom = new HBox(8);
            Button bAff = btn("Affecter culture", STYLE_BTN_PRIMARY);
            bAff.setOnAction(e -> { affecterCultureDialog(zc, false); showPage("cultures"); });
            Button bMod = btn("Modifier culture", STYLE_BTN_SECONDARY);
            bMod.setOnAction(e -> { affecterCultureDialog(zc, true); showPage("cultures"); });
            bottom.getChildren().addAll(bAff, bMod);
            card.getChildren().add(bottom);

            page.getChildren().add(card);
        }

        HBox rapportRow = new HBox();
        Button bRap = btn("Générer rapport global des cultures", STYLE_BTN_SECONDARY);
        bRap.setOnAction(e -> {
            Alert al = new Alert(Alert.AlertType.INFORMATION);
            styleDialog(al);
            al.setHeaderText("Rapport des cultures");
            al.setContentText(null);
            TextArea taRap = new TextArea(ferme.genererRapportCultures());
            taRap.setEditable(false);
            taRap.setWrapText(true);
            taRap.setPrefWidth(560);
            taRap.setPrefHeight(360);
            taRap.setStyle(STYLE_INPUT + "-fx-font-family:monospace;-fx-font-size:12px;");
            ScrollPane spRap = new ScrollPane(taRap);
            spRap.setFitToWidth(true);
            spRap.setPrefHeight(380);
            spRap.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
            al.getDialogPane().setContent(spRap);
            al.getDialogPane().setPrefWidth(600);
            al.showAndWait();
        });
        rapportRow.getChildren().add(bRap);
        page.getChildren().add(rapportRow);
        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE ANIMAUX
    // ─────────────────────────────────────────────────────────────────
    private VBox buildAnimauxPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Animaux", "Registre et suivi sanitaire du bétail"));

        List<ZoneElevage> zes = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneElevage).map(z -> (ZoneElevage) z).collect(Collectors.toList());

        for (ZoneElevage ze : zes) {
            VBox card = new VBox(12);
            card.setStyle(STYLE_CARD);

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lZ = new Label(ze.getName());
            lZ.setStyle("-fx-font-weight:bold;-fx-font-size:15px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
            Label lCode = new Label(ze.getCode());
            lCode.setStyle("-fx-background-color:#f0f2f0;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-padding:2 8;-fx-background-radius:4;-fx-font-size:12px;");
            Label lType = new Label(ze.getTypeAnimal().name());
            lType.setStyle("-fx-background-color:" + COLOR_INFO_BG + ";-fx-text-fill:" + COLOR_INFO_TEXT + ";-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;");
            Region hSp = new Region(); HBox.setHgrow(hSp, Priority.ALWAYS);
            Label lCount = new Label(ze.getAnimaux().size() + " animaux  •  " + ze.getNbAnimauxMalades() + " malade(s)");
            lCount.setStyle("-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-font-size:13px;");
            header.getChildren().addAll(lZ, lCode, lType, hSp, lCount);
            card.getChildren().add(header);
            card.getChildren().add(buildDivider());

            TableView<Animal> ta = buildStyledTable();
            ta.setMaxHeight(200);
            ta.getColumns().addAll(
                    styledCol("ID",          60,  a -> String.valueOf(a.getID())),
                    styledCol("Espèce",      150, a -> a.getEspece().getName()),
                    styledCol("Type",        100, a -> a.getEspece().getType().name()),
                    styledCol("Age",         60,  a -> a.age + " ans"),
                    styledCol("Poids (kg)",  90,  a -> String.format("%.1f", a.getPoids())),
                    styledCol("Etat santé",  120, a -> a.getEtat().name())
            );
            ta.setRowFactory(tv -> new TableRow<>() {
                @Override protected void updateItem(Animal a, boolean empty) {
                    super.updateItem(a, empty);
                    if (a == null || empty) { setStyle(""); return; }
                    setStyle(switch (a.getEtat()) {
                        case MALADE         -> "-fx-background-color:" + COLOR_DANGER_BG  + ";";
                        case EN_QUARANTAINE -> "-fx-background-color:" + COLOR_WARNING_BG + ";";
                        default             -> "";
                    });
                }
            });
            ta.getItems().addAll(ze.getAnimaux());
            card.getChildren().add(ta);

            HBox acts = new HBox(8);
            acts.setAlignment(Pos.CENTER_LEFT);

            Button bAdd = btn("Ajouter un animal", STYLE_BTN_PRIMARY);
            bAdd.setOnAction(e -> { ajouterAnimalDialog(ze); showPage("animaux"); });

            Button bEvt = btn("Consigner événement sanitaire", STYLE_BTN_SECONDARY);
            bEvt.setOnAction(e -> {
                if (ta.getSelectionModel().getSelectedItem() == null) { info("Veuillez sélectionner un animal."); return; }
                evenementSanitaireDialog(ta.getSelectionModel().getSelectedItem());
                showPage("animaux");
            });

            Button bHist = btn("Historique sanitaire", STYLE_BTN_GHOST);
            bHist.setOnAction(e -> {
                if (ta.getSelectionModel().getSelectedItem() == null) { info("Veuillez sélectionner un animal."); return; }
                showHistoriqueSanitaire(ta.getSelectionModel().getSelectedItem());
            });

            Button bProg = btn("Programme alimentaire", STYLE_BTN_GHOST);
            bProg.setOnAction(ev -> showProgAlimDialog(ze));

            acts.getChildren().addAll(bAdd, bEvt, bHist, bProg);
            card.getChildren().add(acts);
            page.getChildren().add(card);
        }

        if (zes.isEmpty()) {
            Label lNone = new Label("Aucune zone d'élevage configurée.");
            lNone.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:14px;");
            page.getChildren().add(lNone);
        }
        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE CAPTEURS
    // ─────────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────
// PAGE CAPTEURS
// ─────────────────────────────────────────────────────────────────
    private VBox buildCapteursPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Capteurs", "Tableau de bord et historique des relevés"));

        HBox actBar = new HBox(8);
        actBar.setAlignment(Pos.CENTER_LEFT);

        VBox tableCard = new VBox(0);
        tableCard.setStyle(STYLE_CARD_FLUSH);

        TableView<Capteurs> tb = buildStyledTable();
        tb.setMaxHeight(280);
        tb.getColumns().addAll(
                styledCol("Code",         90,  c -> c.getCode()),
                styledCol("Type",         110, c -> c.getType().name()),
                styledCol("Zone",         150, c -> c.getLocation() != null ? c.getLocation().getCode() : "—"),
                styledCol("Statut",       90,  c -> c.getStatus().name()),
                styledCol("Dernier relevé", 160, c -> {
                    if (c.getHistorique().isEmpty()) return "Aucun";
                    Releve r = c.getHistorique().get(c.getHistorique().size() - 1);
                    return r.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                            " [" + r.getNiveauReleve() + "]";
                }),
                styledCol("Valeurs", 280, c -> {
                    if (c.getHistorique().isEmpty()) return "—";
                    return c.getHistorique().get(c.getHistorique().size() - 1).getValeurs().toString();
                })
        );
        tb.getItems().addAll(ferme.getTousLesCapteurs());
        tableCard.getChildren().add(tb);

        Button bAdd = btn("Ajouter capteur", STYLE_BTN_PRIMARY);
        bAdd.setOnAction(e -> { ajouterCapteurDialog(); showPage("capteurs"); });

        Button bReleve = btn("Effectuer relevé", STYLE_BTN_SECONDARY);
        bReleve.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            ferme.effectuerReleve(c);
            showPage("capteurs");
        });

        Button bStatut = btn("Changer statut", STYLE_BTN_GHOST);
        bStatut.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            ChoiceDialog<Status> d = new ChoiceDialog<>(c.getStatus(), Status.values());
            styleDialog(d);
            d.setHeaderText("Nouveau statut pour " + c.getCode());
            d.showAndWait().ifPresent(s -> { g.changerStatusCapteur(c, s); showPage("capteurs"); });
        });

        Button bSeuils = btn("Configurer seuils", STYLE_BTN_GHOST);
        bSeuils.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            configurerSeuilsDialog(c);
        });

        Button bAfficherSeuils = btn("Afficher seuils", STYLE_BTN_GHOST);
        bAfficherSeuils.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            afficherSeuilsCapteur(c);
        });

        Button bHist = btn("Historique relevés", STYLE_BTN_GHOST);
        bHist.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            showHistoriqueReleves(c);
        });

        actBar.getChildren().addAll(bAdd, bReleve, bStatut, bSeuils, bAfficherSeuils, bHist);
        page.getChildren().add(actBar);
        page.getChildren().add(tableCard);

        // Nouvelle section : Historique des relevés par zone avec graphiques
        page.getChildren().add(buildSectionHeader("Historique des relevés par zone", null));

        // ComboBox pour sélectionner la zone (afficher les codes)
        HBox selectorBox = new HBox(12);
        selectorBox.setAlignment(Pos.CENTER_LEFT);
        selectorBox.setPadding(new Insets(0, 0, 12, 0));

        ComboBox<Zone> cbZoneHistorique = new ComboBox<>();
        cbZoneHistorique.getItems().addAll(ferme.getZones().stream()
                .filter(z -> !z.getCapteurs().isEmpty())
                .collect(Collectors.toList()));

        // Personnaliser l'affichage des zones pour montrer le code
        cbZoneHistorique.setCellFactory(param -> new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(zone.getCode());
                }
            }
        });
        cbZoneHistorique.setButtonCell(new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(zone.getCode());
                }
            }
        });

        if (!cbZoneHistorique.getItems().isEmpty()) {
            cbZoneHistorique.getSelectionModel().selectFirst();
        }
        styleCombo(cbZoneHistorique);
        cbZoneHistorique.setPromptText("Sélectionner une zone");
        cbZoneHistorique.setPrefWidth(250);

        // ComboBox pour sélectionner le type de mesure
        ComboBox<String> cbMesure = new ComboBox<>();
        cbMesure.getItems().addAll("Toutes les mesures");
        styleCombo(cbMesure);
        cbMesure.setPrefWidth(180);
        cbMesure.setDisable(true);

        // DatePickers pour filtrer
        DatePicker dpDebut = new DatePicker();
        dpDebut.setPromptText("Date début");
        dpDebut.setPrefWidth(150);
        DatePicker dpFin = new DatePicker();
        dpFin.setPromptText("Date fin");
        dpFin.setPrefWidth(150);

        Button bRafraichir = btn("Rafraîchir", STYLE_BTN_PRIMARY);

        selectorBox.getChildren().addAll(
                fieldGroup("Zone", cbZoneHistorique),
                fieldGroup("Mesure", cbMesure),
                fieldGroup("Du", dpDebut),
                fieldGroup("Au", dpFin),
                bRafraichir
        );
        page.getChildren().add(selectorBox);

        // Tableau des relevés par zone
        VBox tableRelevesContainer = new VBox(0);
        tableRelevesContainer.setStyle(STYLE_CARD_FLUSH);
        TableView<Releve> tvReleves = buildStyledTable();
        tvReleves.setMaxHeight(250);
        tvReleves.getColumns().addAll(
                styledCol("Capteur", 100, r -> r.getCapteur().getCode()),
                styledCol("Date/Heure", 160, r -> r.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))),
                styledCol("Niveau", 100, r -> r.getNiveauReleve().name()),
                styledCol("Valeurs", 400, r -> r.getValeurs().toString())
        );
        tableRelevesContainer.getChildren().add(tvReleves);
        page.getChildren().add(tableRelevesContainer);

        // Zone pour afficher les graphiques
        VBox chartsContainer = new VBox(20);
        chartsContainer.setStyle(STYLE_CARD);
        chartsContainer.setPadding(new Insets(16));

        // Mettre à jour les mesures disponibles quand la zone change
        cbZoneHistorique.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Set<String> mesures = new LinkedHashSet<>();
                for (Capteurs capteur : newVal.getCapteurs()) {
                    if (!capteur.getHistorique().isEmpty()) {
                        mesures.addAll(capteur.getHistorique().get(capteur.getHistorique().size() - 1).getValeurs().keySet());
                    }
                }
                cbMesure.getItems().clear();
                cbMesure.getItems().add("Toutes les mesures");
                cbMesure.getItems().addAll(mesures);
                cbMesure.setDisable(mesures.isEmpty());
                if (!mesures.isEmpty()) {
                    cbMesure.getSelectionModel().selectFirst();
                }
                updateZoneData(newVal, cbMesure.getValue(), dpDebut.getValue(), dpFin.getValue(), tvReleves, chartsContainer);
            }
        });

        // Rafraîchir les données
        bRafraichir.setOnAction(e -> {
            Zone zone = cbZoneHistorique.getValue();
            if (zone != null) {
                updateZoneData(zone, cbMesure.getValue(), dpDebut.getValue(), dpFin.getValue(), tvReleves, chartsContainer);
            }
        });

        // Initialiser si une zone est sélectionnée
        if (!cbZoneHistorique.getItems().isEmpty()) {
            cbZoneHistorique.getSelectionModel().selectFirst();
        }

        page.getChildren().add(chartsContainer);

        return page;
    }

    // Méthode pour mettre à jour les données de la zone (tableau et graphiques)
    private void updateZoneData(Zone zone, String selectedMesure, LocalDate dateDebut, LocalDate dateFin,
                                TableView<Releve> tvReleves, VBox chartsContainer) {

        // Mettre à jour le tableau des relevés
        List<Releve> tousReleves = new ArrayList<>();
        for (Capteurs capteur : zone.getCapteurs()) {
            for (Releve r : capteur.getHistorique()) {
                // Filtrer par date
                if (dateDebut != null && r.getDateHeure().toLocalDate().isBefore(dateDebut)) continue;
                if (dateFin != null && r.getDateHeure().toLocalDate().isAfter(dateFin)) continue;

                // Si une mesure spécifique est sélectionnée, ne garder que les relevés qui ont cette mesure
                if (selectedMesure != null && !"Toutes les mesures".equals(selectedMesure)) {
                    if (r.getValeurs().containsKey(selectedMesure)) {
                        tousReleves.add(r);
                    }
                } else {
                    tousReleves.add(r);
                }
            }
        }
        tousReleves.sort(Comparator.comparing(Releve::getDateHeure).reversed());
        tvReleves.getItems().setAll(tousReleves);

        // Mettre à jour les graphiques
        chartsContainer.getChildren().clear();

        if (zone == null || zone.getCapteurs().isEmpty()) {
            Label lEmpty = new Label("Aucun capteur dans cette zone.");
            lEmpty.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
            chartsContainer.getChildren().add(lEmpty);
            return;
        }

        // Filtrer les capteurs qui ont des relevés correspondant à la mesure sélectionnée
        List<Capteurs> capteursAvecReleves = zone.getCapteurs().stream()
                .filter(c -> {
                    if (c.getHistorique().isEmpty()) return false;
                    if (selectedMesure != null && !"Toutes les mesures".equals(selectedMesure)) {
                        // Vérifier si le capteur a au moins un relevé avec cette mesure
                        return c.getHistorique().stream().anyMatch(r -> r.getValeurs().containsKey(selectedMesure));
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if (capteursAvecReleves.isEmpty()) {
            Label lEmpty = new Label("Aucun relevé disponible pour cette zone avec le filtre sélectionné.");
            lEmpty.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
            chartsContainer.getChildren().add(lEmpty);
            return;
        }

        for (Capteurs capteur : capteursAvecReleves) {
            // Filtrer les relevés par date
            List<Releve> relevesFiltres = capteur.getHistorique().stream()
                    .filter(r -> {
                        if (dateDebut != null && r.getDateHeure().toLocalDate().isBefore(dateDebut)) return false;
                        if (dateFin != null && r.getDateHeure().toLocalDate().isAfter(dateFin)) return false;
                        return true;
                    })
                    .collect(Collectors.toList());

            if (relevesFiltres.isEmpty()) continue;

            // Si une mesure spécifique est sélectionnée, ne garder que les relevés qui ont cette mesure
            if (selectedMesure != null && !"Toutes les mesures".equals(selectedMesure)) {
                relevesFiltres = relevesFiltres.stream()
                        .filter(r -> r.getValeurs().containsKey(selectedMesure))
                        .collect(Collectors.toList());
            }

            if (relevesFiltres.isEmpty()) continue;

            // Trier les relevés par date
            relevesFiltres.sort(Comparator.comparing(Releve::getDateHeure));

            // Vérifier quelles mesures sont disponibles
            Set<String> mesuresDisponibles = new LinkedHashSet<>();
            for (Releve r : relevesFiltres) {
                mesuresDisponibles.addAll(r.getValeurs().keySet());
            }

            if (mesuresDisponibles.isEmpty()) continue;

            // Déterminer quelles mesures afficher
            List<String> mesuresAAfficher;
            if (selectedMesure != null && !"Toutes les mesures".equals(selectedMesure) && mesuresDisponibles.contains(selectedMesure)) {
                mesuresAAfficher = List.of(selectedMesure);
            } else {
                mesuresAAfficher = new ArrayList<>(mesuresDisponibles);
            }

            // Créer une carte pour chaque capteur
            VBox capteurCard = new VBox(10);
            capteurCard.setStyle(STYLE_CARD);
            capteurCard.setPadding(new Insets(12));

            // En-tête du capteur
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lCapteur = new Label(capteur.getCode() + " (" + capteur.getType().name() + ")");
            lCapteur.setStyle("-fx-font-weight:bold;-fx-font-size:14px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";");

            Label lNbReleves = new Label(relevesFiltres.size() + " relevé(s)");
            lNbReleves.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:11px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Dernier relevé
            Releve dernier = relevesFiltres.get(relevesFiltres.size() - 1);
            Label lDernier = new Label("Dernier: " + dernier.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) +
                    " [" + dernier.getNiveauReleve() + "]");
            lDernier.setStyle("-fx-text-fill:" + COLOR_INFO_TEXT + ";-fx-font-size:11px;");

            header.getChildren().addAll(lCapteur, lNbReleves, spacer, lDernier);
            capteurCard.getChildren().add(header);

            // Créer un graphique pour chaque mesure
            for (String mesure : mesuresAAfficher) {
                if (!mesuresDisponibles.contains(mesure)) continue;

                // Préparer les données
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Date/Heure");
                yAxis.setLabel(mesure);
                xAxis.setTickLabelRotation(45);

                LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
                chart.setTitle(mesure);
                chart.setPrefHeight(250);
                chart.setCreateSymbols(true);
                chart.setStyle("-fx-background-color:white;-fx-border-color:" + COLOR_CARD_BORDER + ";-fx-border-radius:4;-fx-background-radius:4;");

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(mesure);

                for (Releve r : relevesFiltres) {
                    Object value = r.getValeurs().get(mesure);
                    if (value instanceof Number) {
                        String time = r.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
                        series.getData().add(new XYChart.Data<>(time, ((Number) value).doubleValue()));
                    }
                }

                chart.getData().add(series);
                capteurCard.getChildren().add(chart);
            }

            chartsContainer.getChildren().add(capteurCard);
        }
    }


    // Méthode pour ajouter des seuils sur le graphique
    private void addThresholdsToChart(LineChart<String, Number> chart, Capteurs capteur, String mesure) {
        double minSeuil = Double.NaN;
        double maxSeuil = Double.NaN;

        if (capteur instanceof Cap_env ce) {
            if ("temperature".equals(mesure) || "température".equalsIgnoreCase(mesure)) {
                minSeuil = ce.seuils.temp.getMin();
                maxSeuil = ce.seuils.temp.getMax();
            } else if ("humidite".equals(mesure) || "humidité".equalsIgnoreCase(mesure)) {
                minSeuil = ce.seuils.humidity.getMin();
                maxSeuil = ce.seuils.humidity.getMax();
            } else if ("pluviometrie".equals(mesure) || "pluviométrie".equalsIgnoreCase(mesure)) {
                minSeuil = ce.seuils.pluvi.getMin();
                maxSeuil = ce.seuils.pluvi.getMax();
            }
        } else if (capteur instanceof Cap_sol cs) {
            if ("ph".equalsIgnoreCase(mesure)) {
                minSeuil = cs.seuils.ph.getMin();
                maxSeuil = cs.seuils.ph.getMax();
            } else if ("humidite".equals(mesure) || "humidité".equalsIgnoreCase(mesure)) {
                minSeuil = cs.seuils.humidite.getMin();
                maxSeuil = cs.seuils.humidite.getMax();
            } else if ("azote".equalsIgnoreCase(mesure)) {
                minSeuil = cs.seuils.azote.getMin();
                maxSeuil = cs.seuils.azote.getMax();
            }
        } else if (capteur instanceof Cap_aqua ca) {
            if ("temperature".equals(mesure) || "température".equalsIgnoreCase(mesure)) {
                minSeuil = ca.seuils.temp.getMin();
                maxSeuil = ca.seuils.temp.getMax();
            } else if ("oxygene".equals(mesure) || "oxygène".equalsIgnoreCase(mesure)) {
                minSeuil = ca.seuils.oxygen.getMin();
                maxSeuil = ca.seuils.oxygen.getMax();
            } else if ("ph".equalsIgnoreCase(mesure)) {
                minSeuil = ca.seuils.ph.getMin();
                maxSeuil = ca.seuils.ph.getMax();
            }
        } else if (capteur instanceof Cap_biometrique cb) {
            if ("temperature_corporelle".equals(mesure) || "température corporelle".equalsIgnoreCase(mesure)) {
                minSeuil = cb.seuils.temp_corporelle.getMin();
                maxSeuil = cb.seuils.temp_corporelle.getMax();
            } else if ("activite_par_minute".equals(mesure) || "activité".equalsIgnoreCase(mesure)) {
                minSeuil = cb.seuils.activity_per_min.getMin();
                maxSeuil = cb.seuils.activity_per_min.getMax();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE ALERTES
    // ─────────────────────────────────────────────────────────────────
    private VBox buildAlertesPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Alertes", "Gestion et suivi des alertes système"));

        VBox filterCard = new VBox(12);
        filterCard.setStyle(STYLE_CARD);
        Label lFTitle = new Label("Filtres");
        lFTitle.setStyle(sectionTitleStyle());
        filterCard.getChildren().add(lFTitle);

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbZone = new ComboBox<>();
        cbZone.getItems().add("Toutes les zones");
        ferme.getZones().forEach(z -> cbZone.getItems().add(z.getCode() + " — " + z.getName()));
        cbZone.getSelectionModel().selectFirst();
        styleCombo(cbZone);

        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList(
                "Tous types", "ENV", "SOL", "AQUA", "BIOMETRIQUE", "GPS"));
        cbType.getSelectionModel().selectFirst();
        styleCombo(cbType);

        ComboBox<String> cbNiv = new ComboBox<>(FXCollections.observableArrayList(
                "Tous niveaux", "INFO", "AVERTISSEMENT", "CRITIQUE"));
        cbNiv.getSelectionModel().selectFirst();
        styleCombo(cbNiv);

        DatePicker dpDeb = new DatePicker();
        dpDeb.setPromptText("Date début");
        dpDeb.setPrefWidth(150);
        DatePicker dpFin = new DatePicker();
        dpFin.setPromptText("Date fin");
        dpFin.setPrefWidth(150);

        filterRow.getChildren().addAll(
                fieldGroup("Zone",   cbZone),
                fieldGroup("Type",   cbType),
                fieldGroup("Niveau", cbNiv),
                fieldGroup("Du",     dpDeb),
                fieldGroup("Au",     dpFin)
        );
        filterCard.getChildren().add(filterRow);

        TableView<Alerte> ta = buildAlerteTable();

        // Fonction pour trier les alertes par niveau de gravité
        Comparator<Alerte> graviteComparator = (a1, a2) -> {
            // Définir l'ordre de priorité
            Map<Niveau_gravite, Integer> order = new HashMap<>();
            order.put(Niveau_gravite.CRITIQUE, 1);
            order.put(Niveau_gravite.AVERTISSEMENT, 2);
            order.put(Niveau_gravite.INFO, 3);

            Integer order1 = order.get(a1.getGravite());
            Integer order2 = order.get(a2.getGravite());

            // Les alertes acquittées (non actives) viennent après toutes les alertes actives
            if (a1.isActive() != a2.isActive()) {
                return a1.isActive() ? -1 : 1;
            }

            // Pour les alertes actives, comparer par gravité
            if (a1.isActive() && a2.isActive()) {
                return order1.compareTo(order2);
            }

            // Pour les alertes non actives (acquittées), trier par date décroissante
            return a2.getDateCreation().compareTo(a1.getDateCreation());
        };

        Runnable refresh = () -> {
            Zone zSel = null;
            String zv = cbZone.getValue();
            if (zv != null && !zv.startsWith("Toutes")) {
                String code = zv.split(" — ")[0];
                zSel = ferme.trouverZoneParCode(code);
            }
            TypeCapteur tc = null;
            if (!"Tous types".equals(cbType.getValue())) tc = TypeCapteur.valueOf(cbType.getValue());
            Niveau_gravite nv = null;
            if (!"Tous niveaux".equals(cbNiv.getValue())) nv = Niveau_gravite.valueOf(cbNiv.getValue());

            List<Alerte> filtered = ferme.filtrer(zSel, tc, nv, dpDeb.getValue(), dpFin.getValue());
            filtered.sort(graviteComparator);
            ta.getItems().setAll(filtered);
        };

        Button bSearch = btn("Appliquer les filtres", STYLE_BTN_PRIMARY);
        bSearch.setOnAction(e -> refresh.run());
        filterCard.getChildren().add(bSearch);
        refresh.run();
        page.getChildren().add(filterCard);

        VBox tableCard = new VBox(0);
        tableCard.setStyle(STYLE_CARD_FLUSH);
        tableCard.getChildren().add(ta);
        page.getChildren().add(tableCard);

        HBox acts = new HBox(8);
        Button bAcq = btn("Acquitter", STYLE_BTN_SECONDARY);
        bAcq.setOnAction(e -> {
            Alerte a = ta.getSelectionModel().getSelectedItem();
            if (a == null) { info("Sélectionnez une alerte."); return; }
            g.acquitterAlerte(a);
            refresh.run();
        });
        Button bSup = btn("Supprimer", STYLE_BTN_GHOST);
        bSup.setStyle(STYLE_BTN_GHOST + "-fx-text-fill:" + COLOR_DANGER_TEXT + ";-fx-border-color:" + COLOR_DANGER_BORDER + ";");
        bSup.setOnAction(e -> {
            Alerte a = ta.getSelectionModel().getSelectedItem();
            if (a == null) { info("Sélectionnez une alerte."); return; }
            g.supprimerAlerte(a);
            refresh.run();
        });
        acts.getChildren().addAll(bAcq, bSup);
        page.getChildren().add(acts);
        return page;
    }

    private TableView<Alerte> buildAlerteTable() {
        TableView<Alerte> ta = buildStyledTable();
        ta.setMaxHeight(420);
        ta.getColumns().addAll(
                styledCol("ID",           50,  a -> String.valueOf(a.getId())),
                styledCol("Gravité",      120, a -> a.getGravite().name()),
                styledCol("Message",      320, Alerte::getMessage),
                styledCol("Zone",         130, a -> a.getZone() != null ? a.getZone().getName() : "—"),
                styledCol("Date",         150, a -> a.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                styledCol("Statut",       100, a -> a.getStatut().name()),
                styledCol("Type capteur", 110, a -> a.getReleve().getCapteur().getType().name())
        );
        ta.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Alerte a, boolean empty) {
                super.updateItem(a, empty);
                if (a == null || empty) {
                    setStyle("");
                    return;
                }

                // Si l'alerte est acquittée (non active), couleur verte
                if (!a.isActive()) {
                    setStyle("-fx-background-color: " + COLOR_SUCCESS_BG + ";");
                }
                // Sinon, colorer selon la gravité
                else {
                    setStyle(switch (a.getGravite()) {
                        case CRITIQUE -> "-fx-background-color: " + COLOR_DANGER_BG + ";";
                        case AVERTISSEMENT -> "-fx-background-color: " + COLOR_WARNING_BG + ";";
                        default -> "";
                    });
                }
            }
        });

        return ta;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE PRODUCTION  (point 9: filtered TypeProd per zone type)
    // ─────────────────────────────────────────────────────────────────
    private VBox buildProductionPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Production", "Enregistrement et suivi de la production par zone"));

        for (Zone z : ferme.getZones()) {
            VBox card = new VBox(12);
            card.setStyle(STYLE_CARD);

            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lh = new Label(z.getName());
            lh.setStyle("-fx-font-weight:bold;-fx-font-size:15px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
            Label lCode = new Label(z.getCode());
            lCode.setStyle("-fx-background-color:#f0f2f0;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-padding:2 8;-fx-background-radius:4;-fx-font-size:12px;");
            header.getChildren().addAll(lh, lCode);

            if (!z.getProductions().isEmpty()) {
                Region hSp = new Region(); HBox.setHgrow(hSp, Priority.ALWAYS);
                Label ltot = new Label("Total : " + String.format("%.2f", z.getTotal()));
                ltot.setStyle("-fx-font-weight:bold;-fx-text-fill:" + COLOR_ACCENT + ";-fx-font-size:14px;-fx-font-family:'Segoe UI',system;");
                header.getChildren().addAll(hSp, ltot);
            }
            card.getChildren().add(header);

            if (z.getProductions().isEmpty()) {
                Label lNone = new Label("Aucune production enregistrée pour cette zone.");
                lNone.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
                card.getChildren().add(lNone);
            } else {
                card.getChildren().add(buildDivider());
                TableView<Prod> tp = buildStyledTable();
                tp.setMaxHeight(140);
                tp.getColumns().addAll(
                        styledCol("Date",   160, Prod::getDate),
                        styledCol("Valeur", 100, p -> String.format("%.2f", p.getVal())),
                        styledCol("Unité",  80,  p -> p.getProd().getUnite()),
                        styledCol("Type",   140, p -> p.getProd().name())
                );
                tp.getItems().addAll(z.getProductions());
                card.getChildren().add(tp);
            }

            card.getChildren().add(buildDivider());

            // Point 9: filter TypeProd per zone type
            List<TypeProd> availableTypes = getTypeProdForZone(z);

            HBox addRow = new HBox(10);
            addRow.setAlignment(Pos.CENTER_LEFT);

            ComboBox<TypeProd> cbProd = new ComboBox<>(FXCollections.observableArrayList(availableTypes));
            if (!availableTypes.isEmpty()) cbProd.getSelectionModel().selectFirst();
            styleCombo(cbProd);

            TextField tfVal = styledInput("Valeur numérique", 100);
            Label errVal = errorLabel("");

            Button bEnreg = btn("Enregistrer", STYLE_BTN_PRIMARY);
            bEnreg.setOnAction(e -> {
                // Point 1 & 4: validate value
                if (tfVal.getText().isBlank()) {
                    markError(tfVal, errVal, "La valeur est obligatoire.");
                    return;
                }
                if (!isDouble(tfVal.getText())) {
                    markError(tfVal, errVal, "Valeur invalide : entrez un nombre (ex: 12.5).");
                    return;
                }
                clearError(tfVal, errVal);
                double val = Double.parseDouble(tfVal.getText().trim());
                g.enregistrerProduction(z, new Prod(val, cbProd.getValue()));
                showPage("production");
            });

            VBox valGroup = new VBox(2);
            Label lValLbl = new Label("Valeur");
            lValLbl.setStyle("-fx-font-size:11px;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-font-family:'Segoe UI',system;");
            valGroup.getChildren().addAll(lValLbl, tfVal, errVal);

            addRow.getChildren().addAll(fieldGroup("Type", cbProd), valGroup, bEnreg);
            card.getChildren().add(addRow);

            page.getChildren().add(card);
        }
        return page;
    }

    /** Point 9: returns the TypeProd values relevant to a zone type */
    private List<TypeProd> getTypeProdForZone(Zone z) {
        if (z instanceof ZoneAqua) {
            return Arrays.stream(TypeProd.values())
                    .filter(t -> t.name().contains("POIDS") || t.name().contains("RECOLTE"))
                    .collect(Collectors.toList());
        } else if (z instanceof ZoneCulture) {
            return Arrays.stream(TypeProd.values())
                    .filter(t -> t.name().contains("RENDEMENT") || t.name().contains("CULTURE"))
                    .collect(Collectors.toList());
        } else {
            // ZoneElevage: the other two
            List<TypeProd> all = new ArrayList<>(Arrays.asList(TypeProd.values()));
            all.removeIf(t -> t.name().contains("RENDEMENT") || t.name().contains("CULTURE")
                    || t.name().contains("POIDS") || t.name().contains("RECOLTE"));
            // fallback: if filter leaves nothing, return all
            if (all.isEmpty()) return Arrays.asList(TypeProd.values());
            return all;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // DIALOGS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Point 5 / 11: unified dialog for affecter (isModify=false) and modifier (isModify=true).
     * Point 11: DatePicker for plantation and récolte dates.
     * Point 1: validate required fields.
     */
    private void affecterCultureDialog(ZoneCulture zc, boolean isModify) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle((isModify ? "Modifier" : "Affecter") + " culture — " + zc.getName());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gp = styledGrid();

        TextField tfNom = styledInput(zc.getCultures() != null ? zc.getCultures().getNom() : "", 200);
        Label errNom = errorLabel("");

        // Point 11: DatePicker instead of TextField
        DatePicker dpPlant = new DatePicker();
        dpPlant.setPromptText("Sélectionner date");
        dpPlant.setPrefWidth(180);
        Label errDpPlant = errorLabel("");
        if (zc.getCultures() != null) {
            try { dpPlant.setValue(LocalDate.parse(zc.getCultures().getDatePlantation())); } catch (Exception ignored) {}
        }

        DatePicker dpRecolte = new DatePicker();
        dpRecolte.setPromptText("Sélectionner date");
        dpRecolte.setPrefWidth(180);
        Label errDpRecolte = errorLabel("");
        if (zc.getCultures() != null) {
            try { dpRecolte.setValue(LocalDate.parse(zc.getCultures().getDateRecolte())); } catch (Exception ignored) {}
        }

        ComboBox<StadeCroissance> cbS = new ComboBox<>(FXCollections.observableArrayList(StadeCroissance.values()));
        cbS.getSelectionModel().select(zc.getCultures() != null ? zc.getCultures().getStadeCroiss() : StadeCroissance.GERMINATION);
        styleCombo(cbS);

        TextField tfPhMin  = dlgInput(zc.getCultures() != null && zc.getCultures().getExigPed() != null ? String.valueOf(zc.getCultures().getExigPed().getPhMin())   : "5.5");
        TextField tfPhMax  = dlgInput(zc.getCultures() != null && zc.getCultures().getExigPed() != null ? String.valueOf(zc.getCultures().getExigPed().getPhMax())   : "7.0");
        TextField tfHMin   = dlgInput(zc.getCultures() != null && zc.getCultures().getExigPed() != null ? String.valueOf(zc.getCultures().getExigPed().getHumMin())  : "30");
        TextField tfHMax   = dlgInput(zc.getCultures() != null && zc.getCultures().getExigPed() != null ? String.valueOf(zc.getCultures().getExigPed().getHumMax())  : "80");
        TextField tfAzMin  = dlgInput(zc.getCultures() != null && zc.getCultures().getExigPed() != null ? String.valueOf(zc.getCultures().getExigPed().getAzoteMin()): "50");
        TextField tfAzMax  = dlgInput(zc.getCultures() != null && zc.getCultures().getExigPed() != null ? String.valueOf(zc.getCultures().getExigPed().getAzoteMax()): "150");

        Label errPh = errorLabel(""), errHum = errorLabel(""), errAz = errorLabel("");

        VBox nomBox = new VBox(2, tfNom, errNom);
        VBox plantBox = new VBox(2, dpPlant, errDpPlant);
        VBox recBox   = new VBox(2, dpRecolte, errDpRecolte);

        gp.addRow(0, dlgLbl("Nom de la culture :"),   nomBox);
        gp.addRow(1, dlgLbl("Date de plantation :"),   plantBox);
        gp.addRow(2, dlgLbl("Date de récolte :"),      recBox);
        gp.addRow(3, dlgLbl("Stade de croissance :"),  cbS);
        gp.addRow(4, dlgLbl("pH min / max :"),         new VBox(2, new HBox(6, tfPhMin, new Label("/"), tfPhMax), errPh));
        gp.addRow(5, dlgLbl("Humidité min / max :"),   new VBox(2, new HBox(6, tfHMin,  new Label("/"), tfHMax),  errHum));
        gp.addRow(6, dlgLbl("Azote min / max :"),      new VBox(2, new HBox(6, tfAzMin, new Label("/"), tfAzMax), errAz));

        // Point 7: make dialog scrollable
        ScrollPane sp = new ScrollPane(gp);
        sp.setFitToWidth(true);
        sp.setPrefHeight(420);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        d.getDialogPane().setContent(sp);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;-fx-font-size:13px;");

        // Override OK button to validate
        Button okBtn = (Button) d.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            boolean valid = true;

            if (tfNom.getText().isBlank()) {
                markError(tfNom, errNom, "Le nom est obligatoire.");
                valid = false;
            } else if (!isValidName(tfNom.getText())) {
                markError(tfNom, errNom, "Le nom doit être du texte, pas un nombre.");
                valid = false;
            } else clearError(tfNom, errNom);

            if (dpPlant.getValue() == null) {
                errDpPlant.setText("Date de plantation obligatoire.");
                valid = false;
            } else errDpPlant.setText("");

            if (dpRecolte.getValue() == null) {
                errDpRecolte.setText("Date de récolte obligatoire.");
                valid = false;
            } else errDpRecolte.setText("");

            // Point 4: numeric validation
            if (!isDouble(tfPhMin.getText()) || !isDouble(tfPhMax.getText())) {
                errPh.setText("Valeurs pH invalides (nombres attendus).");
                valid = false;
            } else clearError(null, errPh);

            if (!isDouble(tfHMin.getText()) || !isDouble(tfHMax.getText())) {
                errHum.setText("Valeurs humidité invalides (nombres attendus).");
                valid = false;
            } else clearError(null, errHum);

            if (!isDouble(tfAzMin.getText()) || !isDouble(tfAzMax.getText())) {
                errAz.setText("Valeurs azote invalides (nombres attendus).");
                valid = false;
            } else clearError(null, errAz);

            if (!valid) ev.consume();
        });

        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    ExigPedologiques ep = new ExigPedologiques(
                            dbl(tfPhMin), dbl(tfPhMax), dbl(tfHMin), dbl(tfHMax), dbl(tfAzMin), dbl(tfAzMax));
                    String datePlant  = dpPlant.getValue().toString();
                    String dateRecolte = dpRecolte.getValue().toString();
                    g.affecterCulture(zc, new Culture(tfNom.getText().trim(), datePlant, dateRecolte, cbS.getValue(), ep));
                } catch (Exception ex) { info("Erreur : " + ex.getMessage()); }
            }
        });
    }

    /**
     * Point 2: editable exigences pédologiques dialog (not read-only).
     */
    private void showExigPedologiquesEditable(ZoneCulture zc) {
        if (zc.getCultures() == null) {
            info("Aucune culture affectée à cette zone."); return;
        }

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Exigences pédologiques — " + zc.getCultures().getNom());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;-fx-font-size:13px;");

        GridPane gp = styledGrid();

        ExigPedologiques ep = zc.getCultures().getExigPed();
        TextField tfPhMin  = dlgInput(ep != null ? String.valueOf(ep.getPhMin())    : "5.5");
        TextField tfPhMax  = dlgInput(ep != null ? String.valueOf(ep.getPhMax())    : "7.0");
        TextField tfHMin   = dlgInput(ep != null ? String.valueOf(ep.getHumMin())   : "30");
        TextField tfHMax   = dlgInput(ep != null ? String.valueOf(ep.getHumMax())   : "80");
        TextField tfAzMin  = dlgInput(ep != null ? String.valueOf(ep.getAzoteMin()) : "50");
        TextField tfAzMax  = dlgInput(ep != null ? String.valueOf(ep.getAzoteMax()) : "150");

        Label errPh = errorLabel(""), errHum = errorLabel(""), errAz = errorLabel("");

        gp.addRow(0, dlgLbl("pH min / max :"),        new VBox(2, new HBox(6, tfPhMin, new Label("/"), tfPhMax), errPh));
        gp.addRow(1, dlgLbl("Humidité min / max :"),  new VBox(2, new HBox(6, tfHMin,  new Label("/"), tfHMax),  errHum));
        gp.addRow(2, dlgLbl("Azote min / max :"),     new VBox(2, new HBox(6, tfAzMin, new Label("/"), tfAzMax), errAz));

        ScrollPane sp = new ScrollPane(gp);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        d.getDialogPane().setContent(sp);

        Button okBtn = (Button) d.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            boolean valid = true;
            if (!isDouble(tfPhMin.getText()) || !isDouble(tfPhMax.getText())) { errPh.setText("Valeurs pH invalides."); valid = false; } else errPh.setText("");
            if (!isDouble(tfHMin.getText())  || !isDouble(tfHMax.getText()))  { errHum.setText("Valeurs humidité invalides."); valid = false; } else errHum.setText("");
            if (!isDouble(tfAzMin.getText()) || !isDouble(tfAzMax.getText())) { errAz.setText("Valeurs azote invalides."); valid = false; } else errAz.setText("");
            if (!valid) ev.consume();
        });

        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                ExigPedologiques newEp = new ExigPedologiques(
                        dbl(tfPhMin), dbl(tfPhMax), dbl(tfHMin), dbl(tfHMax), dbl(tfAzMin), dbl(tfAzMax));
                zc.getCultures().setExigPed(newEp);  // needs setter in Culture
                showPage(activePage);
            }
        });
    }

    private void showProgAlimDialog(ZoneElevage ze) {
        Dialog<ButtonType> d = progAlimDialog(ze.getProgAlim());
        d.setTitle("Programme alimentaire — " + ze.getName());
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                GridPane gp = (GridPane) ((ScrollPane) d.getDialogPane().getContent()).getContent();
                String type = ((TextField)((HBox)gp.getChildren().get(1)).getChildren().get(0)).getText();
                double q = dbl(((TextField)((HBox)gp.getChildren().get(3)).getChildren().get(0)));
                int r = (int) dbl(((TextField)((HBox)gp.getChildren().get(5)).getChildren().get(0)));
                if (type.isBlank()) { info("Le type d'aliment est obligatoire."); return; }
                g.definirProgAlim(ze, new ProgAlimentaire(type, q, r));
            }
        });
    }

    private void showProgAlimDialog(ZoneAqua za) {
        Dialog<ButtonType> d = progAlimDialog(za.getProgAlim());
        d.setTitle("Programme alimentaire — " + za.getName());
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                GridPane gp = (GridPane) ((ScrollPane) d.getDialogPane().getContent()).getContent();
                String type = ((TextField)((HBox)gp.getChildren().get(1)).getChildren().get(0)).getText();
                double q = dbl(((TextField)((HBox)gp.getChildren().get(3)).getChildren().get(0)));
                int r = (int) dbl(((TextField)((HBox)gp.getChildren().get(5)).getChildren().get(0)));
                if (type.isBlank()) { info("Le type d'aliment est obligatoire."); return; }
                g.definirProgAlim(za, new ProgAlimentaire(type, q, r));
            }
        });
    }

    private Dialog<ButtonType> progAlimDialog(ProgAlimentaire existing) {
        Dialog<ButtonType> d = new Dialog<>();
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");

        GridPane gp = styledGrid();
        TextField tfType = dlgInput(existing != null ? existing.getTypeAliment() : "");
        Label errType = errorLabel("");
        TextField tfQ   = dlgInput(existing != null ? String.valueOf(existing.getQuantiteParRepas()) : "1.0");
        Label errQ = errorLabel("");
        TextField tfR   = dlgInput(existing != null ? String.valueOf(existing.getRepasParJour()) : "2");
        Label errR = errorLabel("");

        gp.addRow(0, dlgLbl("Type d'aliment :"),        new HBox(tfType));
        gp.addRow(1, new Label(), errType);
        gp.addRow(2, dlgLbl("Quantité / repas (kg) :"),  new HBox(tfQ));
        gp.addRow(3, new Label(), errQ);
        gp.addRow(4, dlgLbl("Repas / jour :"),            new HBox(tfR));
        gp.addRow(5, new Label(), errR);
        if (existing != null) {
            gp.addRow(6, dlgLbl("Total / jour :"), new Label(String.format("%.2f kg", existing.getQuantiteJournaliere())));
        }

        // Point 7: scrollable
        ScrollPane sp = new ScrollPane(gp);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        d.getDialogPane().setContent(sp);

        Button okBtn = (Button) d.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            boolean valid = true;
            if (tfType.getText().isBlank()) { errType.setText("Le type d'aliment est obligatoire."); valid = false; }
            else if (!isValidName(tfType.getText())) { errType.setText("Le type d'aliment doit être du texte, pas un nombre."); valid = false; }
            else errType.setText("");
            if (!isDouble(tfQ.getText()))   { errQ.setText("Valeur numérique requise."); valid = false; } else errQ.setText("");
            if (!isInt(tfR.getText()))      { errR.setText("Entier requis (ex: 2)."); valid = false; } else errR.setText("");
            if (!valid) ev.consume();
        });

        return d;
    }

    private void ajouterAnimalDialog(ZoneElevage ze) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Ajouter un animal — " + ze.getName());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");

        GridPane gp = styledGrid();
        TextField tfEsp = dlgInput("");
        Label errEsp = errorLabel("");
        ComboBox<TypeAnimal> cbT = new ComboBox<>(FXCollections.observableArrayList(TypeAnimal.values()));
        cbT.getSelectionModel().select(ze.getTypeAnimal());
        styleCombo(cbT);
        TextField tfAge  = dlgInput("1");
        Label errAge = errorLabel("");
        TextField tfPoid = dlgInput("50");
        Label errPoid = errorLabel("");

        gp.addRow(0, dlgLbl("Nom de l'espèce :"), new VBox(2, tfEsp, errEsp));
        gp.addRow(1, dlgLbl("Type d'animal :"),   cbT);
        gp.addRow(2, dlgLbl("Age (ans) :"),        new VBox(2, tfAge, errAge));
        gp.addRow(3, dlgLbl("Poids (kg) :"),       new VBox(2, tfPoid, errPoid));

        ScrollPane sp = new ScrollPane(gp);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        d.getDialogPane().setContent(sp);

        Button okBtn = (Button) d.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            boolean valid = true;
            if (tfEsp.getText().isBlank()) { markError(tfEsp, errEsp, "Le nom d'espèce est obligatoire."); valid = false; }
            else if (!isValidName(tfEsp.getText())) { markError(tfEsp, errEsp, "Le nom d'espèce doit être du texte, pas un nombre."); valid = false; }
            else clearError(tfEsp, errEsp);
            if (!isInt(tfAge.getText()))   { markError(tfAge, errAge, "Age invalide (entier requis)."); valid = false; }   else clearError(tfAge, errAge);
            if (!isDouble(tfPoid.getText())){ markError(tfPoid, errPoid, "Poids invalide (nombre requis)."); valid = false; } else clearError(tfPoid, errPoid);
            if (!valid) ev.consume();
        });

        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    g.affecterAnimal(ze, new Animal(new EspeceAnim(cbT.getValue(), tfEsp.getText().trim()),
                            (int) dbl(tfAge), dbl(tfPoid), EtatSante.SAIN));
                } catch (Exception ex) { info(ex.getMessage()); }
            }
        });
    }

    /**
     * Point 8: if TypeEvenement == PRISE_DE_POIDS, the "valeur" field is mandatory.
     */
    private void evenementSanitaireDialog(Animal a) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Evénement sanitaire — Animal #" + a.getID());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");

        GridPane gp = styledGrid();
        ComboBox<TypeEvenement> cbT = new ComboBox<>(FXCollections.observableArrayList(TypeEvenement.values()));
        cbT.getSelectionModel().selectFirst();
        styleCombo(cbT);

        TextField tfDesc = dlgInput("");
        Label errDesc = errorLabel("");
        TextField tfVal  = dlgInput("0");
        Label errVal = errorLabel("");
        Label lValLabel = dlgLbl("Valeur :");

        // Show hint that value is required for PRISE_DE_POIDS
        Label lValHint = new Label("");
        lValHint.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:11px;-fx-font-style:italic;");

        cbT.setOnAction(e -> {
            if (cbT.getValue() != null && cbT.getValue().name().contains("POIDS")) {
                lValLabel.setText("Valeur (kg) * :");
                lValHint.setText("Obligatoire pour prise de poids");
                tfVal.setStyle(STYLE_INPUT + "-fx-border-color:" + COLOR_ACCENT_BORDER + ";");
            } else {
                lValLabel.setText("Valeur :");
                lValHint.setText("Optionnel");
                tfVal.setStyle(STYLE_INPUT);
            }
        });

        gp.addRow(0, dlgLbl("Type d'événement :"),         cbT);
        gp.addRow(1, dlgLbl("Description :"),               new VBox(2, tfDesc, errDesc));
        gp.addRow(2, lValLabel,                             new VBox(2, tfVal, lValHint, errVal));

        ScrollPane sp = new ScrollPane(gp);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        d.getDialogPane().setContent(sp);

        Button okBtn = (Button) d.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            boolean valid = true;
            if (tfDesc.getText().isBlank()) { markError(tfDesc, errDesc, "La description est obligatoire."); valid = false; } else clearError(tfDesc, errDesc);
            // Point 8: mandatory value for PRISE_DE_POIDS
            if (cbT.getValue() != null && cbT.getValue().name().contains("POIDS")) {
                if (tfVal.getText().isBlank() || !isDouble(tfVal.getText())) {
                    markError(tfVal, errVal, "Le poids est obligatoire (nombre requis)."); valid = false;
                } else clearError(tfVal, errVal);
            } else {
                if (!tfVal.getText().isBlank() && !isDouble(tfVal.getText())) {
                    markError(tfVal, errVal, "Valeur invalide (nombre attendu)."); valid = false;
                } else clearError(tfVal, errVal);
            }
            if (!valid) ev.consume();
        });

        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                a.enregistrerEvenementSanitaire(new EvenementSanitaire(cbT.getValue(), tfDesc.getText(), dbl(tfVal)));
            }
        });
    }

    private void showHistoriqueSanitaire(Animal a) {
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(al);
        al.setTitle("Historique sanitaire — Animal #" + a.getID());
        al.setHeaderText(a.getEspece().getName() + "  |  Etat actuel : " + a.getEtat());
        StringBuilder sb = new StringBuilder();
        if (a.getHistoriqueSanitaire().isEmpty()) sb.append("Aucun événement enregistré.");
        else a.getHistoriqueSanitaire().forEach(ev -> sb.append(ev).append("\n"));
        TextArea ta = new TextArea(sb.toString());
        ta.setEditable(false); ta.setWrapText(true); ta.setPrefHeight(220);
        ta.setStyle(STYLE_INPUT + "-fx-pref-width:460px;");
        // Point 7: scrollable
        ScrollPane sp = new ScrollPane(ta);
        sp.setFitToWidth(true);
        sp.setPrefHeight(240);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        al.getDialogPane().setContent(sp);
        al.showAndWait();
    }

    private void ajouterCapteurDialog() {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Ajouter un capteur");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");

        GridPane gp = styledGrid();

        ComboBox<TypeCapteur> cbType = new ComboBox<>(FXCollections.observableArrayList(TypeCapteur.values()));
        cbType.getSelectionModel().selectFirst();
        styleCombo(cbType);

        // Initialisation de la ComboBox des zones (vide au départ)
        ComboBox<Zone> cbZone = new ComboBox<>();
        styleCombo(cbZone);

        // ComboBox pour l'animal (visible uniquement pour GPS)
        ComboBox<Animal> cbAnimal = new ComboBox<>();
        styleCombo(cbAnimal);
        cbAnimal.setVisible(false);
        cbAnimal.setManaged(false);

        // Personnaliser l'affichage des animaux
        cbAnimal.setCellFactory(param -> new ListCell<Animal>() {
            @Override
            protected void updateItem(Animal animal, boolean empty) {
                super.updateItem(animal, empty);
                if (empty || animal == null) {
                    setText(null);
                } else {
                    setText("Animal #" + animal.getID() + " - " + animal.getEspece().getName());
                }
            }
        });

        cbAnimal.setButtonCell(new ListCell<Animal>() {
            @Override
            protected void updateItem(Animal animal, boolean empty) {
                super.updateItem(animal, empty);
                if (empty || animal == null) {
                    setText(null);
                } else {
                    setText("Animal #" + animal.getID() + " - " + animal.getEspece().getName());
                }
            }
        });

        // Personnaliser l'affichage des zones pour montrer le code
        cbZone.setCellFactory(param -> new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(zone.getCode());
                }
            }
        });

        // Personnaliser l'affichage dans le bouton de sélection
        cbZone.setButtonCell(new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(zone.getCode());
                }
            }
        });

        // Mise à jour des zones selon le type sélectionné
        cbType.valueProperty().addListener((obs, oldVal, newVal) -> {
            List<Zone> zonesFiltrees = new ArrayList<>();
            boolean showAnimal = false;

            if (newVal != null) {
                switch (newVal) {
                    case AQUA:
                        zonesFiltrees = ferme.getZones().stream()
                                .filter(z -> z instanceof ZoneAqua)
                                .collect(Collectors.toList());
                        showAnimal = false;
                        break;
                    case ENV:
                        zonesFiltrees = ferme.getZones().stream()
                                .filter(z -> z instanceof ZoneCulture)
                                .collect(Collectors.toList());
                        showAnimal = false;
                        break;
                    case SOL:
                        zonesFiltrees = ferme.getZones().stream()
                                .filter(z -> z instanceof ZoneCulture)
                                .collect(Collectors.toList());
                        showAnimal = false;
                        break;
                    case BIOMETRIQUE:
                        zonesFiltrees = ferme.getZones().stream()
                                .filter(z -> z instanceof ZoneElevage)
                                .collect(Collectors.toList());
                        showAnimal = false;
                        break;
                    case GPS:
                        zonesFiltrees = ferme.getZones().stream()
                                .filter(z -> z instanceof ZoneElevage && !((ZoneElevage)z).getAnimaux().isEmpty())
                                .collect(Collectors.toList());
                        showAnimal = true;
                        break;
                    default:
                        zonesFiltrees = new ArrayList<>(ferme.getZones());
                        showAnimal = false;
                }
            }

            cbZone.setItems(FXCollections.observableArrayList(zonesFiltrees));
            if (!zonesFiltrees.isEmpty()) {
                cbZone.getSelectionModel().selectFirst();
            }

            // Afficher/masquer la comboBox animal
            cbAnimal.setVisible(showAnimal);
            cbAnimal.setManaged(showAnimal);
            if (showAnimal) {
                // Mettre à jour la liste des animaux quand la zone change
                updateAnimalList(cbZone.getValue(), cbAnimal);
            }
        });

        // Mettre à jour les animaux quand la zone change
        cbZone.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (cbType.getValue() == TypeCapteur.GPS) {
                updateAnimalList(newVal, cbAnimal);
            }
        });

        // Déclencher le premier filtrage
        cbType.getSelectionModel().selectFirst();

        gp.addRow(0, dlgLbl("Type :"),  cbType);
        gp.addRow(1, dlgLbl("Zone :"),  cbZone);
        gp.addRow(2, dlgLbl("Animal :"),  cbAnimal);
        d.getDialogPane().setContent(gp);

        d.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK || cbZone.getValue() == null) return;

            // Vérification pour GPS : animal requis
            if (cbType.getValue() == TypeCapteur.GPS && cbAnimal.getValue() == null) {
                info("Veuillez sélectionner un animal pour le capteur GPS");
                return;
            }

            Zone z = cbZone.getValue();
            try {
                Capteurs c = null;
                switch (cbType.getValue()) {
                    case ENV         -> c = new Cap_env(z, Status.ACTIF);
                    case SOL         -> c = new Cap_sol(z, Status.ACTIF);
                    case AQUA        -> c = new Cap_aqua(z, Status.ACTIF);
                    case BIOMETRIQUE -> c = new Cap_biometrique(z, Status.ACTIF);
                    case GPS         -> {
                        Animal anGPS = cbAnimal.getValue();
                        c = new Capteur_GPS(z, Status.ACTIF, anGPS);
                    }
                }
                if (c != null) g.ajouterCapteur(z, c);
            } catch (Exception ex) { info("Erreur : " + ex.getMessage()); }
        });
    }

    // Méthode utilitaire pour mettre à jour la liste des animaux
    private void updateAnimalList(Zone zone, ComboBox<Animal> cbAnimal) {
        if (zone instanceof ZoneElevage ze) {
            List<Animal> animaux = ze.getAnimaux();
            if (animaux != null && !animaux.isEmpty()) {
                cbAnimal.setItems(FXCollections.observableArrayList(animaux));
                cbAnimal.getSelectionModel().selectFirst();
                cbAnimal.setDisable(false);
            } else {
                cbAnimal.setItems(FXCollections.observableArrayList());
                cbAnimal.setDisable(true);
                cbAnimal.setPromptText("Aucun animal dans cette zone");
            }
        } else {
            cbAnimal.setItems(FXCollections.observableArrayList());
            cbAnimal.setDisable(true);
        }
    }

    private void configurerSeuilsDialog(Capteurs c) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Configuration des seuils — " + c.getCode());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");
        GridPane gp = styledGrid();

        if (c instanceof Cap_env ce) {
            TextField tTMin=dlgInput("8"),tTMax=dlgInput("32"),tHMin=dlgInput("40"),tHMax=dlgInput("80"),tPMin=dlgInput("500"),tPMax=dlgInput("1200");
            gp.addRow(0,dlgLbl("Temp. min / max :"),    new HBox(6,tTMin,new Label("/"),tTMax));
            gp.addRow(1,dlgLbl("Humidité min / max :"), new HBox(6,tHMin,new Label("/"),tHMax));
            gp.addRow(2,dlgLbl("Pluvio. min / max :"),  new HBox(6,tPMin,new Label("/"),tPMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{
                if(bt==ButtonType.OK) {
                    double tempMin = dbl(tTMin);
                    double tempMax = dbl(tTMax);
                    double humMin = dbl(tHMin);
                    double humMax = dbl(tHMax);
                    double pluMin = dbl(tPMin);
                    double pluMax = dbl(tPMax);

                    // Vérification et permutation si min > max
                    if (tempMin > tempMax) { double temp = tempMin; tempMin = tempMax; tempMax = temp; }
                    if (humMin > humMax) { double temp = humMin; humMin = humMax; humMax = temp; }
                    if (pluMin > pluMax) { double temp = pluMin; pluMin = pluMax; pluMax = temp; }

                    ce.configurer(tempMin, tempMax, humMin, humMax, pluMin, pluMax);
                    info("Seuils configurés avec succès !");
                }
            });
        } else if (c instanceof Cap_sol cs) {
            TextField tHMin=dlgInput("15"),tHMax=dlgInput("22"),tPhMin=dlgInput("5.8"),tPhMax=dlgInput("6.8"),tAzMin=dlgInput("25"),tAzMax=dlgInput("50");
            gp.addRow(0,dlgLbl("Humidité min / max :"),new HBox(6,tHMin,new Label("/"),tHMax));
            gp.addRow(1,dlgLbl("pH min / max :"),      new HBox(6,tPhMin,new Label("/"),tPhMax));
            gp.addRow(2,dlgLbl("Azote min / max :"),   new HBox(6,tAzMin,new Label("/"),tAzMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{
                if(bt==ButtonType.OK) {
                    double humMin = dbl(tHMin);
                    double humMax = dbl(tHMax);
                    double phMin = dbl(tPhMin);
                    double phMax = dbl(tPhMax);
                    double azMin = dbl(tAzMin);
                    double azMax = dbl(tAzMax);

                    // Vérification et permutation si min > max
                    if (humMin > humMax) { double temp = humMin; humMin = humMax; humMax = temp; }
                    if (phMin > phMax) { double temp = phMin; phMin = phMax; phMax = temp; }
                    if (azMin > azMax) { double temp = azMin; azMin = azMax; azMax = temp; }

                    cs.configurerHum(humMin, humMax);
                    cs.configurerTemp(phMin, phMax);
                    cs.configurerPh(azMin, azMax);
                    info("Seuils configurés avec succès !");
                }
            });
        } else if (c instanceof Cap_aqua ca) {
            TextField tTMin=dlgInput("20"),tTMax=dlgInput("30"),tOMin=dlgInput("5.5"),tOMax=dlgInput("6.5"),tPhMin=dlgInput("6"),tPhMax=dlgInput("9");
            gp.addRow(0,dlgLbl("Temp. min / max :"),  new HBox(6,tTMin,new Label("/"),tTMax));
            gp.addRow(1,dlgLbl("Oxygène min / max :"),new HBox(6,tOMin,new Label("/"),tOMax));
            gp.addRow(2,dlgLbl("pH min / max :"),     new HBox(6,tPhMin,new Label("/"),tPhMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{
                if(bt==ButtonType.OK) {
                    double tempMin = dbl(tTMin);
                    double tempMax = dbl(tTMax);
                    double oxyMin = dbl(tOMin);
                    double oxyMax = dbl(tOMax);
                    double phMin = dbl(tPhMin);
                    double phMax = dbl(tPhMax);

                    // Vérification et permutation si min > max
                    if (tempMin > tempMax) { double temp = tempMin; tempMin = tempMax; tempMax = temp; }
                    if (oxyMin > oxyMax) { double temp = oxyMin; oxyMin = oxyMax; oxyMax = temp; }
                    if (phMin > phMax) { double temp = phMin; phMin = phMax; phMax = temp; }

                    ca.configurer(tempMin, tempMax, oxyMin, oxyMax, phMin, phMax);
                    info("Seuils configurés avec succès !");
                }
            });
        } else if (c instanceof Cap_biometrique cb) {
            TextField tTMin=dlgInput("38"),tTMax=dlgInput("41"),tAMin=dlgInput("0"),tAMax=dlgInput("260");
            gp.addRow(0,dlgLbl("Temp. min / max :"),    new HBox(6,tTMin,new Label("/"),tTMax));
            gp.addRow(1,dlgLbl("Activité min / max :"), new HBox(6,tAMin,new Label("/"),tAMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{
                if(bt==ButtonType.OK) {
                    double tempMin = dbl(tTMin);
                    double tempMax = dbl(tTMax);
                    double actMin = dbl(tAMin);
                    double actMax = dbl(tAMax);

                    // Vérification et permutation si min > max
                    if (tempMin > tempMax) { double temp = tempMin; tempMin = tempMax; tempMax = temp; }
                    if (actMin > actMax) { double temp = actMin; actMin = actMax; actMax = temp; }

                    cb.configurer(tempMin, tempMax, actMin, actMax);
                    info("Seuils configurés avec succès !");
                }
            });
        } else if (c instanceof Capteur_GPS cg) {
            TextField tLonMin=dlgInput("-5"),tLonMax=dlgInput("5"),tLatMin=dlgInput("-5"),tLatMax=dlgInput("5");
            gp.addRow(0,dlgLbl("Longitude min / max :"), new HBox(6,tLonMin,new Label("/"),tLonMax));
            gp.addRow(1,dlgLbl("Latitude min / max :"),  new HBox(6,tLatMin,new Label("/"),tLatMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{
                if(bt==ButtonType.OK) {
                    double lonMin = dbl(tLonMin);
                    double lonMax = dbl(tLonMax);
                    double latMin = dbl(tLatMin);
                    double latMax = dbl(tLatMax);

                    // Vérification et permutation si min > max
                    if (lonMin > lonMax) { double temp = lonMin; lonMin = lonMax; lonMax = temp; }
                    if (latMin > latMax) { double temp = latMin; latMin = latMax; latMax = temp; }

                    cg.configurer(lonMin, lonMax, latMin, latMax);
                    info("Seuils configurés avec succès !");
                }
            });
        } else {
            info("Ce type de capteur n'est pas configurable.");
            return;
        }
        // show only if not already shown inside branches above — the d.showAndWait is inside each branch
    }

    private void showHistoriqueReleves(Capteurs c) {
        Stage s = new Stage();
        s.setTitle("Historique des relevés — " + c.getCode());
        VBox vRoot = new VBox(16);
        vRoot.setPadding(new Insets(20));
        vRoot.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";");

        Label lTitle = new Label("Historique — " + c.getCode() + "  (" + c.getType() + ")");
        lTitle.setStyle("-fx-font-size:17px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
        vRoot.getChildren().add(lTitle);

        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        DatePicker dpD = new DatePicker(), dpF = new DatePicker();
        dpD.setPromptText("Date début"); dpF.setPromptText("Date fin");

        TableView<Releve> tr = buildStyledTable();
        tr.setMaxHeight(300);
        tr.getColumns().addAll(
                styledCol("ID",           50,  r -> String.valueOf(r.getId())),
                styledCol("Date / Heure", 170, r -> r.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))),
                styledCol("Niveau",       120, r -> r.getNiveauReleve().name()),
                styledCol("Valeurs",      380, r -> r.getValeurs().toString())
        );
        tr.getItems().addAll(c.getHistorique());

        Button bFilt = btn("Filtrer", STYLE_BTN_PRIMARY);
        bFilt.setOnAction(e -> {
            List<Releve> filtered = ReleveSpecifications.filtrer(c.getHistorique(), dpD.getValue(), dpF.getValue());
            tr.getItems().setAll(filtered);
        });

        filterRow.getChildren().addAll(fieldGroup("Du", dpD), fieldGroup("Au", dpF), bFilt);
        filterRow.setAlignment(Pos.BOTTOM_LEFT);

        VBox tableCard = new VBox(0);
        tableCard.setStyle(STYLE_CARD_FLUSH);
        tableCard.getChildren().add(tr);

        vRoot.getChildren().addAll(filterRow, tableCard);

        if (c.getHistorique().size() > 1) {
            Label lChart = new Label("Evolution des mesures");
            lChart.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
            vRoot.getChildren().add(lChart);

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(null);
            chart.setLegendVisible(true);
            chart.setPrefHeight(240);
            chart.setStyle("-fx-background-color:white;-fx-border-color:" + COLOR_CARD_BORDER + ";-fx-border-radius:8;-fx-background-radius:8;");

            Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
            for (Releve r : c.getHistorique()) {
                String time = r.getDateHeure().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                for (Map.Entry<String, Object> entry : r.getValeurs().entrySet()) {
                    seriesMap.computeIfAbsent(entry.getKey(), k -> { XYChart.Series<String, Number> ss = new XYChart.Series<>(); ss.setName(k); return ss; });
                    try { seriesMap.get(entry.getKey()).getData().add(new XYChart.Data<>(time, ((Number) entry.getValue()).doubleValue())); } catch (Exception ignored) {}
                }
            }
            chart.getData().addAll(seriesMap.values());
            vRoot.getChildren().add(chart);
        } else {
            Label lNoData = new Label("Données insuffisantes pour afficher le graphique.");
            lNoData.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
            vRoot.getChildren().add(lNoData);
        }

        // Point 7: scrollable window
        ScrollPane sp = new ScrollPane(vRoot);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";-fx-border-color:transparent;-fx-background:" + COLOR_PAGE_BG + ";");
        s.setScene(new Scene(sp, 800, 680));
        s.show();
    }

    private void afficherSeuilsCapteur(Capteurs c) {
        String seuilsText = "";

        if (c instanceof Cap_env) {
            seuilsText = ((Cap_env) c).display_seuils();
        } else if (c instanceof Cap_sol) {
            seuilsText = ((Cap_sol) c).display_seuils();
        } else if (c instanceof Cap_aqua) {
            seuilsText = ((Cap_aqua) c).display_seuils();
        } else if (c instanceof Cap_biometrique) {
            seuilsText = ((Cap_biometrique) c).display_seuils();
        } else if (c instanceof Capteur_GPS) {
            seuilsText = ((Capteur_GPS) c).display_seuils();
        } else {
            seuilsText = "Aucune configuration de seuils disponible pour ce type de capteur.";
        }

        Alert al = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(al);
        al.setTitle("Seuils du capteur — " + c.getCode());
        al.setHeaderText(c.getType().name() + " — " + c.getCode());

        TextArea ta = new TextArea(seuilsText);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefHeight(400);
        ta.setPrefWidth(550);
        ta.setStyle(STYLE_INPUT + "-fx-font-family:'Courier New',monospace;-fx-font-size:12px;");

        al.getDialogPane().setContent(ta);
        al.showAndWait();
    }

    // ─────────────────────────────────────────────────────────────────
    // POINT 10: Delete zone with its capteurs and alertes
    // ─────────────────────────────────────────────────────────────────
    /**
     * Removes zone and all its capteurs from ferme.getTousLesCapteurs(),
     * plus all alertes linked to the zone.
     * Added method logic here; Gestionnaire.supprimerZone() handles zone list removal.
     */
    private void supprimerZoneAvecDependances(Zone z) {
        // Remove capteurs of this zone from global list
        ferme.getTousLesCapteurs().removeAll(z.getCapteurs());
        // Remove alertes linked to this zone
        ferme.getAlertes().removeIf(a -> a.getZone() != null && a.getZone().equals(z));
        // Remove the zone itself
        g.supprimerZone(z.getCode());
        // Also remove from expanded tracking
        expandedZones.remove(z.getCode());
    }

    // ─────────────────────────────────────────────────────────────────
    // UI BUILDING HELPERS
    // ─────────────────────────────────────────────────────────────────

    private VBox pageContainer() {
        VBox p = new VBox(20);
        p.setPadding(new Insets(28, 28, 28, 28));
        p.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";");
        return p;
    }

    private VBox buildPageHeader(String title, String subtitle) {
        VBox h = new VBox(4);
        h.setPadding(new Insets(0, 0, 8, 0));
        Label lTitle = new Label(title);
        lTitle.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
        h.getChildren().add(lTitle);
        if (subtitle != null && !subtitle.isBlank()) {
            Label lSub = new Label(subtitle);
            lSub.setStyle("-fx-font-size:13px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");
            h.getChildren().add(lSub);
        }
        Region sep = new Region();
        sep.setPrefHeight(1); sep.setMaxHeight(1);
        sep.setStyle("-fx-background-color:" + COLOR_CARD_BORDER + ";");
        sep.setPadding(new Insets(8, 0, 0, 0));
        return new VBox(8, h, sep);
    }

    private HBox buildSectionHeader(String title, String note) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(title);
        l.setStyle(sectionTitleStyle());
        row.getChildren().add(l);
        if (note != null) {
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Label lNote = new Label(note);
            lNote.setStyle("-fx-font-size:12px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");
            row.getChildren().addAll(sp, lNote);
        }
        return row;
    }

    private VBox buildStatCard(String label, String value, String sublabel, String bgColor, String textColor) {
        VBox c = new VBox(4);
        c.setStyle("-fx-background-color:" + bgColor + ";-fx-padding:16 20;-fx-background-radius:" + RADIUS_MD + ";-fx-border-color:" + COLOR_CARD_BORDER + ";-fx-border-width:1;-fx-border-radius:" + RADIUS_MD + ";");
        c.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(c, Priority.ALWAYS);
        Label lVal   = new Label(value);   lVal.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:" + textColor + ";-fx-font-family:'Segoe UI',system;");
        Label lLabel = new Label(label);   lLabel.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + textColor + ";-fx-font-family:'Segoe UI',system;");
        Label lSub   = new Label(sublabel);lSub.setStyle("-fx-font-size:11px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");
        c.getChildren().addAll(lVal, lLabel, lSub);
        return c;
    }

    private HBox buildAlertRow(Alerte a, boolean withTopBorder) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        if (withTopBorder) row.setStyle("-fx-border-color:" + COLOR_CARD_BORDER + " transparent transparent transparent;-fx-border-width:1 0 0 0;");

        String nivColor = switch (a.getGravite()) { case CRITIQUE -> COLOR_DANGER_TEXT; case AVERTISSEMENT -> COLOR_WARNING_TEXT; default -> COLOR_SUCCESS_TEXT; };
        String nivBg    = switch (a.getGravite()) { case CRITIQUE -> COLOR_DANGER_BG;   case AVERTISSEMENT -> COLOR_WARNING_BG;   default -> COLOR_SUCCESS_BG;   };

        Label lNiv = new Label(a.getGravite().name());
        lNiv.setStyle("-fx-background-color:" + nivBg + ";-fx-text-fill:" + nivColor + ";-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;-fx-min-width:110px;-fx-alignment:center;");

        Label lMsg = new Label(a.getMessage());
        lMsg.setStyle("-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-size:13px;-fx-font-family:'Segoe UI',system;");
        HBox.setHgrow(lMsg, Priority.ALWAYS);

        Label lZone = new Label(a.getZone() != null ? a.getZone().getName() : "");
        lZone.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:12px;");
        Label lDate = new Label(a.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));
        lDate.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:12px;");

        row.getChildren().addAll(lNiv, lMsg, lZone, lDate);
        return row;
    }

    private VBox buildDetailChip(String key, String value) {
        VBox chip = new VBox(1);
        chip.setStyle("-fx-background-color:#f7f8f6;-fx-border-color:" + COLOR_CARD_BORDER + ";-fx-border-width:1;-fx-padding:6 12;-fx-background-radius:6;-fx-border-radius:6;");
        Label lKey = new Label(key);
        lKey.setStyle("-fx-font-size:10px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");
        Label lVal = new Label(value != null ? value : "—");
        lVal.setStyle("-fx-font-size:13px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;");
        chip.getChildren().addAll(lKey, lVal);
        return chip;
    }

    private Region buildDivider() {
        Region r = new Region();
        r.setPrefHeight(1); r.setMaxHeight(1);
        r.setStyle("-fx-background-color:" + COLOR_CARD_BORDER + ";");
        return r;
    }

    private VBox fieldGroup(String label, javafx.scene.Node control) {
        VBox g = new VBox(4);
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-font-family:'Segoe UI',system;");
        g.getChildren().addAll(l, control);
        return g;
    }
    // ── Table helpers ──────────────────────────────────────────────────

    private <T> TableView<T> buildStyledTable() {
        TableView<T> t = new TableView<>();
        t.setStyle(STYLE_TABLE);
        t.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        return t;
    }

    private <T> TableColumn<T, String> styledCol(String title, int width, java.util.function.Function<T, String> fn) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        c.setStyle("-fx-alignment:center-left;-fx-font-family:'Segoe UI',system;");
        c.setCellValueFactory(cd -> new SimpleStringProperty(fn.apply(cd.getValue())));
        return c;
    }

    private <T> void styledStatusRows(TableView<T> tv, java.util.function.Function<T, String> signal) {
        tv.setRowFactory(t -> new TableRow<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                setStyle(switch (signal.apply(item)) {
                    case "warn" -> "-fx-background-color:" + COLOR_WARNING_BG + ";";
                    case "bad"  -> "-fx-background-color:" + COLOR_DANGER_BG  + ";";
                    default     -> "";
                });
            }
        });
    }

    // ── Form helpers ───────────────────────────────────────────────────

    private TextField styledInput(String prompt, double width) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.setPrefWidth(width);
        t.setStyle(STYLE_INPUT);
        return t;
    }

    private TextField dlgInput(String value) {
        TextField t = new TextField(value);
        t.setPrefWidth(160);
        t.setStyle(STYLE_INPUT);
        return t;
    }

    private Label dlgLbl(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:13px;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-font-family:'Segoe UI',system;");
        l.setMinWidth(200);
        return l;
    }

    private GridPane styledGrid() {
        GridPane gp = new GridPane();
        gp.setHgap(16); gp.setVgap(12);
        gp.setPadding(new Insets(16));
        gp.setStyle("-fx-background-color:white;");
        return gp;
    }

    private <T> void styleCombo(ComboBox<T> cb) {
        cb.setStyle(STYLE_INPUT + "-fx-padding:4 8;");
    }

    private Button btn(String text, String style) {
        Button b = new Button(text);
        b.setStyle(style);
        return b;
    }

    private double dbl(TextField tf) {
        try { return Double.parseDouble(tf.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void info(String msg) {
        Alert al = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        styleDialog(al);
        al.showAndWait();
    }

    private void styleDialog(Dialog<?> d) {
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;-fx-font-size:13px;");
    }

    private String sectionTitleStyle() {
        return "-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;";
    }

    // ── Validation helpers (points 1, 4) ───────────────────────────────

    /** Creates a compact red error label (initially hidden/empty). */
    private Label errorLabel(String msg) {
        Label l = new Label(msg);
        l.setStyle("-fx-text-fill:" + COLOR_DANGER_TEXT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI',system;");
        return l;
    }

    private void markError(TextField tf, Label errLabel, String msg) {
        if (tf != null) tf.setStyle(STYLE_INPUT_ERROR);
        errLabel.setText(msg);
    }

    private void clearError(TextField tf, Label errLabel) {
        if (tf != null) tf.setStyle(STYLE_INPUT);
        errLabel.setText("");
    }

    private boolean isDouble(String s) {
        if (s == null || s.isBlank()) return false;
        try { Double.parseDouble(s.trim()); return true; } catch (NumberFormatException e) { return false; }
    }

    private boolean isInt(String s) {
        if (s == null || s.isBlank()) return false;
        try { Integer.parseInt(s.trim()); return true; } catch (NumberFormatException e) { return false; }
    }

    /** Returns true if string is non-blank and does NOT look like a pure number (valid for name fields). */
    private boolean isValidName(String s) {
        if (s == null || s.isBlank()) return false;
        // Reject purely numeric strings in name fields
        try { Double.parseDouble(s.trim()); return false; } catch (NumberFormatException e) { return true; }
    }

    /** Validate a min/max pair of fields: both must be valid doubles. */
    private boolean validatePair(TextField tfMin, TextField tfMax, Label errLabel) {
        if (!isDouble(tfMin.getText()) || !isDouble(tfMax.getText())) {
            markError(null, errLabel, "Valeurs numériques requises (ex: 5.5 / 7.0).");
            return false;
        }
        clearError(null, errLabel);
        return true;
    }

    /** Quick scrollPane wrapper for dialog content (point 7). */
    private ScrollPane scrollPane(javafx.scene.Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:white;-fx-border-color:transparent;-fx-background:white;");
        return sp;
    }
}