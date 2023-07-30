package com.example.melll.thirdparty;


import com.example.melll.thirdparty.component.MailComponent;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
class MellThirdPartyApplicationTests {
//	@Autowired
//	private OSSClient ossClient;
	@Resource
	private MailComponent smsComponent;



	@Test
	void contextLoads() throws Exception{

//		String objectName = "C:\\Users\\84484\\Pictures\\5.png";
//		// 创建OSSClient实例。
//
//
//			String content = "Hello OSS";
//			ossClient.putObject("movie-sa", objectName, new ByteArrayInputStream(content.getBytes()));



}
	@Test
	void minio(){
		try {

			MinioClient minioClient = MinioClient.builder().endpoint("http://192.168.32.151:9000")
					.credentials("b3sfh7OSClkOdOvf", "Klp0webqmQWUj5mD3ZNgNGvFYUT8Dmbc").build();
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("response-content-type", "application/json");
			String product = minioClient.getPresignedObjectUrl(
					GetPresignedObjectUrlArgs.builder()
							.method(Method.PUT) //这里必须是PUT，如果是GET的话就是文件访问地址了。如果是POST上传会报错.
							.bucket("public")
							.object("1.png")
							.expiry(60 * 60 * 24)
							.extraQueryParams(reqParams)
							.build());
			System.out.println("前端直传需要的url地址:"+product); // 前端直传需要的url地址
		} catch (Exception e) {
			System.out.println("Error occurred: " + e);
		}


	}
	@Test
	void Email(){
		smsComponent.sendSimpleMail("844840392@qq.com","Test","hello");
	}
}
