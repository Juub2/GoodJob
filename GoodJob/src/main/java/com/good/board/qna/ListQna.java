package com.good.board.qna;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.good.board.repository.QnaBoardDAO;

@WebServlet("/user/qna/listqna.do")
public class ListQna extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		QnaBoardDAO dao = new QnaBoardDAO();
		int listCount = dao.listCount();
		
		
		req.setAttribute("listCount", listCount);
		
		
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/qna/qnalist.jsp");
		dispatcher.forward(req, resp);
		
	}
	
}
