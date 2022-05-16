package core.body;

import core.Scene;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Body
{
    public static long idCounter = 0;

    private String id;
    public Body parent;
    public LinkedHashMap<String, Body> children = new LinkedHashMap<>();
    public Scene scene;
    public boolean visible = true;
    private boolean created = false;

    public Body()
    {
        idCounter++;
        id = String.valueOf(idCounter);
    }

    public Body(String id)
    {
        this.id = id;
    }

    //Override
    public void update(float delta)
    {
        for(Runnable r : updateTask)
            r.run();
        while(!nextFrameTask.isEmpty())
        {
            try {
                nextFrameTask.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void updatePhysics(float delta)
    {
        for(Runnable r : updatePhysicsTask)
            r.run();
    }
    public void onCreate()
    {
        created = true;
        while(!onCreateTask.isEmpty())
        {
            try {
                onCreateTask.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void onDestroy()
    {
        while(!onDestroyTask.isEmpty())
        {
            try {
                onDestroyTask.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //

    LinkedBlockingQueue<Runnable> onCreateTask = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Runnable> nextFrameTask = new LinkedBlockingQueue<>();
    List<Runnable> updateTask = new ArrayList<>();
    List<Runnable> updatePhysicsTask = new ArrayList<>();
    LinkedBlockingQueue<Runnable> onDestroyTask = new LinkedBlockingQueue<>();

    public void doOnCreate(Runnable runnable)
    {
        onCreateTask.add(runnable);
    }
    public void doOnDestroy(Runnable runnable)
    {
        onDestroyTask.add(runnable);
    }
    public void doEveryFrame(Runnable runnable)
    {
        updateTask.add(runnable);
    }
    public void doPhysicsTask(Runnable runnable)
    {
        updatePhysicsTask.add(runnable);
    }
    public void doNextFrame(Runnable runnable) {nextFrameTask.add(runnable);}

    public String getId() {return id;}

    public boolean isCreated() {
        return created;
    }

    public Body getChild(int index)
    {
        int count = 0;
        for(Map.Entry<String, Body> entry : children.entrySet())
        {
            if(count == index) return entry.getValue();
            count++;
        }
        return null;
    }

    public Body getChild(String id)
    {
        return children.get(id);
    }

    public Body findBodyById(String id)
    {
        if(children.containsKey(id)) return children.get(id);
        else
        {
            for(Map.Entry<String, Body> entry : children.entrySet())
            {
                Body result = entry.getValue().findBodyById(id);
                if(result != null) return result;
            }
        }
        return null;
    }

    public Body addChild(Body body)
    {
        children.put(body.id, body);
        body.parent = this;
        return this;
    }

    public Body removeChild(Body body)
    {
        children.remove(body.id);
        body.onDestroy();
        body.parent = null;
        return this;
    }

    public Body removeChild(String id)
    {
        children.remove(id);
        children.get(id).onDestroy();
        children.get(id).parent = null;
        return this;
    }
}
