package com.example.jwttest.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "authority")
class Authority(
    @Id
    @Column(name = "authority_name", length = 50)
    val authorityName: String? = null
)