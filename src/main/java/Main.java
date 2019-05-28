import com.dantorrey.Pcap;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {

        // Test DNS Client
        DnsClient dnsClient = new DnsClient();
        dnsClient.start();
        dnsClient.txtLookup("graylog.com");

        Pcap.fromFile("/Users/danieltorrey/Desktop/NewNFall.pcap");
    }
}
