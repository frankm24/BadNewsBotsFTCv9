package com.badnewsbots.auto;

public final class RunCodeBlockTask implements AutonomousTask {
    public interface CodeBlock {
        void run();
    }

    private final CodeBlock codeBlock;
    private boolean completed = false;

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void init() {
        codeBlock.run();
        completed = true;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void updateTask(double deltaTime) {

    }

    public RunCodeBlockTask(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }
}
