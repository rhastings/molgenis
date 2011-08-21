package org.molgenis.framework.ui;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.MolgenisService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.ui.html.FreemarkerInput;
import org.molgenis.framework.ui.html.RichtextInput;
import org.molgenis.util.EmailService;
import org.molgenis.util.FileLink;
import org.molgenis.util.Tuple;

/**
 * The root screen for any MOLGENIS application.
 * <p>
 * The UserInterface manages a Tree of ScreenControllers. A UserInterface is
 * backed by exactly one database (for persistent data) and one Login object
 * (taking care of authentication/authorization).
 */
public class ApplicationController extends
		SimpleScreenController<ApplicationModel>
{
	public static String MOLGENIS_TEMP_PATH = "molgenis_temp";
	/** autogenerated */
	private static final long serialVersionUID = 3108474555679524568L;
	/** */
	private static final transient Logger logger = Logger
			.getLogger(ApplicationController.class.getSimpleName());
	/** The login * */
	private Login login;
	/** The email service used */
	private EmailService emailService;
	/** Other services, mapped by path */
	private Map<String,MolgenisService> services;
	/** The current base url that you may need in your apps */
	private String baseUrl;
	/** Galaxy url*/
	private String galaxyUrl;

	/**
	 * Construct a user interface for a database.
	 * 
	 * @param login
	 *            for authentication/authorization
	 */
	public ApplicationController(Login login)
	{
		super("molgenis_userinterface_root", null, null); // this is the root of
															// the screen tree.
		this.setModel(new ApplicationModel(this));
		this
				.setView(new FreemarkerView("ApplicationView.ftl", this
						.getModel()));

		// this.database = db;
		this.setLogin(login);
	}

	public ApplicationController(Login login, EmailService email)
	{
		this(login);
		this.setLogin(login);
		this.setEmailService(email);
	}

	/**
	 * Retrieve the current login
	 * 
	 * @return Login
	 */
	public Login getLogin()
	{
		return login;
	}

	/**
	 * Set the Login object that is used for authentication.
	 * 
	 * @param login
	 */
	public void setLogin(Login login)
	{
		this.login = login;
	}

	/**
	 * Retrieve the database that is used by this user interface.
	 * 
	 * @return db the database
	 */
	// public Database getDatabase()
	// {
	// return database;
	// }

	// public void setDatabase(Database database)
	// {
	// logger.info("replacing database "+this.database+" with "+database);
	// this.database = database;
	// }

	@Override
	public FileLink getTempFile() throws IOException
	{
		// File temp = new File("d:\\Temp\\"+System.currentTimeMillis());
		// String tempDir = System.getProperty("java.io.tmpdir");
		File temp = File.createTempFile(MOLGENIS_TEMP_PATH, "");
		logger.debug("create temp file: " + temp);
		return new FileLink(temp, "download/" + temp.getName());
	}

	/**
	 * Convenience method that delegates an event to the controller of the
	 * targetted screen.
	 * 
	 * @param db
	 *            reference to the database
	 * @param request
	 *            with the event
	 */
	public void handleRequest(Database db, Tuple request)
	{
		logger.info("delegating handleRequest(" + request.toString() + ")");
		String screen = request.getString(ScreenModel.INPUT_TARGET);

		// action for me?
		if (screen != null && screen.equals(this.getName()))
		{
			if (request.getString("select") != null)
			{
				// the screen to select
				ScreenController<?> selected = this.get(request
						.getString("select"));

				// now we must make sure that alle menu's above select 'me'
				ScreenController<?> currentParent = selected.getParent();
				ScreenController<?> currentChild = selected;
				while (currentParent != null)
				{
					if (currentParent instanceof MenuController)
					{
						((MenuController) currentParent)
								.setSelected(currentChild.getName());
					}
					currentChild = currentParent;
					currentParent = currentParent.getParent();
				}
			}
			return;
		}
		//no target set, handle centrally
		else
		{
			if(!request.isNull("GALAXY_URL"))
			{
				this.setGalaxyUrl(request.getString("GALAXY_URL"));
				logger.info("set galaxy url to "+this.getGalaxyUrl());
			}
		}

		// delegate
		ScreenController<?> target = get(screen);
		if (target != null)
		{
			if (!target.equals(this)) target.handleRequest(db, request);
		}
		else
			logger.debug("handleRequest(" + request
					+ "): no request needs to be handled");

	}

	/**
	 * Convenience method that delegates the refresh to its ScreenController.
	 */
	public void reload(Database db)
	{
		for (ScreenController<?> s : this.getChildren())
		{
			try
			{
				s.reload(db);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				s.getModel().getMessages().add(
						new ScreenMessage("reload failed: " + e.getMessage(),
								false));
			}
		}

		// if(this.getController() != this)
		// {
		// this.getController().reload(db);
		// }
		// else
		// {
		// //refresh whole selected subtree
		// if(this.getSelected() != null)
		// this.getSelected().getController().reload(db);
		// }
	}

	// removed to cleanup MVC pattern where controller manages all
	// @Override
	// public Templateable getScreen()
	// {
	// // TODO Auto-generated method stub
	// return this;
	// }
	//	
	// @Override
	// public String getViewName()
	// {
	// return this.getClass().getSimpleName();
	// }

	@Override
	public void handleRequest(Database db, Tuple request, OutputStream out)
	{
		this.handleRequest(db, request);

	}

	public EmailService getEmailService()
	{
		return emailService;
	}

	public void setEmailService(EmailService emailService)
	{
		this.emailService = emailService;
	}

	// @Override
	// public boolean isVisible()
	// {
	// // TODO Auto-generated method stub
	// return true;
	// }
	//
	// @Override
	// public String getViewTemplate()
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void reset()
	// {
	// // TODO Auto-generated method stub
	//		
	// }

	public void clearAllMessages()
	{
		for (ScreenController<?> s : this.getAllChildren())
		{
			s.getModel().getMessages().clear();
		}
	}

	/**
	 * Get the database object for this application
	 * 
	 * @return
	 * @throws Exception
	 */
	public Database getDatabase()
	{
		throw new UnsupportedOperationException(
				"getDatabase must be implemented for use");
	}

	/**
	 * Add a MolgenisService. TODO: we would like to refactor this to also take
	 * cxf annotated services
	 * @throws Exception 
	 */
	public void addService(MolgenisService matrixView) throws NameNotUniqueException
	{
		if(this.services.containsKey(matrixView.getName())) throw new NameNotUniqueException("addService failed: path already exists");
		this.services.put(matrixView.getName(), matrixView);
	}

	/**
	 * The base url of this app. Generally the path up to %/molgenis.do
	 * @return
	 */
	public String getApplicationUrl()
	{
		return baseUrl;
	}

	/**
	 * This method is used only internally.
	 * 
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public String getGalaxyUrl()
	{
		return galaxyUrl;
	}

	public void setGalaxyUrl(String galaxyUrl)
	{
		this.galaxyUrl = galaxyUrl;
	}
	
	public String getCustomHtmlHeaders()
	{
		//TODO: this should be made more generic
		return 
		new FreemarkerInput("dummy").getCustomHtmlHeaders()+
		new RichtextInput("dummy").getCustomHtmlHeaders() +
		super.getCustomHtmlHeaders();
	}
}
