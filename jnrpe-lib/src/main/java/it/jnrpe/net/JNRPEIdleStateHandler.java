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

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPELogger;
import it.jnrpe.events.LogEvent;
import it.jnrpe.events.LogEvent.LogEventType;

/**
 * Idle timeout handler.
 * 
 * @author Massimiliano Ziccardi
 */
public final class JNRPEIdleStateHandler extends ChannelDuplexHandler {

    private final JNRPELogger LOG = new JNRPELogger(this);
    
    /**
     * The JNRPE execution context.
     */
    private final IJNRPEExecutionContext jnrpeContext;

    /**
     * The constructor.
     * 
     * @param ctx
     *            The JNRPE execution context.
     */
    public JNRPEIdleStateHandler(final IJNRPEExecutionContext ctx) {
        this.jnrpeContext = ctx;
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
                LOG.warn(jnrpeContext, "Read Timeout");
                //jnrpeContext.getEventBus().post(new LogEvent(this, LogEventType.INFO, "Read Timeout"));
                //EventsUtil.sendEvent(this.jnrpeContext, this, LogEvent.INFO, "Read Timeout");
            } else if (e.state() == IdleState.WRITER_IDLE) {
                LOG.warn(jnrpeContext, "Write Timeout");
                //jnrpeContext.getEventBus().post(new LogEvent(this, LogEventType.INFO, "Write Timeout"));
                //EventsUtil.sendEvent(jnrpeContext, this, LogEvent.INFO, "Write Timeout");
                ctx.close();
            }
        }
    }
}
