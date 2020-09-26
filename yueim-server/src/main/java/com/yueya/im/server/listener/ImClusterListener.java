package com.yueya.im.server.listener;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.yueya.im.common.api.NodeStatusListener;
import com.yueya.im.server.actors.ImActorSystem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ImClusterListener extends AbstractActor {
  LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
  Cluster cluster = Cluster.get(getContext().system());
  private NodeStatusListener listener;
  public ImClusterListener(NodeStatusListener listener){
      this.listener = listener;
  }
  @Override
  public void preStart() {
    cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(),
        MemberEvent.class, UnreachableMember.class);
  }

  //re-subscribe when restart
  @Override
  public void postStop() {
    cluster.unsubscribe(self());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
      .match(MemberUp.class, mUp -> {
          logger.info("节点上线:"+mUp.member().address().host()+":"+mUp.member().address().port());
        refreshServer();
          if(listener!=null){
              listener.onMerUp(mUp.member().address().host().get());
          }
        // log.info("Member is Up: {}", mUp.member());
      })
      .match(UnreachableMember.class, mUnreachable -> {
        // log.info("Member detected as unreachable: {}", mUnreachable.member());
      })
      .match(MemberRemoved.class, mRemoved -> {
        refreshServer();
          logger.info("节点下线:"+mRemoved.member().address().host()+":"+mRemoved.member().address().port());
          if(listener!=null){
              listener.onMerDown(mRemoved.member().address().host().get());
          }
        // log.info("Member is Removed: {}", mRemoved.member());
      })
      .match(MemberEvent.class, message -> {
        // ignore
      })
      .build();
  }
  private void refreshServer() {
    List<String> nodes = StreamSupport.stream(cluster.state().getMembers().spliterator(),false)
            .map(member -> member.address().toString())
            .collect(Collectors.toList());
    nodes.forEach(System.out::println);
    ImActorSystem.getInstance().refreshServer(nodes);
  }

}
