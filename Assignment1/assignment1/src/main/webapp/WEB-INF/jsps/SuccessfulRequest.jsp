<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Order Details</title>
    <link rel="stylesheet" href="StoreStyle.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js" type="text/javascript"></script>
</head>
<body>
<section id="add-order">
    <div class="store container">
        <div class="filter">
        <h1>Your request has been successfully processed</h1>
        <c:if test="${not empty requestScope.Result}">

            <c:set var="result" scope="request" value="${requestScope.Result}"/>

            <p>Customer and Order Information</p>
            <table style="width: 100%">
                <tr>
                    <td>Order reference</td>
                    <td>${result.orderReference}</td>
                </tr>
                <tr>
                    <td>Customer Name</td>
                    <td>${result.customerName}</td>
                </tr>
                <tr>
                    <td>Customer Phone</td>
                    <td>${result.customerPhone}</td>
                </tr>
                <tr>
                    <td>Total Bill</td>
                    <td>${result.totalBill}</td>
                </tr>
                <tr>
                    <td>Date Order</td>
                    <td>${result.dateOrder}</td>
                </tr>
            </table>
        </c:if>
        <br/>
            <h1>Update An Order</h1>
            Order Reference: <br>
            <input type="text" class="orderReference" value="${result.orderReference}" disabled><br>
            Total Bill: <br>
            <input type="text" class="totalBill" required="required"><br>
            Order Date:<br>
            <input type="date" class="dateOrder" required="required"><br><br>
            <button onclick="updateAnOrder($('.orderReference').val(), $('.totalBill').val(), $('.dateOrder').val())">
                Update Order
            </button>
            <br>
            <button class="del-btn" onclick="deleteOrder($('.orderReference').val())">Delete Order</button>
            <br>
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

