package connectionpool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ConfigFileParser {
    private static final Logger logger = LoggerFactory.getLogger(ConfigFileParser.class);
    private List<Config> configList = new ArrayList<Config>();

    List<Config> getPoolConfig(String fileName) throws ConnectionPoolException {
        if ((fileName == null) || (fileName.equals(""))) {
            throw new IllegalArgumentException("File Name cannot be null/empty");
        }
        return getPoolConfig(new File(fileName));
    }

    List<Config> getPoolConfig(File file) throws ConnectionPoolException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            configList = mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, PoolConfigInfo.class));
            return configList;
        } catch (Exception e) {
            throw new ConnectionPoolException("Could not parse file:" + file.getName(), e);
        }
    }

    public static void main(String[] args) throws ConnectionPoolException {
        List<Config> s = new ConfigFileParser().getPoolConfig("C:\\eclipse-workspace\\connection-pool-test\\src\\main\\resources\\dbpool.json");

        Iterator<Config> it = s.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }
}
