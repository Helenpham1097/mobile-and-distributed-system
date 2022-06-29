<%@ page contentType="text/html; charset=UTF-8"
                        pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Order Details</title>
    <script src="ajax_library.js" type="text/javascript"></script>
    <style><%@include file="StoreStyle.css"%></style>
</head>
<body>
<section id="add-order">
    <div class="store container">
        <div class="filter">
            <h1>Your request has been successfully processed</h1>
            <br>
            <c:if test="${not empty sessionScope.Result}">

                <c:set var="results" scope="session" value="${sessionScope.Result}"/>

                <table id="customers" style="width:100%">
                    <c:out value="All orders of ${results[0].dateOrder}"/>
                    <br>
                    <tr>
                        <th>Order reference</th>
                        <th>Customer Name</th>
                        <th>Customer Phone</th>
                        <th>Total Bill</th>
                        <th>Date order</th>
                    </tr>
                    <c:forEach items="${results}" var="result" >
                        <tr>
                            <td>${result.orderReference}</td>
                            <td>${result.customerName}</td>
                            <td>${result.customerPhone}</td>
                            <td>${result.totalBill}</td>
                            <td>${result.dateOrder}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>
            <br/>
            <a href='${pageContext.request.contextPath}/StoreManagement.html'>
                <strong>Return to Main Page</strong>
            </a>
        </div>
    </div>
</section>
</body>
<script>
    function updateAnOrder(orderReference, totalBill, dateOrder) {
        $.ajax({
            type: "PUT",
            url: "/store/order",
            data: JSON.stringify({
                orderReference: orderReference,
                totalBill: totalBill,
                dateOrder: dateOrder
            }),

            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (response) {
                window.alert('Successfully Updated');
            }
        })
    }

    function deleteOrder(orderReference) {
        $.ajax({
            type: "DELETE",
            url: "http://localhost:8080/store/order",
            data: JSON.stringify({
                orderReference: orderReference
            }),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (response) {
                window.alert('Successfully Deleted');
            }
        })
    }
</script>
</html>


