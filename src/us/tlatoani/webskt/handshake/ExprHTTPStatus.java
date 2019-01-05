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
public class ExprHTTPStatus extends ChangeablePropertyExpression<Handshakedata, Number> {

    @Override
    public void change(Handshakedata handshakedata, Number number, Changer.ChangeMode changeMode) {
        if (handshakedata instanceof ServerHandshakeBuilder) {
            ((ServerHandshakeBuilder) handshakedata).setHttpStatus(number.shortValue());
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Number convert(Handshakedata handshakedata) {
        return OptionalUtil.cast(handshakedata, ServerHandshake.class).map(ServerHandshake::getHttpStatus).orElse(null);
    }
}
