package com.github.tubicz.coupon_service.adapter.out.geolocation;

import com.github.tubicz.coupon_service.application.port.out.IpGeolocationPort;
import com.github.tubicz.coupon_service.application.exception.IpNotResolvableException;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
class MaxmindIpGeolocationAdapter implements IpGeolocationPort {
    private final String dbPath;
    private volatile DatabaseReader reader;

    MaxmindIpGeolocationAdapter(@Value("${geolocation.db-path}") String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public String getCountryCode(String ipAddress) {
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            String isoCode = getReader().country(ip).getCountry().getIsoCode();
            if (isoCode == null || isoCode.isBlank()) {
                throw new IpNotResolvableException(ipAddress);
            }
            return isoCode;
        } catch (UnknownHostException | AddressNotFoundException e) {
            throw new IpNotResolvableException(ipAddress);
        } catch (IOException | GeoIp2Exception e) {
            throw new IpNotResolvableException(ipAddress);
        }
    }

    private DatabaseReader getReader() throws IOException {
        if (reader == null) {
            synchronized (this) {
                if (reader == null) {
                    var file = new File(dbPath);
                    if (!file.exists()) {
                        throw new IOException("GeoIP database not found at: " + dbPath);
                    }
                    reader = new DatabaseReader.Builder(file).build();
                }
            }
        }
        return reader;
    }
}
