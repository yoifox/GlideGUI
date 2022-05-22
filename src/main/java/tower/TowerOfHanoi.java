package tower;

import core.Scene;
import core.body.ColorValue;
import core.body.Entity;
import core.body.Material;
import core.body.Mesh;
import core.body.light.DirectionalLight;
import core.utils.Transformation;

import java.util.ArrayList;
import java.util.List;

public class TowerOfHanoi extends Scene
{
    final float UP_TIME = 0.1f, MOVE_TIME = 0.5f, DOWN_TIME = 0.1f;
    final int SIZE = 5;
    List<Entity> disks = new ArrayList<>();
    Entity a, b, c;
    float maxA = 0, maxB = 0, maxC = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Mesh cylinder = objectLoader.loadMesh(getClass(), "/shapes/cylinder.fbx");
        a = new Entity(cylinder, new Material(ColorValue.COLOR_WHITE, null, null));
        a.scaleY = 6;
        b = new Entity(cylinder, new Material(ColorValue.COLOR_WHITE, null, null));
        b.scaleY = 6;
        c = new Entity(cylinder, new Material(ColorValue.COLOR_WHITE, null, null));
        c.scaleY = 6;
        b.x = 20;
        c.x = 40;

        ColorValue[] colors = new ColorValue[] {ColorValue.COLOR_BLUE, ColorValue.COLOR_GREEN, ColorValue.COLOR_RED, ColorValue.COLOR_PURPLE, ColorValue.COLOR_YELLOW};
        for(int i = 0; i < SIZE; i++)
        {
            Entity disk = new Entity(cylinder, new Material(colors[i], null, null));
            disk.scale((SIZE - i), 0, (SIZE - i));
            disk.y = i * disk.getBoundingBox().height + 0.1f;
            disks.add(disk);
            addBody(disk);
        }

        addBody(a);
        addBody(b);
        addBody(c);
        directionalLight = new DirectionalLight(90, 10, 10, 1, ColorValue.COLOR_WHITE);
        setWorldColor(0.5f, 0.5f, 0.5f, 1);
        maxA = SIZE * cylinder.boundingBox.height + 0.1f * SIZE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long) (1000 * (UP_TIME + MOVE_TIME + DOWN_TIME)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                towerOfHanoi(SIZE, a, c, b);
            }
        }).start();

        Mesh cube = objectLoader.loadMesh(getClass(), "/shapes/cube.fbx");
        Entity aCube = new Entity(cube, new Material(ColorValue.COLOR_WHITE, null, null));
        Entity bCube = new Entity(cube, new Material(ColorValue.COLOR_WHITE, null, null));
        Entity cCube = new Entity(cube, new Material(ColorValue.COLOR_WHITE, null, null));
        aCube.scale(4, 0, 4);
        bCube.scale(4, 0, 4);
        cCube.scale(4, 0, 4);
        aCube.move(a.x, -aCube.getBoundingBox().height - 0.1f, 0);
        bCube.move(b.x, -bCube.getBoundingBox().height - 0.1f, 0);
        cCube.move(c.x, -cCube.getBoundingBox().height - 0.1f, 0);
        addBody(aCube);
        addBody(bCube);
        addBody(cCube);

        Entity floor = new Entity(cube, new Material(ColorValue.COLOR_BLUE, null, null));
        floor.scale(50, 0, 50);
        floor.y = -aCube.getBoundingBox().height - floor.getBoundingBox().height - 0.1f;
        floor.x = b.x;
        addBody(floor);

        camera.z = 50;
        camera.x = b.x;
        camera.y = b.getBoundingBox().height;
        camera.rotationX = 20;
    }

    boolean animPlaying = false;
    Entity from, to, disk;
    float max, animTime = 0;
    @Override
    public void update(float delta) {
        super.update(delta);
        if(Float.isInfinite(delta)) //I should really just change it in glide
            delta = 0;
        Transformation.moveCamera(mouseInput, keyInput, camera, 0.4f);
        float totalTime = UP_TIME + MOVE_TIME + DOWN_TIME;
        if(animPlaying)
        {
            animTime += delta;
            if(animTime > totalTime)
            {
                animTime = 0;
                animPlaying = false;
                disk.setPosition(to.x, max - disk.getBoundingBox().height, to.z);
            }
            else if(animTime < UP_TIME)
            {
                float dy = from.getBoundingBox().height;
                disk.move(0, delta * (dy / (UP_TIME + MOVE_TIME)), 0);
            }
            else if(animTime < UP_TIME + MOVE_TIME && animTime > UP_TIME)
            {
                disk.y = from.getBoundingBox().height;
                float dx = to.x - from.x;
                disk.move(delta * (dx / (UP_TIME + MOVE_TIME)), 0, 0);
            }
            else if(animTime < totalTime && animTime > UP_TIME + MOVE_TIME)
            {
                disk.x = to.x;
                float dy = -(to.getBoundingBox().height - max);
                disk.move(0, delta * (dy / (UP_TIME + MOVE_TIME)), 0);
            }
            else
            {
                animTime = totalTime;
            }
        }
    }

    void towerOfHanoi(int n, Entity from, Entity to, Entity aux)
    {
        if (n > 0)
        {
            towerOfHanoi(n - 1, from, aux, to);

            Entity disk = disks.get(SIZE - n);

            if(from == a)
                maxA -= (disk.getBoundingBox().height + 0.1f);
            if(from == b)
                maxB -= (disk.getBoundingBox().height + 0.1f);
            if(from == c)
                maxC -= (disk.getBoundingBox().height + 0.1f);
            float max = 0;
            if(to == a) {
                maxA += (disk.getBoundingBox().height + 0.1f);
                max = maxA;
            }
            if(to == b) {
                maxB += (disk.getBoundingBox().height + 0.1f);
                max = maxB;
            }
            if(to == c) {
                maxC += (disk.getBoundingBox().height + 0.1f);
                max = maxC;
            }

            animPlaying = true;
            this.from = from;
            this.to = to;
            this.max = max;
            this.disk = disk;

            try {
                Thread.sleep((long) (1000 * (UP_TIME + MOVE_TIME + DOWN_TIME)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            disk.x = to.x;
            disk.y = max - disk.getBoundingBox().height;

            towerOfHanoi(n - 1, aux, to, from);
        }
    }
}
