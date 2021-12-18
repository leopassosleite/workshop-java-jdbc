package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Client;
import model.entities.Department;
import model.services.ClientService;

public class ClientListController implements Initializable, DataChangeListener {

	private ClientService service;

	@FXML
	private TableView<Client> tableViewClient;

	@FXML
	private TableColumn<Client, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Client, String> tableColumnName;

	@FXML
	private TableColumn<Client, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Client, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Client, String> tableColumnDistrict;

	@FXML
	private TableColumn<Client, String> tableColumnCity;
	
	@FXML
	private TableColumn<Client, Integer> tableColumnCep;
	
	@FXML
	private TableColumn<Client, Integer> tableColumnRg;
	
	@FXML
	private TableColumn<Client, Integer> tableColumnCpf;
	
	@FXML
	private TableColumn<Client, Integer> tableColumnPhone;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private TableColumn<Client, Client> tableColumnREMOVE;

	@FXML
	private TableColumn<Client, Client> tableColumnEDIT;

	@FXML
	private Button btNew;

	private ObservableList<Client> obsList;

	@FXML
	public void OnBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Client obj = new Client();
		CreateDialogForm(obj, "/gui/ClientForm.fxml", parentStage);
	}

	public void setClientService(ClientService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnDistrict.setCellValueFactory(new PropertyValueFactory<>("district"));
		tableColumnCity.setCellValueFactory(new PropertyValueFactory<>("city"));
		tableColumnCep.setCellValueFactory(new PropertyValueFactory<>("cep"));
		tableColumnRg.setCellValueFactory(new PropertyValueFactory<>("rg"));
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewClient.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Client> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewClient.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void CreateDialogForm(Client obj, String absoluteName, Stage parentStage) {
		/*try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ClientFormController controller = loader.getController();
			controller.setClient(obj);
			controller.setDeparmentService(new ClientService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Informe os dados do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}*/
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Client, Client>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Client obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> CreateDialogForm(obj, "/gui/ClientForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Client, Client>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Client obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> ObjectremoveEntity(obj));
			}
		});
	}

	private void ObjectremoveEntity(Client obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmatio", "DELETAR?");
		
		if(result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
