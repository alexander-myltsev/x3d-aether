package org.jwebsocket.netty.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;


/**
 * Decodes {@link ChannelBuffer}s into {@link HttpRequest}s and {@link HttpChunk}s.
 *
 * <h3>Parameters that prevents excessive memory consumption</h3>
 * <table border="1">
 * <tr>
 * <th>Name</th><th>Meaning</th>
 * </tr>
 * <tr>
 * <td>{@code maxInitialLineLength}</td>
 * <td>The maximum length of the initial line (e.g. {@code "GET / HTTP/1.0"})
 *     If the length of the initial line exceeds this value, a
 *     {@link TooLongFrameException} will be raised.</td>
 * </tr>
 * <tr>
 * <td>{@code maxHeaderSize}</td>
 * <td>The maximum length of all headers.  If the sum of the length of each
 *     header exceeds this value, a {@link TooLongFrameException} will be raised.</td>
 * </tr>
 * <tr>
 * <td>{@code maxChunkSize}</td>
 * <td>The maximum length of the content or each chunk.  If the content length
 *     exceeds this value, the transfer encoding of the decoded request will be
 *     converted to 'chunked' and the content will be split into multiple
 *     {@link HttpChunk}s.  If the transfer encoding of the HTTP request is
 *     'chunked' already, each chunk will be split into smaller chunks if the
 *     length of the chunk exceeds this value.  If you prefer not to handle
 *     {@link HttpChunk}s in your handler, insert {@link HttpChunkAggregator}
 *     after this decoder in the {@link ChannelPipeline}.</td>
 * </tr>
 * </table>
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2095 $, $Date: 2010-06-20 04:46:04 +0200 (Sun, 20 Jun 2010) $
 */
public class HttpRequestDecoder extends HttpMessageDecoder {

    /**
     * Creates a new instance with the default
     * {@code maxInitialLineLength (4096}}, {@code maxHeaderSize (8192)}, and
     * {@code maxChunkSize (8192)}.
     */
    public HttpRequestDecoder() {
        super();
    }

    /**
     * Creates a new instance with the specified parameters.
     */
    public HttpRequestDecoder(
            int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception{
        return new DefaultHttpRequest(
                HttpVersion.valueOf(initialLine[2]), HttpMethod.valueOf(initialLine[0]), initialLine[1]);
    }

    @Override
    protected boolean isDecodingRequest() {
        return true;
    }
}
