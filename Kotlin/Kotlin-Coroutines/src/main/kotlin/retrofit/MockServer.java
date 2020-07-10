package retrofit;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import okio.Okio;

public class MockServer extends Dispatcher {

    private final String root;
    private final int port;

    public MockServer(String root, int port) {
        this.root = root;
        this.port = port;
    }

    public void run() throws IOException {
        MockWebServer server = new MockWebServer();
        server.setDispatcher(this);
        server.start(port);
    }

    @NotNull
    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String path = request.getPath();
        try {
            if (!path.startsWith("/") || path.contains("..")) throw new FileNotFoundException();
            File file = new File(root + path);
            return file.isDirectory() ? directoryToResponse(path, file) : fileToResponse(path, file);
        } catch (FileNotFoundException e) {

            return new MockResponse()
                    //.setStatus("HTTP/1.1 404")
                    .setStatus("HTTP/1.1 204")
                    // .setStatus("HTTP/1.1 200")
                    .addHeader("content-type: text/plain; charset=utf-8")
                    .setBody("");


        } catch (IOException e) {

            return new MockResponse()
                    .setStatus("HTTP/1.1 500")
                    .addHeader("content-type: text/plain; charset=utf-8")
                    .setBody("SERVER ERROR: " + e);

        }
    }

    private MockResponse directoryToResponse(String basePath, File directory) {
        if (!basePath.endsWith("/")) basePath += "/";

        StringBuilder response = new StringBuilder();

        response.append(String.format("<html><head><title>%s</title></head><body>", basePath));
        response.append(String.format("<h1>%s</h1>", basePath));

        for (String file : directory.list()) {
            response.append(String.format("<div class='file'><a href='%s'>%s</a></div>", basePath + file, file));
        }

        response.append("</body></html>");

        return new MockResponse()
                .setStatus("HTTP/1.1 200")
                .addHeader("content-type: text/html; charset=utf-8")
                .setBody(response.toString());
    }

    private MockResponse fileToResponse(String path, File file) throws IOException {
        return new MockResponse()
                .setStatus("HTTP/1.1 200")
                .setBody(fileToBytes(file))
                .addHeader("content-type: " + contentType(path));
    }

    private Buffer fileToBytes(File file) throws IOException {
        Buffer result = new Buffer();
        result.writeAll(Okio.source(file));
        return result;
    }

    private String contentType(String path) {
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg")) return "image/jpeg";
        if (path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".txt")) return "text/plain; charset=utf-8";
        return "application/octet-stream";
    }

    public static void main(String[] args) throws Exception {
        MockServer server = new MockServer(ROOT, PORT);
        server.run();
    }

    public static final String ROOT = "127.0.0.1";
    public static final int PORT = 9885;

}