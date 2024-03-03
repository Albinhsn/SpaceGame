package se.liu.albhe576.project;

public abstract class UI {
    abstract UIState render(InputState inputState, Renderer renderer, long window, int score, int hp);
}
