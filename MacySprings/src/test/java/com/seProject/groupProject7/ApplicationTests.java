package com.seProject.groupProject7;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.junit.*;
import org.springframework.util.Assert;

@SpringBootTest
class ApplicationTests {
	@Autowired
	private UserController controller;

	@Test
	public void contexLoads() throws Exception {
		assertThat(controller).isNotNull();
		assert(true);
	}

	@Test
	public void testUserLogin() throws Exception {
		controller.addNewUser("me2", "thisIsAPassword", "me@you.com");
		assert(controller.handleLoginRequest("me2", "thisIsAPassword").contains("loginsuccess"));
		controller.removeUser("me2");
		Assert.isNull(controller.getUser("me2"), "Not deleted");

	}



	//Access to commandline. Might not be needed.
	void commandlineTest() {


		try {


			String s;
			Process p;
			String command;
			if (getOperatingSystem().contains("Windows")){
				command = "dir";
			}
			else{
				command = "ls -aF";
			}
			p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null) System.out.println("line: " + s);
			p.waitFor();
			System.out.println("exit: " + p.exitValue());
			p.destroy();
		}
		catch(Exception e) {
		}

	}

	public String getOperatingSystem() {
		String os = System.getProperty("os.name");
		// System.out.println("Using System Property: " + os);
		return os;
	}
}
