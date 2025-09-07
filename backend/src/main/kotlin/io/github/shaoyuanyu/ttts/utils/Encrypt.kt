package io.github.shaoyuanyu.ttts.utils

import org.mindrot.jbcrypt.BCrypt

/**
 * 采用 BCrypt 算法
 */
fun encryptPasswd(plainPassword: String): String =
    BCrypt.hashpw(plainPassword, BCrypt.gensalt())

/**
 * 验证密码，采用 BCrypt 算法
 */
fun validatePasswd(plainPassword: String, encryptedPassword: String): Boolean =
    BCrypt.checkpw(plainPassword, encryptedPassword)
