package us.tlatoani.webskt.handshake;

import ch.njol.skript.classes.Changer;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;
import us.tlatoani.mundocore.util.OptionalUtil;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.Handshakedata;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprResourceDescriptor extends ChangeablePropertyExpression<Handshakedata, String> {
    @Override
    public void change(Handshakedata handshakedata, String s, Changer.ChangeMode changeMode) {
        if (handshakedata instanceof ClientHandshakeBuilder) {
            ((ClientHandshakeBuilder) handshakedata).setResourceDescriptor(s);
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(Handshakedata handshakedata) {
        return OptionalUtil.cast(handshakedata, ClientHandshake.class).map(ClientHandshake::getResourceDescriptor).orElse(null);
    }
}
