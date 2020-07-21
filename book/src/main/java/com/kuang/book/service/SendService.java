package com.kuang.book.service;

import java.util.Map;

public interface SendService {
    public boolean send(String phoneNum, String templateCode, Map<String,Object> map);

}
