package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Iterator;


/**
 * Created by Yuwen on 5/12/2015.
 */
public class WebsocketEndPoint extends TextWebSocketHandler {
    private HashSet<WebSocketSession> sessions = new HashSet<WebSocketSession>();
    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        broadcastTextMessage(session, sessions, message);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcastTextMessage(WebSocketSession session, HashSet<WebSocketSession> sessions, TextMessage
            message)throws
            Exception{
        Iterator<WebSocketSession> iterator = sessions.iterator();
        HelloMessage helloMessage = null;
        try {
            helloMessage = new ObjectMapper().readValue(message.getPayload(), HelloMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (iterator.hasNext()){
            WebSocketSession webSocketSession = iterator.next();
            if (webSocketSession == session)
                webSocketSession.sendMessage(new TextMessage("Me : " + helloMessage.getMessage()));
            else
                webSocketSession.sendMessage(new TextMessage(helloMessage.getUsername() + " : " + helloMessage
                        .getMessage
                                ()));
        }
    }
}
