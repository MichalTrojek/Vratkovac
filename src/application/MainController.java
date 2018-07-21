package application;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import application.article.Article;
import application.excel.ExcelUtils;
import application.info.InfoModel;
import application.server.Server;
import application.settings.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;

public class MainController {

	private ArrayList<Article> articles = new ArrayList<>();
	private ObservableList<Article> dataForList = FXCollections.observableArrayList();
	private Settings settings = new Settings();
	private Server server = new Server();

	@FXML
	private TextField input;
	@FXML
	private TextField pathTextfield;
	@FXML
	private Label totalAmountOfBooks;
	@FXML
	private Label totalAmountOfRows;
	@FXML
	private Button exportButton;
	@FXML
	private Button removeButton;
	@FXML
	private Button removeAllButton;
	@FXML
	private Button updateButton;
	@FXML
	private TableView<Article> table;
	@FXML
	private TableColumn<Article, String> amountColumn;
	@FXML
	private TableColumn<Article, String> eanColumn;
	@FXML
	private Label infoLabel;
	@FXML
	private Label serverInfoLabel;
	@FXML
	private Label ipLabel;

	@FXML
	public void initialize() {
		pathTextfield.setText(settings.getPath());
		setupTable();
		handleEditingOfAmountColumn();
		InfoModel.getInstance().bindLabelToInfo(infoLabel);
		server.waitForResponse(serverInfoLabel, articles, dataForList);
		refreshTableData();
		ipLabel.setText("IP adresa tohoto počítače " + getIp());
	}

	private String getIp() {
		String ip = "";
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ip = socket.getLocalAddress().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	private void setupTable() {
		table.setPlaceholder(new Label(""));
		table.setEditable(true);
	}

	private void refreshTableData() {
		dataForList.removeAll(dataForList);
		dataForList.addAll(articles);
		table.setItems(dataForList);
		table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("ean"));
		table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("amount"));
		updateInfo();
		InfoModel.getInstance().updateInfo("");
	}

	private void handleEditingOfAmountColumn() {
		amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		amountColumn.setOnEditCommit(event -> {
			int oldValue = Integer.parseInt(event.getOldValue());
			if (!isNegative(oldValue, Integer.parseInt(event.getNewValue()))) {
				((Article) event.getTableView().getItems().get(event.getTablePosition().getRow()))
						.editAmountValue(event.getNewValue());
			}
			refreshTableData();
		});
	}

	private boolean isNegative(int oldValue, int newValue) {
		return (oldValue + newValue) < 0;
	}

	private void updateInfo() {
		totalAmountOfBooks.setText(calculateAmount() + "");
		totalAmountOfRows.setText(dataForList.size() + "");
	}

	private int calculateAmount() {
		int amount = 0;
		for (Article a : articles) {
			amount += Integer.parseInt(a.getAmount());
		}
		return amount;
	}

	@FXML
	public void onEnter(ActionEvent ae) {
		addEan();
		refreshTableData();
	}

	private void addEan() {
		String ean = input.getText();
		if (!ean.isEmpty()) {
			handleAddingEan(ean);
		}
		input.clear();
	}

	private void handleAddingEan(String ean) {
		if (articles.isEmpty()) {
			articles.add(0, new Article(ean));
		} else {
			iterateList(articles, ean);
		}
		input.setText("");
	}

	private void iterateList(ArrayList<Article> articles, String ean) {
		boolean containsEan = false;
		for (int i = 0; i < articles.size(); i++) {
			Article a = articles.get(i);
			containsEan = false;
			if (ean.equalsIgnoreCase(a.getEan())) {
				incrementAmount(a);
				containsEan = true;
				break;
			}
		}
		if (!containsEan) {
			articles.add(0, new Article(ean));
		}
	}

	private void incrementAmount(Article a) {
		int amount = Integer.parseInt(a.getAmount()) + 1;
		String currentEan = a.getEan();
		articles.remove(a);
		articles.add(0, new Article(currentEan));
		articles.get(0).editAmountValue(amount + "");
	}

	@FXML
	void handleExportButton(ActionEvent event) {
		if (table.getItems().size() > 0) {
			ExcelUtils excel = new ExcelUtils();
			excel.writeFileTwoInputs(table.getItems(), settings.getPath(), "vratka.xlsx");
			InfoModel.getInstance().updateInfo("Soubor byl exportován.");
		} else {
			InfoModel.getInstance().updateInfo("\t\tVratka je prázdná.");
		}

	}

	@FXML
	void handleUpdateButton(ActionEvent event) {
		updateInfo();
	}

	@FXML
	void handleChooseButton() {
		DirectoryChooser dc = new DirectoryChooser();
		File path = dc.showDialog(pathTextfield.getScene().getWindow());
		if (path != null) {
			pathTextfield.setText(path.getAbsolutePath());
			settings.savePath(pathTextfield.getText());
		}
	}

	@FXML
	void handleRemoveButton() {
		Article selectedArticle = table.getSelectionModel().getSelectedItem();
		try {
			// articles.remove(selectedArticle.getEan());
			articles.remove(selectedArticle);
		} catch (NullPointerException e) {
			System.out.println("No item is selected");
		}
		refreshTableData();
	}

	@FXML
	void handleRemoveAllButton() {
		articles.clear();
		refreshTableData();
	}

}
