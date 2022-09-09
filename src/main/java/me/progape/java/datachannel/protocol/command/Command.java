package me.progape.java.datachannel.protocol.command;

import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.shared.Request;

/**
 * @author progape
 * @date 2022-02-06
 */
public abstract class Command extends Request {
    protected final Context context;

    public Command(Context context) {
        super((short) 0);
        this.context = context;
    }
}
