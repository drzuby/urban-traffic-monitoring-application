package pl.edu.agh.pp.adapters;

import org.jgroups.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.pp.cron.CronManager;
import pl.edu.agh.pp.utils.SystemScheduler;
import pl.edu.agh.pp.exceptions.IllegalPreferenceObjectExpected;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Maciej on 09.11.2016.
 * 22:09
 * Project: server.
 */
public class Connector {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(Connector.class);
    private static ChannelReceiver channelReceiver = new ChannelReceiver();
    private static ManagementServer managementServer = new ManagementServer();
    private static Server server;
    private static SystemScheduler systemScheduler;

    public static void connect(String[] args, InetAddress bind_addr, int port, boolean nio) throws InterruptedException {
        managementServer = new ManagementServer();
        server = new Server();
        channelReceiver = new ChannelReceiver();

        try {
            // TODO: I am not sure if we should start both management and anomalies channel both at the same time.
            managementServer.start(bind_addr, port - 1, nio);
            server.start(bind_addr, port, nio);
            channelReceiver.start(bind_addr, port, nio);
            logger.info("Server already running.");
        } catch (Exception e) {
            logger.error("Connector :: Exception " + e, e);
        }

        Thread.sleep(10000);

        systemScheduler = new SystemScheduler();
        systemScheduler.sendSystemGeneralMessageEveryHour();

        if(args.length>1)
            new CronManager(server).doSomething(args[1]);
        else
            new CronManager(server).doSomething("");
    }

    public static void updateLever(double leverValue) {
        managementServer.sendLeverInfoMessage(leverValue);
    }

    public static void updateSystem(Address destination) throws IOException, IllegalPreferenceObjectExpected {
        managementServer.sendSystemGeneralMessage(destination);
    }

    public static void updateHistoricalAnomalies(Address destination, String date, int routeID) {
        managementServer.sendHistoricalAnomaliesMessage(destination, date, routeID, server);
    }

}