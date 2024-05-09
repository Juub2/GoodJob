package com.good.company.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.good.company.model.RecruitDTO;
import com.test.util.DBUtil;

public class RecruitDAO {
	private Connection conn;
	private Statement stat;
	private PreparedStatement pstat;
	private ResultSet rs;

	public RecruitDAO() {
		this.conn = DBUtil.open();
	}
	
	public void close() {
		
		try {
			
			this.conn.close();
			
		} catch (Exception e) {
			System.out.println("RecruitDAO conn close 실패");
			e.printStackTrace();
		}
		
	}

	public String getNextSeq() {
		try {
			String sql = "select seqRecruit.nextVal as seq from dual";

			stat = conn.createStatement();
			rs = stat.executeQuery(sql);

			if (rs.next()) {
				return rs.getString("seq");
			}

		} catch (Exception e) {
			System.out.println("RecruitDAO.getNextSeq");
			e.printStackTrace();
		}
		return null;
	}

	
	public ArrayList<RecruitDTO> RecruitList() {
		try {
			String sql = "select * from tblRecruit";

			ArrayList<RecruitDTO> list = new ArrayList<>();

			while (rs.next()) {
				RecruitDTO dto = new RecruitDTO();
				dto.setRcrt_seq(rs.getString("rcrt_seq"));

				list.add(dto);
			}
			return list;

		} catch (Exception e) {
			System.out.println("RecruitList");
			e.printStackTrace();
		}
		return null;
	}

	public int addJobRcrt(HashMap<String, String> map) {
		try {
			String sql = "insert into tblJobRcrt (rcrt_seq, job_seq) values (?,?)";

			pstat = conn.prepareStatement(sql);
			pstat.setString(1, map.get("seq"));
			pstat.setString(2, map.get("code"));

			pstat.executeUpdate();
			pstat.close();
			return 1;

		} catch (Exception e) {
			System.out.println("RecruitDAO.addJobRcrt");
			e.printStackTrace();
		}
		return 0;
	}

	public int addRecruit(RecruitDTO dto) {
		try {
			String sql = "insert into tblRecruit (RCRT_SEQ, RCRT_NAME, RCRT_LINK, STARTDATE,ENDDATE,MIN_CAREER,MAX_CAREER,EDU_SEQ, SALARY_SEQ, CP_SEQ) values (?,?,?,?,?,?,?,?,?,?)";

			pstat = conn.prepareStatement(sql);
			pstat.setString(1, dto.getRcrt_seq());
			pstat.setString(2, dto.getRcrt_name());
			pstat.setString(3, dto.getRcrt_link());
			pstat.setString(4, dto.getStartdate());
			pstat.setString(5, dto.getEnddate());
			pstat.setString(6, dto.getMin_career());
			pstat.setString(7, dto.getMax_career());
			pstat.setString(8, dto.getEdu_seq());
			pstat.setString(9, dto.getSalary_seq());
			pstat.setString(10, dto.getCp_seq());

			pstat.executeUpdate();
			pstat.close();
			return 1;

		} catch (Exception e) {
			System.out.println("RecruitDAO.addJobRcrt");
			e.printStackTrace();
		}
		return 0;
	}


	/**
	 * 기업별 채용공고 목록 조회 메서드
	 * @param cp_seq
	 * @return
	 */
	public ArrayList<RecruitDTO> comRecruitList(String cp_seq) {
	    try {
	    	
	        LocalDate currentDate = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        String formatCurrentDate = currentDate.format(formatter);

	        String sql = "SELECT * FROM vwComRcrt WHERE cp_seq=? AND enddate >= ?";
	        pstat = conn.prepareStatement(sql);
	        pstat.setString(1, cp_seq);
	        pstat.setString(2, formatCurrentDate);

	        rs = pstat.executeQuery();
	        
	        ArrayList<RecruitDTO> comRecruitList = new ArrayList<>();

	        while (rs.next()) {

	            RecruitDTO dto = new RecruitDTO();
	            dto.setRcrt_seq(rs.getString("rcrt_seq"));
	            dto.setCp_seq(rs.getString("cp_seq"));
	            dto.setEdu_seq(rs.getString("edu_seq"));
	            dto.setMax_career(rs.getString("max_career"));
	            dto.setMin_career(rs.getString("min_career"));
	            dto.setRcrt_name(rs.getString("rcrt_name"));
	            dto.setStartdate(rs.getString("startdate"));
	            dto.setEnddate(rs.getString("enddate"));
	            dto.setSalary_seq(rs.getString("salary_seq"));
	            dto.setRcrt_link(rs.getString("rcrt_link"));
	            dto.setSalary_type(rs.getString("salary_type"));
	            dto.setEdu_type(rs.getString("edu_type"));
	            dto.setCp_address(rs.getString("cp_address"));
	            comRecruitList.add(dto);
	        }
	        return comRecruitList;

	    } catch (Exception e) {
	        System.out.println("RecruitDAO.comRecruitList");
	        e.printStackTrace();
	    }
	    return null;
	}


