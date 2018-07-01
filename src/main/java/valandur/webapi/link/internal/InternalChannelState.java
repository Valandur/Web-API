package valandur.webapi.ipcomm.internal;

import org.eclipse.jetty.server.HttpChannelState;

public class InternalChannelState extends HttpChannelState {

    protected InternalChannelState() {
        super(null);
    }

    @Override
    public State getState() {
        return State.DISPATCHED;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
