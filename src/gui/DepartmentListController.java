package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	private DepartmentService service;
	@FXML
	private TableView<Department> tableViewDepartmentList;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private Button btNew;
	private ObservableList<Department> obsList;

	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDepartmentFrom(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<Department, Integer>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getScene().getWindow();
		tableViewDepartmentList.prefHeightProperty().bind(stage.heightProperty());
	}
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("service was null:/");
		}
		List<Department>list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartmentList.setItems(obsList);
	}
	private void createDepartmentFrom(Department obj, String absoluteName,Stage parentStage) {
		FXMLLoader loader =  new FXMLLoader(getClass().getResource(absoluteName));
		try {
			Pane pane = loader.load();
			DepartmentFormController controller = loader.getController();
			controller.SetDepartment(obj);
			controller.SetDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlerts("IO EXCEPTION", "Error loading view", e.getMessage(),AlertType.ERROR);
			
			
			
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
}
