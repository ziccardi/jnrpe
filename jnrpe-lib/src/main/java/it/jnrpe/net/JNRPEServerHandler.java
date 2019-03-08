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
import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPELogger;
import it.jnrpe.ReturnValue;
import it.jnrpe.commands.CommandInvoker;

/**
 * Receives and handles connections to the JNRPE server.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class JNRPEServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * Field LOG.
     */
    private final JNRPELogger LOG = new JNRPELogger(this);
    
    /**
     * The command invoker.
     */
    private final CommandInvoker commandInvoker;

    /**
     * The list of listener that will receive JNRPE events.
     */
    private final IJNRPEExecutionContext context;

    /**
     * Constructor.
     * 
     * @param invoker
     *            the command invoker
     * @param ctx
     *            The execution context
     */
    public JNRPEServerHandler(final CommandInvoker invoker, final IJNRPEExecutionContext ctx) {
        commandInvoker = invoker;
        context = ctx;
    }

    /**
     * Method channelRead.
     * @param ctx ChannelHandlerContext
     * @param msg Object
     * @see io.netty.channel.ChannelInboundHandler#channelRead(ChannelHandlerContext, Object)
     */
    @Override
    public final void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        try {
            JNRPERequest req = (JNRPERequest) msg;

            ReturnValue ret = commandInvoker.invoke(req.getCommand(), req.getArguments());

            JNRPEResponse res = new JNRPEResponse();
            res.setResult(ret.getStatus().intValue(), ret.getMessage());

            ChannelFuture channelFuture = ctx.writeAndFlush(res);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } catch ( RuntimeException re ) {
            re.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * Method exceptionCaught.
     * @param ctx ChannelHandlerContext
     * @param cause Throwable
     * @see io.netty.channel.ChannelInboundHandler#exceptionCaught(ChannelHandlerContext, Throwable)
     */
    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOG.error(context, cause.getMessage(), cause);
        //context.getEventBus().post(new LogEvent(this, LogEventType.ERROR, cause.getMessage(), cause));
        //EventsUtil.sendEvent(listeners, this, LogEvent.ERROR, cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "JNRPEServerHandler [commandInvoker=" + commandInvoker + ", context=" + context + "]";
    }
}
