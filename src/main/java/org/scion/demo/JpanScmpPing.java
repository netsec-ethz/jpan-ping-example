package org.scion.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.scion.jpan.Constants;
import org.scion.jpan.Path;
import org.scion.jpan.Scion;
import org.scion.jpan.ScionService;
import org.scion.jpan.ScionUtil;
import org.scion.jpan.ScmpSenderAsync;

public final class JpanScmpPing {
  private static final long DST_IA = ScionUtil.parseIA("64-2:0:9c");
  // Create a red block at x=52, y=84
  private static final String[] IPS = {
    "fd00::0034:0054:ffff:0000",
    "fd00::0034:0055:ffff:0000",
    "fd00::0035:0054:ffff:0000",
    "fd00::0035:0055:ffff:0000"
  };

  private JpanScmpPing() {}

  public static void main(String[] args) {
    ScionService service = Scion.defaultService();
    ScmpSenderAsync.ResponseHandler noopHandler = new ScmpSenderAsync.ResponseHandler() {
      @Override
      public void onResponse(org.scion.jpan.Scmp.TimedMessage msg) {}

      @Override
      public void onTimeout(org.scion.jpan.Scmp.TimedMessage msg) {}
    };

    try (ScmpSenderAsync sender =
        ScmpSenderAsync.newBuilder(noopHandler).setService(service).build()) {
      for (String ip : IPS) {
        pingOne(service, sender, ip);
      }
    } catch (IOException e) {
      System.err.println("Failed to initialize SCMP sender: " + e.getMessage());
      e.printStackTrace(System.err);
    } finally {
      Scion.closeDefault();
    }
  }

  private static void pingOne(
      ScionService service, ScmpSenderAsync sender, String destinationIp) {
    try {
      InetAddress ip = InetAddress.getByName(destinationIp);
      List<Path> paths = service.getPaths(DST_IA, ip, Constants.SCMP_PORT);
      if (paths.isEmpty()) {
        System.out.println("NO_PATH " + ScionUtil.toStringIA(DST_IA) + "," + destinationIp);
        return;
      }

      Path path = paths.get(0);
      int seq = sender.sendEcho(path, ByteBuffer.wrap("".getBytes(StandardCharsets.UTF_8)));
      System.out.println("SENT " + ScionUtil.toStringIA(DST_IA) + "," + ip.getHostAddress() + " seq=" + seq);
    } catch (Exception e) {
      System.out.println("ERROR " + ScionUtil.toStringIA(DST_IA) + "," + destinationIp + " -> " + e.getMessage());
    }
  }
}
