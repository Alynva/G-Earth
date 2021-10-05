package gearth.services.packet_info;

import gearth.services.packet_info.PacketInfoManager;
import gearth.services.packet_info.providers.RemotePacketInfoProvider;
import gearth.services.packet_info.providers.implementations.HarblePacketInfoProvider;
import gearth.services.packet_info.providers.implementations.SulekPacketInfoProvider;
import gearth.services.packet_info.providers.implementations.GEarthUnityPacketInfoProvider;
import gearth.protocol.connection.HClient;

import java.util.*;
import java.util.concurrent.Semaphore;

public class PacketInfoManagerExtra {

    public static PacketInfoManager fromHotelVersion(String hotelversion, HClient clientType) {
        List<PacketInfo> result = new ArrayList<>();

        if (clientType == HClient.UNITY) {
            result.addAll(new GEarthUnityPacketInfoProvider(hotelversion).provide());
        }
        else if (clientType == HClient.FLASH) {
            try {
                List<RemotePacketInfoProvider> providers = new ArrayList<>();
                providers.add(new HarblePacketInfoProvider(hotelversion));
                providers.add(new SulekPacketInfoProvider(hotelversion));

                Semaphore blockUntilComplete = new Semaphore(providers.size());
                blockUntilComplete.acquire(providers.size());

                List<PacketInfo> synchronizedResult = Collections.synchronizedList(result);
                for (RemotePacketInfoProvider provider : providers) {
                    new Thread(() -> {
                        synchronizedResult.addAll(provider.provide());
                        blockUntilComplete.release();
                    }).start();
                }

                blockUntilComplete.acquire(providers.size());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return new PacketInfoManager(result);
    }

}
