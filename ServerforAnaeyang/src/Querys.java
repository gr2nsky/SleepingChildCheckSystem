import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Querys {
	
	//토큰전송을 위한 쿼리
	public String getNotificationInfo(Connection conn, String userid) {
		PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
        String sql = "SELECT token, ride, name FROM student WHERE xid = ?"; 
        String result = null;
        
        try {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	        rs = pstmt.executeQuery();
	         if(rs.next()) {
	        	 result = rs.getString("name")+","+rs.getString("token")+","+rs.getString("ride");
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	//Parent가 최초 접속시에 전달해야하는 정보
	public String getParentField(Connection conn, String userid) {
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM student WHERE xid = ?"; 
		ResultSet rs = null;
		
		PreparedStatement pstmt2 = null;
        String sql2 = "SELECT * FROM bus WHERE busNum = ?"; 
	    ResultSet rs2 = null;
	    
	    StringBuffer result = new StringBuffer();
        String busNum = null;
        try {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	        rs = pstmt.executeQuery();
	         if(rs.next()) {
	        	 busNum = rs.getString("student_busNum");
	        	 result.append(rs.getString("student_busNum")+"#"+rs.getString("ride")+"#"+rs.getString("name")+"#");
	        	 if(rs.getInt("ride") == 0) {
	        		 result.append(rs.getString("takeoff_time"));
	        	 }
	        	 else {
	        		 result.append(rs.getString("ride_time"));
	        	 }
	        	 pstmt.close();
	         }
	         pstmt2 = conn.prepareStatement(sql2);
	         pstmt2.setString(1, busNum);
	         rs2 = pstmt2.executeQuery();
	         if(rs2.next()) {
	        	 result.append("#"+rs2.getString("name")+"#"+rs2.getString("phone")+"#"+rs2.getString("location"));
	        	 pstmt2.close();
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		}
        System.out.println("getParentField : "+result.toString());
		return result.toString();
	}
	
	//Driver가 최초 접속시에 전달해야하는 정보
	public String getDriverField(Connection conn, String userid) {
		System.out.println("getDriverField메서드 작동");
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM bus WHERE xid = ?"; 
		ResultSet rs = null;
		
		PreparedStatement pstmt2 = null;
        String sql2 = "SELECT * FROM student WHERE student_busNum = ?"; 
	    ResultSet rs2 = null;
	    
	    String busNum = null;
	    ArrayList<String> name = new ArrayList<String>();
	    ArrayList<Integer> ride = new ArrayList<Integer>();
	    StringBuffer result = new StringBuffer();
	    try {
		    pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	        rs = pstmt.executeQuery();
	        if(rs.next()) {
		        busNum = rs.getString("busNum");
		    }
	        pstmt.close();
	        
	        pstmt2 = conn.prepareStatement(sql2);
	        pstmt2.setString(1,busNum);
	        rs2 = pstmt2.executeQuery();
	         while(rs2.next()) {
	        	 //따로 받아 arraylist에 저장하지만, index는 동일하기때문에 문제가 없다.
	             name.add(rs2.getString("name"));//아이들 이름
	             ride.add(rs2.getInt("ride"));//차량 탑승여부            
	          }//arrayList이름대로 뒤에 값 바꿔주기
	         rs2.close();
	         
	         result.append(busNum+"#");
	         for (int i = 0; i < name.size(); i++) {
	        	 result.append(name.get(i));
	        	 if(i != (name.size()-1))
	        		 result.append(",");
	         }
	         result.append("#");
	         for (int i = 0; i < ride.size(); i++) {
	        	 result.append(ride.get(i));
	        	 if(i != (ride.size()-1))
	        		 result.append(",");
	         }
	    	
	    }catch(SQLException e) {
	    	e.printStackTrace();
	    }
		System.out.println(result.toString());
		return result.toString();
	}
	//driver의 위치정보를 갱신
	public void LocationSet(Connection conn, String point, String userid) {
		System.out.println("LocationSet메서드 작동"+userid+"의 location이"+point);
		PreparedStatement pstmt = null;
		String sql = "UPDATE bus SET location=? WHERE xid=?"; 
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,point);
			pstmt.setString(2,userid);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}               
	}
	//driver에 탑승중인 아이의 부모id를 리턴한다.
	public ArrayList<String> parentForChildInBus(Connection conn, String userid) {
		PreparedStatement pstmt = null;
		String sql = "SELECT * FROM bus WHERE xid = ?"; 
		ResultSet rs = null;
		
		PreparedStatement pstmt2 = null;
        String sql2 = "SELECT * FROM student WHERE student_busNum = ?"; 
	    ResultSet rs2 = null;
	    
	    String busNum = null;
	    ArrayList<String> xid = new ArrayList<String>();
	    ArrayList<Integer> ride = new ArrayList<Integer>();
	    StringBuffer result = new StringBuffer();
	    ArrayList<String> riding = new ArrayList<>();
	    try {
		    pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	        rs = pstmt.executeQuery();
	        if(rs.next()) {
		        busNum = rs.getString("busNum");
		    }
	        pstmt.close();
	        
	        pstmt2 = conn.prepareStatement(sql2);
	        pstmt2.setString(1,busNum);
	        rs2 = pstmt2.executeQuery();
	         while(rs2.next()) {
	        	 xid.add(rs2.getString("xid"));
	             ride.add(rs2.getInt("ride"));        
	          }
	         rs2.close();
	         for (int i = 0; i < xid.size(); i++) {
	             if (ride.get(i) == 1) {
	            	 riding.add(xid.get(i));
	             }
	         }
	    }catch(SQLException e) {
	    	e.printStackTrace();
	    }
		System.out.println(result.toString());

		return riding;
	}
	//student의 ride 상태를 변경한다. 혹시 몰라서 쿼리 남겨놓았음.
	public void chaneRideStudent(Connection conn, String token, String ride) {
		PreparedStatement pstmt = null;
		String sql = "UPDATE studen SET ride=? WHERE token=?"; 
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1,Integer.parseInt(ride));
			pstmt.setString(2,token);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
		
	}
	//token을 가진 학생의 탑승상태가 변경됬음을 알린다 - 부모
	public String changeRideParent(Connection conn, String token) {
		System.out.println("changeRideParent 가동");
		PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
        String sql = "SELECT xid FROM student WHERE token = ?"; 
        String result = null;
        
        try {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, token);
	        rs = pstmt.executeQuery();
	         if(rs.next()) {
	        	 result = rs.getString("xid");
	        	 System.out.println("changeRideParent : 찾은 xid : " + result);
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	//탑승상태가 변경된 아이의 튜플에서 기사를 찾아 해당 아이의 탑승상태가 변화했음을 알린다 - 기사
	public String changeRideDriver(Connection conn, String xid) {
		System.out.println("changeRideDriver 가동");
		PreparedStatement pstmt = null;
	    ResultSet rs = null;
        String sql = "SELECT * FROM student WHERE xid = ?"; 
        String busNum = "";
        String cName = "";
	    
		PreparedStatement pstmt2 = null;
	    ResultSet rs2 = null;
        String sql2 = "SELECT xid FROM bus WHERE busNum = ?"; 
        String driverId = "";
        String result;
        
        try {
        	pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, xid);
	        rs = pstmt.executeQuery();
	        System.out.println("changeRideDriver 작동한 쿼리문 1: " + sql);
	        if(rs.next()) {
	        	busNum = rs.getString("student_busNum"); 
	        	cName = rs.getString("name");
	        	System.out.println("changeRideDriver 찾은 값 : " + busNum + "에 탄" + cName);
	        }
	        pstmt.close();
	        
	 	    pstmt2 = conn.prepareStatement(sql2);
		    pstmt2.setString(1, busNum);
		    rs2 = pstmt2.executeQuery();
		    System.out.println("changeRideDriver 작동한 쿼리문 2: " + sql2);
	        if(rs2.next()) {
	        	driverId = rs2.getString("xid");
	        	System.out.println("changeRideDriver driverid : "+busNum);
	        }
	        pstmt2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        result = driverId+","+cName;
		return result;
	}
	
	//원장에게 소속된 운전기사의 id, 이름을 모두 불러온다.
	public String getManagerField(Connection conn, String xid) {
		PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
        String sql = "SELECT * FROM bus WHERE wonjang_xid = ?"; 
        ArrayList<String> arr = new ArrayList<>();
        StringBuffer result = new StringBuffer();
        
        
        try {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, xid);
	        rs = pstmt.executeQuery();
	         while(rs.next()) {
	        	 arr.add(rs.getString("name")+","+rs.getString("xid"));
	         }
	         pstmt.close();
	         for(int i = 0; i < arr.size(); i++) {
	        	 result.append(arr.get(i));
	        	 if( i != (arr.size()-1))
	        		 result.append("#");
	         }
		} catch (SQLException e) {
			e.printStackTrace();
		}
        //   name,xid#name,xid...
		return result.toString();
	}
	
	//해당 Driver의 좌표값을 리턴한다.
	public static String returnPosition(Connection conn, String xid) {
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String sql = "SELECT location FROM bus WHERE xid=?";
		
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, xid);
			rs = psmt.executeQuery();
			if(rs.next())
				return rs.getString("location");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String position = "";
		return null;
	}
	
}
