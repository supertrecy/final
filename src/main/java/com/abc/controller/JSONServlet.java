package com.abc.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * return response of json format
 */
@WebServlet("/flare.json")
public class JSONServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	public JSONServlet() {

	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String filePath = req.getServletContext().getRealPath("/flare.json");
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			StringBuilder sbuilder = new StringBuilder();
			String buffer = null;
			while ((buffer  = reader.readLine())!=null) {
				sbuilder.append(buffer);
			}
			resp.setContentType("application/json;charset=utf-8");
			resp.getWriter().write(sbuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	
}
