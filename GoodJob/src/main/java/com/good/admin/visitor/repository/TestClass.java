package com.good.admin.visitor.repository;

import java.time.LocalDate;

public class TestClass {
	
	public static void main(String[] args) {
		
		
		LocalDate nowDate = LocalDate.now();

		VisitorDAO dao = new VisitorDAO();
		dao.insertTblVisitor(nowDate);

	}//main

}
