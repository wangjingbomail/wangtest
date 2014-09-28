package com.wang.netty;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;

public class HelloWorldTest {

	
	public static void main(String[] args) {
	    testServer();
	    testClient();
	}
	
	
	public static void testServer() {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		ChannelPipelineFactory channelPipelineFactory = new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("handler", new HelloWorldServerHandler());

				return pipeline;
			}
		};

		bootstrap.setPipelineFactory(channelPipelineFactory);

		bootstrap.bind(new InetSocketAddress(8080));
	}

	public static void testClient() {
		ClientBootstrap clientBootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		ChannelPipelineFactory channelPipelineFactory = new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("handler", new HelloWorldClientHandler());

				return pipeline;
			}
		};

		clientBootstrap.setPipelineFactory(channelPipelineFactory);

		ChannelFuture channelFuture = clientBootstrap
				.connect(new InetSocketAddress("localhost", 8080));

		channelFuture.getChannel().getCloseFuture().awaitUninterruptibly();
		clientBootstrap.releaseExternalResources();
		
	}

	private static class HelloWorldServerHandler extends SimpleChannelHandler {

		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent event) throws Exception {

			String msg = "hello world";
			ChannelBuffer buffer = ChannelBuffers.buffer(msg.length());
			buffer.writeBytes(msg.getBytes());
			event.getChannel().write(buffer);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx,
				ExceptionEvent event) {
			System.out.println(" unexpected exception from downstream "
					+ event.getChannel().close());
			event.getChannel().close();
		}
	}

	private static class HelloWorldClientHandler extends SimpleChannelHandler {

		@Override
		public void messageReceived(ChannelHandlerContext ctx,
				MessageEvent event) throws Exception {

			ChannelBuffer buffer = (ChannelBuffer)event.getMessage();
			System.out.println( buffer.toString(Charset.defaultCharset()));

			event.getChannel().close();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx,
				ExceptionEvent event) {
			System.out.println(" unexpected exception from downstream "
					+ event.getChannel().close());
			event.getChannel().close();
		}
	}

}
