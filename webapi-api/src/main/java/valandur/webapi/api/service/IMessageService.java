package valandur.webapi.api.service;

import valandur.webapi.api.message.Message;

public interface IMessageService {

    boolean sendMessage(Message msg);
}
