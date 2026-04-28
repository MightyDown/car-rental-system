package com.carrental.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carrental.entity.*;
import com.carrental.mapper.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitService {

    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;
    private final VehicleConfigMapper configMapper;
    private final BookingMapper bookingMapper;
    private final AccidentMapper accidentMapper;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // @Transactional is intentionally NOT used here — Spring invokes
        // @PostConstruct on the raw bean instance, bypassing the proxy,
        // so @Transactional would be silently ignored. Each step guards
        // itself with existence checks and catches duplicates safely.
        initUsers();
        initVehicles();
        initBookings();
        initAccidents();
    }

    private void initUsers() {
        createUserIfAbsent("admin", "admin123", "系统管理员", "ADMIN", "110101199001011234");
        createUserIfAbsent("testuser", "test123", "测试用户", "USER", "110101199501018888");
        createUserIfAbsent("li_customer", "li123456", "刘先生", "USER", "110101199001011234");
        createUserIfAbsent("zhang_customer", "zhang123", "张小姐", "USER", "110101199502025678");

        // Set Zhang's credit score lower for testing
        User zhang = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "zhang_customer"));
        if (zhang != null && zhang.getCreditScore() == 100) {
            zhang.setCreditScore(75);
            userMapper.updateById(zhang);
        }
    }

    private void initVehicles() {
        if (vehicleMapper.selectCount(null) > 0) return;

        Vehicle v1 = new Vehicle();
        v1.setPlateNo("京A12345");
        v1.setModel("凯美瑞");
        v1.setType("economy");
        v1.setStatus("idle");
        v1.setCurrentMileage(50000);
        vehicleMapper.insert(v1);

        Vehicle v2 = new Vehicle();
        v2.setPlateNo("京A88888");
        v2.setModel("奔驰 C级");
        v2.setType("luxury");
        v2.setStatus("idle");
        v2.setCurrentMileage(30000);
        vehicleMapper.insert(v2);

        Vehicle v3 = new Vehicle();
        v3.setPlateNo("京A66666");
        v3.setModel("雅阁");
        v3.setType("economy");
        v3.setStatus("idle");
        v3.setCurrentMileage(80000);
        vehicleMapper.insert(v3);
    }

    private void initBookings() {
        if (bookingMapper.selectCount(null) > 0) return;

        Long liuId = getUserId("li_customer");
        Long zhangId = getUserId("zhang_customer");
        Long camryId = getVehicleId("京A12345");
        Long benzId = getVehicleId("京A88888");
        Long accordId = getVehicleId("京A66666");

        // Guard: skip if prerequisite users/vehicles were not created
        if (liuId == null || zhangId == null || camryId == null || benzId == null || accordId == null) {
            log.warn("Skipping booking init: prerequisite users or vehicles not found");
            return;
        }

        // 1. Pending booking - Liu wants Camry
        createBooking("BK202603150001", liuId, camryId,
                now().plusDays(1), now().plusDays(4), 3,
                new BigDecimal("600"), "pending", null);

        // 2. Confirmed booking - Liu confirmed for Benz
        createBooking("BK202603150002", liuId, benzId,
                now().minusDays(2), now().plusDays(1), 3,
                new BigDecimal("1800"), "confirmed", null);
        updateVehicleStatus(benzId, "confirmed");

        // 3. Picked up booking - Zhang using Accord
        createBooking("BK202603100001", zhangId, accordId,
                now().minusDays(3), now().minusDays(1), 2,
                new BigDecimal("400"), "picked_up",
                now().minusDays(3).plusHours(1));
        updateVehicleStatus(accordId, "rented");
        updateBookingPickup("BK202603100001", now().minusDays(3).plusHours(1), 80000);

        // 4. Returned booking - Liu returned Camry (completed)
        LocalDateTime pickupTime = now().minusDays(7);
        LocalDateTime returnTime = pickupTime.plusDays(2).plusHours(3);
        createBooking("BK202603010001", liuId, camryId,
                pickupTime.minusDays(1), pickupTime.plusDays(2), 3,
                new BigDecimal("600"), "returned", pickupTime);
        updateBookingFull("BK202603010001", pickupTime, 50000,
                returnTime, 50080,
                180, new BigDecimal("60"), BigDecimal.ZERO, new BigDecimal("660"));
        updateVehicleStatus(camryId, "idle");
        updateVehicleMileage(camryId, 50080);
    }

    private void initAccidents() {
        if (accidentMapper.selectCount(null) > 0) return;

        Long zhangId = getUserId("zhang_customer");
        Long accordId = getVehicleId("京A66666");
        if (zhangId == null || accordId == null) {
            log.warn("Skipping accident init: prerequisite users or vehicles not found");
            return;
        }

        // Find Zhang's picked-up booking
        Booking booking = bookingMapper.selectOne(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getUserId, zhangId)
                .eq(Booking::getStatus, "picked_up"));
        if (booking == null) return;

        // Accident 1: Pending
        Accident a1 = new Accident();
        a1.setBookingId(booking.getId());
        a1.setVehicleId(accordId);
        a1.setDescription("车辆右前门剐蹭，需钣金喷漆修复");
        a1.setStatus("pending");
        accidentMapper.insert(a1);

        // Find Liu's returned booking
        Long liuId = getUserId("li_customer");
        Long camryId = getVehicleId("京A12345");
        if (liuId == null || camryId == null) return;

        Booking liuBooking = bookingMapper.selectOne(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getUserId, liuId)
                .eq(Booking::getStatus, "returned"));
        if (liuBooking != null) {
            Accident a2 = new Accident();
            a2.setBookingId(liuBooking.getId());
            a2.setVehicleId(camryId);
            a2.setDescription("后保险杠碰撞损坏，需更换保险杠");
            a2.setDeductionPoints(10);
            a2.setExpectedCompletionDate(LocalDate.now().plusDays(5));
            a2.setStatus("in_progress");
            accidentMapper.insert(a2);

            updateVehicleStatus(camryId, "maintenance");
        }
    }

    // ==================== Helpers ====================

    private void createUserIfAbsent(String username, String rawPwd, String realName, String role, String licenseNo) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (count > 0) return;

        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPwd));
            user.setRealName(realName);
            user.setRole(role);
            user.setStatus(1);
            user.setCreditScore(100);
            user.setLicenseNo(licenseNo);
            user.setPhone("138" + String.format("%08d", new Random().nextInt(99999999)));
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            // A soft-deleted (deleted=1) record with this username exists;
            // MyBatis-Plus logic-deletion excludes it from selectCount,
            // but the database UNIQUE constraint still rejects the duplicate.
            log.info("User '{}' already exists (soft-deleted), skipping", username);
        }
    }

    private void createBooking(String bookingNo, Long userId, Long vehicleId,
                               LocalDateTime pickup, LocalDateTime returnTime, int days,
                               BigDecimal rent, String status, LocalDateTime actualPickup) {
        Booking b = new Booking();
        b.setBookingNo(bookingNo);
        b.setUserId(userId);
        b.setVehicleId(vehicleId);
        b.setPlannedPickupTime(pickup);
        b.setPlannedReturnTime(returnTime);
        b.setPlannedDays(days);
        b.setEstimatedRent(rent);
        b.setStatus(status);
        if (actualPickup != null) {
            b.setActualPickupTime(actualPickup);
        }
        bookingMapper.insert(b);
    }

    private void updateBookingPickup(String bookingNo, LocalDateTime pickupTime, int mileage) {
        Booking b = bookingMapper.selectOne(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getBookingNo, bookingNo));
        if (b != null) {
            b.setActualPickupTime(pickupTime);
            b.setPickupMileage(mileage);
            b.setPlannedReturnTime(pickupTime.plusDays(b.getPlannedDays()));
            bookingMapper.updateById(b);
        }
    }

    private void updateBookingFull(String bookingNo, LocalDateTime pickupTime, int pickupMileage,
                                    LocalDateTime returnTime, int returnMileage,
                                    int overtimeMin, BigDecimal overtimeFee,
                                    BigDecimal excessFee, BigDecimal totalFee) {
        Booking b = bookingMapper.selectOne(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getBookingNo, bookingNo));
        if (b != null) {
            b.setActualPickupTime(pickupTime);
            b.setPickupMileage(pickupMileage);
            b.setActualReturnTime(returnTime);
            b.setReturnMileage(returnMileage);
            b.setOvertimeMinutes(overtimeMin);
            b.setOvertimeFee(overtimeFee);
            b.setExcessMileageFee(excessFee);
            b.setTotalFee(totalFee);
            bookingMapper.updateById(b);
        }
    }

    private void updateVehicleStatus(Long vehicleId, String status) {
        Vehicle v = vehicleMapper.selectById(vehicleId);
        if (v != null) {
            v.setStatus(status);
            vehicleMapper.updateById(v);
        }
    }

    private void updateVehicleMileage(Long vehicleId, int mileage) {
        Vehicle v = vehicleMapper.selectById(vehicleId);
        if (v != null) {
            v.setCurrentMileage(mileage);
            vehicleMapper.updateById(v);
        }
    }

    private Long getUserId(String username) {
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return u != null ? u.getId() : null;
    }

    private Long getVehicleId(String plateNo) {
        Vehicle v = vehicleMapper.selectOne(new LambdaQueryWrapper<Vehicle>().eq(Vehicle::getPlateNo, plateNo));
        return v != null ? v.getId() : null;
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }
}
