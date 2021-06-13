package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyDemo {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new NettyServer(8888).serverStart();
	}
}

class NettyServer {
	
	int port = 8888;
	
	public NettyServer(int port) {
		this.port = port;
	}
	
	/*
	 * The below codes is the most important kernel of Netty model
	 * 
	 */
	public void serverStart() {
		//create two threads pools named bossGroup and workerGroup
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		//ServerBootstrap is preparation step for configuring the server features before its start 
		ServerBootstrap b = new ServerBootstrap();
		
		//first bossGroup is in charge of connection, second workerGroup for handling IO affair
		//NioServerSocketChannel means the what's the channel type
		//childHandler() method is the key of Observer model, its add a listener for every client connection 
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// add a event handler on channel pipeline
				ch.pipeline().addLast(new Handler());
			}
			
		});
		
		try {
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

//concrete handler offer concrete methods to handle the event  
class Handler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//super.channelRead(ctx, msg);
		System.out.println("server: channel read");
		ByteBuf buf = (ByteBuf)msg;
		
		System.out.println(buf.toString(CharsetUtil.UTF_8));
		
		ctx.writeAndFlush(msg);
		ctx.close();
		
		//buf.release();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}
}



