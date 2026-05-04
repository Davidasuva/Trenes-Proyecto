package boardingScreen.controller;

import boardingScreen.factory.BoardingFactory;
import boardingScreen.model.BoardingScreenModel;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BoardingOrderController implements Initializable {

    @FXML private Label lblRutaNombre;
    @FXML private Label lblRutaDetalle;
    @FXML private Label lblTurnoActual;
    @FXML private Label lblNombrePasajero;
    @FXML private Label lblCategoria;
    @FXML private Label lblVagon;
    @FXML private ProgressBar progressBar;
    @FXML private Label lblProgreso;
    @FXML private VBox panelAnterior;
    @FXML private Label lblAnteriorNombre;
    @FXML private Label lblAnteriorDetalle;
    @FXML private Button btnSeleccionarRuta;
    @FXML private Button btnPausarReanudar;
    @FXML private Label lblEstado;

    private BoardingScreenModel model;
    private List<String[]> entradas = new ArrayList<>();
    private int indiceActual = 0;
    private Timeline timeline;
    private boolean pausado = false;

    private static final int INTERVALO_SEG = 3;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnPausarReanudar.setText("⏸  Pausar");
    }

    public void setModel(BoardingScreenModel model) {
        this.model = model;
        cargarOrden();
    }

    private void cargarOrden() {
        if (model.getCurrentRoute() == null) return;

        lblRutaNombre.setText(model.getCurrentRoute().getName());
        lblRutaDetalle.setText(
                model.getCurrentRoute().getOrigin().getName() + " → "
                + model.getCurrentRoute().getDestiny().getName()
                + "   ·   Salida: " + model.getCurrentRoute().getDateTravelStr()
        );
        lblEstado.setVisible(false);

        new Thread(() -> {
            LinkedList<String> orden = model.getBoardingOrder(model.getCurrentRoute().getId());
            List<String[]> parsed = parsear(orden);
            Platform.runLater(() -> {
                entradas = parsed;
                if (entradas.isEmpty()) {
                    lblNombrePasajero.setText("Sin pasajeros registrados");
                    lblCategoria.setText("");
                    lblVagon.setText("");
                    lblTurnoActual.setText("—");
                    progressBar.setProgress(0);
                    lblProgreso.setText("0 / 0");
                    return;
                }
                iniciarTimeline();
            });
        }).start();
    }

    /**
     * Parsea las líneas del formato:
     *   "=== ORDEN DE ABORDAJE: xxx ===" → ignorar
     *   "── Vagón 2 ──" → registrar vagón actual
     *   "  [Premium]" → registrar categoría actual
     *   "    Turno 1 → Nombre Apellido" → crear entrada
     */
    private List<String[]> parsear(LinkedList<String> lineas) {
        List<String[]> result = new ArrayList<>();
        String vagonActual = "?";
        String catActual = "?";
        Iterator<String> it = lineas.iterator();
        while (it.hasNext()) {
            String linea = it.next().trim();
            if (linea.startsWith("──")) {
                // "── Vagón 2 ──"
                vagonActual = linea.replace("──", "").trim();
            } else if (linea.startsWith("[") && linea.endsWith("]")) {
                catActual = linea.substring(1, linea.length() - 1);
            } else if (linea.startsWith("Turno")) {
                // "Turno 1 → Nombre Apellido"
                int arrIdx = linea.indexOf("→");
                if (arrIdx >= 0) {
                    String turnoStr = linea.substring(0, arrIdx).trim().replace("Turno", "").trim();
                    String nombre = linea.substring(arrIdx + 1).trim();
                    result.add(new String[]{ turnoStr, nombre, catActual, vagonActual });
                }
            }
        }
        return result;
    }

    private void iniciarTimeline() {
        if (timeline != null) timeline.stop();
        mostrarEntrada(indiceActual);

        timeline = new Timeline(new KeyFrame(Duration.seconds(INTERVALO_SEG), e -> {
            if (indiceActual < entradas.size() - 1) {
                indiceActual++;
                mostrarEntrada(indiceActual);
            } else {
                lblEstado.setText("Abordaje completado — reiniciando...");
                lblEstado.setVisible(true);
                timeline.stop();
                btnPausarReanudar.setDisable(true);
                javafx.animation.PauseTransition pausa =
                        new javafx.animation.PauseTransition(Duration.seconds(3));
                pausa.setOnFinished(ev -> {
                    indiceActual = 0;
                    lblEstado.setVisible(false);
                    btnPausarReanudar.setDisable(false);
                    btnPausarReanudar.setText("⏸  Pausar");
                    pausado = false;
                    iniciarTimeline();
                });
                pausa.play();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void mostrarEntrada(int idx) {
        if (idx < 0 || idx >= entradas.size()) return;

        if (idx > 0) {
            String[] anterior = entradas.get(idx - 1);
            panelAnterior.setVisible(true);
            panelAnterior.setManaged(true);
            lblAnteriorNombre.setText(anterior[1]);
            lblAnteriorDetalle.setText("Turno " + anterior[0] + "  ·  " + anterior[2] + "  ·  " + anterior[3]);
        } else {
            panelAnterior.setVisible(false);
            panelAnterior.setManaged(false);
        }

        String[] entrada = entradas.get(idx);
        lblTurnoActual.setText("Turno " + entrada[0]);
        lblNombrePasajero.setText(entrada[1]);
        lblCategoria.setText(entrada[2]);
        lblVagon.setText(entrada[3]);

        double progreso = (double)(idx + 1) / entradas.size();
        progressBar.setProgress(progreso);
        lblProgreso.setText((idx + 1) + " / " + entradas.size());
    }

    @FXML
    private void handlePausarReanudar() {
        if (timeline == null) return;
        if (pausado) {
            timeline.play();
            btnPausarReanudar.setText("⏸  Pausar");
        } else {
            timeline.pause();
            btnPausarReanudar.setText("▶  Reanudar");
        }
        pausado = !pausado;
    }

    @FXML
    private void handleSeleccionarOtraRuta() {
        if (timeline != null) timeline.stop();
        Stage stage = (Stage) btnSeleccionarRuta.getScene().getWindow();
        BoardingFactory.showRouteSelect(stage, model);
    }
}
