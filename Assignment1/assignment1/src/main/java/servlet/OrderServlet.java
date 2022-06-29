package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.OrderDao;
import lombok.SneakyThrows;
import model.Gender;
import service.OrderService;
import servlet.request.OrderRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {

    private final Logger logger;

    public OrderServlet() {
        logger = Logger.getLogger(getClass().getName());
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(true);
        Date dateOrder = parseDate(request.getParameter("dateOrder"));
        OrderDao orderDao = (OrderDao) getServletContext().getAttribute("orderDao");
        List<Result> results = orderDao.getAllOrdersFromADate(dateOrder);
        session.setAttribute("Result", results);

        RequestDispatcher dispatcher = getServletContext().
                getRequestDispatcher("/OrderDetails.jsp");
        dispatcher.forward(request, response);
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        OrderService orderService = (OrderService) getServletContext().getAttribute("orderService");
        String customerName = request.getParameter("customerName");
        String customerPhone = request.getParameter("customerPhone");
        Gender gender = Gender.valueOf(request.getParameter("gender"));
        double totalBill = Double.parseDouble(request.getParameter("totalBill"));
        Date dateOrder = parseDate(request.getParameter("dateOrder"));

        String orderReference = orderService.addNewOrder(customerName, customerPhone, gender, totalBill, dateOrder);
        Result result = new Result(orderReference, customerName, customerPhone, totalBill, dateOrder);
        request.setAttribute("Result", result);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/SuccessfulRequest.jsp");
        dispatcher.forward(request, response);
    }

    private static Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(date);
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        OrderService orderService = (OrderService) getServletContext().getAttribute("orderService");
        String requestData = request.getReader().lines().collect(Collectors.joining());
        ObjectMapper objectMapper = new ObjectMapper();
        OrderRequest orderRequest = objectMapper.readValue(requestData, OrderRequest.class);
        String orderReference = orderRequest.getOrderReference();
        double totalBill = orderRequest.getTotalBill();
        Date dateOrder = orderRequest.getDateOrder();

        orderService.upDateOrderByOrderReference(orderReference, totalBill, dateOrder);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\": \"true\" }");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderService orderService = (OrderService) getServletContext().getAttribute("orderService");
        String requestData = request.getReader().lines().collect(Collectors.joining());
        ObjectMapper objectMapper = new ObjectMapper();
        OrderRequest orderRequest = objectMapper.readValue(requestData, OrderRequest.class);
        String orderReference = orderRequest.getOrderReference();
        orderService.deleteOrderByReference(orderReference);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\": \"true\" }");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
