package com.chasinglemons.empeg;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/** Run with javac/java; no Android runtime or player needed. */
public final class EmpegHttpTest {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        ExecutorService workers = Executors.newCachedThreadPool();
        server.setExecutor(workers);
        CountDownLatch stalled = new CountDownLatch(1);
        CountDownLatch commandStarted = new CountDownLatch(1);
        CountDownLatch releaseCommand = new CountDownLatch(1);
        List<String> commands = Collections.synchronizedList(new ArrayList<String>());
        server.createContext("/ok", exchange -> {
            byte[] bytes = "player ready".getBytes("UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.createContext("/error", exchange -> {
            exchange.sendResponseHeaders(503, -1); exchange.close();
        });
        server.createContext("/stall", exchange -> {
            stalled.countDown();
            try { Thread.sleep(6000); } catch (InterruptedException ignored) {}
            exchange.close();
        });
        server.createContext("/command-a", exchange -> { commands.add("a"); byte[] bytes = "a".getBytes("UTF-8"); exchange.sendResponseHeaders(200, bytes.length); exchange.getResponseBody().write(bytes); exchange.close(); });
        server.createContext("/command-b", exchange -> { commands.add("b"); byte[] bytes = "b".getBytes("UTF-8"); exchange.sendResponseHeaders(200, bytes.length); exchange.getResponseBody().write(bytes); exchange.close(); });
        server.start();
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        try {
            if (!"player ready".equals(EmpegHttp.getText(base + "/ok"))) throw new AssertionError("text");
            try { EmpegHttp.getBytes(base + "/error"); throw new AssertionError("status accepted"); }
            catch (IOException expected) { if (!expected.getMessage().contains("503")) throw expected; }
            Future<?> image = workers.submit(() -> {
                try { EmpegHttp.getBytes(base + "/stall"); throw new AssertionError("no timeout"); }
                catch (SocketTimeoutException expected) {}
                catch (IOException other) { throw new RuntimeException(other); }
            });
            if (!stalled.await(1, TimeUnit.SECONDS)) throw new AssertionError("fixture not reached");
            FutureTask<String> command = new FutureTask<>(() -> EmpegHttp.getText(base + "/ok"));
            EmpegHttp.COMMANDS.execute(command);
            if (!"player ready".equals(command.get(1, TimeUnit.SECONDS))) throw new AssertionError("blocked command");
            image.get(5, TimeUnit.SECONDS);
            FutureTask<String> gate = new FutureTask<>(() -> { commandStarted.countDown(); releaseCommand.await(); return "released"; });
            EmpegHttp.COMMANDS.execute(gate);
            if (!commandStarted.await(1, TimeUnit.SECONDS)) throw new AssertionError("queue gate not reached");
            FutureTask<String> stale = new FutureTask<>(() -> EmpegHttp.getText(base + "/command-a"));
            EmpegHttp.COMMANDS.execute(stale);
            Thread.sleep(3200);
            releaseCommand.countDown();
            gate.get(1, TimeUnit.SECONDS);
            try { stale.get(1, TimeUnit.SECONDS); throw new AssertionError("stale command replayed"); } catch (ExecutionException expected) { if (!(expected.getCause() instanceof IOException)) throw expected; }
            FutureTask<String> first = new FutureTask<>(() -> EmpegHttp.getText(base + "/command-a"));
            FutureTask<String> second = new FutureTask<>(() -> EmpegHttp.getText(base + "/command-b"));
            EmpegHttp.COMMANDS.execute(first); EmpegHttp.COMMANDS.execute(second);
            if (!"a".equals(first.get(1, TimeUnit.SECONDS)) || !"b".equals(second.get(1, TimeUnit.SECONDS))) throw new AssertionError("command result");
            if (!commands.equals(java.util.Arrays.asList("a", "b"))) throw new AssertionError("command order");
            if (!"player ready".equals(EmpegHttp.getText(base + "/ok"))) throw new AssertionError("recovery");
            System.out.println("PASS: response, HTTP error, stalled-header timeout, expiry, command ordering, recovery");
        } finally {
            server.stop(0); workers.shutdownNow();
            ((ExecutorService) EmpegHttp.COMMANDS).shutdownNow();
            ((ExecutorService) EmpegHttp.PLAYLISTS).shutdownNow();
        }
    }
}
