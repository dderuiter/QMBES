/**********************************************************************************************************************
 *
 * WARNING:
 * Copyright Ⓒ 2016 by D.DeRuiter
 * Do not use, modify, or distribute in any way without express written consent.
 *
 * PROJECT:
 * QMBES (Quine McCluskey Boolean Expression Simplifier)
 *
 * DESCRIPTION:
 * Controller class for performing logic for the root JavaFX GUI layout.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RootController
{
    // Class variable
    private static Stage primaryStage;

    // Instance variable
    private BorderPane rootLayout;

    /**
     * Constructs a root layout controller.
     */
    public RootController(Stage primaryStage)
    {
        RootController.primaryStage = primaryStage;

        try
        {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(getClass().getResource("/com/deruiter/view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(getClass().getResource("/com/deruiter/view/DarkTheme.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initializes controller class. This method is automatically called after fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {
    }

    /**
     * Shows the QuineMcCluskey algorithm layout inside the root layout.
     */
    public void showQMLayout()
    {
        try
        {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/deruiter/view/QMLayout.fxml"));
            AnchorPane qmLayout = (AnchorPane) loader.load();

            // Set client layout into the center of root layout.
            rootLayout.setCenter(qmLayout);
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the primary GUI stage.
     *
     * @return the primary GUI stage.
     */
    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }
}
