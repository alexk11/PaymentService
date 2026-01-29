package com.iprody.api;

import com.iprody.async.message.Message;

public interface AsyncListener<T extends Message> {
    /**
     * Вызывается для каждого нового входящего сообщения.
     *
     * @param message сообщение для обработки
     */
    void onMessage(T message);
}
