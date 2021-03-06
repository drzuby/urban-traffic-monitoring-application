package pl.edu.agh.pp.adapters;

import org.jgroups.Address;

import java.nio.ByteBuffer;

/**
 * Created by Maciej on 11.09.2016.
 * 14:36
 * Project: server.
 */
public class AnomaliesServer extends Server {

    @Override
    public void receive(Address sender, byte[] buf, int offset, int length) {
        try {
            server.send(null, buf, offset, length);
        } catch (Exception e) {
            logger.error("AnomaliesServer: Error occurred while receiving and sending message by the anomalies channel. "
                    + "Info: {}", e);
        }
    }

    @Override
    public void receive(Address sender, ByteBuffer buf) {
        try {
            server.send(null, buf);
        } catch (Exception e) {
            logger.error("AnomaliesServer: Error occurred while receiving and sending message by the anomalies channel. "
                    + "Info: {}", e);
        }
    }
}
