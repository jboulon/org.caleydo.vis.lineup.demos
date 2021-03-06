/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package demo.handler;

import generic.GenericView;
import generic.ImportSpec;
import generic.ImportWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
public class ShowWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImportSpec spec = new ImportWizard().call();
		showView(spec, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
		return null;
	}

	public static void showView(ImportSpec spec, IWorkbenchPage page) {
		if (spec == null)
			return;
		GenericView.lastSpec = spec;
		try {
			page.showView("lineup.demo.generic", System.currentTimeMillis() + "", IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
