package com.example.jwttest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;


    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }

    public Authority() {
    }

    public static AuthorityBuilder builder() {
        return new AuthorityBuilder();
    }

    public String getAuthorityName() {
        return this.authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public static class AuthorityBuilder {
        private String authorityName;

        AuthorityBuilder() {
        }

        public AuthorityBuilder authorityName(String authorityName) {
            this.authorityName = authorityName;
            return this;
        }

        public Authority build() {
            return new Authority(authorityName);
        }

        public String toString() {
            return "Authority.AuthorityBuilder(authorityName=" + this.authorityName + ")";
        }
    }
}
