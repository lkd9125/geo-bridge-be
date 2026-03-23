package com.geo.bridge.domain.user.dto.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("USERS")
public class UserDTO implements Persistable<String>{

    @Id
    @Column("USERNAME")
    private String username;

    @Column("PASSWORD")
    private String password;

    @Column("CREATE_DT")
    private LocalDateTime createDt;
    
    @Column("UPDATE_DT")
    private LocalDateTime updateDt;
    
    @Column("CREATE_AT")
    private String createAt;
    
    @Column("UPDATE_AT")
    private String updateAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.username;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

}
