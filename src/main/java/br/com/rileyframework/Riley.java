package br.com.rileyframework;

import br.com.rileyframework.exceptions.RileyException;
import br.com.rileyframework.server.ConfigureServerAdapter;
import br.com.rileyframework.server.JettyServer;
import br.com.rileyframework.utils.SetupLoader;
import lombok.Getter;
import lombok.Setter;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Riley {

	@Getter @Setter
	private List<Route> routes;
	private ConfigureServerAdapter configureServerAdapter;
	private static Riley instance;
	private SetupLoader setupLoader;

	public static synchronized Riley getInstance(){
		if (instance == null){
			instance = new Riley();
		}
		return instance;
	}

	public Riley() {
		this.setupLoader = new SetupLoader();
	}

	public List<Route> registerControllers() {
		try {
			System.out.println("[INFO] ==> base-package: " + this.setupLoader.basePackage());
			Reflections reflections = new Reflections(this.setupLoader.basePackage().trim());

			// get all controller of application
			Set<Class<? extends Resource>> allControllers = reflections.getSubTypesOf(Resource.class);

			// list to register urls
			routes = new ArrayList<>();

			for (Class<?> controller : allControllers) {
				Class c = Class.forName(controller.getName());
				Resource controllerIstanced = (Resource) createNewInstance(c);
				for (int j = 0; j < controllerIstanced.getRoutes().size(); j++) {
					routes.add(controllerIstanced.getRoutes().get(j));
					System.out.println("[INFO] ==> mapped route: " +
							"HTTP METHOD ["+controllerIstanced.getRoutes().get(j).getHttpMethod()+"]"+
							", PATH["+ controllerIstanced.getRoutes().get(j).getRoute()+"]");
				}
			}
		} catch (Exception e) {
			System.out.println("[ERROR] ==> location: Riley.registerControllers(), error: ".concat(e.getMessage()));
		}

		return routes;
	}

	private static Object createNewInstance(Class<?> clazz) throws Exception {
		try{
			Constructor<?> ctor;
			ctor = clazz.getConstructor();
			Object object = ctor.newInstance();
			return object;
		} catch(Exception e) {
			throw new Exception("error:" + e.getMessage());
		}
	}

	public void registerController(Route route) {
		routes = registerControllers();
		routes.add(route);
	}

	public void configureServer(ConfigureServerAdapter configureServerAdapter) {
		this.configureServerAdapter = configureServerAdapter;
	}

	public void start() throws Exception {
		if (configureServerAdapter == null) configureServerAdapter = getServerDefault();
		this.configureServerAdapter.start();
	}

	private ConfigureServerAdapter getServerDefault() {
		return JettyServer.builder().build();
	}

	public void shutDown() throws Exception {
		this.configureServerAdapter.shutDown();
	}

	public Server getServer() {
		Server server = new Server();
		server.setPort(configureServerAdapter.port());
		server.setIsStaterd(configureServerAdapter.isStarted());
		return server;
	}

}
