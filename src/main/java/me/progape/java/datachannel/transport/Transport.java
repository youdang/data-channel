package me.progape.java.datachannel.transport;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;

/**
 * @author progape
 * @date 2022-02-04
 */
public class Transport implements Disposable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Transport.class);

    private final Connection connection;
    private final Sender sender;
    private final LinkedBlockingDeque<Receiver> queue;

    public Transport(String host, int port) {
        this.connection = TcpClient.create()
            .host(host)
            .port(port)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .wiretap(true)
            .connectNow();
        this.connection.addHandlerLast(new Codec());
        this.queue = new LinkedBlockingDeque<>();

        this.sender = new Sender();
        this.connection.outbound().sendObject(sender).then().subscribe();
        this.connection.inbound().receiveObject().map(o -> (Packet) o).subscribe(this::handlePacket);
    }

    public Mono<Packet> receive() {
        ReceiveOneReceiver receiver = new ReceiveOneReceiver();
        return receiver.doOnSubscribe(s -> queue.offer(receiver));
    }

    public Mono<Packet> sendReceive(Packet packet) {
        SendOneReceiveOneReceiver receiver = new SendOneReceiveOneReceiver(packet, this.sender);
        return receiver.doOnSubscribe(s -> queue.offer(receiver));
    }

    public Flux<Packet> sendReceiveMany(Packet packet, StreamFinisher finisher) {
        SendOneReceiveManyReceiver receiver = new SendOneReceiveManyReceiver(packet, this.sender, finisher);
        return receiver.doOnSubscribe(s -> queue.offer(receiver))
            .doOnNext(p -> {
                if (!finisher.get()) {
                    queue.offer(receiver);
                }
            })
            .doOnComplete(queue::poll);
    }

    public ByteBufAllocator alloc() {
        return connection.channel().alloc();
    }

    @Override
    public void dispose() {
        this.sender.cancel();
        this.connection.dispose();
    }

    @Override
    public boolean isDisposed() {
        return this.connection.isDisposed();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void handlePacket(Packet packet) {
        Receiver receiver;
        while ((receiver = queue.poll()) == null) {
            Thread.yield();
        }
        receiver.receive(packet);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private interface Receiver extends Subscription {
        void receive(Packet packet);
    }

    private static class ReceiveOneReceiver extends Mono<Packet> implements Receiver {
        private CoreSubscriber<? super Packet> subscriber;

        @Override
        public void subscribe(CoreSubscriber<? super Packet> actual) {
            this.subscriber = actual;
            this.subscriber.onSubscribe(this);
        }

        @Override
        public void receive(Packet packet) {
            LOGGER.info("<<< packet: #{}", packet.getSequenceId());
            this.subscriber.onNext(packet);
            this.subscriber.onComplete();
        }

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
        }
    }

    private static class SendOneReceiveOneReceiver extends Mono<Packet> implements Receiver {
        private final Packet packet;
        private final Sender sender;
        private CoreSubscriber<? super Packet> subscriber;

        public SendOneReceiveOneReceiver(Packet packet, Sender sender) {
            this.packet = packet;
            this.sender = sender;
        }

        @Override
        public void subscribe(CoreSubscriber<? super Packet> actual) {
            this.subscriber = actual;
            this.subscriber.onSubscribe(this);

            this.sender.send(this.packet);
            LOGGER.info(">>> packet: #{}", this.packet.getSequenceId());
        }

        @Override
        public void receive(Packet packet) {
            LOGGER.info("<<< packet: #{}", packet.getSequenceId());
            this.subscriber.onNext(packet);
            this.subscriber.onComplete();
        }

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
        }
    }

    private static class SendOneReceiveManyReceiver extends Flux<Packet> implements Receiver {
        private final Packet packet;
        private final Sender sender;
        private final Supplier<Boolean> finisher;
        private CoreSubscriber<? super Packet> subscriber;

        public SendOneReceiveManyReceiver(Packet packet, Sender sender, Supplier<Boolean> finisher) {
            this.packet = packet;
            this.sender = sender;
            this.finisher = finisher;
        }

        @Override
        public void subscribe(CoreSubscriber<? super Packet> actual) {
            this.subscriber = actual;
            this.subscriber.onSubscribe(this);

            this.sender.send(this.packet);
            LOGGER.info(">>> packet: #{}", this.packet.getSequenceId());
        }

        @Override
        public void receive(Packet packet) {
            LOGGER.info("<<< packet: #{}", packet.getSequenceId());
            this.subscriber.onNext(packet);
            if (this.finisher.get()) {
                this.subscriber.onComplete();
            }
        }

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
            this.subscriber.onComplete();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class Sender extends Flux<Packet> implements Subscription {
        private CoreSubscriber<? super Packet> actual;

        @Override
        public void subscribe(CoreSubscriber<? super Packet> actual) {
            this.actual = actual;
            this.actual.onSubscribe(this);
        }

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
            this.actual.onComplete();
        }

        public void send(Packet buf) {
            this.actual.onNext(buf);
        }
    }
}
