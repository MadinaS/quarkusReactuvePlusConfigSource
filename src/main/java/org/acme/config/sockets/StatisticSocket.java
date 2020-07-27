package org.acme.config.sockets;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.mutiny.Uni;
import org.acme.config.entities.UserEntity;
import org.acme.config.entities.UserReactiveRepository;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/sync/{chatKey}")
@ApplicationScoped
public class StatisticSocket {
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    public UserReactiveRepository userRepository;

    @Inject
    private JWTParser jwtParser;

    @Inject
    @Channel("friends-channel")
    Emitter<String> friendsChannelEmitter;

    @Transactional
    @OnOpen
    public void onOpen(Session session, @PathParam("chatKey") String chatKey) throws Exception {
        // ....

        Uni<UserEntity> user = userRepository.findByChatKey(chatKey);

        user.subscribe()
                .with(
                        result -> {
                            if (result != null) {
                                System.out.println("User Login: " + result.getUserNickname());
                                String username = result.getUserNickname();
                                sessions.put(username, session);

                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                                LocalDateTime now = LocalDateTime.now();

                                //--- Send Notification to FriendsChannel
                                // ...
                                //---
                            }
                        },
                        failure -> failure.printStackTrace()
                );

    // ....
    }

    @OnClose
    public void onClose(Session session, @PathParam("chatKey") String chatKey) {
        // onClose
    }

    //ToDo
    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("chatKey") String chatKey) {
//        sessions.remove(username);
//        broadcast("User " + username + " left on error: " + throwable);
    }


    @OnMessage
    public void onMessage(String message) {
//        broadcast(">> " + username + ": " + message);
    }
}
