package com.mycompany.gestorpracticasprueba;

import Hibernate.HibernateUtil;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import models.Actividad;
import models.Alumno;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class SecondaryController implements Initializable {
    
    @FXML
    private TextField nombreTxt;
    @FXML
    private TextField horasTxt;
    @FXML
    private DatePicker fechaTxt;
    @FXML
    private TextArea observacionesTxt;
    @FXML
    private Button anadirBtn;
    @FXML
    private Button salirBtn;
    @FXML
    private TableView<Actividad> tabla;
    @FXML
    private TableColumn<Actividad, Long> cId;
    @FXML
    private TableColumn<Actividad, String> cNombre;
    @FXML
    private TableColumn<Actividad, Integer> cHoras;
    @FXML
    private TableColumn<Actividad, Date> cFecha;
    @FXML
    private TableColumn<Actividad, String> cObservaciones;
    @FXML
    private Button eliminarBtn;
    
    private Actividad a;
    @FXML
    private Button actualizarBtn;
    
    private void switchToPrimary() throws IOException {
        App.setRoot(" ");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cHoras.setCellValueFactory(new PropertyValueFactory<>("horas"));
        cFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cObservaciones.setCellValueFactory(new PropertyValueFactory<>("incidencias"));
        
        actualizarTabla();
        
    }
    
    private void actualizarTabla() throws HibernateException {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Alumno a = s.load(Alumno.class, SessionData.getAlumnoActual().getId());
        SessionData.setAlumnoActual(a);
        
        tabla.getItems().clear();
        tabla.getItems().addAll(a.getActividades());
        s.close();
    }

    //static EntityManagerFactory emf = Persistence.createEntityManagerFactory("");
    @FXML
    private void eliminar(ActionEvent event) {
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        //s.delete( SessionData.getActividadActual() );
        s.remove(tabla.getSelectionModel().getSelectedItem());
        tr.commit();
        actualizarTabla();
        s.close();
        
    }

    //Hecho
    @FXML
    private void salir(ActionEvent event) {
        Stage stage = (Stage) this.salirBtn.getScene().getWindow();
        stage.close();
        
    }
    
    @FXML
    private void añadir(ActionEvent event) {
        Actividad a = SessionData.getActividadActual();
        if (a != null) {
            
            SessionData.getActividadActual().setNombre(nombreTxt.getText());
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = s.beginTransaction();
            s.update(SessionData.getActividadActual());
            tr.commit();
            s.close();
            
        } else {
            
            a = new Actividad();
            
            a.setNombre(nombreTxt.getText());
            a.setHoras(Integer.parseInt(horasTxt.getText()));
            
            LocalDate localDate = fechaTxt.getValue();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            System.out.println(date);
            
            a.setFecha(date);
            
            a.setIncidencias(observacionesTxt.getText());
            a.setAlumno(SessionData.getAlumnoActual());
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = s.beginTransaction();
            s.save(a);
            tr.commit();
            s.close();
            
            actualizarTabla();
            
        }
        
    }
    
    
    
    @FXML
    private void update(MouseEvent event) {
        
        Actividad a = tabla.getSelectionModel().getSelectedItem();
        SessionData.setActividadActual(a);
        
        if (a != null) {
            
            nombreTxt.setText(a.getNombre());
            horasTxt.setText(a.getHoras() + "");
            fechaTxt.setValue(a.getFecha().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
            
            observacionesTxt.setText(a.getIncidencias());
            
            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = s.beginTransaction();
            s.update(a);
            tr.commit();
            s.close();
            
            actualizarTabla();
            

        }
        
        
    }
}
