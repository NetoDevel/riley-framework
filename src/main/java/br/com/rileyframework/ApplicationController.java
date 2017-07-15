package br.com.rileyframework;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import br.com.rileyframework.utils.GeneratorRegex;

public abstract class ApplicationController {

	private List<Route> routes = new ArrayList<Route>();
	
	public void get(String route, HttpHandlerRequest handler) {
		Route routeObj = new Route();
		
		routeObj.setRoute(route);
		routeObj.setHandler(handler);
		routeObj.setHttpMethod("GET");
		routeObj.setRouteRegex(GeneratorRegex.generatorRegexFromUrl(route));
		
		getRoutes().add(routeObj);
	}
	
	public ApplicationController(){
		
	}
	
	public interface HttpHandlerRequest {
		void handler(Request request, HttpServletResponse response);
	}
	
	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
}