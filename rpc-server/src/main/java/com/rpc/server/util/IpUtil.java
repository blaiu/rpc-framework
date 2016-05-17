package com.rpc.server.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class IpUtil {

	private static String IP = null;
	
	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
	
	private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
	
	public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";
	
	public static String getIp() throws SocketException {
		if (IP == null) {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress address = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					address = (InetAddress) addresses.nextElement();
					if (address != null && address instanceof Inet4Address) {
						if(isValidAddress(address)) {
							IP = address.getHostAddress();
						}
					} 
				}
			}
		}
		return IP;
	}
	
	private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null 
                && ! ANYHOST.equals(name)
                && ! LOCALHOST.equals(name) 
                && IP_PATTERN.matcher(name).matches());
    }
	
	public static boolean isInvalidLocalHost(String host) {
        return host == null 
        			|| host.length() == 0
                    || host.equalsIgnoreCase("localhost")
                    || host.equals("0.0.0.0")
                    || (LOCAL_IP_PATTERN.matcher(host).matches());
    }
	
//	public static void main(String[] args) throws SocketException, UnknownHostException {
//		String host = InetAddress.getLocalHost().getHostAddress();
//		System.out.println(host);
//		System.out.println(isInvalidLocalHost(host));
//		System.out.println(getIp());
//	}
}
