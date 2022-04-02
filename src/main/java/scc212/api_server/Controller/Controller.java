package scc212.api_server.Controller;

import scc212.api_server.DAO.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.*;
import scc212.api_server.Entity.NationHistory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;

@CrossOrigin(origins = {"*","null"})
@RestController
public class Controller {
    private JdbcTemplate jdbcTemplate;

    public Controller() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://112.125.95.205:3306/covid_19?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&serverTimezone=Asia/Shanghai");
        dataSource.setUsername("root");
        dataSource.setPassword("2020");
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // >>> World API Part
    @RequestMapping("/get/Country")
    public List getCountry(@RequestParam(value = "country" , required = false, defaultValue = "all") String name) {
        CountryDAO country = new CountryDAO();
        country.reset();
        country.setInput(name);
        country.setJdbc(this.jdbcTemplate);
        country.access();
        return country.getCountry();
    }

    @RequestMapping("/get/WorldHistory")
    public List getWorldHistory(@RequestParam(value = "country" , required = false, defaultValue = "all") String name,
                                @RequestParam(value = "date", required = false, defaultValue = "all") String inputDate,
                                @RequestParam(value = "startDate", required = false, defaultValue = "none") String startDate,
                                @RequestParam(value = "endDate", required = false, defaultValue = "none") String endDate) {
        WorldHistoryDAO worldHistory = new WorldHistoryDAO();
        worldHistory.reset();
        worldHistory.setInput(name);
        worldHistory.setDate(inputDate);
        worldHistory.setStartDate(startDate);
        worldHistory.setEndDate(endDate);
        worldHistory.setJdbc(this.jdbcTemplate);
        worldHistory.access();
        return worldHistory.getCountry();
    }
    @RequestMapping("/get/WorldHistorySum")
    public List getWorldHistorySum(@RequestParam(value = "name" , required = false, defaultValue = "all") String name,
                                   @RequestParam(value = "date", required = false, defaultValue = "all") String inputDate,
                                   @RequestParam(value = "startDate", required = false, defaultValue = "none") String startDate,
                                   @RequestParam(value = "endDate", required = false, defaultValue = "none") String endDate) {

        WorldHistorySumDAO worldHistorySum = new WorldHistorySumDAO();
        worldHistorySum.reset();
        worldHistorySum.setInput(name);
        worldHistorySum.setDate(inputDate);
        worldHistorySum.setStartDate(startDate);
        worldHistorySum.setEndDate(endDate);
        worldHistorySum.setJdbc(this.jdbcTemplate);
        worldHistorySum.access();
        return worldHistorySum.getData();
    }
    // >>> The end of World API Part


    /*
    APIs of China
     */

    //Get the information for provinces in China, can choose one or all provinces
    @RequestMapping("/get/CurrentProInfo")
    public List getCityInfo(@RequestParam(value = "proName" , required = false, defaultValue = "all") String name)
    {
        CurrentProDAO currentPro = new CurrentProDAO();
        currentPro.reset();
        currentPro.setInput(name);
        currentPro.setJdbc(this.jdbcTemplate);
        currentPro.access();
        return currentPro.getPor();
    }

    @RequestMapping(value = "/get/getProHistoryData")
    public Object getHistoryData(@RequestParam(value = "proName") String name,
                               @RequestParam(value = "date", required = false, defaultValue = "all") String date)
    {
        ProHistoryDAO proHisData = new ProHistoryDAO();
        proHisData.reset();
        proHisData.setParaPro(name);
        proHisData.setParaDate(date);
        proHisData.setJdbc(this.jdbcTemplate);
        proHisData.access();
        return proHisData.returnList();
    }

    @RequestMapping("/get/CurrentChina")
    public Object getNation()
    {
        NationDAO nation = new NationDAO();
        nation.reset();
        nation.setJdbc(this.jdbcTemplate);
        nation.access();
        return nation.getNation();
    }

    @RequestMapping("/get/CurrentCities")
    public Object getProWithCities(@RequestParam(value = "proName", required = false, defaultValue = "all") String name)
    {
        ProvinceWithCitiesDAO cities = new ProvinceWithCitiesDAO();
        cities.reset();
        cities.setJdbc(this.jdbcTemplate);
        cities.setInput(name);
        cities.access();
        return cities.getCityObjects();
    }
    
    @RequestMapping("/get/NationHistory")
    public List<NationHistory> getChinaHistory(@RequestParam(value = "date" , required = false, defaultValue = "all") String date)
    {
        NationHistoryDAO nationalHistory = new NationHistoryDAO();
        nationalHistory.reset();
        nationalHistory.setJdbcTemplate(this.jdbcTemplate);
        nationalHistory.setInput(date);
        nationalHistory.access();
        return nationalHistory.getNationalHistory();
    }
    
    @RequestMapping("/get/CurrentLocation")
    private Object getIpAddress(HttpServletRequest request)
    {
        String clientIp = null;
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
                ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                //Get ip address according to network card.
                if (ip.equals("127.0.0.1")) {
                    //Get local ip use network card.
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (Exception e) {
                        clientIp = "112.125.95.205";
                        e.printStackTrace();
                    }
                    ip = inet.getHostAddress();
                }
            }
            //Multi-proxy, the first one is the real ip of client.
            if (ip != null && ip.length() > 15) {
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
            clientIp = ip;
        }catch(Exception e)
        {
            clientIp = "112.125.95.205";
        }
        //Note that can not use localhost as an client, or can not return the local data.
        CurrentLocationDAO curPro = new CurrentLocationDAO(clientIp, this.jdbcTemplate);
        curPro.process();
        return curPro.getCurPro();
    }

    @RequestMapping("/NationChart")
    public Object getNationChart(@RequestParam(value = "type") String type)
    {
        NationChartDAO nationChart = new NationChartDAO();
        nationChart.reset();
        nationChart.setInput(type);
        nationChart.setJdbc(this.jdbcTemplate);
        nationChart.access();
        return nationChart.getReturnChart();
    }

    /*
    APIs for others
     */

    @RequestMapping("/MedicalComments")
    public List getMedicalComments()
    {
        MedicalCommentsDAO comments = new MedicalCommentsDAO();
        comments.reset();
        comments.access();
        return comments.getReturn_list();
    }

    @RequestMapping("/CurrentNews")
    public List getCurrentNews()
    {
        CurrentNewsDAO newsDAO = new CurrentNewsDAO();
        newsDAO.reset();
        newsDAO.access();
        return newsDAO.getCurNewsList();
    }


    @RequestMapping("/Knowledge")
    public List getKnowledge()
    {
        KnowledgeDAO knowledge = new KnowledgeDAO();
        knowledge.reset();
        knowledge.access();
        return knowledge.getReturn_list();
    }
}
