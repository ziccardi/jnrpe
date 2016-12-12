package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.plugin.CheckHttp;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by ziccardi on 09/12/2016.
 */
public class CheckHTTPTest {

    private final static int PORT = 8580;
    private final static Server SERVER = new Server(PORT);

    @BeforeClass
    public static void setup() throws Exception {

        SERVER.setHandler(new AbstractHandler() {

            public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
                if ("GET".equals(request.getMethod())) {
                    String resp = "<h1>Hello World</h1><p>This is a paragraph</p>";

                    if (request.getHeader("User-Agent").contains("JNRPE")) {
                        resp += "<p>GET from JNRPE detected</p>";
                    }
                    httpServletResponse.setContentType("text/html;charset=utf-8");
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    request.setHandled(true);
                    httpServletResponse.getWriter().println(resp);
                } else if ("POST".equals(request.getMethod())) {
                    StringBuilder buf = new StringBuilder();

                    // Sort keys
                    TreeMap<Object,Object> ret = new TreeMap<Object, Object>();
                    ret.putAll(request.getParameterMap());

                    for (Object key : ret.keySet()) {
                        String value = request.getParameter(String.valueOf(key));
                        buf.append(key).append(':').append(value).append(',');
                    }
                    httpServletResponse.setContentType("text/html;charset=utf-8");
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    request.setHandled(true);
                    httpServletResponse.getWriter().println(buf.toString());
                }
            }
        });

        // TODO: configure HTTPS and add tests for certificate and authentication

        SERVER.start();
    }

    @AfterClass
    public static void shutdown() throws Exception {
        SERVER.stop();
    }

    @Test
    public void checkHttpOk() {
        PluginTester.given(new CheckHttp())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', Integer.toString(PORT))
            .expect(Status.OK);
    }

    @Test
    public void checkExpectedOk() {
        PluginTester.given(new CheckHttp())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', Integer.toString(PORT))
            .withOption("expect", 'e', "Hello World,This is a paragraph")
            .withOption("useragent", 'A', "Java")
            .expect(Status.OK);
    }

    @Test
    public void checkExpectedWarning() {
        PluginTester.given(new CheckHttp())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', Integer.toString(PORT))
            .withOption("expect", 'e', "This string is not found")
            .withOption("useragent", 'A', "Java")
            .expect(Status.WARNING);
    }

    @Test
    public void checkRegexp() throws Exception {
        PluginTester.given(new CheckHttp())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', Integer.toString(PORT))
            .withOption("regex", 'r', "(hello world)")
            .withOption("invert-regex", 'I', null)
            .withOption("eregi", 'R', null)
            .expect(Status.CRITICAL);
    }

    @Test
    public void checkPost() throws Exception {
        PluginTester.given(new CheckHttp())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', Integer.toString(PORT))
            .withOption("post", 'P', "param1=val1&param2=val2&param3=val3")
            .withOption("string", 's', "param1:val1,param2:val2,param3:val3")
            .expect(Status.OK);
    }

    @Test
    public void checkNobody() throws Exception {
        PluginTester.given(new CheckHttp())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', Integer.toString(PORT))
            .withOption("string", 's', "200 OK")
            .withOption("no-body", 'n', null)
            .expect(Status.OK);
    }

    @Test
    @Ignore("checkCertificate - Not yet implemented")
    public final void checkCertificate() {

    }

    @Test
    @Ignore("checkAuth - Not yet implemented")
    public final void checkAuth() {

    }
}
