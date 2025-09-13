package io.github.shaoyuanyu.ttts.exceptions

/**
 * 参数异常（前端向后端传递的参数不符合标准或无效）
 */
class BadRequestException(message: String) : Throwable(message)