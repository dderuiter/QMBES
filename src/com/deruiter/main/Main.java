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
 * Class for starting the application's execution and JavaFX root GUI.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.main;

import com.deruiter.controller.RootController;
import com.deruiter.utilities.Constants;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
    /**
     * Starts the JavaFX GUI.
     *
     * @param primaryStage
     *          the primary GUI stage.
     */
	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle(Constants.APPLICATION_NAME);

		RootController rootController = new RootController(primaryStage);
        rootController.showQMLayout();
	}

	/**
	 * Starts the application's execution.
	 *
	 * @param args
	 * 			parameters to include for the program run.
	 */
	public static void main(String[] args)
	{
		// Launch the JavaFX GUI.
		launch(args);
	}
}