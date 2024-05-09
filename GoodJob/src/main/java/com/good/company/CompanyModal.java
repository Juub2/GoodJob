package com.good.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.good.company.model.CompanyDTO;
import com.good.company.repository.CompanyDAO;

@WebServlet("/user/company/cp_selectModal.do")
public class CompanyModal extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		

		//페이징
		String page = req.getParameter("page");
		
		int nowPage = 0;//현재페이지
		int totalCount=0;//총 게시물 수
		int pageSize=10;//출력 게시물 수
		int totalPage=0;//총페이지
		int begin=0; //페이지 시작
		int end=0;	//페이지 끝
		int n = 0; //페이지바
		int loop = 0;
		int blockSize = 5;
		
		if (page == null || page.equals("")) {
			nowPage = 1;
		} else {
			nowPage = Integer.parseInt(page);
		}
		
		begin = ((nowPage-1)*pageSize)+1;
		end = begin + pageSize -1;
		
		

		// 검색 기록 가져오기
		String column = (req.getParameter("column") != null ? req.getParameter("column") : "");
		String word = (req.getParameter("word") != null ? req.getParameter("word") : "");
		String search = "n"; // 목록보기(n), 검색하기(y)

		if ((column == null && word == null) || word.equals("")) {
			search = "n";
		} else {
			search = "y";
		}
		
		
		String hiring = req.getParameter("hiring");
		if(hiring != null) {
			hiring = "y";
		}else {
			hiring = "n";
		}
		
		
		HashMap<String, String> map = new HashMap<>();

		map.put("search", search);
		map.put("column", column);
		map.put("word", word);
		map.put("begin", begin + "");
		map.put("end", end + "");
		map.put("hiring",hiring);
		
		
		HttpSession session = req.getSession();
		
		//목록 출력
		CompanyDAO dao = new CompanyDAO();
		ArrayList<CompanyDTO> comListInfo = dao.comListInfo(map);
		
		String unit="";
		
		for (CompanyDTO dto : comListInfo) {
			
			//주소 
			String address = dto.getCp_address();
			if(address.contains("서울특별시")) {
				address = address.replaceAll("서울특별시", "서울");
			}
			int firstSpaceIndex = address.indexOf(' '); // 첫 번째 공백의 위치
		    int secondSpaceIndex = address.indexOf(' ', firstSpaceIndex + 1); // 두 번째 공백의 위치
		    address = address.substring(0, secondSpaceIndex);
		    dto.setCp_address(address);
	
		}
		
		//총게시물수
		totalCount = dao.countCompanys();
		int searchTotalCount = dao.searchCompanyCount(map);			
		totalPage = (int) Math.ceil((double) searchTotalCount / pageSize);
		

		String[] cp = req.getParameterValues("compareCp");

		if (cp != null) {
		    for (int i = 0; i < cp.length; i++) {
		        if (cp[i] == null) {
		            cp[i] = ""; // null을 빈 문자열로 변경
		        }
		    }
		} else {
		    cp = new String[0]; // "compareCp" 매개변수가 없는 경우 빈 배열 생성
		}

		String tag1 = (cp.length > 0) ? (String)cp[0] : "";
		String tag2 = (cp.length > 1) ? (String)cp[1] : "";
		String tag3 = (cp.length > 2) ? (String)cp[2] : "";


		map.put("tag1", tag1);
		map.put("tag2", tag2);
		map.put("tag3", tag3);


		
		// 페이지 바 작업
		StringBuilder sb = new StringBuilder();


		loop = 1; // 루프 변수(10바퀴)
		n = ((nowPage - 1) / blockSize) * blockSize + 1; // 페이지 번호 역할

		// 이전 5페이지
		if (n == 1) {
			sb.append(
					"<li class='page-item z-custom'><a class='page-link' href='#!'><span class='material-symbols-outlined paging-icon z-custom'>keyboard_double_arrow_left</span></a></li>");
			sb.append(
					"<li class='page-item z-custom'><a class='page-link' href='#!'><span class='material-symbols-outlined paging-icon z-custom'>navigate_before</span></a></li>");
		} else if (n <= 5) {
			sb.append(
					"<li class='page-item z-custom'><a class='page-link' href='#!'><span class='material-symbols-outlined paging-icon z-custom'>keyboard_double_arrow_left</span></a></li>");
			sb.append(String.format(
					"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s'><span class='material-symbols-outlined paging-icon z-custom'>navigate_before</span></a></li>",
					n - 1, hiring, word));
		} else if (n > 5) {
			sb.append(String.format(
					"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s'><span class='material-symbols-outlined paging-icon z-custom'>keyboard_double_arrow_left</span></a></li>",
					n - 5, hiring, word));
			sb.append(String.format(
					"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s'><span class='material-symbols-outlined paging-icon z-custom'>navigate_before</span></a></li>",
					n - 1, hiring, word));
		}

		while (!(loop > blockSize || n > totalPage)) {
			if (n == nowPage) {

				sb.append(String.format(
						"<li class='page-item z-custom'><a class='page-link' href='#!' style='background-color: #6777EE; color: #FFF; border-color: #6777EE;'>%d</a></li>",
						n));
			} else {
				sb.append(String.format(
						"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s'>%d</a></li>",
						n, hiring, word, n));
			}
			loop++;
			n++;
		}

		// 다음 5페이지
		if (n >= totalPage) {
			sb.append(
					"<li class='page-item z-custom'><a class='page-link' href='#!'><span class='material-symbols-outlined paging-icon z-custom'>navigate_next</span></a></li>");
			sb.append(
					"<li class='page-item z-custom'><a class='page-link' href='#!'><span class='material-symbols-outlined paging-icon z-custom'>keyboard_double_arrow_right</span></a></li>");
		} else if (n >= totalPage - 5) {
			sb.append(String.format(
					"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s&tag1=%s'><span class='material-symbols-outlined paging-icon z-custom'>navigate_next</span></a></li>",
					n, hiring, word, tag1));
			sb.append(
					"<li class='page-item z-custom'><a class='page-link' href='#!'><span class='material-symbols-outlined paging-icon z-custom'>keyboard_double_arrow_right</span></a></li>");
		} else {
			sb.append(String.format(
					"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s'><span class='material-symbols-outlined paging-icon z-custom'>navigate_next</span></a></li>",
					n, hiring, word));
			sb.append(String.format(
					"<li class='page-item z-custom'><a class='page-link' href='/good/user/company/cp_selectModal.do?page=%d&hiring=%s&word=%s'><span class='material-symbols-outlined paging-icon z-custom'>keyboard_double_arrow_right</span></a></li>",
					n + 5, hiring, word));
		}
		
		
		req.setAttribute("comListInfo" , comListInfo);
		req.setAttribute("map" , map); //페이지 begin. end hiring
		
		
		
		//페이징
		req.setAttribute("nowPage", nowPage); //페이지 번호
		req.setAttribute("totalCount", totalCount);
		req.setAttribute("searchTotalCount", searchTotalCount);
		req.setAttribute("totalPage", totalPage); //페이지 번호
		req.setAttribute("pagebar", sb.toString()); //페이지 바 작업
		


		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/user/company/cp_selectModal.jsp");
		dispatcher.forward(req, resp);

	}

}