package swx.springboot.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecuredPasswordGenerator
{

	public static void main(String[] args)
	{
		String uid = "jetty";
		String pwd = "spboot";
		if (args.length >= 2) {
			uid = args[0];
			pwd = args[1];
			System.out.println(uid + ':' + pwd);
		}
		
        var encoder = new BCryptPasswordEncoder();
                
        String encodedPassword = encoder.encode(pwd);       
        System.out.println(encodedPassword);
        
        byte[] bytes = (uid + ":" + encodedPassword).getBytes(StandardCharsets.UTF_8);     
        System.out.println(Base64.getEncoder().encodeToString(bytes));
	}

}
