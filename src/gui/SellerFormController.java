package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private Seller entity;
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthdate;
	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Button txtSave;

	@FXML
	private Button btnCancel;

	@FXML
	private Label IblErrorName;
	@FXML
	private Label IblErrorEmail;
	@FXML
	private Label IblErrorBirthDate;
	@FXML
	private Label IblErrorBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	private ObservableList<Department> obsList;

	@FXML
	public void onbtnSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("entity was null:/");
		}
		if (service == null) {
			throw new IllegalStateException("service was null:/");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifysubscribeDataChangeListener();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setMessageError(e.getError());
		} catch (DbException e) {
			Alerts.showAlerts("error saving seller", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifysubscribeDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Campo obrigatório!");
		}
		if (exception.getError().size() > 0) {
			throw exception;
		}
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onbtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void SetSeller(Seller entity) {
		this.entity = entity;
	}

	public void SetServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFildInteger(txtId);
		Constraints.setTextFildMaxLength(txtName, 15);
		Constraints.setTextFildMaxLength(txtEmail, 70);
		Constraints.setTextFildDouble(txtBaseSalary);
		Utils.formatDatePicker(dpBirthdate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("entity was null:/");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if(entity.getBirthDate()!= null) {
			dpBirthdate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if(entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}else {
		comboBoxDepartment.setValue(entity.getDepartment());
	 }
	}
	public void setMessageError(Map<String, String> errors) {
		Set<String> filds = errors.keySet();
		if (filds.contains("name")) {
			IblErrorName.setText(errors.get("name"));
		}
	}
	public void loadAssociatedObjects() {
		if(departmentService == null) {
			throw new IllegalStateException("departmentService was null :/");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}
	private void initializeComboBoxDepartment() { 
		 Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() { 
		     @Override 
		     protected void updateItem(Department item, boolean empty) { 
		         super.updateItem(item, empty); 
		         setText(empty ? "" : item.getName()); 
		     } 
		 }; 
		 
		 comboBoxDepartment.setCellFactory(factory); 
		 comboBoxDepartment.setButtonCell(factory.call(null));   
		}
}
