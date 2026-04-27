package com.dacsan.entity;

public enum OrderStatus {
    PENDING, // Chờ xác nhận
    PAID,//Đã thanh toán
    CONFIRMED, // Đã xác nhận
    PROCESSING, // Đang xử lý
    PREPARING, // Đang chuẩn bị
    READY, // Sẵn sàng giao
    DELIVERING, // Đang giao
    COMPLETED, // Hoàn thành
    CANCELLED // Đã hủy
}
