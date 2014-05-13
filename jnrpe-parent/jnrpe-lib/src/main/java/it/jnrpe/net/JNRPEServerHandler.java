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
package it.jnrpe.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.commands.CommandInvoker;
import it.jnrpe.events.EventsUtil;
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.events.LogEvent;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class JNRPEServerHandler extends ChannelInboundHandlerAdapter {

	private final CommandInvoker commandInvoker;
	private final Collection<IJNRPEEventListener> listeners;

	public JNRPEServerHandler(CommandInvoker invoker,
			Collection<IJNRPEEventListener> eventListeners) {
		this.commandInvoker = invoker;
		this.listeners = eventListeners;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		// Discard the received data silently.
		try {
			JNRPERequest req = (JNRPERequest) msg;

			ReturnValue ret = commandInvoker.invoke(req.getCommand(),
					req.getArguments());

			if (ret == null) {
				String args = StringUtils.join(req.getArguments(), ',');

				ret = new ReturnValue(Status.UNKNOWN, "Command ["
						+ req.getCommand() + "] with args [" + args
						+ "] returned null");
			}

			JNRPEResponse res = new JNRPEResponse();
			res.setPacketVersion(PacketVersion.VERSION_2);

			res.setResultCode(ret.getStatus().intValue());
			res.setMessage(ret.getMessage());

			ChannelFuture f = ctx.writeAndFlush(res);
			f.addListener(ChannelFutureListener.CLOSE);
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		EventsUtil.sendEvent(listeners, this, LogEvent.ERROR,
				cause.getMessage(), cause);
		ctx.close();
	}
}
