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

import org.eclipse.jetty.server.Server;
/**
 * A simple http server to handle basic request to test the CheckHttp plugin
 * 
 * @author Frederico Campos
 * 
 */
public class SimpleHttpServer  {
	public static final int PORT = 9999;

	private Server server = null;

	public SimpleHttpServer(){
		this.server = new Server(PORT);
		this.server.setHandler(new SimpleHttpHandler());

	}

	public void start() throws Exception {
		this.server.start();
	}

	public void stop() throws Exception {
		this.server.stop();
	}

	public static void main(String[] args) throws Exception {
		SimpleHttpServer server = new SimpleHttpServer();
		server.start();		
	}

}
