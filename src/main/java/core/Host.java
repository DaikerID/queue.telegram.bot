package core;

import org.telegram.telegrambots.meta.api.objects.Chat;

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

    public String findInHosts(long ChatID){
        for(EntityArray host: hosts)
            if (host.find(ChatID)!=-1)
                return host.getName();
        return null;
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

    public int addInHost(String hostName, long ChatID,String name) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.insert(ChatID,name);
        } else return -2;
    }

    public int removeFromHost(String hostName, long ChatID) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.delete(ChatID);
        } else return -2;
    }

    public int removeFromHost(long ChatID) {
        EntityArray host = findHost(this.findInHosts(ChatID));
        if (host != null) {
            return host.delete(ChatID);
        } else return -2;
    }

    public int removeFromHost(String hostName,int index) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.delete(index);
        } else return -2;
    }

    public int entityStatus(long ChatID){
        EntityArray host = findHost(this.findInHosts(ChatID));
        if (host != null) {
            return host.entityStatus(ChatID);
        } else return -2;
    }

    public String hostStatus(String hostName){
        EntityArray host = findHost(hostName);
        String status;
        switch (host.getStatus()){
            case OPEN:
                status="OPEN";
                break;
            case CLOSE:
                status="CLOSE";
                break;
            default:
                status="INVALID";
                break;
        }
        return "Resource: "+hostName+ " Status: "+status+" Number of entities: "+host.entiryCount() + " Max time: " + (int)host.getTimer()/60000;
    }

    public List<String> queueInHost(String hostName, PERMISSION permission) {
        EntityArray host = findHost(hostName);
        if (host != null) {
            return host.arrayToString(permission);
        } else return null;
    }

    public String getInfoByIndex(String hostName,int index) {
        EntityArray host = findHost(hostName);
        if (host != null)
            return host.findByIndex(index);
        else
            return null;
    }



}

