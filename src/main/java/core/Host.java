package core;

import java.util.ArrayList;
import java.util.List;

public class Host {

    private List<EntityArray> hosts;

    private Host() {
        hosts = new ArrayList<EntityArray>();
    }

    public static Host init() {
        return new Host();
    }

    public int addHost(long timer, String hostName) {
        if (findIndex(hostName) == -1) {
            hosts.add(EntityArray.init(timer, hostName));
            return 0;
        } else
            return 1;
    }

    public int removeHost(String hostName) {
        int index = findIndex(hostName);
        if (index != -1) {
            hosts.remove(index);
            return 0;
        } else
            return 1;
    }

    private int findIndex(String hostName) {
        int index = 0;
        boolean exist = false;
        for (EntityArray host : hosts) {
            if (host.getName().equals(hostName)) {
                exist = true;
                break;
            }
            index++;
        }
        if (exist)
            return index;
        return -1;
    }

    private EntityArray findHost(String hostName) {
        for (EntityArray host : hosts) {
            if (host.getName().equals(hostName)) {
                return host;
            }
        }
        return null;
    }

    public int setStatus(String hostName, STATUS status) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            host.setStatus(status);
            return 0;
        } else
            return 1;
    }

    public STATUS getStatus(String hostName) {
        EntityArray host = findHost(hostName);
        if (host != null)
            return host.getStatus();
        else
            return STATUS.NOT_EXIST;
    }

    public List<String> getNameList(STATUS status) {
        List<String> stringList = new ArrayList<String>();
        for (EntityArray host : hosts) {
            if (status == STATUS.ALL || host.getStatus() == STATUS.OPEN)
                stringList.add(host.getName());
        }
        return stringList;
    }

    public int addInHost(String hostName, long ChatID) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.insert(ChatID);
        } else return -2;
    }

    public int removeFromHost(String hostName, long ChatID) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.delete(ChatID);
        } else return -2;
    }

    public List<String> queueInHost(String hostName) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.arrayToString();
        } else return null;
    }

}

