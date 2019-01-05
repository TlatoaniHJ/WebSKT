package us.tlatoani.webskt.handshake;

import ch.njol.skript.classes.Changer;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;
import us.tlatoani.mundocore.util.OptionalUtil;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprHTTPStatusMessage extends ChangeablePropertyExpression<Handshakedata, String> {

    @Override
    public void change(Handshakedata handshakedata, String s, Changer.ChangeMode changeMode) {
        if (handshakedata instanceof ServerHandshakeBuilder) {
            ((ServerHandshakeBuilder) handshakedata).setHttpStatusMessage(s);
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(Handshakedata handshakedata) {
        return OptionalUtil.cast(handshakedata, ServerHandshake.class).map(ServerHandshake::getHttpStatusMessage).orElse(null);
    }
}
