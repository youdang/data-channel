package me.progape.java.datachannel.protocol;

import me.progape.java.datachannel.Configuration;
import me.progape.java.datachannel.protocol.command.QueryCommand;
import me.progape.java.datachannel.protocol.handshake.Authenticator;
import me.progape.java.datachannel.protocol.handshake.HandshakeResponse;
import me.progape.java.datachannel.protocol.query.ResultSetBuilder;
import me.progape.java.datachannel.protocol.shared.ErrorResponse;
import me.progape.java.datachannel.protocol.shared.OkResponse;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.StreamFinisher;
import me.progape.java.datachannel.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author progape
 * @date 2022-02-04
 */
public abstract class Protocol {
    private static final Logger LOGGER = LoggerFactory.getLogger(Protocol.class);
    protected final Transport transport;
    protected final Context context;

    public Protocol(Configuration configuration) {
        this.context = Context.from(configuration);
        this.transport = new Transport(configuration.getHost(), configuration.getPort());
    }

    public Mono<Response> handshake() {
        LOGGER.debug("do handshake");
        return transport.receive()
            .map(p -> {
                LOGGER.debug("handshake response received");
                return new HandshakeResponse(p, context);
            })
            .doOnNext(context::update)
            .flatMap(hr -> Authenticator.auth(hr, transport, context))
            .map(this::requiresOk)
            .doOnNext(p -> LOGGER.debug("authentication finished"));
    }

    public Mono<Response> query(String sql) {
        LOGGER.debug("send query {}", sql);
        StreamFinisher finisher = new StreamFinisher();

        QueryCommand queryCommand = new QueryCommand(sql, context);
        return transport.sendReceiveMany(queryCommand.toPacket(transport.alloc()), finisher)
            .switchOnFirst((signal, flux) -> {
                if (signal.hasValue()) {
                    Packet firstPacket = Objects.requireNonNull(signal.get());
                    if (firstPacket.isOk()) {
                        LOGGER.debug("OK packet received");
                        finisher.finish();
                        return Mono.just(new OkResponse(firstPacket, context));
                    } else if (firstPacket.isError()) {
                        finisher.finish();
                        throw new ProtocolException(new ErrorResponse(firstPacket, context));
                    } else if (firstPacket.getPayload().getByte(0) == (byte) 0xFB) {
                        finisher.finish();
                        throw new ProtocolException("LOCAL INFILE is not supported");
                    }
                }

                return flux
                    .reduceWith(
                        () -> new ResultSetBuilder(finisher),
                        (rsb, p) -> rsb.readPacket(p, context)
                    )
                    .map(resultSetBuilder -> {
                        LOGGER.debug("build result set finished");
                        return resultSetBuilder.build();
                    });
            })
            .singleOrEmpty()
            .doOnCancel(finisher::finish)
            .doOnError(th -> finisher.finish());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected Response requiresOk(Packet packet) {
        if (packet.isOk()) {
            return new OkResponse(packet, context);
        } else if (packet.isError()) {
            throw new ProtocolException(new ErrorResponse(packet, context));
        } else {
            throw new ProtocolException("unknown response");
        }
    }
}
