package com.example.tpoop;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

 class MainController {

    // ── Palette ──────────────────────────────────────────────────────
    private static final String GREEN_DARK  = "#2e7d32";
    private static final String GREEN_LIGHT = "#c8e6c9";
    private static final String GREEN_MID   = "#66bb6a";
    private static final String WHITE       = "#ffffff";
    private static final String GREY_BG     = "#f5f5f5";
    private static final String GREY_BORDER = "#bdbdbd";
    private static final String RED_ALERT   = "#c62828";
    private static final String YELLOW_WARN = "#f9a825";

    private static final String BTN_STYLE =
            "-fx-background-color:" + GREEN_DARK + ";-fx-text-fill:white;" +
                    "-fx-font-size:12px;-fx-padding:5 14;-fx-cursor:hand;" +
                    "-fx-background-radius:3;";
    private static final String BTN_SEC =
            "-fx-background-color:white;-fx-text-fill:" + GREEN_DARK + ";" +
                    "-fx-border-color:" + GREEN_DARK + ";-fx-border-width:1;-fx-font-size:12px;" +
                    "-fx-padding:4 12;-fx-cursor:hand;-fx-background-radius:3;";
    private static final String CARD_STYLE =
            "-fx-background-color:white;-fx-border-color:" + GREY_BORDER + ";" +
                    "-fx-border-width:1;-fx-padding:10;-fx-background-radius:4;-fx-border-radius:4;";

    // ── State ─────────────────────────────────────────────────────────
    private final Stage stage;
    private final Ferme ferme;
    private final Gestionnaire g;
    private BorderPane root;
    private VBox sidebar;

    public MainController(Stage stage) {
        this.stage = stage;
        this.ferme = new Ferme("Ferme Principale");
        this.g = new Gestionnaire(ferme);
        Seeds.initialiser(ferme);
    }

    public void show() {
        root = new BorderPane();
        root.setStyle("-fx-background-color:" + GREY_BG + ";");

        sidebar = buildSidebar();
        root.setLeft(sidebar);
        showPage("ferme");

        Scene scene = new Scene(root, 1200, 750);
        stage.setTitle("Gestion de Ferme");
        stage.setScene(scene);
        stage.show();
    }

    // ─────────────────────────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sb = new VBox(0);
        sb.setStyle("-fx-background-color:" + GREEN_DARK + ";-fx-min-width:170px;-fx-pref-width:170px;");

        Label title = new Label("Ferme");
        title.setStyle("-fx-text-fill:white;-fx-font-size:18px;-fx-font-weight:bold;" +
                "-fx-padding:18 14 18 14;-fx-border-color:transparent transparent " + GREEN_MID + " transparent;-fx-border-width:0 0 1 0;");
        title.setMaxWidth(Double.MAX_VALUE);
        sb.getChildren().add(title);

        String[][] items = {
                {"Ferme",     "ferme"},
                {"Zones",     "zones"},
                {"Cultures",  "cultures"},
                {"Animaux",   "animaux"},
                {"Capteurs",  "capteurs"},
                {"Alertes",   "alertes"},
                {"Production","production"}
        };

        for (String[] item : items) {
            Button btn = new Button(item[0]);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color:transparent;-fx-text-fill:white;" +
                    "-fx-font-size:13px;-fx-alignment:center-left;-fx-padding:10 14;" +
                    "-fx-cursor:hand;-fx-border-color:transparent;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:" + GREEN_MID +
                    ";-fx-text-fill:white;-fx-font-size:13px;-fx-alignment:center-left;" +
                    "-fx-padding:10 14;-fx-cursor:hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent;" +
                    "-fx-text-fill:white;-fx-font-size:13px;-fx-alignment:center-left;" +
                    "-fx-padding:10 14;-fx-cursor:hand;"));
            String page = item[1];
            btn.setOnAction(e -> showPage(page));
            sb.getChildren().add(btn);
        }
        return sb;
    }

    private void showPage(String page) {
        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + GREY_BG + ";-fx-border-color:transparent;");
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
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));

        page.getChildren().add(pageTitle("Ferme : " + ferme.getNom()));

        // Stats row
        HBox stats = new HBox(12);
        long alertesActives = ferme.getAlertes().stream().filter(Alerte::isActive).count();
        int totalCapteurs = ferme.getTousLesCapteurs().size();
        int totalZones    = ferme.getZones().size();

        stats.getChildren().addAll(
                statCard("Zones", String.valueOf(totalZones)),
                statCard("Capteurs", String.valueOf(totalCapteurs)),
                statCard("Alertes actives", String.valueOf(alertesActives))
        );
        page.getChildren().add(stats);

        // Vue d'ensemble zones
        page.getChildren().add(sectionLabel("Vue d'ensemble des zones"));
        TableView<Zone> tz = new TableView<>();
        tz.setStyle("-fx-background-color:white;");
        tz.setMaxHeight(200);
        tz.getColumns().addAll(
                col("Code", 80, z -> z.getCode()),
                col("Nom",  160, z -> z.getName()),
                col("Type", 100, z -> z instanceof ZoneCulture ? "Culture" : z instanceof ZoneElevage ? "Elevage" : "Aqua"),
                col("Statut", 90, z -> z.getStatus().name()),
                col("Capteurs", 80, z -> String.valueOf(z.getCapteurs().size())),
                col("Info", 180, z -> {
                    if (z instanceof ZoneCulture zc) return zc.getCultures() != null ? zc.getCultures().getNom() : "-";
                    if (z instanceof ZoneElevage ze) return ze.getAnimaux().size() + " animaux";
                    if (z instanceof ZoneAqua za) return za.getEspece() + " (" + za.getNbAnimaux() + ")";
                    return "";
                })
        );
        tz.getItems().addAll(ferme.getZones());
        page.getChildren().add(tz);

        // Alertes actives triées par gravité
        page.getChildren().add(sectionLabel("Alertes actives (triées par gravité)"));
        VBox alertBox = new VBox(4);
        alertBox.setStyle(CARD_STYLE);
        List<Alerte> actives = ferme.getAlertes().stream()
                .filter(Alerte::isActive)
                .sorted((a, b) -> b.getGravite().compareTo(a.getGravite()))
                .collect(Collectors.toList());
        if (actives.isEmpty()) {
            alertBox.getChildren().add(new Label("Aucune alerte active."));
        } else {
            for (Alerte a : actives) {
                alertBox.getChildren().add(alerteRow(a));
            }
        }
        page.getChildren().add(alertBox);

        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE ZONES
    // ─────────────────────────────────────────────────────────────────
    private VBox buildZonesPage() {
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));
        page.getChildren().add(pageTitle("Gestion des Zones"));

        // Ajouter zone
        HBox addRow = new HBox(8);
        addRow.setAlignment(Pos.CENTER_LEFT);
        TextField tfNom = new TextField();
        tfNom.setPromptText("Nom de la zone");
        ComboBox<TypeZone> cbType = new ComboBox<>(FXCollections.observableArrayList(TypeZone.values()));
        cbType.getSelectionModel().selectFirst();
        Button btnAjout = btn("Ajouter", BTN_STYLE);
        btnAjout.setOnAction(e -> {
            if (tfNom.getText().isBlank()) return;
            if (cbType.getValue() == TypeZone.AQUA) {
                TextInputDialog d = new TextInputDialog("Tilapia");
                d.setHeaderText("Espèce aquacole");
                d.showAndWait().ifPresent(esp -> {
                    ZoneAqua za = new ZoneAqua(tfNom.getText(), Status.ACTIF, esp, ferme);
                    ferme.ajouterZone(za);
                    showPage("zones");
                });
            } else if (cbType.getValue() == TypeZone.ELEVAGE) {
                ChoiceDialog<TypeAnimal> d = new ChoiceDialog<>(TypeAnimal.RUMINANT, TypeAnimal.values());
                d.setHeaderText("Type d'animal");
                d.showAndWait().ifPresent(ta -> {
                    ZoneElevage ze = new ZoneElevage(tfNom.getText(), Status.ACTIF, ta, ferme);
                    ferme.ajouterZone(ze);
                    showPage("zones");
                });
            } else {
                ZoneCulture zc = new ZoneCulture(tfNom.getText(), Status.ACTIF, null, ferme);
                ferme.ajouterZone(zc);
                showPage("zones");
            }
        });
        addRow.getChildren().addAll(new Label("Nom:"), tfNom, new Label("Type:"), cbType, btnAjout);
        page.getChildren().add(addRow);

        // Tabs par type
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-background-color:white;");
        tabs.getTabs().addAll(
                buildZoneTab("Cultures",  ferme.getZones().stream().filter(z -> z instanceof ZoneCulture).collect(Collectors.toList())),
                buildZoneTab("Elevage",   ferme.getZones().stream().filter(z -> z instanceof ZoneElevage).collect(Collectors.toList())),
                buildZoneTab("Aquaculture", ferme.getZones().stream().filter(z -> z instanceof ZoneAqua).collect(Collectors.toList()))
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        page.getChildren().add(tabs);
        return page;
    }

    private Tab buildZoneTab(String title, List<Zone> zones) {
        Tab tab = new Tab(title);
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color:white;");
        for (Zone z : zones) {
            content.getChildren().add(buildZoneCard(z));
        }
        if (zones.isEmpty()) content.getChildren().add(new Label("Aucune zone."));
        tab.setContent(new ScrollPane(content));
        return tab;
    }

    private VBox buildZoneCard(Zone z) {
        VBox card = new VBox(6);
        card.setStyle(CARD_STYLE);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lNom = new Label(z.getName());
        lNom.setStyle("-fx-font-weight:bold;-fx-font-size:14px;");
        Label lCode = new Label("[" + z.getCode() + "]");
        lCode.setStyle("-fx-text-fill:#555;");
        Label lStatus = new Label(z.getStatus().name());
        lStatus.setStyle("-fx-background-color:" + (z.getStatus() == Status.ACTIF ? GREEN_LIGHT : "#ffcdd2") +
                ";-fx-padding:2 8;-fx-background-radius:10;-fx-font-size:11px;");
        header.getChildren().addAll(lNom, lCode, lStatus);

        // Actions
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_LEFT);
        if (z.getStatus() == Status.ACTIF) {
            Button bSusp = btn("Désactiver", BTN_SEC);
            bSusp.setOnAction(e -> { g.desactiverZone(z); showPage("zones"); });
            actions.getChildren().add(bSusp);
        } else {
            Button bAct = btn("Réactiver", BTN_STYLE);
            bAct.setOnAction(e -> { g.reactiverZone(z); showPage("zones"); });
            actions.getChildren().add(bAct);
        }
        Button bRen = btn("Renommer", BTN_SEC);
        bRen.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog(z.getName());
            d.setHeaderText("Nouveau nom");
            d.showAndWait().ifPresent(n -> { g.modifierNomZone(z, n); showPage("zones"); });
        });
        Button bDel = btn("Supprimer", BTN_SEC);
        bDel.setOnAction(e -> {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + z.getName() + "?", ButtonType.YES, ButtonType.NO);
            conf.showAndWait().ifPresent(bt -> { if (bt == ButtonType.YES) { g.supprimerZone(z.getCode()); showPage("zones"); }});
        });
        actions.getChildren().addAll(bRen, bDel);

        card.getChildren().addAll(header, actions);

        // Specific info
        if (z instanceof ZoneCulture zc) {
            HBox row = new HBox(8);
            Label lCult = new Label("Culture: " + (zc.getCultures() != null ? zc.getCultures().getNom() : "Aucune"));
            Button bExig = btn("Exigences", BTN_SEC);
            bExig.setOnAction(e -> showExigPedologiques(zc));
            Button bProg = btn("Affecter culture", BTN_SEC);
            bProg.setOnAction(e -> affecterCultureDialog(zc));
            row.getChildren().addAll(lCult, bExig, bProg);
            card.getChildren().add(row);
            if (zc.getCultures() != null) {
                Culture c = zc.getCultures();
                Label info = new Label("Plantation: " + c.getDatePlantation() + "  Récolte: " + c.getDateRecolte() +
                        "  Stade: " + c.getStadeCroiss());
                info.setStyle("-fx-text-fill:#444;-fx-font-size:12px;");
                card.getChildren().add(info);
                Button bStade = btn("Mettre à jour stade", BTN_SEC);
                bStade.setOnAction(ev -> {
                    ChoiceDialog<StadeCroissance> d = new ChoiceDialog<>(c.getStadeCroiss(), StadeCroissance.values());
                    d.setHeaderText("Choisir le stade");
                    d.showAndWait().ifPresent(s -> { g.mettreAJourStadeCroissance(zc, s); showPage("zones"); });
                });
                card.getChildren().add(bStade);
            }
        } else if (z instanceof ZoneElevage ze) {
            Label lAnim = new Label("Animaux: " + ze.getAnimaux().size() + "  (malades: " + ze.getNbAnimauxMalades() + ")  Type: " + ze.getTypeAnimal());
            lAnim.setStyle("-fx-font-size:12px;-fx-text-fill:#444;");
            Button bProg = btn("Programme alimentaire", BTN_SEC);
            bProg.setOnAction(e -> showProgAlimDialog(ze));
            card.getChildren().addAll(lAnim, bProg);
        } else if (z instanceof ZoneAqua za) {
            Label lInfo = new Label("Espèce: " + za.getEspece() + "  Individus: " + za.getNbAnimaux());
            lInfo.setStyle("-fx-font-size:12px;-fx-text-fill:#444;");
            Button bProg = btn("Programme alimentaire", BTN_SEC);
            bProg.setOnAction(e -> showProgAlimDialog(za));
            Button bNb = btn("Modifier nb individus", BTN_SEC);
            bNb.setOnAction(e -> {
                TextInputDialog d = new TextInputDialog(String.valueOf(za.getNbAnimaux()));
                d.setHeaderText("Nombre d'individus");
                d.showAndWait().ifPresent(s -> { try { za.setNbAnimaux(Integer.parseInt(s)); showPage("zones"); } catch(NumberFormatException ex){} });
            });
            card.getChildren().addAll(lInfo, new HBox(6, bProg, bNb));
        }

        // Productions
        if (!z.getProductions().isEmpty()) {
            Label lp = new Label("Production totale: " + String.format("%.1f", z.getTotal()));
            lp.setStyle("-fx-font-size:12px;-fx-text-fill:" + GREEN_DARK + ";-fx-font-weight:bold;");
            card.getChildren().add(lp);
        }

        // Capteurs count
        Label lCap = new Label("Capteurs: " + z.getCapteurs().size());
        lCap.setStyle("-fx-font-size:11px;-fx-text-fill:#888;");
        card.getChildren().add(lCap);

        return card;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE CULTURES
    // ─────────────────────────────────────────────────────────────────
    private VBox buildCulturesPage() {
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));
        page.getChildren().add(pageTitle("Cultures"));

        List<ZoneCulture> zcs = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneCulture).map(z -> (ZoneCulture) z).collect(Collectors.toList());

        for (ZoneCulture zc : zcs) {
            VBox card = new VBox(6);
            card.setStyle(CARD_STYLE);
            Label lZone = new Label("Zone : " + zc.getName() + "  [" + zc.getCode() + "]  Statut: " + zc.getStatus());
            lZone.setStyle("-fx-font-weight:bold;");
            card.getChildren().add(lZone);

            if (zc.getCultures() == null) {
                card.getChildren().add(new Label("Aucune culture."));
            } else {
                Culture c = zc.getCultures();
                GridPane gp = new GridPane();
                gp.setHgap(14); gp.setVgap(4);
                int r = 0;
                addGridRow(gp, r++, "Nom", c.getNom());
                addGridRow(gp, r++, "Plantation", c.getDatePlantation());
                addGridRow(gp, r++, "Récolte", c.getDateRecolte());
                addGridRow(gp, r++, "Stade", c.getStadeCroiss().name());
                if (c.getExigPed() != null) addGridRow(gp, r++, "Exigences", c.getExigPed().toString());
                card.getChildren().add(gp);

                HBox acts = new HBox(6);
                Button bStade = btn("Mettre à jour stade", BTN_SEC);
                bStade.setOnAction(e -> {
                    ChoiceDialog<StadeCroissance> d = new ChoiceDialog<>(c.getStadeCroiss(), StadeCroissance.values());
                    d.setHeaderText("Choisir stade");
                    d.showAndWait().ifPresent(s -> { g.mettreAJourStadeCroissance(zc, s); showPage("cultures"); });
                });
                Button bExig = btn("Exigences pédologiques", BTN_SEC);
                bExig.setOnAction(e -> showExigPedologiques(zc));
                acts.getChildren().addAll(bStade, bExig);
                card.getChildren().add(acts);
            }

            Button bAff = btn("Affecter / Modifier culture", BTN_STYLE);
            bAff.setOnAction(e -> { affecterCultureDialog(zc); showPage("cultures"); });
            card.getChildren().add(bAff);

            // Rapport
            page.getChildren().add(card);
        }

        // Rapport global
        Button bRap = btn("Rapport global des cultures", BTN_STYLE);
        bRap.setOnAction(e -> {
            Alert al = new Alert(Alert.AlertType.INFORMATION);
            al.setHeaderText("Rapport des cultures");
            al.setContentText(ferme.genererRapportCultures());
            al.showAndWait();
        });
        page.getChildren().add(bRap);

        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE ANIMAUX
    // ─────────────────────────────────────────────────────────────────
    private VBox buildAnimauxPage() {
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));
        page.getChildren().add(pageTitle("Animaux"));

        List<ZoneElevage> zes = ferme.getZones().stream()
                .filter(z -> z instanceof ZoneElevage).map(z -> (ZoneElevage) z).collect(Collectors.toList());

        for (ZoneElevage ze : zes) {
            VBox card = new VBox(8);
            card.setStyle(CARD_STYLE);
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label lZ = new Label(ze.getName() + "  [" + ze.getCode() + "]  Type: " + ze.getTypeAnimal());
            lZ.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
            Label lCnt = new Label(ze.getAnimaux().size() + " animaux  |  malades: " + ze.getNbAnimauxMalades());
            lCnt.setStyle("-fx-text-fill:#555;");
            header.getChildren().addAll(lZ, lCnt);
            card.getChildren().add(header);

            // Table animaux
            TableView<Animal> ta = new TableView<>();
            ta.setMaxHeight(180);
            ta.setStyle("-fx-background-color:white;");
            ta.getColumns().addAll(
                    col("ID", 50, a -> String.valueOf(a.getID())),
                    col("Espèce", 130, a -> a.getEspece().getName()),
                    col("Type", 90, a -> a.getEspece().getType().name()),
                    col("Age", 50, a -> String.valueOf(a.age)),
                    col("Poids(kg)", 80, a -> String.format("%.1f", a.getPoids())),
                    col("Santé", 110, a -> a.getEtat().name())
            );
            ta.getItems().addAll(ze.getAnimaux());
            card.getChildren().add(ta);

            // Actions
            HBox acts = new HBox(6);
            Button bAdd = btn("Ajouter animal", BTN_STYLE);
            bAdd.setOnAction(e -> { ajouterAnimalDialog(ze); showPage("animaux"); });
            Button bEvt = btn("Evénement sanitaire", BTN_SEC);
            bEvt.setOnAction(e -> {
                if (ta.getSelectionModel().getSelectedItem() == null) {
                    info("Sélectionnez un animal dans la liste."); return;
                }
                evenementSanitaireDialog(ta.getSelectionModel().getSelectedItem());
                showPage("animaux");
            });
            Button bHist = btn("Historique sanitaire", BTN_SEC);
            bHist.setOnAction(e -> {
                if (ta.getSelectionModel().getSelectedItem() == null) {
                    info("Sélectionnez un animal."); return;
                }
                showHistoriqueSanitaire(ta.getSelectionModel().getSelectedItem());
            });
            Button bProg = btn("Programme alim.", BTN_SEC);
            bProg.setOnAction(ev -> showProgAlimDialog(ze));
            acts.getChildren().addAll(bAdd, bEvt, bHist, bProg);
            card.getChildren().add(acts);

            page.getChildren().add(card);
        }
        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE CAPTEURS
    // ─────────────────────────────────────────────────────────────────
    private VBox buildCapteursPage() {
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));
        page.getChildren().add(pageTitle("Capteurs"));

        // Tableau de bord
        page.getChildren().add(sectionLabel("Tableau de bord"));
        TableView<Capteurs> tb = new TableView<>();
        tb.setMaxHeight(220);
        tb.setStyle("-fx-background-color:white;");
        tb.getColumns().addAll(
                col("Code", 80, c -> c.getCode()),
                col("Type", 100, c -> c.getType().name()),
                col("Zone", 130, c -> c.getLocation() != null ? c.getLocation().getName() : "-"),
                col("Statut", 90, c -> c.getStatus().name()),
                col("Dernier relevé", 220, c -> {
                    if (c.getHistorique().isEmpty()) return "Aucun";
                    Releve r = c.getHistorique().get(c.getHistorique().size()-1);
                    return r.getDateHeure().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                            " [" + r.getNiveauReleve() + "]";
                }),
                col("Valeurs", 250, c -> {
                    if (c.getHistorique().isEmpty()) return "-";
                    return c.getHistorique().get(c.getHistorique().size()-1).getValeurs().toString();
                })
        );
        tb.getItems().addAll(ferme.getTousLesCapteurs());
        page.getChildren().add(tb);

        // Actions globales
        HBox acts = new HBox(8);
        Button bReleve = btn("Effectuer relevé", BTN_STYLE);
        bReleve.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            ferme.effectuerReleve(c);
            showPage("capteurs");
        });
        Button bStatut = btn("Changer statut", BTN_SEC);
        bStatut.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            ChoiceDialog<Status> d = new ChoiceDialog<>(c.getStatus(), Status.values());
            d.setHeaderText("Nouveau statut pour " + c.getCode());
            d.showAndWait().ifPresent(s -> { g.changerStatusCapteur(c, s); showPage("capteurs"); });
        });
        Button bSeuils = btn("Configurer seuils", BTN_SEC);
        bSeuils.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            configurerSeuilsDialog(c);
        });
        Button bHist = btn("Historique relevés", BTN_SEC);
        bHist.setOnAction(e -> {
            Capteurs c = tb.getSelectionModel().getSelectedItem();
            if (c == null) { info("Sélectionnez un capteur."); return; }
            showHistoriqueReleves(c);
        });
        Button bAdd = btn("Ajouter capteur", BTN_STYLE);
        bAdd.setOnAction(e -> { ajouterCapteurDialog(); showPage("capteurs"); });
        acts.getChildren().addAll(bAdd, bReleve, bStatut, bSeuils, bHist);
        page.getChildren().add(acts);

        // Evolution par zone
        page.getChildren().add(sectionLabel("Relevés par zone"));
        for (Zone z : ferme.getZones()) {
            if (z.getCapteurs().isEmpty()) continue;
            VBox zcard = new VBox(4);
            zcard.setStyle(CARD_STYLE);
            Label lz = new Label(z.getName() + " [" + z.getCode() + "]");
            lz.setStyle("-fx-font-weight:bold;");
            zcard.getChildren().add(lz);
            for (Capteurs c : z.getCapteurs()) {
                if (c.getHistorique().isEmpty()) continue;
                Label lc = new Label(c.getCode() + " (" + c.getType() + ") — " + c.getHistorique().size() + " relevé(s) — Dernier: " +
                        c.getHistorique().get(c.getHistorique().size()-1).getValeurs());
                lc.setStyle("-fx-font-size:12px;-fx-text-fill:#444;");
                zcard.getChildren().add(lc);
            }
            page.getChildren().add(zcard);
        }

        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE ALERTES
    // ─────────────────────────────────────────────────────────────────
    private VBox buildAlertesPage() {
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));
        page.getChildren().add(pageTitle("Alertes"));

        // Filtres
        VBox filtres = new VBox(8);
        filtres.setStyle(CARD_STYLE);
        filtres.getChildren().add(sectionLabel("Filtres"));

        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> cbZone = new ComboBox<>();
        cbZone.getItems().add("Toutes les zones");
        ferme.getZones().forEach(z -> cbZone.getItems().add(z.getCode() + " - " + z.getName()));
        cbZone.getSelectionModel().selectFirst();

        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList(
                "Tous types", "ENV", "SOL", "AQUA", "BIOMETRIQUE", "GPS"));
        cbType.getSelectionModel().selectFirst();

        ComboBox<String> cbNiv = new ComboBox<>(FXCollections.observableArrayList(
                "Tous niveaux", "INFO", "AVERTISSEMENT", "CRITIQUE"));
        cbNiv.getSelectionModel().selectFirst();

        DatePicker dpDeb = new DatePicker();
        dpDeb.setPromptText("Date début");
        DatePicker dpFin = new DatePicker();
        dpFin.setPromptText("Date fin");
        row1.getChildren().addAll(new Label("Zone:"), cbZone, new Label("Type:"), cbType,
                new Label("Niveau:"), cbNiv, new Label("Du:"), dpDeb, new Label("Au:"), dpFin);
        filtres.getChildren().add(row1);
        page.getChildren().add(filtres);

        // Table alertes
        TableView<Alerte> ta = buildAlerteTable();

        Runnable refresh = () -> {
            Zone zSel = null;
            String zv = cbZone.getValue();
            if (zv != null && !zv.startsWith("Toutes")) {
                String code = zv.split(" - ")[0];
                zSel = ferme.trouverZoneParCode(code);
            }
            TypeCapteur tc = null;
            if (!"Tous types".equals(cbType.getValue()))
                tc = TypeCapteur.valueOf(cbType.getValue());
            Niveau_gravite nv = null;
            if (!"Tous niveaux".equals(cbNiv.getValue()))
                nv = Niveau_gravite.valueOf(cbNiv.getValue());
            List<Alerte> res = ferme.filtrer(zSel, tc, nv, dpDeb.getValue(), dpFin.getValue());
            ta.getItems().setAll(res);
        };

        Button bSearch = btn("Rechercher", BTN_STYLE);
        bSearch.setOnAction(e -> refresh.run());
        filtres.getChildren().add(bSearch);
        refresh.run();

        // Actions sur sélection
        HBox acts = new HBox(8);
        Button bAcq = btn("Acquitter", BTN_SEC);
        bAcq.setOnAction(e -> {
            Alerte a = ta.getSelectionModel().getSelectedItem();
            if (a == null) { info("Sélectionnez une alerte."); return; }
            g.acquitterAlerte(a);
            refresh.run();
        });
        Button bSup = btn("Supprimer", BTN_SEC);
        bSup.setOnAction(e -> {
            Alerte a = ta.getSelectionModel().getSelectedItem();
            if (a == null) { info("Sélectionnez une alerte."); return; }
            g.supprimerAlerte(a);
            refresh.run();
        });
        acts.getChildren().addAll(bAcq, bSup);
        page.getChildren().addAll(ta, acts);

        return page;
    }

    private TableView<Alerte> buildAlerteTable() {
        TableView<Alerte> ta = new TableView<>();
        ta.setStyle("-fx-background-color:white;");
        ta.setMaxHeight(400);

        TableColumn<Alerte, String> cId    = col("ID", 40, a -> String.valueOf(a.getId()));
        TableColumn<Alerte, String> cNiv   = col("Gravité", 110, a -> a.getGravite().name());
        TableColumn<Alerte, String> cMsg   = col("Message", 300, Alerte::getMessage);
        TableColumn<Alerte, String> cZone  = col("Zone", 120, a -> a.getZone() != null ? a.getZone().getName() : "-");
        TableColumn<Alerte, String> cDate  = col("Date", 140, a -> a.getDateCreation().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        TableColumn<Alerte, String> cStat  = col("Statut", 90, a -> a.getStatut().name());
        TableColumn<Alerte, String> cType  = col("Capteur type", 100, a -> a.getReleve().getCapteur().getType().name());
        ta.getColumns().addAll(cId, cNiv, cMsg, cZone, cDate, cStat, cType);

        // Color rows by severity
        ta.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Alerte a, boolean empty) {
                super.updateItem(a, empty);
                if (a == null || empty) { setStyle(""); return; }
                if (a.getGravite() == Niveau_gravite.CRITIQUE)
                    setStyle("-fx-background-color:#ffebee;");
                else if (a.getGravite() == Niveau_gravite.AVERTISSEMENT)
                    setStyle("-fx-background-color:#fffde7;");
                else setStyle("-fx-background-color:white;");
            }
        });
        ta.getItems().addAll(ferme.getAlertes());
        return ta;
    }

    // ─────────────────────────────────────────────────────────────────
    // PAGE PRODUCTION
    // ─────────────────────────────────────────────────────────────────
    private VBox buildProductionPage() {
        VBox page = new VBox(14);
        page.setPadding(new Insets(20));
        page.getChildren().add(pageTitle("Production"));

        for (Zone z : ferme.getZones()) {
            VBox card = new VBox(6);
            card.setStyle(CARD_STYLE);
            Label lh = new Label(z.getName() + " [" + z.getCode() + "]");
            lh.setStyle("-fx-font-weight:bold;");
            card.getChildren().add(lh);

            if (z.getProductions().isEmpty()) {
                card.getChildren().add(new Label("Aucune production enregistrée."));
            } else {
                Label ltot = new Label("Total: " + String.format("%.2f", z.getTotal()));
                ltot.setStyle("-fx-font-weight:bold;-fx-text-fill:" + GREEN_DARK + ";");
                TableView<Prod> tp = new TableView<>();
                tp.setMaxHeight(120);
                tp.setStyle("-fx-background-color:white;");
                tp.getColumns().addAll(
                        col("Date", 150, Prod::getDate),
                        col("Valeur", 90, p -> String.format("%.2f", p.getVal())),
                        col("Unité", 60, p -> p.getProd().getUnite()),
                        col("Type", 120, p -> p.getProd().name())
                );
                tp.getItems().addAll(z.getProductions());
                card.getChildren().addAll(ltot, tp);
            }

            // Enregistrer production
            HBox addProd = new HBox(8);
            addProd.setAlignment(Pos.CENTER_LEFT);
            ComboBox<TypeProd> cbProd = new ComboBox<>(FXCollections.observableArrayList(TypeProd.values()));
            cbProd.getSelectionModel().selectFirst();
            TextField tfVal = new TextField();
            tfVal.setPromptText("Valeur");
            tfVal.setMaxWidth(80);
            Button bEnreg = btn("Enregistrer", BTN_STYLE);
            bEnreg.setOnAction(e -> {
                try {
                    double val = Double.parseDouble(tfVal.getText());
                    g.enregistrerProduction(z, new Prod(val, cbProd.getValue()));
                    showPage("production");
                } catch (Exception ex) { info("Valeur invalide."); }
            });
            addProd.getChildren().addAll(new Label("Type:"), cbProd, tfVal, bEnreg);
            card.getChildren().add(addProd);

            page.getChildren().add(card);
        }
        return page;
    }

    // ─────────────────────────────────────────────────────────────────
    // DIALOGS
    // ─────────────────────────────────────────────────────────────────

    private void affecterCultureDialog(ZoneCulture zc) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Affecter une culture à " + zc.getName());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(10));
        TextField tfNom = new TextField(zc.getCultures() != null ? zc.getCultures().getNom() : "");
        TextField tfDp  = new TextField(zc.getCultures() != null ? zc.getCultures().getDatePlantation() : "");
        TextField tfDr  = new TextField(zc.getCultures() != null ? zc.getCultures().getDateRecolte() : "");
        ComboBox<StadeCroissance> cbS = new ComboBox<>(FXCollections.observableArrayList(StadeCroissance.values()));
        cbS.getSelectionModel().select(zc.getCultures() != null ? zc.getCultures().getStadeCroiss() : StadeCroissance.GERMINATION);
        TextField tfPhMin = tf("5.5"), tfPhMax = tf("7.0");
        TextField tfHMin  = tf("30"),  tfHMax  = tf("80");
        TextField tfAzMin = tf("50"),  tfAzMax = tf("150");
        gp.addRow(0, lbl("Nom:"), tfNom);
        gp.addRow(1, lbl("Plantation (AAAA-MM-JJ):"), tfDp);
        gp.addRow(2, lbl("Récolte (AAAA-MM-JJ):"), tfDr);
        gp.addRow(3, lbl("Stade:"), cbS);
        gp.addRow(4, lbl("pH min/max:"), new HBox(4, tfPhMin, new Label("/"), tfPhMax));
        gp.addRow(5, lbl("Humidité min/max:"), new HBox(4, tfHMin, new Label("/"), tfHMax));
        gp.addRow(6, lbl("Azote min/max:"), new HBox(4, tfAzMin, new Label("/"), tfAzMax));
        d.getDialogPane().setContent(gp);
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    ExigPedologiques ep = new ExigPedologiques(
                            dbl(tfPhMin), dbl(tfPhMax), dbl(tfHMin), dbl(tfHMax), dbl(tfAzMin), dbl(tfAzMax));
                    g.affecterCulture(zc, new Culture(tfNom.getText(), tfDp.getText(), tfDr.getText(), cbS.getValue(), ep));
                } catch (Exception ex) { info("Erreur: " + ex.getMessage()); }
            }
        });
    }

    private void showExigPedologiques(ZoneCulture zc) {
        if (zc.getCultures() == null || zc.getCultures().getExigPed() == null) {
            info("Aucune exigence pédologique définie."); return;
        }
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("Exigences pédologiques - " + zc.getCultures().getNom());
        al.setHeaderText(null);
        al.setContentText(zc.getCultures().getExigPed().toString());
        al.showAndWait();
    }

    private void showProgAlimDialog(ZoneElevage ze) {
        Dialog<ButtonType> d = progAlimDialog(ze.getProgAlim());
        d.setTitle("Programme alimentaire - " + ze.getName());
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
        d.setTitle("Programme alimentaire - " + za.getName());
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
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(10));
        TextField tfType = tf(existing != null ? existing.getTypeAliment() : "");
        TextField tfQ    = tf(existing != null ? String.valueOf(existing.getQuantiteParRepas()) : "1.0");
        TextField tfR    = tf(existing != null ? String.valueOf(existing.getRepasParJour()) : "2");
        gp.addRow(0, lbl("Type d'aliment:"), new HBox(tfType));
        gp.addRow(1, lbl("Quantité/repas (kg):"), new HBox(tfQ));
        gp.addRow(2, lbl("Repas/jour:"), new HBox(tfR));
        if (existing != null) {
            gp.addRow(3, lbl("Total/jour:"), new Label(String.format("%.2f kg", existing.getQuantiteJournaliere())));
        }
        d.getDialogPane().setContent(gp);
        return d;
    }

    private void ajouterAnimalDialog(ZoneElevage ze) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Ajouter un animal - " + ze.getName());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(10));
        TextField tfEsp = tf("");
        ComboBox<TypeAnimal> cbT = new ComboBox<>(FXCollections.observableArrayList(TypeAnimal.values()));
        cbT.getSelectionModel().select(ze.getTypeAnimal());
        TextField tfAge  = tf("1");
        TextField tfPoid = tf("50");
        gp.addRow(0, lbl("Nom espèce:"), tfEsp);
        gp.addRow(1, lbl("Type:"), cbT);
        gp.addRow(2, lbl("Age (ans):"), tfAge);
        gp.addRow(3, lbl("Poids (kg):"), tfPoid);
        d.getDialogPane().setContent(gp);
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    g.affecterAnimal(ze, new Animal(new EspeceAnim(cbT.getValue(), tfEsp.getText()),
                            (int)dbl(tfAge), dbl(tfPoid), EtatSante.SAIN));
                } catch (Exception ex) { info(ex.getMessage()); }
            }
        });
    }

    private void evenementSanitaireDialog(Animal a) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Evénement sanitaire - Animal #" + a.getID());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(10));
        ComboBox<TypeEvenement> cbT = new ComboBox<>(FXCollections.observableArrayList(TypeEvenement.values()));
        cbT.getSelectionModel().selectFirst();
        TextField tfDesc = tf("");
        TextField tfVal  = tf("0");
        Label lVal = lbl("Valeur (poids si PRISE_DE_POIDS):");
        gp.addRow(0, lbl("Type:"), cbT);
        gp.addRow(1, lbl("Description:"), tfDesc);
        gp.addRow(2, lVal, tfVal);
        d.getDialogPane().setContent(gp);
        d.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                double val = dbl(tfVal);
                a.enregistrerEvenementSanitaire(new EvenementSanitaire(cbT.getValue(), tfDesc.getText(), val));
            }
        });
    }

    private void showHistoriqueSanitaire(Animal a) {
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("Historique sanitaire - Animal #" + a.getID());
        al.setHeaderText(a.getEspece().getName() + " | " + a.getEtat());
        StringBuilder sb = new StringBuilder();
        if (a.getHistoriqueSanitaire().isEmpty()) sb.append("Aucun événement.");
        else a.getHistoriqueSanitaire().forEach(ev -> sb.append(ev).append("\n"));
        TextArea ta = new TextArea(sb.toString());
        ta.setEditable(false); ta.setWrapText(true); ta.setPrefHeight(200);
        al.getDialogPane().setContent(ta);
        al.showAndWait();
    }

    private void ajouterCapteurDialog() {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Ajouter un capteur");
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(10));
        TextField tfCode = tf("");
        ComboBox<TypeCapteur> cbType = new ComboBox<>(FXCollections.observableArrayList(TypeCapteur.values()));
        cbType.getSelectionModel().selectFirst();
        ComboBox<Zone> cbZone = new ComboBox<>(FXCollections.observableArrayList(ferme.getZones()));
        cbZone.getSelectionModel().selectFirst();
        // Values
        TextField v1 = tf("20"), v2 = tf("60"), v3 = tf("800");
        Label lv1 = lbl("Val 1:"), lv2 = lbl("Val 2:"), lv3 = lbl("Val 3:");
        cbType.setOnAction(e -> {
            switch (cbType.getValue()) {
                case ENV  -> { lv1.setText("Temp:"); lv2.setText("Humidité:"); lv3.setText("Pluvio:"); v3.setVisible(true); }
                case SOL  -> { lv1.setText("Azote:"); lv2.setText("Humidité:"); lv3.setText("pH:"); v3.setVisible(true); }
                case AQUA -> { lv1.setText("Temp eau:"); lv2.setText("Oxygène:"); lv3.setText("pH:"); v3.setVisible(true); }
                case BIOMETRIQUE -> { lv1.setText("Temp corp.:"); lv2.setText("Activité/min:"); lv3.setVisible(false); }
                case GPS  -> { lv1.setText("Latitude:"); lv2.setText("Longitude:"); lv3.setVisible(false); }
            }
        });
        gp.addRow(0, lbl("Code:"), tfCode);
        gp.addRow(1, lbl("Type:"), cbType);
        gp.addRow(2, lbl("Zone:"), cbZone);
        gp.addRow(3, lv1, v1);
        gp.addRow(4, lv2, v2);
        gp.addRow(5, lv3, v3);
        d.getDialogPane().setContent(gp);
        d.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK || cbZone.getValue() == null) return;
            Zone z = cbZone.getValue();
            try {
                Capteurs c = null;
                switch (cbType.getValue()) {
                    case ENV  -> c = new Cap_env(tfCode.getText(), z, Status.ACTIF, dbl(v1), dbl(v2), dbl(v3));
                    case SOL  -> c = new Cap_sol(tfCode.getText(), z, Status.ACTIF, dbl(v1), dbl(v2), dbl(v3));
                    case AQUA -> c = new Cap_aqua(tfCode.getText(), z, Status.ACTIF, dbl(v1), dbl(v2), dbl(v3));
                    case BIOMETRIQUE -> c = new Cap_biometrique(tfCode.getText(), z, Status.ACTIF, dbl(v1), dbl(v2));
                    case GPS  -> {
                        Animal anGPS = null;
                        if (z instanceof ZoneElevage ze && !ze.getAnimaux().isEmpty())
                            anGPS = ze.getAnimaux().get(0);
                        else
                            anGPS = new Animal(new EspeceAnim(TypeAnimal.RUMINANT, "Animal GPS"), 1, 50, EtatSante.SAIN);
                        c = new Capteur_GPS(tfCode.getText(), z, Status.ACTIF,
                                new PositionGeographique(dbl(v1), dbl(v2)), anGPS);
                    }
                }
                if (c != null) g.ajouterCapteur(z, c);
            } catch (Exception ex) { info("Erreur: " + ex.getMessage()); }
        });
    }

    private void configurerSeuilsDialog(Capteurs c) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Seuils - " + c.getCode());
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(8); gp.setPadding(new Insets(10));

        if (c instanceof Cap_env ce) {
            TextField tTMin=tf("8"),tTMax=tf("32"),tHMin=tf("40"),tHMax=tf("80"),tPMin=tf("500"),tPMax=tf("1200");
            gp.addRow(0,lbl("Temp min/max:"),new HBox(4,tTMin,new Label("/"),tTMax));
            gp.addRow(1,lbl("Humidité min/max:"),new HBox(4,tHMin,new Label("/"),tHMax));
            gp.addRow(2,lbl("Pluvio min/max:"),new HBox(4,tPMin,new Label("/"),tPMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{ if(bt==ButtonType.OK) ce.configurer(dbl(tTMin),dbl(tTMax),dbl(tHMin),dbl(tHMax),dbl(tPMin),dbl(tPMax)); });
        } else if (c instanceof Cap_sol cs) {
            TextField tHMin=tf("15"),tHMax=tf("22"),tPhMin=tf("5.8"),tPhMax=tf("6.8"),tAzMin=tf("25"),tAzMax=tf("50");
            gp.addRow(0,lbl("Humidité min/max:"),new HBox(4,tHMin,new Label("/"),tHMax));
            gp.addRow(1,lbl("pH min/max:"),new HBox(4,tPhMin,new Label("/"),tPhMax));
            gp.addRow(2,lbl("Azote min/max:"),new HBox(4,tAzMin,new Label("/"),tAzMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{ if(bt==ButtonType.OK){ cs.configurerHum(dbl(tHMin),dbl(tHMax)); cs.configurerTemp(dbl(tPhMin),dbl(tPhMax)); cs.configurerPh(dbl(tAzMin),dbl(tAzMax)); }});
        } else if (c instanceof Cap_aqua ca) {
            TextField tTMin=tf("20"),tTMax=tf("30"),tOMin=tf("5.5"),tOMax=tf("6.5"),tPhMin=tf("6"),tPhMax=tf("9");
            gp.addRow(0,lbl("Temp min/max:"),new HBox(4,tTMin,new Label("/"),tTMax));
            gp.addRow(1,lbl("Oxygène min/max:"),new HBox(4,tOMin,new Label("/"),tOMax));
            gp.addRow(2,lbl("pH min/max:"),new HBox(4,tPhMin,new Label("/"),tPhMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{ if(bt==ButtonType.OK) ca.configurer(dbl(tTMin),dbl(tTMax),dbl(tOMin),dbl(tOMax),dbl(tPhMin),dbl(tPhMax)); });
        } else if (c instanceof Cap_biometrique cb) {
            TextField tTMin=tf("38"),tTMax=tf("41"),tAMin=tf("0"),tAMax=tf("260");
            gp.addRow(0,lbl("Temp min/max:"),new HBox(4,tTMin,new Label("/"),tTMax));
            gp.addRow(1,lbl("Activité min/max:"),new HBox(4,tAMin,new Label("/"),tAMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{ if(bt==ButtonType.OK) cb.configurer(dbl(tTMin),dbl(tTMax),dbl(tAMin),dbl(tAMax)); });
        } else if (c instanceof Capteur_GPS cg) {
            TextField tLonMin=tf("-5"),tLonMax=tf("5"),tLatMin=tf("-5"),tLatMax=tf("5");
            gp.addRow(0,lbl("Longitude min/max:"),new HBox(4,tLonMin,new Label("/"),tLonMax));
            gp.addRow(1,lbl("Latitude min/max:"),new HBox(4,tLatMin,new Label("/"),tLatMax));
            d.getDialogPane().setContent(gp);
            d.showAndWait().ifPresent(bt->{ if(bt==ButtonType.OK) cg.configurer(dbl(tLonMin),dbl(tLonMax),dbl(tLatMin),dbl(tLatMax)); });
        } else {
            info("Type de capteur non configurable.");
        }
    }

    private void showHistoriqueReleves(Capteurs c) {
        Stage s = new Stage();
        s.setTitle("Historique relevés - " + c.getCode());
        VBox root = new VBox(10);
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color:white;");

        // Filtre dates
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        DatePicker dpD = new DatePicker(), dpF = new DatePicker();
        dpD.setPromptText("Début"); dpF.setPromptText("Fin");
        Button bFilt = btn("Filtrer", BTN_STYLE);
        row.getChildren().addAll(new Label("Du:"), dpD, new Label("Au:"), dpF, bFilt);
        root.getChildren().add(row);

        TableView<Releve> tr = new TableView<>();
        tr.setMaxHeight(300);
        tr.getColumns().addAll(
                col("ID", 40, r -> String.valueOf(r.getId())),
                col("Date/Heure", 150, r -> r.getDateHeure().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                col("Niveau", 110, r -> r.getNiveauReleve().name()),
                col("Valeurs", 350, r -> r.getValeurs().toString())
        );
        tr.getItems().addAll(c.getHistorique());
        bFilt.setOnAction(e -> {
            List<Releve> filtered = ReleveSpecifications.filtrer(c.getHistorique(), dpD.getValue(), dpF.getValue());
            tr.getItems().setAll(filtered);
        });
        root.getChildren().add(tr);

        // Evolution chart
        root.getChildren().add(new Label("Evolution des relevés:"));
        if (c.getHistorique().size() > 1) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(null); chart.setLegendVisible(true);
            chart.setPrefHeight(220);
            // One series per value key
            if (!c.getHistorique().isEmpty()) {
                Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
                for (Releve r : c.getHistorique()) {
                    String time = r.getDateHeure().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    for (Map.Entry<String, Object> entry : r.getValeurs().entrySet()) {
                        seriesMap.computeIfAbsent(entry.getKey(), k -> {
                            XYChart.Series<String, Number> ss = new XYChart.Series<>();
                            ss.setName(k); return ss;
                        });
                        try {
                            double val = ((Number) entry.getValue()).doubleValue();
                            seriesMap.get(entry.getKey()).getData().add(new XYChart.Data<>(time, val));
                        } catch (Exception ignored) {}
                    }
                }
                chart.getData().addAll(seriesMap.values());
            }
            root.getChildren().add(chart);
        } else {
            root.getChildren().add(new Label("Pas assez de données pour le graphique."));
        }

        s.setScene(new Scene(new ScrollPane(root), 700, 600));
        s.show();
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────

    private VBox statCard(String label, String value) {
        VBox c = new VBox(2);
        c.setStyle("-fx-background-color:" + GREEN_LIGHT + ";-fx-padding:12 20;" +
                "-fx-background-radius:4;-fx-min-width:120px;");
        c.setAlignment(Pos.CENTER);
        Label lv = new Label(value);
        lv.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:" + GREEN_DARK + ";");
        Label ll = new Label(label);
        ll.setStyle("-fx-font-size:12px;-fx-text-fill:#333;");
        c.getChildren().addAll(lv, ll);
        return c;
    }

    private HBox alerteRow(Alerte a) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 8, 4, 8));
        String bg = a.getGravite() == Niveau_gravite.CRITIQUE ? "#ffebee" :
                a.getGravite() == Niveau_gravite.AVERTISSEMENT ? "#fffde7" : "#f1f8e9";
        row.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:3;");
        Label lNiv = new Label(a.getGravite().name());
        lNiv.setStyle("-fx-font-weight:bold;-fx-min-width:100px;");
        Label lMsg = new Label(a.getMessage());
        Label lZone = new Label(a.getZone() != null ? "[" + a.getZone().getName() + "]" : "");
        lZone.setStyle("-fx-text-fill:#666;");
        row.getChildren().addAll(lNiv, lMsg, lZone);
        return row;
    }

    private Label pageTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:" + GREEN_DARK + ";" +
                "-fx-border-color:transparent transparent " + GREEN_MID + " transparent;" +
                "-fx-border-width:0 0 2 0;-fx-padding:0 0 6 0;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + GREEN_DARK + ";");
        return l;
    }

    private Button btn(String text, String style) {
        Button b = new Button(text);
        b.setStyle(style);
        return b;
    }

    private <T> TableColumn<T, String> col(String title, int width, java.util.function.Function<T, String> fn) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        c.setCellValueFactory(cd -> new SimpleStringProperty(fn.apply(cd.getValue())));
        return c;
    }

    private void addGridRow(GridPane gp, int row, String label, String value) {
        gp.add(lbl(label), 0, row);
        Label lv = new Label(value != null ? value : "-");
        lv.setStyle("-fx-text-fill:#333;");
        gp.add(lv, 1, row);
    }

    private Label lbl(String t) { Label l = new Label(t); l.setStyle("-fx-font-weight:bold;"); return l; }
    private TextField tf(String v) { TextField t = new TextField(v); t.setMaxWidth(80); return t; }

    private double dbl(TextField tf) {
        try { return Double.parseDouble(tf.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void info(String msg) {
        Alert al = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        al.showAndWait();
    }
}