package es.ies;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* ACTIVIDAD 1 - GESTOR DE TAREAS CON JAVAFX
    * Conceptos principales:
    * - Application, Stage, Scene
    + - VBox y HBox
    + - TaxtField, ComboBox, ListView, Button y Label
    * - Propiedades de componentes
    * - Eventos con setOnAction()
    + - Listeners sobre propiedades
    + - SelectionModel
    * - Alert
    * - Validación y feedback

    * La aplicación hereda de Application, la clase base de JavaFX

    + El flujo de arranque de wna aplicación JavaFX es:
    * main () -> launch() -> JavaFX inicia su entorno -> start (Stage state) -> Se construye la interfaz -> stage. show()
*/

public class App extends Application {

    private ComboBox<String> comboPrioridad;
    private ListView<String> listaTareas;
    private Button botonCompletar, botonEliminar;
    private Label mensaje, estado;
    private TextField campoTarea;

    @Override
    public void start(Stage stage) {
        // 1. Creamos una etiqueta y se modifica su estilo vía CSS
        Label titulo = new Label("Gestor de Tareas");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // 2. Creamos un TextField para introducir el texto de la tarea
        campoTarea = new TextField();
        campoTarea.setPromptText("Escribe una tarea");

        // 3. Permitir que este TextField puede crecer horizontalmente cuando se redimensiona la ventana
        HBox.setHgrow(campoTarea, Priority.ALWAYS);
        
        // 4. Creamos un desplegable con tres prioridades (baja, media, alta).
        // Queremos que se quede seleccionada la prioridad media
        comboPrioridad = new ComboBox<>();
        comboPrioridad.getItems().addAll("Baja", "Media", "Alta");
        comboPrioridad.setValue("Media");

        // 5. Añadimos un botón para añadir la tarea. Es el botón predeterminado de la ventana
        Button botonAnadir = new Button("Añadir tarea");
        botonAnadir.setDefaultButton(true);

        // 6. Creamos un contenedor horizontal para colocar los elementos
        HBox formulario = new HBox(10, 
            new Label("Nueva tarea:"),
            campoTarea,
            new Label("Prioridad:"),
            comboPrioridad,
            botonAnadir
        );

        formulario.setAlignment(Pos.CENTER_LEFT);

        // 7. Creamos una lista donde se van a mostrar las tareas creadas
        listaTareas = new ListView<>();
        listaTareas.setPrefHeight(280);

        // 8. Añadimos una lista de botones
        botonCompletar = new Button("Marcar como completada");
        botonCompletar.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        botonEliminar = new Button("Eliminar");
        botonEliminar.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        Button botonLimpiar = new Button("Limpiar todas");
        botonLimpiar.setStyle("-fx-background-color: blue; -fx-text-fill: white;");

        // 9. Defino un contenedor horizontal para poner todos los botones
        HBox acciones = new HBox(10,
            botonCompletar,
            botonEliminar,
            botonLimpiar
        );

        // 10. Lo alineo al centro y a la izquierda
        acciones.setAlignment(Pos.CENTER);

        // 11. Defino dos labels para mostrar información
        mensaje = new Label();
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(Double.MAX_VALUE);
        mensaje.setAlignment(Pos.CENTER);
        estado = new Label();

        // 12. Defino mi elemento raíz de la ventana donde voy a ir apilando (verticalmente)
        // cada uno de los elementos gráficos de la ventana
        VBox raiz = new VBox(14,
            titulo,
            formulario,
            new Label("Lista de tareas"),
            listaTareas,
            acciones,
            mensaje,
            estado
        );

        // 13. Añadimos 20 pixeles de margen inferior
        raiz.setPadding(new Insets(20));

        // 14. Permitimos que la lista crezca verticalmente si crece la ventana
        VBox.setVgrow(listaTareas, Priority.ALWAYS);

        // 15. Añadimos eventos a los botones usando expresiones lambda
        botonAnadir.setOnAction(e -> anadirTarea());
        botonEliminar.setOnAction(e -> eliminarTarea());
        botonCompletar.setOnAction(e -> completarTarea());
        botonLimpiar.setOnAction(e -> limpiarCompletadas());

        // Stage es Ventana
        // Scene es Contenido
        Scene scene = new Scene(raiz, 820, 500);
        stage.setTitle("Gestor de tareas con JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    private void limpiarCompletadas() {
        int eliminadas = 0;

        for (int i = listaTareas.getItems().size() - 1; i >= 0; i--) {
            String tarea = listaTareas.getItems().get(i);

            if (tarea.endsWith("— Completada")) {
                listaTareas.getItems().remove(i);
                eliminadas++;
            }
        }

        if (eliminadas > 0) {
            mostrarMensaje("Se han eliminado las tareas completadas");
        } else {
            mostrarMensaje("No se ha eliminado ninguna tarea porque no hay ninguna completada");
        }
    }

    private void completarTarea() {
        int indiceSeleccionado = listaTareas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado == -1) {
            mostrarMensaje("Selecciona una tarea para completar");
            return;
        }

        String tarea = listaTareas.getItems().get(indiceSeleccionado);

        if (tarea.endsWith("— Completada")) {
            mostrarMensaje("La tarea ya está completada");
            return;
        }

        tarea += " — Completada";

        listaTareas.getItems().set(indiceSeleccionado, tarea);
        mostrarMensaje("Tarea completada");
    }

    private void eliminarTarea() {
        int indiceSeleccionado = listaTareas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado == -1) {
            mostrarMensaje("Selecciona una tarea para eliminar");
            return;
        }

        listaTareas.getItems().remove(indiceSeleccionado);
        mostrarMensaje("Se ha eliminado la tarea");
    }

    private void anadirTarea() {
        String texto = campoTarea.getText().trim();

        if (texto.isEmpty()) {
            mostrarMensaje("Introduce una tarea. No se permiten tareas vacías");
            campoTarea.requestFocus();
            return;
        }

        String prioridad = comboPrioridad.getValue();
        String tarea = "[" + prioridad + "] " + texto;

        listaTareas.getItems().add(tarea);
        listaTareas.getSelectionModel().selectLast();
        listaTareas.scrollTo(listaTareas.getItems().size() - 1);

        campoTarea.clear();
        comboPrioridad.setValue("Media");
        campoTarea.requestFocus();
        
        mostrarMensaje("Tarea añadida");
    }

    private void mostrarMensaje(String mensaje) {
        this.mensaje.setText(mensaje);
    }

    public static void main( String[] args ) {
        launch();
    }
}
