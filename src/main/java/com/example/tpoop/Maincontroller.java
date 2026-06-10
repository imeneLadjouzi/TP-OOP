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
    // Primary palette — deep forest green
    private static final String COLOR_SIDEBAR_BG    = "#1a3a2a";
    private static final String COLOR_SIDEBAR_HOVER = "#2d5c3e";
    private static final String COLOR_SIDEBAR_ACTIVE = "#3a7a52";
    private static final String COLOR_ACCENT        = "#4a9e6b";
    private static final String COLOR_ACCENT_LIGHT  = "#e8f5ee";
    private static final String COLOR_ACCENT_BORDER = "#b8ddc8";

    // Content area
    private static final String COLOR_PAGE_BG       = "#f7f8f6";
    private static final String COLOR_CARD_BG        = "#ffffff";
    private static final String COLOR_CARD_BORDER    = "#e4e8e2";

    // Typography
    private static final String COLOR_TEXT_PRIMARY   = "#1a2a1e";
    private static final String COLOR_TEXT_SECONDARY = "#5a7060";
    private static final String COLOR_TEXT_MUTED     = "#94a89c";

    // Semantic
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

    // Spacing & radius (as CSS strings for -fx-background-radius etc.)
    private static final String RADIUS_SM   = "4";
    private static final String RADIUS_MD   = "8";
    private static final String RADIUS_LG   = "12";

    // ── Component Styles ─────────────────────────────────────────────
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

    // ── State ─────────────────────────────────────────────────────────
    private final Stage stage;
    private final Ferme ferme;
    private final Gestionnaire g;
    private BorderPane root;
    private String activePage = "ferme";

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

        // Brand header
        VBox brand = new VBox(2);
        brand.setPadding(new Insets(24, 20, 20, 20));
        brand.setStyle("-fx-border-color:transparent transparent rgba(255,255,255,0.08) transparent;-fx-border-width:0 0 1 0;");
        Label lBrand = new Label("GreenField");
        lBrand.setStyle("-fx-text-fill:#ffffff;-fx-font-size:17px;-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;");
        Label lSub = new Label("Gestion de ferme");
        lSub.setStyle("-fx-text-fill:rgba(255,255,255,0.45);-fx-font-size:11px;-fx-font-family:'Segoe UI',system;");
        brand.getChildren().addAll(lBrand, lSub);
        sb.getChildren().add(brand);

        // Spacer top
        Region topSpacer = new Region();
        topSpacer.setPrefHeight(8);
        sb.getChildren().add(topSpacer);

        // Nav items: label, page key, icon text (using simple unicode or text abbreviation)
        String[][] navItems = {
                {"Tableau de bord", "ferme",      ""},
                {"Zones",           "zones",      ""},
                {"Cultures",        "cultures",   ""},
                {"Animaux",         "animaux",    ""},
                {"Capteurs",        "capteurs",   ""},
                {"Alertes",         "alertes",    ""},
                {"Production",      "production", ""},
        };

        // Unicode icons mapped per item
        String[] icons = { "\u25A6", "\u25A3", "\u2663", "\u2726", "\u25C9", "\u25B2", "\u25BA" };

        for (int i = 0; i < navItems.length; i++) {
            String[] item = navItems[i];
            String icon = icons[i];
            Button btn = buildNavButton(item[0], icon, item[1]);
            sb.getChildren().add(btn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sb.getChildren().add(spacer);

        // Bottom area
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

        String baseStyle =
                "-fx-background-color:transparent;" +
                        "-fx-cursor:hand;" +
                        "-fx-border-color:transparent;" +
                        "-fx-background-radius:0;";
        String hoverStyle =
                "-fx-background-color:" + COLOR_SIDEBAR_HOVER + ";" +
                        "-fx-cursor:hand;" +
                        "-fx-border-color:transparent;" +
                        "-fx-background-radius:0;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        btn.setOnAction(e -> {
            activePage = page;
            showPage(page);
        });
        return btn;
    }

    private void showPage(String page) {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle(
                "-fx-background-color:" + COLOR_PAGE_BG + ";" +
                        "-fx-border-color:transparent;" +
                        "-fx-background:" + COLOR_PAGE_BG + ";"
        );
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

        // Page header
        page.getChildren().add(buildPageHeader(ferme.getNom(), "Vue d'ensemble de votre exploitation agricole"));

        // Stat cards
        long alertesActives = ferme.getAlertes().stream().filter(Alerte::isActive).count();
        int totalCapteurs   = ferme.getTousLesCapteurs().size();
        int totalZones      = ferme.getZones().size();
        int totalAnimaux    = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneElevage)
                .mapToInt(z -> ((ZoneElevage) z).getAnimaux().size())
                .sum();

        HBox statsRow = new HBox(12);
        statsRow.getChildren().addAll(
                buildStatCard("Zones actives",   String.valueOf(totalZones),    "Total des zones gérées",    COLOR_ACCENT_LIGHT, COLOR_ACCENT),
                buildStatCard("Capteurs",        String.valueOf(totalCapteurs), "Capteurs déployés",         "#eef4fb", COLOR_INFO_TEXT),
                buildStatCard("Animaux",         String.valueOf(totalAnimaux),  "Têtes de bétail",           "#fef9ec", COLOR_WARNING_TEXT),
                buildStatCard("Alertes actives", String.valueOf(alertesActives), "Requièrent attention",     alertesActives > 0 ? COLOR_DANGER_BG : COLOR_SUCCESS_BG,
                        alertesActives > 0 ? COLOR_DANGER_TEXT : COLOR_SUCCESS_TEXT)
        );
        page.getChildren().add(statsRow);

        // Zones overview
        page.getChildren().add(buildSectionHeader("Zones de la ferme", null));

        VBox zonesCard = new VBox(0);
        zonesCard.setStyle(STYLE_CARD_FLUSH);

        TableView<Zone> tz = buildStyledTable();
        tz.setMaxHeight(240);
        tz.getColumns().addAll(
                styledCol("Code",       80,  z -> z.getCode()),
                styledCol("Nom",        180, z -> z.getName()),
                styledCol("Type",       110, z -> z instanceof ZoneCulture ? "Culture" : z instanceof ZoneElevage ? "Elevage" : "Aquaculture"),
                styledCol("Statut",     100, z -> z.getStatus().name()),
                styledCol("Capteurs",   80,  z -> String.valueOf(z.getCapteurs().size())),
                styledCol("Détail",     200, z -> {
                    if (z instanceof ZoneCulture zc) return zc.getCultures() != null ? zc.getCultures().getNom() : "—";
                    if (z instanceof ZoneElevage ze) return ze.getAnimaux().size() + " animaux";
                    if (z instanceof ZoneAqua za)    return za.getEspece() + " (" + za.getNbAnimaux() + ")";
                    return "";
                })
        );

        // Status badge coloring for zone table
        styledStatusRows(tz, z -> z.getStatus() == Status.ACTIF ? "ok" : "off");

        tz.getItems().addAll(ferme.getZones());
        zonesCard.getChildren().add(tz);
        page.getChildren().add(zonesCard);

        // Active alerts
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
    // PAGE ZONES
    // ─────────────────────────────────────────────────────────────────
    private VBox buildZonesPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Zones", "Gérez les zones de culture, d'élevage et d'aquaculture"));

        // Add zone form card
        VBox addCard = new VBox(12);
        addCard.setStyle(STYLE_CARD);

        Label lAddTitle = new Label("Ajouter une zone");
        lAddTitle.setStyle(sectionTitleStyle());

        HBox addRow = new HBox(10);
        addRow.setAlignment(Pos.CENTER_LEFT);

        TextField tfNom = styledInput("Nom de la zone", 200);
        ComboBox<TypeZone> cbType = new ComboBox<>(FXCollections.observableArrayList(TypeZone.values()));
        cbType.getSelectionModel().selectFirst();
        styleCombo(cbType);
        cbType.setPrefWidth(160);

        Button btnAjout = btn("Ajouter la zone", STYLE_BTN_PRIMARY);
        btnAjout.setOnAction(e -> {
            if (tfNom.getText().isBlank()) return;
            if (cbType.getValue() == TypeZone.AQUA) {
                TextInputDialog d = new TextInputDialog("Tilapia");
                styleDialog(d);
                d.setHeaderText("Espèce aquacole");
                d.showAndWait().ifPresent(esp -> {
                    ferme.ajouterZone(new ZoneAqua(tfNom.getText(), Status.ACTIF, esp, ferme));
                    showPage("zones");
                });
            } else if (cbType.getValue() == TypeZone.ELEVAGE) {
                ChoiceDialog<TypeAnimal> d = new ChoiceDialog<>(TypeAnimal.RUMINANT, TypeAnimal.values());
                styleDialog(d);
                d.setHeaderText("Type d'animal");
                d.showAndWait().ifPresent(ta -> {
                    ferme.ajouterZone(new ZoneElevage(tfNom.getText(), Status.ACTIF, ta, ferme));
                    showPage("zones");
                });
            } else {
                //ajouter culture
                ferme.ajouterZone(new ZoneCulture(tfNom.getText(), Status.ACTIF, null, ferme));
                showPage("zones");
            }
        });

        addRow.getChildren().addAll(
                fieldGroup("Nom", tfNom),
                fieldGroup("Type", cbType),
                btnAjout
        );
        addRow.setAlignment(Pos.BOTTOM_LEFT);

        addCard.getChildren().addAll(lAddTitle, addRow);
        page.getChildren().add(addCard);

        // Tabbed zone list
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";-fx-tab-min-height:38px;");
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        List<Zone> cultures    = ferme.getZones().stream().filter(z -> z instanceof ZoneCulture).collect(Collectors.toList());
        List<Zone> elevages    = ferme.getZones().stream().filter(z -> z instanceof ZoneElevage).collect(Collectors.toList());
        List<Zone> aquaculture = ferme.getZones().stream().filter(z -> z instanceof ZoneAqua).collect(Collectors.toList());

        tabs.getTabs().addAll(
                buildZoneTab("Cultures (" + cultures.size() + ")",        cultures),
                buildZoneTab("Elevage (" + elevages.size() + ")",          elevages),
                buildZoneTab("Aquaculture (" + aquaculture.size() + ")",   aquaculture)
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

    private VBox buildZoneCard(Zone z) {
        VBox card = new VBox(12);
        card.setStyle(STYLE_CARD);

        // Header row
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lNom = new Label(z.getName());
        lNom.setStyle("-fx-font-weight:bold;-fx-font-size:15px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");

        Label lCode = new Label(z.getCode());
        lCode.setStyle("-fx-background-color:#f0f2f0;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";" +
                "-fx-padding:2 8;-fx-background-radius:4;-fx-font-size:12px;-fx-font-family:'Segoe UI',system;");

        String statusBg   = z.getStatus() == Status.ACTIF ? COLOR_SUCCESS_BG   : "#f5f5f5";
        String statusText = z.getStatus() == Status.ACTIF ? COLOR_SUCCESS_TEXT  : COLOR_TEXT_MUTED;
        Label lStatus = new Label(z.getStatus().name());
        lStatus.setStyle("-fx-background-color:" + statusBg + ";-fx-text-fill:" + statusText + ";" +
                "-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;-fx-font-family:'Segoe UI',system;");

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Action buttons in header
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (z.getStatus() == Status.ACTIF) {
            Button bSusp = btn("Désactiver", STYLE_BTN_GHOST);
            bSusp.setOnAction(e -> { g.desactiverZone(z); showPage("zones"); });
            actions.getChildren().add(bSusp);
        } else {
            Button bAct = btn("Réactiver", STYLE_BTN_SECONDARY);
            bAct.setOnAction(e -> { g.reactiverZone(z); showPage("zones"); });
            actions.getChildren().add(bAct);
        }

        Button bRen = btn("Renommer", STYLE_BTN_GHOST);
        bRen.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog(z.getName());
            styleDialog(d);
            d.setHeaderText("Nouveau nom pour la zone");
            d.showAndWait().ifPresent(n -> { g.modifierNomZone(z, n); showPage("zones"); });
        });

        Button bDel = btn("Supprimer", STYLE_BTN_GHOST);
        bDel.setStyle(STYLE_BTN_GHOST +
                "-fx-text-fill:" + COLOR_DANGER_TEXT + ";" +
                "-fx-border-color:" + COLOR_DANGER_BORDER + ";");
        bDel.setOnAction(e -> {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                    "Confirmer la suppression de \"" + z.getName() + "\" ?", ButtonType.YES, ButtonType.NO);
            styleDialog(conf);
            conf.showAndWait().ifPresent(bt -> { if (bt == ButtonType.YES) { g.supprimerZone(z.getCode()); showPage("zones"); }});
        });

        actions.getChildren().addAll(bRen, bDel);
        header.getChildren().addAll(lNom, lCode, lStatus, hSpacer, actions);
        card.getChildren().add(header);

        // Divider
        card.getChildren().add(buildDivider());

        // Zone-specific details
        HBox detailRow = new HBox(12);
        detailRow.setAlignment(Pos.CENTER_LEFT);

        if (z instanceof ZoneCulture zc) {
            String cultName = zc.getCultures() != null ? zc.getCultures().getNom() : "Aucune culture affectée";
            detailRow.getChildren().add(buildDetailChip("Culture", cultName));

            if (zc.getCultures() != null) {
                Culture c = zc.getCultures();
                detailRow.getChildren().addAll(
                        buildDetailChip("Plantation", c.getDatePlantation()),
                        buildDetailChip("Récolte",    c.getDateRecolte()),
                        buildDetailChip("Stade",      c.getStadeCroiss().name())
                );
            }

            Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            HBox btns = new HBox(6);

            Button bExig = btn("Exigences sol", STYLE_BTN_GHOST);
            bExig.setOnAction(e -> showExigPedologiques(zc));

            Button bAff = btn("Enregistrer culture", STYLE_BTN_SECONDARY);
            bAff.setOnAction(e -> affecterCultureDialog(zc));

            btns.getChildren().addAll(bExig, bAff);

            if (zc.getCultures() != null) {
                Culture c = zc.getCultures();
                Button bStade = btn("Mettre à jour stade", STYLE_BTN_SECONDARY);
                bStade.setOnAction(ev -> {
                    ChoiceDialog<StadeCroissance> d = new ChoiceDialog<>(c.getStadeCroiss(), StadeCroissance.values());
                    styleDialog(d);
                    d.setHeaderText("Sélectionner le stade de croissance");
                    d.showAndWait().ifPresent(s -> { g.mettreAJourStadeCroissance(zc, s); showPage("zones"); });
                });
                btns.getChildren().add(bStade);
            }

            detailRow.getChildren().addAll(sp2, btns);

        } else if (z instanceof ZoneElevage ze) {
            detailRow.getChildren().addAll(
                    buildDetailChip("Animaux",      String.valueOf(ze.getAnimaux().size())),
                    buildDetailChip("Malades",       String.valueOf(ze.getNbAnimauxMalades())),
                    buildDetailChip("Type",          ze.getTypeAnimal().name())
            );
            Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            Button bProg = btn("Programme alimentaire", STYLE_BTN_SECONDARY);
            bProg.setOnAction(e -> showProgAlimDialog(ze));
            detailRow.getChildren().addAll(sp2, bProg);

        } else if (z instanceof ZoneAqua za) {
            detailRow.getChildren().addAll(
                    buildDetailChip("Espèce",     za.getEspece()),
                    buildDetailChip("Individus",  String.valueOf(za.getNbAnimaux()))
            );
            Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
            HBox btns2 = new HBox(6);
            Button bProg = btn("Programme alimentaire", STYLE_BTN_SECONDARY);
            bProg.setOnAction(e -> showProgAlimDialog(za));
            Button bNb = btn("Modifier effectif", STYLE_BTN_GHOST);
            bNb.setOnAction(e -> {
                TextInputDialog d = new TextInputDialog(String.valueOf(za.getNbAnimaux()));
                styleDialog(d);
                d.setHeaderText("Nombre d'individus");
                d.showAndWait().ifPresent(s -> { try { za.setNbAnimaux(Integer.parseInt(s)); showPage("zones"); } catch (NumberFormatException ex) {} });
            });
            btns2.getChildren().addAll(bProg, bNb);
            detailRow.getChildren().addAll(sp2, btns2);
        }

        card.getChildren().add(detailRow);

        // Footer: production & capteurs
        if (!z.getProductions().isEmpty() || !z.getCapteurs().isEmpty()) {
            card.getChildren().add(buildDivider());
            HBox footer = new HBox(16);
            footer.setAlignment(Pos.CENTER_LEFT);

            if (!z.getProductions().isEmpty()) {
                Label lProd = new Label("Production totale : " + String.format("%.1f", z.getTotal()));
                lProd.setStyle("-fx-text-fill:" + COLOR_ACCENT + ";-fx-font-size:13px;-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;");
                footer.getChildren().add(lProd);
            }

            Label lCap = new Label(z.getCapteurs().size() + " capteur(s) installé(s)");
            lCap.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:12px;-fx-font-family:'Segoe UI',system;");
            footer.getChildren().add(lCap);

            card.getChildren().add(footer);
        }

        return card;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE CULTURES
    // ─────────────────────────────────────────────────────────────────
    private VBox buildCulturesPage() {
        VBox page = pageContainer();
        page.getChildren().add(buildPageHeader("Cultures", "Suivi des cultures par zone"));

        List<ZoneCulture> zcs = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneCulture).map(z -> (ZoneCulture) z).collect(Collectors.toList());

        for (ZoneCulture zc : zcs) {
            VBox card = new VBox(12);
            card.setStyle(STYLE_CARD);

            // Card header
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lZone = new Label(zc.getName());
            lZone.setStyle("-fx-font-weight:bold;-fx-font-size:15px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;");
            Label lCode = new Label(zc.getCode());
            lCode.setStyle("-fx-background-color:#f0f2f0;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-padding:2 8;-fx-background-radius:4;-fx-font-size:12px;");
            String sBg   = zc.getStatus() == Status.ACTIF ? COLOR_SUCCESS_BG  : "#f5f5f5";
            String sTxt  = zc.getStatus() == Status.ACTIF ? COLOR_SUCCESS_TEXT : COLOR_TEXT_MUTED;
            Label lStat  = new Label(zc.getStatus().name());
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
                if (c.getExigPed() != null) chips.getChildren().add(buildDetailChip("Exigences", c.getExigPed().toString()));
                card.getChildren().add(chips);

                HBox acts = new HBox(8);
                Button bStade = btn("Mettre à jour stade", STYLE_BTN_SECONDARY);
                bStade.setOnAction(e -> {
                    ChoiceDialog<StadeCroissance> d = new ChoiceDialog<>(c.getStadeCroiss(), StadeCroissance.values());
                    styleDialog(d);
                    d.setHeaderText("Sélectionner le stade de croissance");
                    d.showAndWait().ifPresent(s -> { g.mettreAJourStadeCroissance(zc, s); showPage("cultures"); });
                });
                Button bExig = btn("Exigences pédologiques", STYLE_BTN_GHOST);
                bExig.setOnAction(e -> showExigPedologiques(zc));
                acts.getChildren().addAll(bStade, bExig);
                card.getChildren().add(acts);
            }

            HBox bottom = new HBox(8);
            Button bAff = btn("Enregistrer / Modifier culture", STYLE_BTN_PRIMARY);
            bAff.setOnAction(e -> { affecterCultureDialog(zc); showPage("cultures"); });
            bottom.getChildren().add(bAff);
            card.getChildren().add(bottom);

            page.getChildren().add(card);
        }

        HBox rapportRow = new HBox();
        Button bRap = btn("Générer rapport global des cultures", STYLE_BTN_SECONDARY);
        bRap.setOnAction(e -> {
            Alert al = new Alert(Alert.AlertType.INFORMATION);
            styleDialog(al);
            al.setHeaderText("Rapport des cultures");
            al.setContentText(ferme.genererRapportCultures());
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

            // Header
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

            // Animal table
            TableView<Animal> ta = buildStyledTable();
            ta.setMaxHeight(200);
            ta.getColumns().addAll(
                    styledCol("ID",         60,  a -> String.valueOf(a.getID())),
                    styledCol("Espèce",     150, a -> a.getEspece().getName()),
                    styledCol("Type",       100, a -> a.getEspece().getType().name()),
                    styledCol("Age",        60,  a -> a.age + " ans"),
                    styledCol("Poids (kg)", 90,  a -> String.format("%.1f", a.getPoids())),
                    styledCol("Etat de santé", 120, a -> a.getEtat().name())
            );

            // Color rows by health
            ta.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Animal a, boolean empty) {
                    super.updateItem(a, empty);
                    if (a == null || empty) { setStyle(""); return; }
                    String bg = switch (a.getEtat()) {
                        case MALADE    -> "-fx-background-color:" + COLOR_DANGER_BG  + ";";
                        case EN_QUARANTAINE -> "-fx-background-color:" + COLOR_WARNING_BG + ";";
                        case SAIN -> "-fx-background-color"+ COLOR_SUCCESS_BG +";";
                        default        -> "";
                    };
                    setStyle(bg);
                }
            });

            ta.getItems().addAll(ze.getAnimaux());
            card.getChildren().add(ta);

            // Actions
            HBox acts = new HBox(8);
            acts.setAlignment(Pos.CENTER_LEFT);

            Button bAdd = btn("Ajouter un animal", STYLE_BTN_PRIMARY);
            bAdd.setOnAction(e -> { ajouterAnimalDialog(ze); showPage("animaux"); });

            Button bEvt = btn("Consigner evénement sanitaire", STYLE_BTN_SECONDARY);
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

        // Actions bar
        HBox actBar = new HBox(8);
        actBar.setAlignment(Pos.CENTER_LEFT);

        // Table
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

        // Build actions referencing table
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

        // Filter card
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
                fieldGroup("Zone",    cbZone),
                fieldGroup("Type",    cbType),
                fieldGroup("Niveau",  cbNiv),
                fieldGroup("Du",      dpDeb),
                fieldGroup("Au",      dpFin)
        );
        filterCard.getChildren().add(filterRow);

        page.getChildren().add(filterCard);

        // Alert table
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

        // Table wrapped in card
        VBox tableCard = new VBox(0);
        tableCard.setStyle(STYLE_CARD_FLUSH);
        tableCard.getChildren().add(ta);
        page.getChildren().add(tableCard);

        // Actions
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

        // Colorer les lignes selon le statut et la gravité
        ta.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Alerte a, boolean empty) {
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
    // PAGE PRODUCTION
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
                        styledCol("Date",  160, Prod::getDate),
                        styledCol("Valeur", 100, p -> String.format("%.2f", p.getVal())),
                        styledCol("Unité",  80,  p -> p.getProd().getUnite()),
                        styledCol("Type",   140, p -> p.getProd().name())
                );
                tp.getItems().addAll(z.getProductions());
                card.getChildren().add(tp);
            }

            card.getChildren().add(buildDivider());

            // Add production
            HBox addRow = new HBox(10);
            addRow.setAlignment(Pos.CENTER_LEFT);
            ComboBox<TypeProd> cbProd = new ComboBox<>(FXCollections.observableArrayList(TypeProd.values()));
            cbProd.getSelectionModel().selectFirst();
            styleCombo(cbProd);
            TextField tfVal = styledInput("Valeur", 100);
            Button bEnreg = btn("Enregistrer", STYLE_BTN_PRIMARY);
            bEnreg.setOnAction(e -> {
                try {
                    double val = Double.parseDouble(tfVal.getText());
                    g.enregistrerProduction(z, new Prod(val, cbProd.getValue()));
                    showPage("production");
                } catch (Exception ex) { info("Valeur invalide."); }
            });
            addRow.getChildren().addAll(
                    fieldGroup("Type", cbProd),
                    fieldGroup("Valeur", tfVal),
                    bEnreg
            );
            addRow.setAlignment(Pos.BOTTOM_LEFT);
            card.getChildren().add(addRow);

            page.getChildren().add(card);
        }

        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // DIALOGS
    // ─────────────────────────────────────────────────────────────────

    private void affecterCultureDialog(ZoneCulture zc) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Enregistrer une culture — " + zc.getName());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");

        GridPane gp = styledGrid();
        TextField tfNom = styledInput(zc.getCultures() != null ? zc.getCultures().getNom() : "", 200);
        TextField tfDp  = styledInput(zc.getCultures() != null ? zc.getCultures().getDatePlantation() : "AAAA-MM-JJ", 150);
        TextField tfDr  = styledInput(zc.getCultures() != null ? zc.getCultures().getDateRecolte() : "AAAA-MM-JJ", 150);
        ComboBox<StadeCroissance> cbS = new ComboBox<>(FXCollections.observableArrayList(StadeCroissance.values()));
        cbS.getSelectionModel().select(zc.getCultures() != null ? zc.getCultures().getStadeCroiss() : StadeCroissance.GERMINATION);
        styleCombo(cbS);

        TextField tfPhMin = dlgInput("5.5"), tfPhMax = dlgInput("7.0");
        TextField tfHMin  = dlgInput("30"),  tfHMax  = dlgInput("80");
        TextField tfAzMin = dlgInput("50"),  tfAzMax = dlgInput("150");

        gp.addRow(0, dlgLbl("Nom de la culture :"), tfNom);
        gp.addRow(1, dlgLbl("Date de plantation :"), tfDp);
        gp.addRow(2, dlgLbl("Date de récolte :"),    tfDr);
        gp.addRow(3, dlgLbl("Stade de croissance :"), cbS);
        gp.addRow(4, dlgLbl("pH min / max :"),        new HBox(6, tfPhMin, new Label("/"), tfPhMax));
        gp.addRow(5, dlgLbl("Humidité min / max :"),  new HBox(6, tfHMin, new Label("/"), tfHMax));
        gp.addRow(6, dlgLbl("Azote min / max :"),     new HBox(6, tfAzMin, new Label("/"), tfAzMax));
        d.getDialogPane().setContent(gp);

        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    ExigPedologiques ep = new ExigPedologiques(
                            dbl(tfPhMin), dbl(tfPhMax), dbl(tfHMin), dbl(tfHMax), dbl(tfAzMin), dbl(tfAzMax));
                    g.affecterCulture(zc, new Culture(tfNom.getText(), tfDp.getText(), tfDr.getText(), cbS.getValue(), ep));
                } catch (Exception ex) { info("Erreur : " + ex.getMessage()); }
            }
        });
    }

    private void showExigPedologiques(ZoneCulture zc) {
        if (zc.getCultures() == null || zc.getCultures().getExigPed() == null) {
            info("Aucune exigence pédologique définie pour cette culture."); return;
        }
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        styleDialog(al);
        al.setTitle("Exigences pédologiques — " + zc.getCultures().getNom());
        al.setHeaderText(null);
        al.setContentText(zc.getCultures().getExigPed().toString());
        al.showAndWait();
    }

    private void showProgAlimDialog(ZoneElevage ze) {
        Dialog<ButtonType> d = progAlimDialog(ze.getProgAlim());
        d.setTitle("Programme alimentaire — " + ze.getName());
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                GridPane gp = (GridPane) d.getDialogPane().getContent();
                String type = ((TextField)((HBox)gp.getChildren().get(1)).getChildren().get(0)).getText();
                double q = dbl(((TextField)((HBox)gp.getChildren().get(3)).getChildren().get(0)));
                int r = (int) dbl(((TextField)((HBox)gp.getChildren().get(5)).getChildren().get(0)));
                g.definirProgAlim(ze, new ProgAlimentaire(type, q, r));
            }
        });
    }

    private void showProgAlimDialog(ZoneAqua za) {
        Dialog<ButtonType> d = progAlimDialog(za.getProgAlim());
        d.setTitle("Programme alimentaire — " + za.getName());
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                GridPane gp = (GridPane) d.getDialogPane().getContent();
                String type = ((TextField)((HBox)gp.getChildren().get(1)).getChildren().get(0)).getText();
                double q = dbl(((TextField)((HBox)gp.getChildren().get(3)).getChildren().get(0)));
                int r = (int) dbl(((TextField)((HBox)gp.getChildren().get(5)).getChildren().get(0)));
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
        TextField tfQ    = dlgInput(existing != null ? String.valueOf(existing.getQuantiteParRepas()) : "1.0");
        TextField tfR    = dlgInput(existing != null ? String.valueOf(existing.getRepasParJour()) : "2");
        gp.addRow(0, dlgLbl("Type d'aliment :"),       new HBox(tfType));
        gp.addRow(1, dlgLbl("Quantité / repas (kg) :"), new HBox(tfQ));
        gp.addRow(2, dlgLbl("Repas / jour :"),           new HBox(tfR));
        if (existing != null) {
            gp.addRow(3, dlgLbl("Total / jour :"),
                    new Label(String.format("%.2f kg", existing.getQuantiteJournaliere())));
        }
        d.getDialogPane().setContent(gp);
        return d;
    }

    private void ajouterAnimalDialog(ZoneElevage ze) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Ajouter un animal — " + ze.getName());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setStyle("-fx-background-color:#ffffff;-fx-font-family:'Segoe UI',system;");
        GridPane gp = styledGrid();
        TextField tfEsp = dlgInput("");
        ComboBox<TypeAnimal> cbT = new ComboBox<>(FXCollections.observableArrayList(TypeAnimal.values()));
        cbT.getSelectionModel().select(ze.getTypeAnimal());
        styleCombo(cbT);
        TextField tfAge  = dlgInput("1");
        TextField tfPoid = dlgInput("50");
        gp.addRow(0, dlgLbl("Nom de l'espèce :"), tfEsp);
        gp.addRow(1, dlgLbl("Type d'animal :"),   cbT);
        gp.addRow(2, dlgLbl("Age (ans) :"),        tfAge);
        gp.addRow(3, dlgLbl("Poids (kg) :"),       tfPoid);
        d.getDialogPane().setContent(gp);
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    g.affecterAnimal(ze, new Animal(new EspeceAnim(cbT.getValue(), tfEsp.getText()),
                            (int) dbl(tfAge), dbl(tfPoid), EtatSante.SAIN));
                } catch (Exception ex) { info(ex.getMessage()); }
            }
        });
    }

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
        TextField tfVal  = dlgInput("0");
        gp.addRow(0, dlgLbl("Type d'événement :"),      cbT);
        gp.addRow(1, dlgLbl("Description :"),            tfDesc);
        gp.addRow(2, dlgLbl("Valeur (poids si applicable) :"), tfVal);
        d.getDialogPane().setContent(gp);
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
        al.getDialogPane().setContent(ta);
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
        }
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

        // Filter row
        HBox filterRow = new HBox(10);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        DatePicker dpD = new DatePicker(), dpF = new DatePicker();
        dpD.setPromptText("Date début"); dpF.setPromptText("Date fin");

        TableView<Releve> tr = buildStyledTable();
        tr.setMaxHeight(300);
        tr.getColumns().addAll(
                styledCol("ID",         50,  r -> String.valueOf(r.getId())),
                styledCol("Date / Heure", 170, r -> r.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))),
                styledCol("Niveau",     120, r -> r.getNiveauReleve().name()),
                styledCol("Valeurs",    380, r -> r.getValeurs().toString())
        );
        tr.getItems().addAll(c.getHistorique());

        Button bFilt = btn("Filtrer", STYLE_BTN_PRIMARY);
        bFilt.setOnAction(e -> {
            List<Releve> filtered = ReleveSpecifications.filtrer(c.getHistorique(), dpD.getValue(), dpF.getValue());
            tr.getItems().setAll(filtered);
        });

        filterRow.getChildren().addAll(
                fieldGroup("Du",  dpD),
                fieldGroup("Au",  dpF),
                bFilt
        );
        filterRow.setAlignment(Pos.BOTTOM_LEFT);

        VBox tableCard = new VBox(0);
        tableCard.setStyle(STYLE_CARD_FLUSH);
        tableCard.getChildren().add(tr);

        vRoot.getChildren().addAll(filterRow, tableCard);

        // Chart
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
                    seriesMap.computeIfAbsent(entry.getKey(), k -> {
                        XYChart.Series<String, Number> ss = new XYChart.Series<>();
                        ss.setName(k); return ss;
                    });
                    try {
                        seriesMap.get(entry.getKey()).getData().add(
                                new XYChart.Data<>(time, ((Number) entry.getValue()).doubleValue()));
                    } catch (Exception ignored) {}
                }
            }
            chart.getData().addAll(seriesMap.values());
            vRoot.getChildren().add(chart);
        } else {
            Label lNoData = new Label("Données insuffisantes pour afficher le graphique.");
            lNoData.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:13px;");
            vRoot.getChildren().add(lNoData);
        }

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
    // UI BUILDING HELPERS
    // ─────────────────────────────────────────────────────────────────

    /** Standard page container with consistent padding & spacing */
    private VBox pageContainer() {
        VBox p = new VBox(20);
        p.setPadding(new Insets(28, 28, 28, 28));
        p.setStyle("-fx-background-color:" + COLOR_PAGE_BG + ";");
        return p;
    }

    /** Page-level title + subtitle block */
    private VBox buildPageHeader(String title, String subtitle) {
        VBox h = new VBox(4);
        h.setPadding(new Insets(0, 0, 8, 0));
        Label lTitle = new Label(title);
        lTitle.setStyle(
                "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";" +
                        "-fx-font-family:'Segoe UI',system;"
        );
        h.getChildren().add(lTitle);
        if (subtitle != null && !subtitle.isBlank()) {
            Label lSub = new Label(subtitle);
            lSub.setStyle("-fx-font-size:13px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");
            h.getChildren().add(lSub);
        }
        // Underline separator
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setMaxHeight(1);
        sep.setStyle("-fx-background-color:" + COLOR_CARD_BORDER + ";");
        sep.setPadding(new Insets(8, 0, 0, 0));
        VBox wrapper = new VBox(8, h, sep);
        return wrapper;
    }

    /** Section header with optional right-side note */
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

    /** Metric stat card */
    private VBox buildStatCard(String label, String value, String sublabel, String bgColor, String textColor) {
        VBox c = new VBox(4);
        c.setStyle(
                "-fx-background-color:" + bgColor + ";" +
                        "-fx-padding:16 20;" +
                        "-fx-background-radius:" + RADIUS_MD + ";" +
                        "-fx-border-color:" + COLOR_CARD_BORDER + ";" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:" + RADIUS_MD + ";"
        );
        c.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(c, Priority.ALWAYS);

        Label lVal = new Label(value);
        lVal.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:" + textColor + ";-fx-font-family:'Segoe UI',system;");

        Label lLabel = new Label(label);
        lLabel.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + textColor + ";-fx-font-family:'Segoe UI',system;");

        Label lSub = new Label(sublabel);
        lSub.setStyle("-fx-font-size:11px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");

        c.getChildren().addAll(lVal, lLabel, lSub);
        return c;
    }

    /** Alert row with left color accent */
    private HBox buildAlertRow(Alerte a, boolean withTopBorder) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));

        if (withTopBorder) {
            row.setStyle("-fx-border-color:" + COLOR_CARD_BORDER + " transparent transparent transparent;-fx-border-width:1 0 0 0;");
        }

        String nivColor = switch (a.getGravite()) {
            case CRITIQUE      -> COLOR_DANGER_TEXT;
            case AVERTISSEMENT -> COLOR_WARNING_TEXT;
            default            -> COLOR_SUCCESS_TEXT;
        };
        String nivBg = switch (a.getGravite()) {
            case CRITIQUE      -> COLOR_DANGER_BG;
            case AVERTISSEMENT -> COLOR_WARNING_BG;
            default            -> COLOR_SUCCESS_BG;
        };

        Label lNiv = new Label(a.getGravite().name());
        lNiv.setStyle(
                "-fx-background-color:" + nivBg + ";" +
                        "-fx-text-fill:" + nivColor + ";" +
                        "-fx-padding:3 10;-fx-background-radius:12;-fx-font-size:11px;" +
                        "-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;" +
                        "-fx-min-width:110px;-fx-alignment:center;"
        );

        Label lMsg = new Label(a.getMessage());
        lMsg.setStyle("-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-size:13px;-fx-font-family:'Segoe UI',system;");
        HBox.setHgrow(lMsg, Priority.ALWAYS);

        Label lZone = new Label(a.getZone() != null ? a.getZone().getName() : "");
        lZone.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:12px;-fx-font-family:'Segoe UI',system;");

        Label lDate = new Label(a.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));
        lDate.setStyle("-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-size:12px;-fx-font-family:'Segoe UI',system;");

        row.getChildren().addAll(lNiv, lMsg, lZone, lDate);
        return row;
    }

    /** Small key/value chip */
    private VBox buildDetailChip(String key, String value) {
        VBox chip = new VBox(1);
        chip.setStyle(
                "-fx-background-color:#f7f8f6;" +
                        "-fx-border-color:" + COLOR_CARD_BORDER + ";" +
                        "-fx-border-width:1;" +
                        "-fx-padding:6 12;" +
                        "-fx-background-radius:6;" +
                        "-fx-border-radius:6;"
        );
        Label lKey = new Label(key);
        lKey.setStyle("-fx-font-size:10px;-fx-text-fill:" + COLOR_TEXT_MUTED + ";-fx-font-family:'Segoe UI',system;");
        Label lVal = new Label(value != null ? value : "—");
        lVal.setStyle("-fx-font-size:13px;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-weight:bold;-fx-font-family:'Segoe UI',system;");
        chip.getChildren().addAll(lKey, lVal);
        return chip;
    }

    /** Thin horizontal divider */
    private Region buildDivider() {
        Region r = new Region();
        r.setPrefHeight(1);
        r.setMaxHeight(1);
        r.setStyle("-fx-background-color:" + COLOR_CARD_BORDER + ";");
        return r;
    }

    /** Vertical field group: small label above control */
    private VBox fieldGroup(String label, javafx.scene.Node control) {
        VBox g = new VBox(4);
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px;-fx-text-fill:" + COLOR_TEXT_SECONDARY + ";-fx-font-family:'Segoe UI',system;");
        g.getChildren().addAll(l, control);
        return g;
    }

    // ── Table helpers ────────────────────────────────────────────────

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

    /** Color table rows by a simple tri-state signal */
    private <T> void styledStatusRows(TableView<T> tv, java.util.function.Function<T, String> signal) {
        tv.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
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

    // ── Form helpers ─────────────────────────────────────────────────

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
        d.getDialogPane().setStyle(
                "-fx-background-color:#ffffff;" +
                        "-fx-font-family:'Segoe UI',system;" +
                        "-fx-font-size:13px;"
        );
    }

    private String sectionTitleStyle() {
        return "-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + COLOR_TEXT_PRIMARY + ";-fx-font-family:'Segoe UI',system;";
    }
}