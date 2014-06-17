/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
 *******************************************************************************/
package it.jnrpe;

import it.jnrpe.events.IJNRPEEventListener;

import java.nio.charset.Charset;
import java.util.Collection;

public class JNRPEExecutionContext {

	private final Collection<IJNRPEEventListener> eventListenersList;

	private final Charset charset;

	JNRPEExecutionContext(Collection<IJNRPEEventListener> eventListeners,
			Charset charset) {
		this.eventListenersList = eventListeners;
		this.charset = charset;
	}

	public Collection<IJNRPEEventListener> getListeners() {
		return eventListenersList;
	}

	public Charset getCharset() {
		return charset;
	}
}