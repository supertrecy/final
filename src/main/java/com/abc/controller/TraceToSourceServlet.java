package com.abc.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**@author yh
 * Servlet implementation class TraceToSourceServlet
 */
@WebServlet(description = "no data input, but output json", urlPatterns = { "/TraceToSourceServlet" })
public class TraceToSourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TraceToSourceServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
