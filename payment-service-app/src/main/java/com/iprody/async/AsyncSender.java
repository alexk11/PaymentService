package com.iprody.async;

import com.iprody.async.message.Message;

public interface AsyncSender<T extends Message> {
    /**
     * Отправляет сообщение.
     *
     * @param message сообщение для отправки
     */
    void send(T message);
}
