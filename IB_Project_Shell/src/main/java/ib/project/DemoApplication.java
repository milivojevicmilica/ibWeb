package ib.project;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ib.rest.DemoController;

@SpringBootApplication
public class DemoApplication {

	private static String DATA_DIR_PATH;
	private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);
	
	static {
		ResourceBundle rb = ResourceBundle.getBundle("application");
		DATA_DIR_PATH = rb.getString("dataDir");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		//create files folder in target/classes
		new File(DemoController.class.getProtectionDomain().getCodeSource().getLocation().getPath() + File.separator + DATA_DIR_PATH).mkdirs();
	}
}
 