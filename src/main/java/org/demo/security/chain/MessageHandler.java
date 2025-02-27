package org.demo.security.chain;

public abstract class MessageHandler {
    private MessageHandler nextHandler;

    // 设置下一个处理器（链式调用）
    public void setNext(MessageHandler next) {
        this.nextHandler  = next;
    }
    public void next(MessageRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }

    // 统一入口方法
    public void handle(MessageRequest request) {
        if (canHandle(request)) {
            process(request);
        }
    }

    // 是否执行处理逻辑（抽象方法）
    protected abstract boolean canHandle(MessageRequest request);

    // 具体处理逻辑（抽象方法）
    protected abstract void process(MessageRequest request);
}
