package com.example.activity.service;


import com.example.activity.message.AnswerMessage;
import com.example.activity.message.CompeteMessage;
import com.example.activity.message.GamerMessage;
import com.example.activity.message.MatchMessage;

import java.util.HashMap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class CientService {
    private String user;
    private SocketChannel channel;
    private String[] playername = null;
    private HashMap<String, String> gamerscore = new HashMap<>();
    private HashMap<String, Boolean> if_renew = new HashMap<>();
    boolean ifMatched = false;

    public void start()
    {
        gamerscore.put(user, "0");
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception{
                            new Thread(()->{
                               System.out.println("连接服务器中");
                            },"system in").start();
                        }

                        public void channelRead(ChannelHandlerContext ctx, Object msg){
                            String retmes = (String) msg;
                            String[] news = retmes.split("_");
                            int size = news.length;
                            switch (news[0])
                            {
                                case "1":
                                    break;
                                case "2":
                                    playername = new String[size - 1];
                                    for(int i = 0; i < size - 1; i++)
                                    {
                                        playername[i] = news[i + 1];
                                        gamerscore.put(news[i + 1], "0");
                                    }
                                    ifMatched = true;
                                    break;
                                case "3":
                                    if_renew.put(news[1], true);
                                    if(news[1] != user)
                                    {
                                        gamerscore.put(news[1],news[2]);
                                    }
                                    break;
                            }
                        }
                    });
                }
            });
            ChannelFuture future =bootstrap.connect("192.168.0.56",8888).sync();
            channel = (SocketChannel) future.channel();

        } catch (Exception e){
            group.shutdownGracefully();
        }
    }

    public int getSubnow()
    {
        return if_renew.size();
    }

    public boolean isIfMatched() {
        return ifMatched;
    }

    public HashMap<String, String> getGameresult()
    {
       return gamerscore;
    }

    public String[] getPlayername()
    {
        if(playername == null)
        {
            playername = new String[1];
            playername[0] = user;
        }
        return playername;
    }

    public void resetSubnow()
    {
        if_renew.clear();
    }

    public void setScore(String score)
    {
        gamerscore.put(user, score);
    }

    public void sendMessage(GamerMessage mes)
    {
        String msg = mes.MessageString();
        channel.writeAndFlush(msg);
    }

    public void sendMessage(MatchMessage mes)
    {
        String msg = mes.MessageString();
        channel.writeAndFlush(msg);
    }

    public void sendMessage(CompeteMessage mes)
    {
        String msg = mes.MessageString();
        channel.writeAndFlush(msg);
    }

    public void sendMessage(AnswerMessage mes)
    {
        String msg = mes.MessageString();
        channel.writeAndFlush(msg);
    }

    public void resetPlayername()
    {
        playername = null;
    }

    public void resetGameresult()
    {
        gamerscore = new HashMap<String, String>();
        gamerscore.put(user, "0");
    }
    public void resetIfMatched()
    {
        ifMatched = false;
    }

    public void setUser(String name)
    {
        user = name;
    }

}
