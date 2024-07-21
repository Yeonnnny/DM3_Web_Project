package net.kdigital.ec21.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.BlacklistDTO;
import net.kdigital.ec21.dto.check.ReportCategory;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "blacklist")
public class BlacklistEntity {
    @SequenceGenerator(name = "blacklist_seq", sequenceName = "blacklist_seq", allocationSize = 1, initialValue = 1)
    @Id
    @Column(name = "blacklist_id")
    @GeneratedValue(generator = "blacklist_seq")
    private Long blacklistId;
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(name = "comp_name", nullable = false)
    private String compName;
    @Column(name = "remote_ip", nullable = false)
    private String remoteIp;
    @Column(name = "country")
    private String country;
    @Column(name = "black_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportCategory blackType;
    @Column(name = "black_reason")
    private String blackReason;
    @Column(name = "input_date")
    @CreationTimestamp
    private LocalDateTime inputDate;

    public static BlacklistEntity toEntity(BlacklistDTO dto) {
        return BlacklistEntity.builder()
                .blacklistId(dto.getBlacklistId())
                .customerId(dto.getCustomerId())
                .compName(dto.getCompName())
                .remoteIp(dto.getRemoteIp())
                .country(dto.getCountry())
                .blackType(dto.getBlackType())
                .blackReason(dto.getBlackReason())
                .inputDate(dto.getInputDate())
                .build();
    }
}
