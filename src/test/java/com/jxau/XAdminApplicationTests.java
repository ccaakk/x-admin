package com.jxau;

import com.jxau.common.utils.JwtUtil;
import com.jxau.sys.entity.User;
import com.jxau.sys.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class XAdminApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void contextLoads() {
        List<User> users = userMapper.selectList(null);
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    void test1(){
        Scanner scanner = new Scanner("zhangsan  lisi wangwu zhaoliu");
        while(scanner.hasNext()){
            String next = scanner.next();
            System.out.println(next);
//            String s = scanner.nextLine();
//            System.out.println(s);
        }
    }

    @Test
    public void testCreateJwt(){
        User user = new User();
        user.setUsername("张三");
        user.setPhone("12345678901");
        String token = jwtUtil.createToken(user);
        System.out.println(token);
    }

    @Test
    public void testParseJwt(){
        System.out.println(jwtUtil.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwYmEyMWNiZi1jODdhLTRiZTYtODI0MS01Y2RiMzM1NjRmNDQiLCJzdWIiOiJ7XCJwaG9uZVwiOlwiMTIzNDU2Nzg5MDFcIixcInVzZXJuYW1lXCI6XCLlvKDkuIlcIn0iLCJpc3MiOiJzeXN0ZW0iLCJpYXQiOjE2ODQ4MjA3ODMsImV4cCI6MTY4NDgyMjU4M30.jKogcvrTjE573VOelPCNDk7tURmNlS_o3uLV0QbMLqU"));
        System.out.println(jwtUtil.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwYmEyMWNiZi1jODdhLTRiZTYtODI0MS01Y2RiMzM1NjRmNDQiLCJzdWIiOiJ7XCJwaG9uZVwiOlwiMTIzNDU2Nzg5MDFcIixcInVzZXJuYW1lXCI6XCLlvKDkuIlcIn0iLCJpc3MiOiJzeXN0ZW0iLCJpYXQiOjE2ODQ4MjA3ODMsImV4cCI6MTY4NDgyMjU4M30.jKogcvrTjE573VOelPCNDk7tURmNlS_o3uLV0QbMLqU", User.class));

    }

}
