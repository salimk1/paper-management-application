package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import application.Account;
import application.Main;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * LoginController
 * 
 * Controller class for Login.FXML
 */
public class LoginController implements Initializable {

	public static final String ADMIN = "Admin";
	public static final String EDITOR = "Editor";
	public static final String RESEARCHER = "Researcher";
	public static final String REVIEWER = "Reviewer";

	@FXML
	private BorderPane root;
	@FXML
	public Label myLabel;
	@FXML
	public TextField username;
	public TextField passwordText;
	public JFXTextField username2 = new JFXTextField();
	public JFXPasswordField password2 = new JFXPasswordField();
	public JFXTextField passwordText2 = new JFXTextField();
	public Pane content = new Pane();
	@FXML
	public PasswordField password;
	@FXML
	public CheckBox showPassword;
	@FXML
	public Label infoLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/* Next 3 lines loads the Welcome Page once, instead of going into a loop */

		if (!Main.isLoadScreenOn)
			loadWelcomeWindow();

		startingAnimation();
		infoLabel.setVisible(false);

		this.togglevisiblePassword(null);
	}

	/**
	 * Creates a subtle fadeIn animation when opened.
	 */
	private void startingAnimation() {
		FadeTransition fadeIn = new FadeTransition();

		fadeIn.setDuration(Duration.seconds(1));
		fadeIn.setNode(root);

		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.setCycleCount(1);
		fadeIn.play();
	}

	/**
	 * Loads and displays Welcome window with transition animations.
	 */
	private void loadWelcomeWindow() {
		try {
			Main.isLoadScreenOn = true;
			/* Load splash screen view FXML */
			AnchorPane pane = FXMLLoader.load(getClass().getResource(("/application/AppLoadingScreen.fxml")));
			/* Add it to root container (Can be StackPane, AnchorPane etc.) */
			root.getChildren().setAll(pane);

			/* Load splash screen with fade in effect */
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(.5), pane);
			fadeIn.setFromValue(0);
			fadeIn.setToValue(1);
			fadeIn.setCycleCount(1);

			// Finish splash with fade out effect
			FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), pane);
			fadeOut.setDelay(Duration.seconds(3));
			fadeOut.setFromValue(1);
			fadeOut.setToValue(0);
			fadeOut.setCycleCount(1);

			fadeIn.play();

			/* After fade in, start fade out */
			fadeIn.setOnFinished((e) -> {

				fadeOut.play();
			});

			/* After fade out, load actual content */
			fadeOut.setOnFinished((e) -> {
				try {
					BorderPane parentContent = FXMLLoader.load(getClass().getResource(("/application/Login.fxml")));
					root.getChildren().setAll(parentContent);
				} catch (IOException ex) {
					Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
				}
			});
		} catch (IOException ex) {
			Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * @param pressed: Action taken by user.
	 * @throws IOException
	 */
	@FXML
	public void onEnter(ActionEvent pressed) throws IOException {
		buttonAction(pressed);
	}

	/**
	 * Hides error messages if error has not yet been encountered.
	 */
	@FXML
	private void hideErrorMsg() {
		infoLabel.setVisible(false);
	}

	/**
	 * User has the option to view the password as it is being typed/
	 * 
	 * @param event: Event component that does a desired action when pressed.
	 */
	public void togglevisiblePassword(ActionEvent event) {
		if (showPassword.isSelected()) {
			passwordText2.setText(password2.getText());
			passwordText2.setVisible(true);
			password2.setVisible(false);
			return;
		} else {
			password2.setText(passwordText2.getText());
			password2.setVisible(true);
			passwordText2.setVisible(false);
		}
	}

	/**
	 * Opens the Registration window.
	 * 
	 * @param event: Event component that does a desired action when pressed.
	 * @throws IOException
	 */
	public void openRegistration(MouseEvent event) throws IOException {
		Parent fxmlRegParentParent = FXMLLoader.load(getClass().getResource("/application/Registration.fxml"));
		content.getChildren().removeAll();
		content.getChildren().setAll(fxmlRegParentParent);

	}

	/**
	 * Opens the correct window after a user enters their credentials and presses
	 * 'Login'.
	 * 
	 * @param event: Event component that does a desired action when pressed.
	 * @throws IOException
	 */
	public void buttonAction(ActionEvent event) throws IOException {

		String user = username2.getText();
		String pass;
		if (showPassword.isSelected()) {
			pass = passwordText2.getText();
		} else {
			pass = password2.getText();
		}
		Account acc = Main.AuthSys.login(user, pass);

		if (acc != null) {
			String fxmlFileName = "";
			String usertype = acc.accountTypetoString();
			String applicationAddr = "/application/";

			switch (usertype) {
			case ADMIN:
				fxmlFileName = applicationAddr + usertype + ".fxml";
				break;
			case RESEARCHER:
				fxmlFileName = applicationAddr + usertype + ".fxml";
				break;
			case REVIEWER:
				fxmlFileName = applicationAddr + usertype + ".fxml";
				break;
			case EDITOR:
				fxmlFileName = applicationAddr + usertype + ".fxml";
				break;
			default:
				fxmlFileName = "/application/Login.fxml";
				break;
			}

			// openNewWindow(event, fxmlFileName);
			openNewAnchorPaneWindow(event, fxmlFileName, usertype, user);

		} else {
			infoLabel.setVisible(true);
			infoLabel.setText("Username/Password not found!" + System.getProperty("line.separator")
					+ "Please try again or create an account. ");

		}

	}

	/* Switch Window (AnchorPane) */
	/**
	 * @param event:     Event component that does a desired action when pressed.
	 * @param newWindow: New window to be created.
	 * @param userType:  The type of user.
	 * @param username:  The username corresponding to the that type of user.
	 * @throws IOException
	 */
	public void openNewAnchorPaneWindow(ActionEvent event, String newWindow, String userType, String username)
			throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(newWindow));
		AnchorPane root = (AnchorPane) loader.load();

		/*
		 * This is used to pass the username and type of user to the next scene in order
		 * to use this, to be able to create custom folders
		 */
		if (userType == "Researcher") {
			ResearcherController controller = loader.getController();
			controller.initUser(username, 1);

		} else if (userType == "Reviewer") {
			ReviewerController controller = loader.getController();
			controller.initUser(username, 3);

		}

		Scene scene = new Scene(root);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(scene);
		window.show();
	}

}
