package org.molgenis.framework.ui.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.SimpleModel;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Implementation of screen command.
 */
public abstract class SimpleCommand<E extends Entity> implements ScreenCommand<E>
{		
	private static final long serialVersionUID = -3289941539731301135L;

	/** Logger */
	//private static final transient Logger logger = Logger.getLogger(SimpleCommand.class.getSimpleName());

	/** Internal name of this command (unique within the screen) */
	private String name;

	/** The pretty label of this command (default: getName()) */
	private String label;

	/** Path to an icon image to show on this command */
	private String icon;

	/** Piece of javascript; if none is provide it is autogenerated depend on all settings */
	private String onClickJavascript;
	
	/** The Screen this command is linked to */
	private ScreenModel<E> screen;	

	/** The name of the screen to be target (default: this.screen) */
	private String targetScreen;

	/** Indicates to the screen to show a dialog box for this action */
	private boolean showDialog = false;

	/** Indicates to the screen to start a download procedure for this action */
	private boolean download = false;

	/** The name of the menu this command should be shown on */
	private String menu;

	/** Indicates to the screen to show this command on the toolbar */
	private boolean showOnToolbar;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            unique name of the command (within the screen)
	 * @param parentScreen
	 */
	public SimpleCommand(String name, ScreenModel<E> parentScreen)
	{
		this.setName(name);
		this.setScreen(parentScreen);
	}

	/**
	 * Get the javascript needed. If not provided it will be auto-generated.
	 */
	public String getJavaScriptAction()
	{
		if (onClickJavascript == null)
		{
			StringBuffer jScript = new StringBuffer();

			// make sure the current window has a name (so popup can call back
			// if necessary)
			jScript.append("if( window.name == '' ){ window.name = 'molgenis'+Math.random();}");

			// default target screen is 'self'
			jScript.append("document.forms." + this.getScreen().getName() + "_form." + FormModel.INPUT_TARGET
					+ ".value='" + this.getScreen().getName() + "';");

			// default action handler is also 'self'
			jScript.append("document.forms." + this.getScreen().getName() + "_form." + FormModel.INPUT_ACTION
					+ ".value='" + getName() + "';");

			// in case of dialog make a popup
			if (this.isDialog())
			{
				// open a new screen named 'popup'
				jScript
						.append("molgenis_window = window.open('','"
								+ "molgenis_"
								+ this.getName()
								+ "','height=800,width=600,location=no,status=no,menubar=no,directories=no,toolbar=no,resizable=yes,scrollbars=yes');");

				// make this new screen the target of this form
				jScript.append("document.forms." + this.getScreen().getName() + "_form.target='" + "molgenis_"
						+ this.getName() + "';");

				// set the 'show' parameter to 'popup' so the server knows to
				// show only this dialog
				jScript.append("document.forms." + this.getScreen().getName() + "_form." + FormModel.INPUT_SHOW
						+ ".value='" + ScreenModel.Show.SHOW_DIALOG + "';");
			}

			// in case of download set the download parameter
			else if (this.isDownload())
			{
				// tell molgenis to download
				jScript.append("document.forms." + this.getScreen().getName() + "_form." + FormModel.INPUT_SHOW
						+ ".value='" + ScreenModel.Show.SHOW_DOWNLOAD + "';");
			}

			// default show inline
			else
			{
				// make this this action target current window
				jScript.append("document.forms." + this.getScreen().getName() + "_form.target=window.name;");

				jScript.append("document.forms." + this.getScreen().getName() + "_form." + FormModel.INPUT_SHOW
						+ ".value='" + ScreenModel.Show.SHOW_MAIN + "';");
			}

			jScript.append("document.forms." + this.getScreen().getName() + "_form.submit();");

			// make popup on front
			if (this.isDialog())
			{
				jScript.append("molgenis_window.focus();");
			}

			return jScript.toString();
		}
		return onClickJavascript;
	}

	/**
	 * Default, a command has no inputs
	 * 
	 * @throws DatabaseException
	 */
	public abstract List<HtmlInput> getInputs() throws DatabaseException;

	// GETTERS AND SETTERS BELOW
	@Override
	public String getIcon()
	{
		return icon;
	}

	@Override
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	@Override
	public String getLabel()
	{
		if (label != null) return label;
		return this.getName();
	}

	@Override
	public void setLabel(String label)
	{
		this.label = label;
	}

	@Override
	public void setJavaScriptAction(String action)
	{
		this.onClickJavascript = action;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getTarget()
	{
		return this.targetScreen;
	}

	@Override
	public void setTarget(String target)
	{
		this.targetScreen = target;
	}

	@Override
	public ScreenModel<E> getScreen()
	{
		return this.screen;
	}

	@Override
	public FormModel<E> getFormScreen()
	{
		return (FormModel<E>)this.screen;
	}

	@Override
	public void setScreen(ScreenModel<E> screen)
	{
		this.screen = screen;
	}

	@Override
	public boolean isDialog()
	{
		return showDialog;
	}

	@Override
	public void setDialog(boolean dialog)
	{
		this.showDialog = dialog;
	}

	@Override
	public String getMenu()
	{
		return this.menu;
	}

	@Override
	public void setMenu(String menu)
	{
		// TODO Auto-generated method stub
		this.menu = menu;
	}

	@Override
	public abstract List<HtmlInput> getActions();

	@Override
	public Show handleRequest(Database db, Tuple request, PrintWriter downloadStream) throws ParseException,
			DatabaseException, IOException
	{
		// TODO Auto-generated method stub
		return ScreenModel.Show.SHOW_MAIN;
	}

	@Override
	public boolean isDownload()
	{
		return download;
	}

	@Override
	public void setDownload(boolean download)
	{
		this.download = download;
	}

	@Override
	public boolean isToolbar()
	{
		return showOnToolbar;
	}

	@Override
	public void setToolbar(boolean toolbar)
	{
		this.showOnToolbar = toolbar;
	}

	/**
	 * Default view name = 'SimpleCommand'
	 */
	public String getViewName()
	{
		return ScreenCommand.class.getSimpleName();
	}

	@Override
	public String getViewTemplate()
	{
		return null;
	}

	@Override
	public boolean isVisible()
	{
		return Boolean.TRUE;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "";
	}

	@Override
	public String getCustomHtmlBodyOnLoad()
	{
		return "";
	}
}
