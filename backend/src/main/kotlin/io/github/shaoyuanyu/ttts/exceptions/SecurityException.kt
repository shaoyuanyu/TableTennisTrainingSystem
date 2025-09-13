package io.github.shaoyuanyu.ttts.exceptions

/**
 * 安全异常（权限不足、访问被拒绝等安全相关问题）
 */
class SecurityException(message: String) : Throwable(message)