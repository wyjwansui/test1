package excel.main;

import com.util.Util;
import excel.jdbc.JdbcUtil;
import excel.util.ExcelService;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportCode {

    public static void main(String[] args) {
        //import1("ti","Ti评分","/Users/meliora/Downloads/test1.xlsx");
        //import1("gcs","GCS评分","/Users/meliora/Downloads/test2.xlsx");
        //import1("apgar","Apgar评分","/Users/meliora/Downloads/test3.xlsx");
        import1("chestpain","胸痛评分","/Users/meliora/Downloads/test4.xlsx");
        //import1("fast","fast评分","/Users/meliora/Downloads/test5.xlsx");
        //import1("uastimi","NSTIMI评分","/Users/meliora/Downloads/test6.xlsx");
        //import1("sttimi","STIMI评分","/Users/meliora/Downloads/test7.xlsx");
        //import1("grace","Grace评分","/Users/meliora/Downloads/test8.xlsx");
        //import1("nihss","NIHSS评分","/Users/meliora/Downloads/test9.xlsx");
        //import1("pregnant","高危孕产妇评分","/Users/meliora/Downloads/test10.xlsx");
        //import1("iss","ISS评分","/Users/meliora/Downloads/test11.xlsx");
    }

    private static void import1(String template,String templateVlaue,String fileName){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("评分项", "item");
        map.put("子项单选", "subItem");
        map.put("评分项标题", "title");
        map.put("评分项大类型", "big");
        map.put("评分项小类型", "less");
        Map bigMap = new HashMap();
        try {
            File file = new File(fileName);
            List<Map<String,String>> valueList = ExcelService.readExcel(map, file);
            System.out.println(valueList);
            insert(template,"title",templateVlaue,null,template);
            bigMap.put(template,template);
            for(Map<String,String> cloumn : valueList){
                String big = cloumn.get("big");
                if (!Util.valid(cloumn.get("subItem"))){
                    continue;
                }
                String subItem = cloumn.get("subItem").equals("是")?"single":"judgement";
                if(bigMap.get(big)==null){
                    insert(big,"judgement".equals(subItem)?"judgement_title":subItem,cloumn.get("item"),null,template);
                    bigMap.put(big,big);
                }
                if (!Util.valid(cloumn.get("less"))){
                    continue;
                }
                insert(cloumn.get("less"),"single".equals(subItem)?"text":subItem,cloumn.get("title"),big,template);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static int insert(String code,String type,String display,String parentCode,String template) {
        Connection conn = JdbcUtil.getConn();
        int i = 0;
        String sql = "INSERT c_grade_code (code,type,display,parent_code,template) VALUES(?,?,?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, code);
            pstmt.setString(2, type);
            pstmt.setString(3, display);
            pstmt.setString(4, parentCode);
            pstmt.setString(5, template);
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }
    private static Integer getAll() {
        Connection conn = JdbcUtil.getConn();
        String sql = "select * from students";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            System.out.println("============================");
            while (rs.next()) {
                for (int i = 1; i <= col; i++) {
                    System.out.print(rs.getString(i) + "\t");
                    if ((i == 2) && (rs.getString(i).length() < 8)) {
                        System.out.print("\t");
                    }
                }
                System.out.println("");
            }
            System.out.println("============================");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
