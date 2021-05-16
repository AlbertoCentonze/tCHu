package ch.epfl.tchu.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class NetUtils {
    private static final String ERROR = "couldn't load ip";
    public static String getHostIp(){
        try{
            return NetworkInterface.networkInterfaces()
                    .filter(i -> {
                        try { return i.isUp() && !i.isLoopback(); }
                        catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .flatMap(NetworkInterface::inetAddresses)
                    .filter(a -> a instanceof Inet4Address)
                    .map(InetAddress::getCanonicalHostName)
                    .findFirst().orElse(ERROR);
        }catch (IOException e){
            return ERROR;
        }
    }

    public static void main(String[] args){
        System.out.println(getHostIp());
    }
}
