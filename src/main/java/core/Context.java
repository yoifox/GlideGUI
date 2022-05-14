package core;

public interface Context
{
    void render(float delta);
    void updatePhysics(float delta);
    void init(Window window);
    void cleanup();
}
