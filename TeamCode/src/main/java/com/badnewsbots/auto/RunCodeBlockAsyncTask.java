package com.badnewsbots.auto;

public final class RunCodeBlockAsyncTask implements AutonomousTask {
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
        Thread thread = new Thread(() -> {
            codeBlock.run();
        });
        thread.start();
        completed = true;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void updateTask(double deltaTime) {

    }

    public RunCodeBlockAsyncTask(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }
}
