package io.github.shaoyuanyu.ttts.exceptions

/**
 * 授权异常（登录验证失败、访问未授权资源）
 */
class UnauthorizedException(message: String) : Throwable(message)