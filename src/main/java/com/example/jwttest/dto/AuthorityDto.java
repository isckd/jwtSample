package com.example.jwttest.dto;


public class AuthorityDto {
    private String authorityName;

    public AuthorityDto(String authorityName) {
        this.authorityName = authorityName;
    }

    public AuthorityDto() {
    }

    public static AuthorityDtoBuilder builder() {
        return new AuthorityDtoBuilder();
    }

    public String getAuthorityName() {
        return this.authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public static class AuthorityDtoBuilder {
        private String authorityName;

        AuthorityDtoBuilder() {
        }

        public AuthorityDtoBuilder authorityName(String authorityName) {
            this.authorityName = authorityName;
            return this;
        }

        public AuthorityDto build() {
            return new AuthorityDto(authorityName);
        }

        public String toString() {
            return "AuthorityDto.AuthorityDtoBuilder(authorityName=" + this.authorityName + ")";
        }
    }
}
