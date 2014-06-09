package it.jnrpe.net;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import it.jnrpe.JNRPEExecutionContext;
import it.jnrpe.events.EventsUtil;
import it.jnrpe.events.LogEvent;

public class JNRPEIdleStateHandler extends ChannelDuplexHandler {

	private final JNRPEExecutionContext jnrpeContext;

	public JNRPEIdleStateHandler(final JNRPEExecutionContext ctx) {
		this.jnrpeContext = ctx;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				ctx.close();
				EventsUtil.sendEvent(this.jnrpeContext, this, LogEvent.INFO,
						"Read Timeout");
			} else if (e.state() == IdleState.WRITER_IDLE) {
				EventsUtil.sendEvent(jnrpeContext, this, LogEvent.INFO,
						"Write Timeout");
				ctx.close();
			}
		}
	}
}
