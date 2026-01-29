package com.iprody.api;

public interface AsyncListener<T extends Message> {
    /**
     * Вызывается для каждого нового входящего сообщения.
     *
     * @param message сообщение для обработки
     */
    void onMessage(T message);
}