	public int getTotalCount(HashMap<String, Object> map) {
		try {

			String where = "";

			if (map.get("search").equals("y")) {
				// 검색어 조건 추가
				if (map.get("word") != null && !((String)map.get("word")).isEmpty()) {
					where += "cp_name LIKE '%' || ? || '%' AND ";
				}

				// 학력 조건 추가
				if (map.get("edu_seq") != null && !((String)map.get("edu_seq")).isEmpty()) {
					where += "edu_seq = ? AND ";
				}

				// 경력 조건 추가
				if (map.get("career") != null && !((String)map.get("career")).isEmpty()) {
					where += "min_career <= ? AND ";
				}

				// 연봉 조건 추가
				if (map.get("salary_seq") != null && !((String)map.get("salary_seq")).isEmpty()) {
					where += "salary_seq >= ? AND ";
				}

				// 지역 조건 추가
				if (map.get("cp_address") != null && ((String[])map.get("cp_address")).length > 0) {
				    String[] addressArray = (String[])map.get("cp_address");
				    
				    if (addressArray.length > 0) {
				        where += "(";
				        for (int i = 0; i < addressArray.length; i++) {
				            if (i > 0) {
				                where += " OR ";
				            }
				            where += "cp_address LIKE '%' || ? || '%'";
				        }
				        where += ") AND ";
				    }
				}


				// 맨 끝에 AND 제거
				if (where.endsWith(" AND ")) {
					where = where.substring(0, where.length() - 5);
				}
			}

			String sql = String.format("SELECT count(*) as cnt FROM vwRecruitList%s", where.isEmpty() ? "" : " WHERE " + where);
			pstat = conn.prepareStatement(sql);

			int parameterIndex = 1;
			if (map.get("search").equals("y")) {
			    if (map.get("word") != null && !((String)map.get("word")).isEmpty()) {
			        pstat.setString(parameterIndex++, "%" + map.get("word") + "%");
			    }
			    if (map.get("edu_seq") != null && !((String)map.get("edu_seq")).isEmpty()) {
			        pstat.setString(parameterIndex++, (String)map.get("edu_seq"));
			    }
			    if (map.get("career") != null && !((String)map.get("career")).isEmpty()) {
			        pstat.setString(parameterIndex++, (String)map.get("career"));
			    }
			    if (map.get("salary_seq") != null && !((String)map.get("salary_seq")).isEmpty()) {
			        pstat.setString(parameterIndex++, (String)map.get("salary_seq"));
			    }
			    if (map.get("cp_address") != null && ((String[])map.get("cp_address")).length > 0) {
			        String[] addressArray = (String[])map.get("cp_address");
			        for (String address : addressArray) {
			        	address = address.replace("전체", "").trim();
			            pstat.setString(parameterIndex++, address);
			        }
			    }
			}

			rs = pstat.executeQuery();
			
			int result = 0;

			if(rs.next()) {
				
				result = rs.getInt("cnt");
				
			}
			
			return result;
			
		} catch (Exception e) {
			System.out.println("채용공고 갯수 로드 실패");
			e.printStackTrace();
		}
		return 0;
	}

