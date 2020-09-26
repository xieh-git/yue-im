package com.yueya.im.server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yueya.im.server.actors.UserActor;
import com.yueya.im.server.listener.ImClusterListener;

import java.util.ArrayList;
import java.util.List;

public class TestCluster {
    public static List<ActorSystem> systems = new ArrayList<>();
    public static void main(String[] args) {
        startup(new String[] { "2551","2552"});
        systems.get(0).actorOf(Props.create(UserActor.class),"001");
        systems.get(1).actorSelection("akka.tcp://YueImClusterSystem@127.0.0.1:2551/user/0012")
        .tell("hello test",null);
    }

    public static void startup(String[] ports) {
        for (String port : ports) {
            // Override the configuration of the port
            // To use artery instead of netty, change to "akka.remote.artery.canonical.port"
            // See https://doc.akka.io/docs/akka/current/remoting-artery.html for details
            Config config = ConfigFactory.parseString(
                    "akka.remote.netty.tcp.port=" + port)
                    .withFallback(ConfigFactory.load());

            // Create an Akka system
            ActorSystem system = ActorSystem.create("YueImClusterSystem", config);
            // Create an actor that handles cluster domain events
            system.actorOf(Props.create(ImClusterListener.class),
                    "clusterListener");
            systems.add(system);
        }
    }
}
