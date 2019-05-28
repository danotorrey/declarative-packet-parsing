import io.netty.buffer.ByteBuf;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DnsClient {

    private NioEventLoopGroup nettyEventLoop;
    private DnsNameResolver resolver;


    public void start() {

        nettyEventLoop = new NioEventLoopGroup();

        final DnsNameResolverBuilder dnsNameResolverBuilder = new DnsNameResolverBuilder(nettyEventLoop.next());
        dnsNameResolverBuilder.channelType(NioDatagramChannel.class);

        resolver = dnsNameResolverBuilder.build();
    }

    public void txtLookup(String hostName) throws InterruptedException, ExecutionException {

        DnsResponse content = null;
        try {
            content = resolver.query(new DefaultDnsQuestion(hostName, DnsRecordType.TXT)).sync().get().content();
            int count = content.count(DnsSection.ANSWER);
            for (int i = 0; i < count; i++) {

                final DnsRecord dnsRecord = content.recordAt(DnsSection.ANSWER, i);

                byte[] ipAddressBytes;
                final DefaultDnsRawRecord dnsRawRecord = (DefaultDnsRawRecord) dnsRecord;
                try {
                    final ByteBuf byteBuf = dnsRawRecord.content();
                    ipAddressBytes = new byte[byteBuf.readableBytes()];
                    int readerIndex = byteBuf.readerIndex();
                    byteBuf.getBytes(readerIndex, ipAddressBytes);
                } finally {
                    /* Must manually release references on dnsRawRecord object since the DefaultDnsRawRecord class
                     * extends ReferenceCounted. This also releases the above ByteBuf, since DefaultDnsRawRecord is
                     * the holder for it. */
                    dnsRawRecord.release();
                }

                System.out.println("");

            }


        } finally {
            if (content != null) {
                // Must manually release references on content object since the DnsResponse class extends ReferenceCounted
                content.release();
            }
        }
    }
}
