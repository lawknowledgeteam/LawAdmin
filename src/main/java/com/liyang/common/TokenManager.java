package com.liyang.common;


import com.liyang.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TokenManager {
    private static TokenManager me = new TokenManager();
    
    private Map<String, User> tokens;
    //private Map<String, String> userToken;

    public TokenManager() {
        tokens = new ConcurrentHashMap<String, User>();
        //userToken = new ConcurrentHashMap<String, String>();
    }

    /**
     * 获取单例对象
     * @return
     */
    public static TokenManager getMe() {
        return me;
    }

    /**
     * 验证token
     * @param token
     * @return
     */
    public User validate(String token) {
    	User objUser= tokens.get(token);
        return objUser;
    }

    /**
     * 生成token值
     * @param user
     * @return
     */
    public String generateToken(User user) {
        //String token = TokenUtil.generateToken();
        String token = RandomUtils.randomCustomUUID().concat(RandomUtils.randomString(6));;
        //userToken.put(user.getStr("UserID"), token);
        tokens.put(token, user);
        return token;
    }
    
    public boolean removeToken(String token) {
        User objUser=tokens.remove(token);
        if(objUser!=null)
           return true;
        else
        	return false;
    }
}
