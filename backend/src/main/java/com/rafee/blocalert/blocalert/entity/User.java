package com.rafee.blocalert.blocalert.entity;

import com.rafee.blocalert.blocalert.entity.enums.Theme;
import com.rafee.blocalert.blocalert.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth0_id", nullable = false, length = 100)
    private String auth0Id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, length = 60)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.ROLE_FREE_USER;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme-preference", nullable = false)
    private Theme themePreference = Theme.DARK;

    @Column(name = "stripe_customer_id", length = 60)
    String stripeCustomerId;

    @Column(name = "is_subscribed")
    boolean isSubscribed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime updatedAt;

}
