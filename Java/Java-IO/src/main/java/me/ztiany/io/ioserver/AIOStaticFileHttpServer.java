package me.ztiany.io.ioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIOStaticFileHttpServer {

    private static final Logger LOGGER = Logger.getLogger(AIOStaticFileHttpServer.class.getName());

    private static final Pattern PATH_EXTRACTOR = Pattern.compile("GET(.*?)HTTP");

    private static final String INDEX_PAGE = "index.html";

    public static void main(String... args) throws IOException {
        new AIOStaticFileHttpServer().start(Paths.get("."));
    }

    public void start(final Path root) throws IOException {

        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());

        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(10080));

        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                serverChannel.accept(null, this);

                try {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    clientChannel.read(buffer).get();
                    buffer.flip();

                    String request = new String(buffer.array());
                    String requestPath = extractPath(request);
                    Path filePath = getFilePath(root, requestPath);

                    if (!Files.exists(filePath)) {
                        String error404 = generateErrorResponse(404, "Not Found");
                        clientChannel.write(ByteBuffer.wrap(error404.getBytes()));
                        return;
                    }

                    LOGGER.log(Level.INFO, "处理请求：{0}", requestPath);
                    String header = generateFileContentResponseHeader(filePath);
                    clientChannel.write(ByteBuffer.wrap(header.getBytes())).get();
                    Files.copy(filePath, Channels.newOutputStream(clientChannel));

                } catch (Exception e) {

                    String error = generateErrorResponse(500, "Internal Server Error");
                    clientChannel.write(ByteBuffer.wrap(error.getBytes()));
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);

                } finally {
                    try {
                        clientChannel.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }

            @Override
            public void failed(Throwable throwable, Void attachment) {
                LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable);
            }
        });

        LOGGER.log(Level.INFO, "服务器已经启动,文件根目录为：" + root);
    }

    private String extractPath(String request) {
        Matcher matcher = PATH_EXTRACTOR.matcher(request);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Path getFilePath(Path root, String requestPath) {
        if (requestPath == null || "/".equals(requestPath)) {
            requestPath = INDEX_PAGE;
        }

        if (requestPath.startsWith("/")) {
            requestPath = requestPath.substring(1);
        }

        int pos = requestPath.indexOf("?");

        if (pos != -1) {
            requestPath = requestPath.substring(0, pos);
        }

        return root.resolve(requestPath);
    }

    private String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    private String generateFileContentResponseHeader(Path filePath) throws IOException {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type：" +
                getContentType(filePath) +
                "\r\n" +
                "Content-Length：" + Files.size(filePath) + "\r\n" +
                "\r\n";
    }

    private String generateErrorResponse(int statusCode, String message) {
        return "HTTP/1.1" + statusCode + "" + message + "\r\n" +
                "Content-Type：text/plain\r\n" +
                "Content-Length：" + message.length() + "\r\n" +
                "\r\n" +
                message;
    }

}