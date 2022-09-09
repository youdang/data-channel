package me.progape.java.datachannel.transport;

import java.util.function.Supplier;

/**
 * @author progape
 * @date 2022-02-07
 */
public class StreamFinisher implements Supplier<Boolean> {
    private boolean finished = false;

    public static StreamFinisher never() {
        return new StreamFinisher();
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public Boolean get() {
        return finished;
    }
}
