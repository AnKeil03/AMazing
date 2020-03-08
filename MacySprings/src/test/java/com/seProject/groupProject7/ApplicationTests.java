package com.seProject.groupProject7;

import com.seProject.groupProject7.login.Cryptography;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import org.springframework.util.Assert;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
		HttpServletResponse response = new HttpServletResponse() {
			@Override
			public void addCookie(Cookie cookie) {

			}

			@Override
			public boolean containsHeader(String name) {
				return false;
			}

			@Override
			public String encodeURL(String url) {
				return null;
			}

			@Override
			public String encodeRedirectURL(String url) {
				return null;
			}

			@Override
			public String encodeUrl(String url) {
				return null;
			}

			@Override
			public String encodeRedirectUrl(String url) {
				return null;
			}

			@Override
			public void sendError(int sc, String msg) throws IOException {

			}

			@Override
			public void sendError(int sc) throws IOException {

			}

			@Override
			public void sendRedirect(String location) throws IOException {

			}

			@Override
			public void setDateHeader(String name, long date) {

			}

			@Override
			public void addDateHeader(String name, long date) {

			}

			@Override
			public void setHeader(String name, String value) {

			}

			@Override
			public void addHeader(String name, String value) {

			}

			@Override
			public void setIntHeader(String name, int value) {

			}

			@Override
			public void addIntHeader(String name, int value) {

			}

			@Override
			public void setStatus(int sc) {

			}

			@Override
			public void setStatus(int sc, String sm) {

			}

			@Override
			public int getStatus() {
				return 0;
			}

			@Override
			public String getHeader(String name) {
				return null;
			}

			@Override
			public Collection<String> getHeaders(String name) {
				return null;
			}

			@Override
			public Collection<String> getHeaderNames() {
				return null;
			}

			@Override
			public String getCharacterEncoding() {
				return null;
			}

			@Override
			public String getContentType() {
				return null;
			}

			@Override
			public ServletOutputStream getOutputStream() throws IOException {
				return null;
			}

			@Override
			public PrintWriter getWriter() throws IOException {
				return null;
			}

			@Override
			public void setCharacterEncoding(String charset) {

			}

			@Override
			public void setContentLength(int len) {

			}

			@Override
			public void setContentLengthLong(long length) {

			}

			@Override
			public void setContentType(String type) {

			}

			@Override
			public void setBufferSize(int size) {

			}

			@Override
			public int getBufferSize() {
				return 0;
			}

			@Override
			public void flushBuffer() throws IOException {

			}

			@Override
			public void resetBuffer() {

			}

			@Override
			public boolean isCommitted() {
				return false;
			}

			@Override
			public void reset() {

			}

			@Override
			public void setLocale(Locale loc) {

			}

			@Override
			public Locale getLocale() {
				return null;
			}
		};
		controller.addNewUser("jkljkljkljkljkl", Cryptography.cipherMatchJS("poingasdflkj"), "me@you.com");
		assert(controller.handleLoginRequest("-1","jkljkljkljkljkl", Cryptography.cipherMatchJS("poingasdflkj"), response).contains("loginsuccess"));
		controller.removeUser("jkljkljkljkljkl");
		Assert.isNull(controller.getUser("jkljkljkljkljkl"), "Not deleted");

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
