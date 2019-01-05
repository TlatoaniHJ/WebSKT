package us.tlatoani.webskt.events;

import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.HandshakedataImpl1;
import us.tlatoani.mundocore.base_event.BaseEvent;

import java.net.URI;

public class WebSocketClientConstructorEvent extends BaseEvent {
    public URI serverURI = null;
    public Handshakedata request = new HandshakedataImpl1();
}
