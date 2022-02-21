package top.caohongchuan.newsearch.config.redis;

import lombok.Data;

@Data
public class RedisCommonProperty {
    private String host;
    private int port;
    private int database;
    private String password;
}