	public ArrayList<RecruitDTO> getList(HashMap<String, Object> map) {
	    try {
	        String where = "";

	        if (map.get("search").equals("y")) {
	            // 검색어 조건 추가
	            if (map.get("word") != null && !((String)map.get("word")).isEmpty()) {
	                where += "cp_name LIKE '%' || ? || '%' AND ";
	            }

	            // 학력 조건 추가
	            if (map.get("edu_seq") != null && !((String)map.get("edu_seq")).isEmpty()) {
	                where += "edu_seq = ? AND ";
	            }

	            // 경력 조건 추가
	            if (map.get("career") != null && !((String)map.get("career")).isEmpty()) {
	                where += "min_career <= ? AND ";
	            }

	            // 연봉 조건 추가
	            if (map.get("salary_seq") != null && !((String)map.get("salary_seq")).isEmpty()) {
	                where += "salary_seq >= ? AND ";
	            }

	            // 지역 조건 추가
	            if (map.get("cp_address") != null && ((String[])map.get("cp_address")).length > 0) {
	                String[] addressArray = (String[])map.get("cp_address");
	                
	                if (addressArray.length > 0) {
	                    where += "(";
	                    for (int i = 0; i < addressArray.length; i++) {
	                        if (i > 0) {
	                            where += " OR ";
	                        }
	                        where += "cp_address LIKE '%' || ? || '%'";
	                    }
	                    where += ") AND ";
	                }
	            }

	            // 맨 끝에 AND 제거
	            if (where.endsWith(" AND ")) {
	                where = where.substring(0, where.length() - 5);
	            }
	        }

	        String sql = String.format("SELECT * FROM (SELECT ROWNUM AS rnum, a.* FROM (SELECT * FROM vwRecruitList %s ORDER BY %s) a) WHERE rnum BETWEEN %s AND %s", where.isEmpty() ? "" : " WHERE " + where, map.get("sort"), map.get("begin"), map.get("end"));
	        
	        System.out.println(sql);
	        pstat = conn.prepareStatement(sql);

	        int parameterIndex = 1;
	        if (map.get("search").equals("y")) {
	            if (map.get("word") != null && !((String)map.get("word")).isEmpty()) {
	                pstat.setString(parameterIndex++, "%" + map.get("word") + "%");
	            }
	            if (map.get("edu_seq") != null && !((String)map.get("edu_seq")).isEmpty()) {
	                pstat.setString(parameterIndex++, (String)map.get("edu_seq"));
	            }
	            if (map.get("career") != null && !((String)map.get("career")).isEmpty()) {
	                pstat.setString(parameterIndex++, (String)map.get("career"));
	            }
	            if (map.get("salary_seq") != null && !((String)map.get("salary_seq")).isEmpty()) {
	                pstat.setString(parameterIndex++, (String)map.get("salary_seq"));
	            }
	            if (map.get("cp_address") != null && ((String[])map.get("cp_address")).length > 0) {
	                String[] addressArray = (String[])map.get("cp_address");
	                for (String address : addressArray) {
	                    address = address.replace("전체", "").trim();
	                    pstat.setString(parameterIndex++, address);
	                }
	            }
	        }

	        rs = pstat.executeQuery();

	        ArrayList<RecruitDTO> list = new ArrayList<>();

			while (rs.next()) {
				RecruitDTO dto = new RecruitDTO();
				dto.setCp_name(rs.getString("cp_name"));
				dto.setRcrt_name(rs.getString("rcrt_name"));
				dto.setCp_address(cutAddress(rs.getString("cp_address")));
				dto.setEnddate(formatEndDate(rs.getString("enddate")));
				dto.setRcrt_link(rs.getString("rcrt_link"));
				dto.setHire_avr_salary(rs.getString("hire_avr_salary"));
				dto.setRcrt_seq(rs.getString("rcrt_seq"));
				dto.setEdu_seq(rs.getString("edu_seq"));
				dto.setEdu_type(rs.getString("edu_type"));
				dto.setMin_career(rs.getString("min_career"));
				dto.setMax_career(rs.getString("max_career"));
				dto.setSalary_type(rs.getString("salary_type"));
				ArrayList<String> jobname = foundJob(rs.getString("rcrt_seq"));
				dto.setJob_name(jobname);
				list.add(dto);
			}

			return list;

		} catch (Exception e) {
			System.out.println("채용공고 목록 로드 실패");
			e.printStackTrace();
		}

		return null;
	}

	private String cutAddress(String cp_address) {
		String address = cp_address;
		if(address.contains("서울특별시")) {
		    address = address.replaceAll("서울특별시", "서울");
		}
		int firstSpaceIndex = address.indexOf(' '); // 첫 번째 공백의 위치
		int secondSpaceIndex = address.indexOf(' ', firstSpaceIndex + 1); // 두 번째 공백의 위치
		address = address.substring(0, secondSpaceIndex);
		return address;
	}
	

	private String formatEndDate(String endDateString) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd(E)", Locale.KOREAN);

		try {
			Date endDate = inputFormat.parse(endDateString);
			return outputFormat.format(endDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private ArrayList<String> foundJob(String rcrt_seq) {
	    ArrayList<String> list = new ArrayList<>();

	    try (Connection conn2 = DBUtil.open();
	         PreparedStatement pstat2 = conn2.prepareStatement("select job_name from tblJobRcrt jr inner join tblJob j on jr.job_seq = j.job_seq where jr.rcrt_seq = ?")) {

	        pstat2.setString(1, rcrt_seq);

	        try (ResultSet rs2 = pstat2.executeQuery()) {
	            while (rs2.next() && list.size() < 5) {
	                list.add(rs2.getString("job_name"));
	            }
	        }

	    } catch (Exception e) {
	        System.out.println("직무이름 반환 실패");
	        e.printStackTrace();
	    }

	    return list;
	}
	
	
	public ArrayList<String> comJob(String cp_seq) {
	    ArrayList<String> jlist = new ArrayList<>();

	    try (Connection conn = DBUtil.open();
	         
	    	PreparedStatement pstat = conn.prepareStatement("select distinct (job_name) from tblCompany c inner join tblRecruit r  on c.cp_seq = r.cp_seq inner join tblJobRcrt jr on r.rcrt_seq = jr.rcrt_seq inner join tblJob j on jr.job_seq = ?")) {

	        pstat.setString(1, cp_seq);

	        try (ResultSet rs = pstat.executeQuery()) {
	            while (rs.next()) {
	                jlist.add(rs.getString("job_name"));
	            }
	        }

	    } catch (Exception e) {
	        System.out.println("직무이름 반환 실패");
	        e.printStackTrace();
	    }

	    return jlist;
	}
	
	
	
	
}
