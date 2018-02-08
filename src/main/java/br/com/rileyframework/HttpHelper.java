package br.com.rileyframework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHelper {

    public Response buildResponse(HttpServletResponse resp) throws IOException {
        Response response = new Response();
        response.setPrintWriter(resp.getWriter());
        return response;
    }

    public Request buildRequest(final String servletPath, Route route, String body) {
        Request request = new Request();
        request.setPathVariables(getPathVariables(route.getRoute(), servletPath));

        if (route.getHttpMethod().equals("POST")) {
            request.setRequestBody(body);
        }

        return request;
    }

    /**
     * get path variables url
     * @return
     */
    public HashMap<String, String> getPathVariables(String url, String contextPath) {
        url = validateUrlToRegex(url);

        String[] paramName = url.split("\\/\\w*\\/");
        String[] paramValue = contextPath.split("\\/\\w*\\/");
        HashMap<String, String> pathVariables = new HashMap<>();

        for (int i = 0; i < paramValue.length; i++) {
            if (paramValue[i].equals("") || paramValue[i].equals(null)) {
                continue;
            } else {
                pathVariables.put(paramName[i], paramValue[i]);
            }
        }

        return pathVariables;
    }

    public String validateUrlToRegex(String url) {
        String firstCaracter = String.valueOf(url.charAt(0));
        if (!firstCaracter.equals("/")) {
            url = "/" + url;
        }
        return url;
    }

    public boolean matchUrl(String regex, String urlOrigin) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(urlOrigin);
        return m.matches();
    }

    public String getBodyRequest(HttpServletRequest request) {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        return jb.toString();
    }

}
