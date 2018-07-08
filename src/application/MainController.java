package application;

import java.io.File;
import java.util.HashMap;

import application.article.Article;
import application.excel.ExcelUtils;
import application.info.InfoModel;
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

	private HashMap<String, Article> articles = new HashMap<>();
	private ObservableList<Article> dataForList = FXCollections.observableArrayList();
	private Settings settings = new Settings();

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
	private TableView<Article> table;
	@FXML
	private TableColumn<Article, String> amountColumn;
	@FXML
	private TableColumn<Article, String> eanColumn;
	@FXML
	private Label infoLabel;

	@FXML
	public void initialize() {
		pathTextfield.setText(settings.getPath());
		setupTable();
		refreshTableData();
		handleEditingOfAmountColumn();
		InfoModel.getInstance().bindLabelToInfo(infoLabel);
	}

	private void setupTable() {
		table.setPlaceholder(new Label(""));
		table.setEditable(true);
	}

	private void refreshTableData() {
		dataForList.removeAll(dataForList);
		dataForList.addAll(articles.values());
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
		for (Article a : articles.values()) {
			amount += Integer.parseInt(a.getAmount());
		}
		return amount;
	}

	@FXML
	public void onEnter(ActionEvent ae) {
		String data = input.getText();
		if (data.length() > 0) {
			if (articles.containsKey(data)) {
				articles.get(data).increaseAmount();
			} else {
				articles.put(data, new Article(data));
			}
			input.clear();
		}
		refreshTableData();
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
			articles.remove(selectedArticle.getEan());
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
