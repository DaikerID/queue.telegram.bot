package core;
import com.google.inject.internal.asm.$TypeReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityArray {
    @Getter
    final String name;
    List<Entity> array;
    @Getter
    @Setter
    STATUS status=STATUS.OPEN;
    @Getter
    @Setter
    long timer;
    ReadWriteLock lock = new ReentrantReadWriteLock();
    Thread observer;

    private EntityArray (long timer, String name){
        array=new LinkedList<Entity>();
        this.timer=timer;
        this.name=name;
        observer=new Thread(new Observer(this));
        observer.start();
    }

    static synchronized EntityArray init(long timer, String name){
        return new EntityArray(timer,name);
    }

    public int insert(long ChatID,String name){
        if (status==STATUS.OPEN) {
            int index = find(ChatID);
            if (index == -1) {
                lock.readLock().lock();
                try {
                    array.add(new Entity(ChatID,name));
                } finally {
                    lock.readLock().unlock();
                }
            }
            if (array.size() == 1)
                this.start();
            return find(ChatID);
        }
        return -1;
    }

    public int delete(long ChatID){
        int index=find(ChatID);
        if (index!=-1) {
            lock.readLock().lock();
            try{
                array.remove(index);
            }
            finally {
                lock.readLock().unlock();
            }
        }
        return -1;
    }

    public int delete(int index){
        if (index>=0&&index<array.size()) {
            lock.readLock().lock();
            try{
                array.remove(index);
            }
            finally {
                lock.readLock().unlock();
            }
        }
        return -1;
    }

    public int find(long ChatID){
        boolean exist=false;
        int index=0;
        lock.writeLock().lock();
        try{
            for(Entity item:array){
                if (item.getChatID() == ChatID) {
                    exist = true;
                    break;
                }
                index++;
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        if (exist)
            return index;
        else
            return -1;
    }

    public int entityStatus(long ChatID){
        int index=0;
        for (Entity item:array){
            if (item.getChatID()==ChatID) {
                if (item.isInProcess() == true)
                    return 0;
                else
                    return index;
            }
            index++;
        }
        return -1;
    }

    private void start(){
        lock.readLock().lock();
        try {
            Entity item = array.iterator().next();
            item.setInProcess(true);
            item.setTime(new Date());
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public void check(){
        if (array.iterator().hasNext()){
            Entity item = array.iterator().next();
            if (item.isInProcess()==true&&(item.getTime().getTime()+timer<System.currentTimeMillis())) {
                lock.readLock().lock();
                try{
                    array.remove(0);
                }
                finally {
                    lock.readLock().unlock();
                }
            }
            this.start();
        }
    }

    public List<String> arrayToString(PERMISSION permission){
        int index=0;
        List<String> stringList=new ArrayList<String>();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("kk:mm:ss");
        for(Entity item:array){
            if (index==0){
                switch (permission){
                    case USER:
                        stringList.add("In Process. ChatID: " + item.getChatID()+" Started in: "+formatForDateNow.format(item.getTime()));
                        break;
                    case ADMIN:
                        stringList.add("In Process. Name: " + item.getName() + ". ChatID: " + item.getChatID()+" Started in: "+formatForDateNow.format(item.getTime()));
                        break;
                }
            }
            else
                switch (permission){
                    case USER:
                        stringList.add(index+". ChatID: " + item.getChatID());
                        break;
                    case ADMIN:
                        stringList.add(index + ". Name: " + item.getName() + ". ChatID: " + item.getChatID());
                        break;
                }
            index++;
        }
        return stringList;
    }

    public int entiryCount(){
        return array.size();
    }

    public String findByIndex(int index){
        int count=0;
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("kk:mm:ss");
        if (array.size()>index)
            for(Entity item:array){
                if (index==count&&index==0)
                    return "In Process. Name: " + item.getName() + ". ChatID: " + item.getChatID()+" Started in: "+formatForDateNow.format(item.getTime());
                else if (index==count&&index>0)
                    return index + ". Name: " + item.getName() + ". ChatID: " + item.getChatID();
        }
        return null;
    }

}
