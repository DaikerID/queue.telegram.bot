package core;
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

    public int insert(long ChatID){
        if (status==STATUS.OPEN) {
            int index = find(ChatID);
            if (index == -1) {
                lock.readLock().lock();
                try {
                    array.add(new Entity(ChatID));
                } finally {
                    lock.readLock().unlock();
                }
            }
            if (array.size() == 1)
                this.start();
            return index;
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

    public int find(long ChatID){
        boolean exist=false;
        int index=0;
        lock.writeLock().lock();
        try{
            for(Entity item:array){
                if (item.getChatID()==ChatID) {
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

    public List<String> arrayToString(){
        int index=0;
        List<String> stringList=new ArrayList<String>();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss k");
        for(Entity item:array){
            if (index==0){
                stringList.add(index+". ChatID: " + item.getChatID()+" Started in: "+formatForDateNow.format(item.getTime()));
            }
            else
                stringList.add(index+". ChatID: " + item.getChatID());
            index++;
        }
        return stringList;
    }
}
