/*
 * Copyright (c) 2008 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.jnrpe.plugins.mocks.httpserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * @author Frederico Campos 
 *
 */
public class SimpleHttpHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */

	public void handle(String target, 
			Request baseRequest,
			HttpServletRequest request, 
			HttpServletResponse response)
					throws IOException, ServletException {
		if (request.getMethod().equals("GET")){
			String resp = "<h1>Hello World</h1><p>This is a paragraph</p>";

			if (request.getHeader("User-Agent").contains("JNRPE")) {
				resp += "<p>GET from JNRPE detected</p>";
			}	    
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);	    
			response.getWriter().println(resp);
		}else if (request.getMethod().equals("POST")){
			StringBuffer buf = new StringBuffer();
			for (Object key: request.getParameterMap().keySet()) {
				String value = request.getParameter(key + "");
				buf.append(key + ":" + value);
				buf.append(",");
			}		    
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			//System.out.println(buf.toString());
			response.getWriter().println(buf.toString());
		}
	}

}
